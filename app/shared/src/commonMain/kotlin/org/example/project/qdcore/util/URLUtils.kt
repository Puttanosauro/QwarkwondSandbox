package org.example.project.qdcore.util

import io.ktor.http.Url
import io.ktor.http.URLParserException
private const val ANCHOR_DELIMITER = '#'

/**
 * Strips the anchor (fragment) from a URL string.
 * @return a pair of the base URL and the anchor, or `null` if no anchor is present
 */
fun String.stripAnchor(): Pair<String, String>? =
    when (val anchorIndex = indexOf(ANCHOR_DELIMITER)) {
        -1 -> null
        else -> Pair(substring(0, anchorIndex), substring(anchorIndex + 1))
    }

/**
 * @return a URL from [this] string if it's a valid URL, or `null` otherwise
 */
fun String.toURLOrNull(): Url? =
    try {
        Url(this)
    } catch (_: URLParserException) {
        null
    }

/**
 * Whether [this] string is a valid URL.
 */
val String.isURL: Boolean
    get() = toURLOrNull() != null
