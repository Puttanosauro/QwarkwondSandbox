package org.example.project.qdcore.util

//TODO check for dead code

/**
 * @param prefix prefix to remove
 * @param ignoreCase whether to ignore case when searching for the prefix
 * @return a pair of this string without [prefix] and a boolean value indicating whether the prefix was removed.
 */
fun String.removeOptionalPrefix(
    prefix: String,
    ignoreCase: Boolean = false,
): Pair<String, Boolean> =
    if (startsWith(prefix, ignoreCase)) {
        substring(prefix.length) to true
    } else {
        this to false
    }

/**
 * @return a sliced copy of this string from start to the last occurrence of [string] if it exists,
 * this string otherwise
 */
fun String.takeUntilLastOccurrence(string: String): String {
    val trailingIndex = lastIndexOf(string)
    return if (trailingIndex >= 0) {
        substring(0, trailingIndex)
    } else {
        this
    }
}

/**
 * @return a substring of [this] string from [startIndex] to [endIndex] if the indices are within bounds.
 */
fun CharSequence.substringWithinBounds(
    startIndex: Int,
    endIndex: Int,
): String {
    val start = startIndex.coerceAtLeast(0)
    return substring(start, endIndex.coerceAtMost(length).coerceAtLeast(start))
}

/**
 * @return [this] string without the first and last characters, if possible
 */
fun String.trimDelimiters(): String = if (length >= 2) substring(1, length - 1) else this

/**
 * Indents each line of [this] string by [indent].
 */
fun CharSequence.indent(indent: String) =
    buildString {
        this@indent
            .lineSequence()
            .filterNot { it.isEmpty() }
            .forEach { append(indent).append(it).append("\n") }
    }

/**
 * @param count number of lines to take
 * @param addOmittedLinesSuffix whether to add a suffix indicating how many lines were omitted
 */
fun CharSequence.takeLines(
    count: Int,
    addOmittedLinesSuffix: Boolean,
): String {
    if (!addOmittedLinesSuffix) {
        return this.lines().take(count).joinToString(separator = "\n")
    }

    val lines = this.lines()
    return if (lines.size <= count) {
        this.toString()
    } else {
        buildString {
            lines.take(count).forEach { appendLine(it) }
            appendLine("... (${lines.size - count} more lines)")
        }
    }
}

/**
 * An optimized way to replace all occurrences of [oldValue] with [newValue] in a [StringBuilder].
 * (Rewritten to be KMP-safe without java.lang.StringBuilder)
 */
fun StringBuilder.replace(
    oldValue: String,
    newValue: String,
) = apply { // todo check this
    val result = this.toString().replace(oldValue, newValue)
    this.clear()
    this.append(result)
}
/**
 * @return [this] string with all non-alphanumeric characters replaced with [replacement].
 */
fun String.sanitizeFileName(replacement: String) = this.replace("^\\.|\\.$|[^a-zA-Z0-9\\-_.@]+".toRegex(), replacement)

/**
 * @return [this] string with line separators replaced with `\n`
 * (Rewritten to avoid java.lang.System)
 */
fun CharSequence.normalizeLineSeparators(): CharSequence =
    this.toString().replace("\r\n", "\n").replace("\r", "\n")

/**
 * Discards blank entries and trims each remaining entry.
 */
fun List<String>.trimEntries(): List<String> =
    filter { it.isNotBlank() }
        .map { it.trim() }