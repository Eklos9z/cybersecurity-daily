package com.cybersecdaily.widget

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CyberSecDailyTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val baseUrl = "https://unclecheng-li.github.io/cybersecurity-daily"
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1a1a1a))
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Title
        Text(
            text = "网络安全日报速递",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFc9a227),
            letterSpacing = 4.sp
        )

        Text(
            text = "CYBERSECURITY DAILY EXPRESS",
            fontSize = 12.sp,
            color = Color(0xFF888888),
            letterSpacing = 6.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2d2d2d))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "关于",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFc9a227)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "每日精选全球网络安全动态，覆盖 CVE 漏洞、安全事件、威胁情报与执法行动。",
                    color = Color(0xFFe8e0d0),
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "内容由 AI 自动化工作流生成，人工校验。每条内容附带原始出处链接，方便进一步追踪。",
                    color = Color(0xFFa0a0a0),
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // How to use card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2d2d2d))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "如何使用小组件",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFc9a227)
                )
                Spacer(modifier = Modifier.height(12.dp))
                UsageStep("1", "长按桌面空白区域，选择「小组件」")
                UsageStep("2", "在列表中搜索「网络安全日报」")
                UsageStep("3", "拖拽到桌面，每日自动更新最新安全要闻")
                UsageStep("4", "点击小组件可直接跳转阅读完整日报")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl))
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFc9a227))
            ) {
                Text("打开日报首页", color = Color(0xFF1a1a1a))
            }
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Unclecheng-li/cybersecurity-daily"))
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFc9a227))
            ) {
                Text("GitHub")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "AI基 · 网络安全日报",
            color = Color(0xFF666666),
            fontSize = 12.sp
        )
        Text(
            text = "v1.0.0",
            color = Color(0xFF555555),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun UsageStep(number: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = number,
            color = Color(0xFFe85d04),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.width(24.dp)
        )
        Text(
            text = text,
            color = Color(0xFFe8e0d0),
            lineHeight = 22.sp
        )
    }
}

@Composable
fun CyberSecDailyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFc9a227),
            secondary = Color(0xFFc41e3a),
            background = Color(0xFF1a1a1a),
            surface = Color(0xFF2d2d2d),
            onPrimary = Color(0xFF1a1a1a),
            onBackground = Color(0xFFe8e0d0),
            onSurface = Color(0xFFe8e0d0)
        ),
        content = content
    )
}
