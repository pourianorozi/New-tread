package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BuyGreen
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeutralYellow
import com.example.ui.theme.OnDarkTextMuted
import com.example.ui.theme.OnDarkTextPrimary
import com.example.ui.theme.OnDarkTextSecondary
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SellRed

@Composable
fun EducationScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Academy Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.School, contentDescription = null, tint = PrimaryBlue)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "آکادمی آموزش تحلیل تکنیکال برای مبتدیان", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnDarkTextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "مفاهیم پایه اندیکاتورها و اصول معامله‌گری هوشمند به زبان ساده", fontSize = 12.sp, color = OnDarkTextSecondary)
                }
            }
        }

        // Lesson 1: RSI
        LessonCard(
            titleFa = "۱. شاخص قدرت نسبی (RSI) چیست؟",
            icon = Icons.Default.Lightbulb,
            accentColor = BuyGreen,
            contentFa = "اندیکاتور RSI قدرت خریداران و فروشندگان را بین عدد ۰ تا ۱۰۰ اندازه می‌گیرد.\n\n• عدد زیر ۳۰: نشان‌دهنده اشباع فروش (Oversold) و احتمال بالا رفتن قیمت است.\n• عدد بالای ۷۰: نشان‌دهنده اشباع خرید (Overbought) و احتمال افت قیمت است.\n• عدد ۵۰: مرز خنثی بودن روند را نشان می‌دهد."
        )

        // Lesson 2: MACD
        LessonCard(
            titleFa = "۲. اندیکاتور مک‌دی (MACD)",
            icon = Icons.Default.Book,
            accentColor = PrimaryBlue,
            contentFa = "مک‌دی از تقاطع دو میانگین متحرک ساخته می‌شود و شتاب حرکت قیمت را نشان می‌دهد.\n\n• تقاطع صعودی: وقتی خط مک‌دی از زیر خط سیگنال به سمت بالا حرکت می‌کند (سیگنال خرید).\n• تقاطع نزولی: وقتی خط مک‌دی از بالای خط سیگنال به سمت پایین عبور می‌کند (سیگنال فروش)."
        )

        // Lesson 3: Moving Averages
        LessonCard(
            titleFa = "۳. میانگین‌های متحرک (EMA 20, EMA 50, SMA 200)",
            icon = Icons.Default.Lightbulb,
            accentColor = NeutralYellow,
            contentFa = "میانگین متحرک نوسانات جزئی قیمت را هموار کرده و جهت اصلی بازار را شفاف می‌سازد.\n\n• اگر EMA 20 بالای EMA 50 باشد، روند کوتاه مدت صعودی است.\n• قیمت بالای SMA 200 نشان‌دهنده صعودی بودن روند کلان و بلندمدت است."
        )

        // Lesson 4: Risk Management & Stop Loss
        LessonCard(
            titleFa = "۴. مدیریت ریسک، حد ضرر و حد سود",
            icon = Icons.Default.Shield,
            accentColor = SellRed,
            contentFa = "مهم‌ترین عامل موفقیت در بازارهای مالی، کنترل حجم معامله و حد ضرر است.\n\n• حد ضرر (Stop-Loss): قیمتی که اگر بازار بر خلاف تحلیل شما حرکت کرد، معامله با زیان کوچک بسته شود.\n• نسبت ریسک به بهاداش (R/R): همواره سعی کنید حد سود شما حداقل ۲ برابر حد ضرر باشد (نسبت ۱ به ۲)."
        )
    }
}

@Composable
private fun LessonCard(
    titleFa: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: androidx.compose.ui.graphics.Color,
    contentFa: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = titleFa, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnDarkTextPrimary)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = contentFa,
                fontSize = 12.sp,
                color = OnDarkTextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}
