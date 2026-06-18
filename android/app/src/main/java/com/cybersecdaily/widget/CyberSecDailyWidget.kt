package com.cybersecdaily.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

private const val BASE_URL = "https://unclecheng-li.github.io/cybersecurity-daily"

/**
 * Jetpack Glance widget that displays the latest cybersecurity daily report
 * summary on the Android home screen. Tap to open full report in browser.
 */
class CyberSecDailyWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(SizeMode.Single, SizeMode.Exact)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val report = ReportFetcher.fetchLatest()
        val dailyUrl = "$BASE_URL/daily/${report.date}.html"

        provideContent {
            GlanceTheme {
                val size = LocalSize.current
                val isCompact = size.width.value < 280
                val openAction = actionStartActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(dailyUrl))
                )

                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(ColorProvider(android.graphics.Color.parseColor("#1a1a1a")))
                        .padding(12.dp)
                        .clickable(openAction),
                ) {
                    // ---- Title bar ----
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\u7f51\u7edc\u5b89\u5168\u65e5\u62a5",
                            style = TextStyle(
                                color = ColorProvider(android.graphics.Color.parseColor("#c9a227")),
                                fontWeight = FontWeight.Bold,
                            )
                        )
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        if (report.date.isNotEmpty()) {
                            Text(
                                text = formatDateShort(report.date),
                                style = TextStyle(
                                    color = ColorProvider(android.graphics.Color.parseColor("#888888"))
                                )
                            )
                        }
                    }

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    // ---- Keywords row (red accent bar) ----
                    if (report.keywords.isNotEmpty()) {
                        Row(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .background(ColorProvider(android.graphics.Color.parseColor("#c41e3a")))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = truncate(report.keywords, if (isCompact) 40 else 60),
                                style = TextStyle(
                                    color = ColorProvider(android.graphics.Color.WHITE),
                                ),
                                maxLines = if (isCompact) 1 else 2
                            )
                        }
                        Spacer(modifier = GlanceModifier.height(8.dp))
                    }

                    // ---- Headlines ----
                    val displayItems = report.headlines.take(if (isCompact) 2 else 4)
                    for ((index, item) in displayItems.withIndex()) {
                        if (index > 0) {
                            Spacer(modifier = GlanceModifier.height(4.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "\u25b8",
                                style = TextStyle(
                                    color = ColorProvider(android.graphics.Color.parseColor("#e85d04")),
                                    fontWeight = FontWeight.Bold,
                                )
                            )
                            Spacer(modifier = GlanceModifier.width(6.dp))
                            Text(
                                text = truncate(item, if (isCompact) 40 else 60),
                                style = TextStyle(
                                    color = ColorProvider(android.graphics.Color.parseColor("#e8e0d0")),
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    // ---- Error state ----
                    if (report.error != null) {
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Text(
                            text = report.error,
                            style = TextStyle(
                                color = ColorProvider(android.graphics.Color.parseColor("#e85d04"))
                            ),
                            maxLines = 2
                        )
                    }

                    // ---- Bottom bar (hidden in compact mode) ----
                    if (!isCompact) {
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AI\u57fa \u00b7 \u6bcf\u65e5\u7cbe\u9009",
                                style = TextStyle(
                                    color = ColorProvider(android.graphics.Color.parseColor("#666666"))
                                )
                            )
                            Spacer(modifier = GlanceModifier.defaultWeight())
                            Text(
                                text = "\u70b9\u51fb\u9605\u8bfb\u5b8c\u6574\u65e5\u62a5 \u2192",
                                style = TextStyle(
                                    color = ColorProvider(android.graphics.Color.parseColor("#c9a227"))
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun truncate(text: String, maxLen: Int): String {
        return if (text.length > maxLen) text.take(maxLen) + "\u2026" else text
    }

    companion object {
        fun formatDateShort(date: String): String {
            val parts = date.split("-")
            if (parts.size != 3) return date
            val month = parts[1].trimStart('0')
            val day = parts[2].trimStart('0')
            return "$month/$day"
        }
    }
}
