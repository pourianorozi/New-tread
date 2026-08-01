package com.example.domain.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BuyGreen
import com.example.ui.theme.BuyGreenContainer
import com.example.ui.theme.NeutralYellow
import com.example.ui.theme.NeutralYellowContainer
import com.example.ui.theme.SellRed
import com.example.ui.theme.SellRedContainer

enum class SignalType(
    val labelFa: String,
    val descriptionFa: String,
    val color: Color,
    val containerColor: Color
) {
    STRONG_BUY(
        labelFa = "خرید قوی",
        descriptionFa = "پیشنهاد خرید کامل - همگرایی قوی اندیکاتورها",
        color = BuyGreen,
        containerColor = BuyGreenContainer
    ),
    BUY(
        labelFa = "خرید",
        descriptionFa = "سیگنال صعودی - ورود با حد ضرر مشخص",
        color = BuyGreen,
        containerColor = BuyGreenContainer
    ),
    NEUTRAL(
        labelFa = "خنثی / نگهداری",
        descriptionFa = "بازار بدون روند - انتظار برای تاییدیه بیشتر",
        color = NeutralYellow,
        containerColor = NeutralYellowContainer
    ),
    SELL(
        labelFa = "فروش",
        descriptionFa = "سیگنال نزولی - خروج یا پوزیشن شورت",
        color = SellRed,
        containerColor = SellRedContainer
    ),
    STRONG_SELL(
        labelFa = "فروش قوی",
        descriptionFa = "پیشنهاد خروج فوری - فشار بالای فروش",
        color = SellRed,
        containerColor = SellRedContainer
    )
}
