package org.example.project.qdcore.pipeline.output.visitor

import org.example.project.qdcore.log.Log
import org.example.project.qdcore.pipeline.output.ArtifactType
import org.example.project.qdcore.pipeline.output.BinaryOutputArtifact
import org.example.project.qdcore.pipeline.output.FileReferenceOutputArtifact
import org.example.project.qdcore.pipeline.output.OutputArtifact
import org.example.project.qdcore.pipeline.output.OutputResource
import org.example.project.qdcore.pipeline.output.OutputResourceGroup
import org.example.project.qdcore.pipeline.output.OutputResourceVisitor
import org.example.project.qdcore.pipeline.output.TextOutputArtifact
import org.example.project.qdcore.pipeline.output.visitor.FileResourceExporter.NameProvider.fileNameWithoutExtension
import org.example.project.qdcore.pipeline.output.visitor.FileResourceExporter.NameProvider.fullFileName
import org.example.project.qdcore.util.IOUtils
import org.example.project.qdcore.util.sanitizeFileName
import org.example.project.qdcore.context.file.FileSystem

/**
 * A visitor that saves each type of [OutputResource] to a file and returns it.
 * @param location directory to save the resources to
 * @param write whether to actually write to the file system (if `false`, the visitor only returns the corresponding file paths without creating them)
 */

/*
* !! the modification of this class has been pretty precarious as of right now: if something breaks exporting file, ill bet is this thing
*/
class FileResourceExporter(
    private val location: String,
    private val fileSystem: FileSystem,
    private val write: Boolean = true,
) : OutputResourceVisitor<String> {
    /**
     * Mapping of [OutputResource]s to their file names.
     */
    object NameProvider {
        /**
         * Given a string, returns a sanitized version of it to be used as a valid file name.
         * @see sanitizeFileName
         */
        internal fun stringToFileName(string: String): String = string.sanitizeFileName(replacement = "-")

        /**
         * Name of the corresponding file of this resource, without the extension,
         * with symbols removed and spaces replaced with dashes.
         */
        val OutputResource.fileNameWithoutExtension: String
            get() = stringToFileName(name)

        /**
         * File extension relative to the [ArtifactType] of this resource.
         */
        val OutputArtifact<*>.fileExtension: String
            get() =
                when (type) {
                    ArtifactType.HTML -> ".html"
                    ArtifactType.CSS -> ".css"
                    ArtifactType.JAVASCRIPT -> ".js"
                    ArtifactType.JSON -> ".json"
                    ArtifactType.PLAIN_TEXT -> ".txt"
                    ArtifactType.QUARKDOWN -> ".qd"
                    ArtifactType.AUTO -> "" // Assumes the file name already contains an extension.
                }

        /**
         * Full name of the file, including the extension relative to the [ArtifactType] of this resource.
         */
        val OutputArtifact<*>.fullFileName: String
            get() = fileNameWithoutExtension + fileExtension
    }

    /**
     * Saves an [OutputArtifact] to a file with text content.
     * @return the file itself
     */
    override fun visit(artifact: TextOutputArtifact): String {
        // the if statements is just to ensure the file path is not something like: folder1/foler2//folder3/file.exention
        val targetPath = if (location.endsWith("/")) {
            "$location${artifact.fullFileName}"
        }else {
            "$location/${artifact.fullFileName}"
        }
        if (write) {
            fileSystem.writeText(targetPath, artifact.content.toString())
        }

        return targetPath
    }

    override fun visit(artifact: BinaryOutputArtifact): String {
        val targetPath = if (location.endsWith("/")) {
            "$location${artifact.fullFileName}"
        } else {
            "$location/${artifact.fullFileName}"
        }
        if (write) {
            fileSystem.writeBytes(targetPath, artifact.content.toByteArray())
        }

        return targetPath
    }

    /**
     * Copies a [FileReferenceOutputArtifact] to the output location.
     * If the source is a directory, it is copied recursively.
     *
     * When [FileReferenceOutputArtifact.useChecksumInvalidation] is enabled, a sibling
     * `.checksum` file is maintained next to the output. If the checksum of the source
     * matches the stored value, the copy is skipped entirely. This avoids redundant I/O
     * for large assets (fonts, third-party libraries) that rarely change between builds.
     *
     * @return the copied file or directory
     */
    override fun visit(artifact: FileReferenceOutputArtifact) =
        File(location, artifact.name).also { target ->
            if (!write) return@also

            target.parentFile?.mkdirs()

            if (artifact.useChecksumInvalidation) {
                val checksumFile = target.resolveSibling("${target.name}.checksum")
                val currentChecksum = IOUtils.computeChecksum(artifact.file)
                val storedChecksum = checksumFile.takeIf { it.isFile }?.readText()

                if (currentChecksum == storedChecksum && target.exists()) {
                    Log.debug { "Skipping '${artifact.name}': checksum unchanged ($currentChecksum)" }
                    return@also
                }

                Log.debug {
                    "Copying '${artifact.name}': checksum changed " +
                        "(stored=${storedChecksum ?: "<none>"}, current=$currentChecksum)"
                }
                copyFileOrDirectory(artifact.file, target)
                checksumFile.writeText(currentChecksum)
            } else {
                copyFileOrDirectory(artifact.file, target)
            }
        }

    private fun copyFileOrDirectory(
        source: File,
        target: File,
    ) {
        if (source.isDirectory) {
            source.copyRecursively(target, overwrite = true)
        } else {
            source.copyTo(target, overwrite = true)
        }
    }

    /**
     * Saves an [OutputResourceGroup] to a directory which contains its nested files.
     * @return the directory file itself
     */
    override fun visit(group: OutputResourceGroup): File {
        val directory = File(location, group.fileNameWithoutExtension)

        // The directory is not created if it has no content.
        if (group.resources.isEmpty()) {
            return directory
        }

        if (write) directory.mkdirs()

        // Saves the subfiles in the new directory.
        group.resources.forEach {
            it.accept(FileResourceExporter(directory, write))
        }

        return directory
    }
}

/**
 * Saves [this] resource to file in a [directory].
 * @see FileResourceExporter
 * @return the saved file
 */
fun OutputResource.saveTo(directory: File): File = accept(FileResourceExporter(location = directory))
