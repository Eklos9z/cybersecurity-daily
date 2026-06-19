package com.cybersecdaily.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
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
private const val TAG = "CyberSecWidget"

class CyberSecDailyWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val report: DailyReport
        val dailyUrl: String

        try {
            report = ReportFetcher.fetchLatest()
            dailyUrl = "$BASE_URL/daily/${report.date}.html"
        } catch (e: Exception) {
            Log.e(TAG, "Fetch failed", e)
            // Fallback: show placeholder content
            renderContent(context, DailyReport.error("加载中，请检查网络"), BASE_URL)
            return
        }

        renderContent(context, report, dailyUrl)
    }

    private suspend fun renderContent(context: Context, report: DailyReport, dailyUrl: String) {
        provideContent {
            GlanceTheme {
                val hasError = report.error != null
                val openAction = actionStartActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(dailyUrl))
                )

                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(ColorProvider(android.graphics.Color.parseColor("#1a1a1a")))
                        .padding(12)
                        .clickable(openAction),
                ) {
                    // Title bar
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

                    Spacer(modifier = GlanceModifier.height(6))

                    if (hasError) {
                        // Error state display
                        Text(
                            text = report.error ?: "未知错误",
                            style = TextStyle(
                                color = ColorProvider(android.graphics.Color.parseColor("#e85d04")),
                            ),
                            maxLines = 3
                        )
                    } else {
                        // Keywords row
                        if (report.keywords.isNotEmpty()) {
                            Row(
                                modifier = GlanceModifier
                                    .fillMaxWidth()
                                    .background(ColorProvider(android.graphics.Color.parseColor("#c41e3a")))
                                    .padding(horizontal = 8, vertical = 4)
                            ) {
                                Text(
                                    text = truncate(report.keywords, 60),
                                    style = TextStyle(
                                        color = ColorProvider(android.graphics.Color.WHITE),
                                    ),
                                    maxLines = 2
                                )
                            }
                            Spacer(modifier = GlanceModifier.height(8))
                        }

                        // Headlines
                        val items = if (report.headlines.isEmpty()) listOf("正在获取最新内容...") else report.headlines.take(4)
                        for ((index, item) in items.withIndex()) {
                            if (index > 0) Spacer(modifier = GlanceModifier.height(4))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "\u25b8",
                                    style = TextStyle(
                                        color = ColorProvider(android.graphics.Color.parseColor("#e85d04")),
                                        fontWeight = FontWeight.Bold,
                                    )
                                )
                                Spacer(modifier = GlanceModifier.width(6))
                                Text(
                                    text = truncate(item, 60),
                                    style = TextStyle(
                                        color = ColorProvider(android.graphics.Color.parseColor("#e8e0d0")),
                                    ),
                                    maxLines = 1
                                )
                            }
                        }

                        // Bottom bar
                        Spacer(modifier = GlanceModifier.height(8))
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
                                text = "\u70b9\u51fb\u9605\u8bfb\u2192",
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
