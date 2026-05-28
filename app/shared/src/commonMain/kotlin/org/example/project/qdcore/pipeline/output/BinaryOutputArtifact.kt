package org.example.project.qdcore.pipeline.output

/**
 * Represents an [OutputResource] that contains binary data.
 * @param name name of the resource (without file extensions)
 * @param content binary content of the resource
 * @param type type of content the resource contains
 */
data class BinaryOutputArtifact(
    override val name: String,
    override val content: List<Byte>,
    override val type: ArtifactType,
) : OutputArtifact<List<Byte>> {
    override fun <T> accept(visitor: OutputResourceVisitor<T>): T = visitor.visit(this)

    companion object {
        /**
         * Creates a [BinaryOutputArtifact] from a file.
         * @param file file to read the content from
         * @return a [BinaryOutputArtifact] with the file's name and content
         */
        fun fromFile(name: String, bytes: ByteArray): BinaryOutputArtifact =
            BinaryOutputArtifact(
                name = name,
                content = bytes.toList(),
                type = ArtifactType.AUTO,
            )
    }
}
