package com.cybersecdaily.widget

/**
 * Parsed data model for a single daily cybersecurity report.
 */
data class DailyReport(
    val date: String = "",
    val dateCN: String = "",
    val editionNumber: String = "",
    val keywords: String = "",
    val mainHeadline: String = "",
    val headlines: List<String> = emptyList(),
    val quickNews: List<String> = emptyList(),
    val error: String? = null
) {
    val hasContent: Boolean get() = keywords.isNotEmpty() || headlines.isNotEmpty()

    companion object {
        fun error(msg: String) = DailyReport(error = msg)
    }
}
