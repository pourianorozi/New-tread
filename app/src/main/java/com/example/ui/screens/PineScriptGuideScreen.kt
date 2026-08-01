package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BuyGreen
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.OnDarkTextMuted
import com.example.ui.theme.OnDarkTextPrimary
import com.example.ui.theme.OnDarkTextSecondary
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.TradingViewModel

@Composable
fun PineScriptGuideScreen(
    viewModel: TradingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var testSymbol by remember { mutableStateOf("BTCUSDT") }
    var testSignal by remember { mutableStateOf("BUY") }
    var testMessage by remember { mutableStateOf("تقاطع صعودی مک‌دی و RSI بالای ۳۰") }

    val macdRsiScript = """
        //@version=5
        indicator("Smart Trading Signal - MACD & RSI", overlay=true)

        // Indicators Calculation
        fastMA = ta.ema(close, 12)
        slowMA = ta.ema(close, 26)
        macd = fastMA - slowMA
        signalLine = ta.ema(macd, 9)
        rsiValue = ta.rsi(close, 14)

        // Signal Logic
        buyCondition = ta.crossover(macd, signalLine) and rsiValue > 30
        sellCondition = ta.crossunder(macd, signalLine) and rsiValue < 70

        // Visual Shapes on TradingView Chart
        plotshape(buyCondition, title="BUY Alert", style=shape.labelup, location=location.belowbar, color=color.green, text="BUY")
        plotshape(sellCondition, title="SELL Alert", style=shape.labeldown, location=location.abovebar, color=color.red, text="SELL")

        // Mobile Alert Trigger Setup
        alertcondition(buyCondition, title="BUY Signal Alert", message="Strong BUY Signal - Entry Level Met")
        alertcondition(sellCondition, title="SELL Signal Alert", message="Strong SELL Signal - Exit Level Met")
    """.trimIndent()

    val emaCrossoverScript = """
        //@version=5
        indicator("Smart Trading - EMA 20/50 Crossover", overlay=true)

        ema20 = ta.ema(close, 20)
        ema50 = ta.ema(close, 50)

        goldenCross = ta.crossover(ema20, ema50)
        deathCross = ta.crossunder(ema20, ema50)

        alertcondition(goldenCross, title="تقاطع طلایی (خرید)", message="تقاطع صعودی EMA20 از بالای EMA50")
        alertcondition(deathCross, title="تقاطع مرگ (فروش)", message="تقاطع نزولی EMA20 از زیر EMA50")
    """.trimIndent()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step By Step Setup Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = PrimaryBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "راهنمای اتصال هشدارهای تریدینگ‌ویو دسکتاپ به برنامه",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnDarkTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val steps = listOf(
                    "۱. نرم‌افزار یا وب‌سایت TradingView را روی کامپیوتر (Desktop) باز کنید.",
                    "۲. به بخش Pine Editor در پایین صفحه رفته و کد کپی‌شده زیر را جای‌گذاری کنید.",
                    "۳. دکمه Add to chart را بزنید تا اسکریپت روی نمودار فعال شود.",
                    "۴. روی علامت زنگوله (Alert) کلیک کرده و گزینه alertcondition را انتخاب کنید.",
                    "۵. آدرس وب‌هوک و اعلان‌های Push به اپلیکیشن موبایل را فعال کنید تا سیگنال‌ها به صورت خودکار به گوشی شما ارسال شوند."
                )

                steps.forEach { step ->
                    Text(
                        text = step,
                        fontSize = 12.sp,
                        color = OnDarkTextSecondary,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
            }
        }

        // Webhook Simulator Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = BuyGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تست ارسال هشدار وب‌هوک (ویژه تست اعلان‌ها)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnDarkTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = testSymbol,
                        onValueChange = { testSymbol = it },
                        label = { Text("نماد (نمونه BTCUSDT)", fontSize = 11.sp, color = OnDarkTextMuted) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = OnDarkTextPrimary,
                            unfocusedTextColor = OnDarkTextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = testSignal,
                        onValueChange = { testSignal = it },
                        label = { Text("نوع سیگنال (BUY/SELL)", fontSize = 11.sp, color = OnDarkTextMuted) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = OnDarkTextPrimary,
                            unfocusedTextColor = OnDarkTextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = testMessage,
                    onValueChange = { testMessage = it },
                    label = { Text("متن پیام هشدار", fontSize = 11.sp, color = OnDarkTextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = OnDarkTextPrimary,
                        unfocusedTextColor = OnDarkTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.simulateWebhookAlert(testSymbol, testSignal, testMessage)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BuyGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "ارسال هشدار آزمایشی وب‌هوک به برنامه", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }

        // PineScript Code Template 1
        PineScriptSnippetCard(
            titleFa = "قالب پاین اسکریپت استراتژی MACD + RSI (نسخه ۵)",
            code = macdRsiScript,
            onCopy = { copyToClipboard(context, macdRsiScript) }
        )

        // PineScript Code Template 2
        PineScriptSnippetCard(
            titleFa = "قالب تقاطع میانگین متحرک EMA 20/50",
            code = emaCrossoverScript,
            onCopy = { copyToClipboard(context, emaCrossoverScript) }
        )
    }
}

@Composable
private fun PineScriptSnippetCard(
    titleFa: String,
    code: String,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = PrimaryBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = titleFa, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnDarkTextPrimary)
                }
                Button(
                    onClick = onCopy,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.padding(end = 4.dp))
                    Text(text = "کپی کد", fontSize = 11.sp, color = PrimaryBlue)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF07090E))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFF64B5F6),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("PineScript", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "کد پاین اسکریپت با موفقیت کپی شد", Toast.LENGTH_SHORT).show()
}
