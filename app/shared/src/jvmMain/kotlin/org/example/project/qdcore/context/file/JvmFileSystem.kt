package org.example.project.qdcore.context.file

import java.io.File

/**
 * JVM-specific implementation of the FileSystem.
 * Allows the Ktorr server to utilize java.io.File
 */
class JvmFileSystem(
    override val workingDirectory: String? = null,
    override val root: FileSystem? = null,
) : FileSystem {

    override fun branch(workingDirectory: String?): FileSystem =
        JvmFileSystem(workingDirectory, root ?: this)

    override fun resolve(path: String): String {
        return if (workingDirectory != null) {
            File(workingDirectory, path).absolutePath
        } else {
            File(path).absolutePath
        }
    }

    override fun relativePathTo(other: FileSystem): String? {
        val fromDir = this.workingDirectory ?: return null
        val toDir = other.workingDirectory ?: return null
        return try {
            File(fromDir).toPath().relativize(File(toDir).toPath()).toString()
        } catch (e: IllegalArgumentException) {
            null
        }
    }


    override fun exists(path: String): Boolean {
        return File(path).exists()
    }

    override fun readText(path: String): String {
        return File(path).readText()
    }
}