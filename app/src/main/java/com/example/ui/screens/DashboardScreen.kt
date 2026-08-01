package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.SignalType
import com.example.ui.components.SignalBadge
import com.example.ui.components.SignalStrengthMeter
import com.example.ui.components.TimeframeMatrixRow
import com.example.ui.theme.BuyGreen
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeutralYellow
import com.example.ui.theme.OnDarkTextMuted
import com.example.ui.theme.OnDarkTextPrimary
import com.example.ui.theme.OnDarkTextSecondary
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SellRed
import com.example.ui.viewmodel.TradingViewModel

@Composable
fun DashboardScreen(
    viewModel: TradingViewModel,
    onNavigateToChart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedAsset by viewModel.selectedAsset.collectAsState()
    val selectedTimeframe by viewModel.selectedTimeframe.collectAsState()
    val analysis by viewModel.currentAnalysis.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Asset Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedAsset.nameFa,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnDarkTextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${selectedAsset.symbol})",
                            fontSize = 14.sp,
                            color = OnDarkTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "قیمت لحظه‌ای: $${"%.2f".format(selectedAsset.currentPrice)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnDarkTextPrimary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        val isPositive = selectedAsset.change24h >= 0
                        Text(
                            text = "${if (isPositive) "+" else ""}${"%.2f".format(selectedAsset.change24h)}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPositive) BuyGreen else SellRed
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.toggleFavorite(selectedAsset) }
                ) {
                    Icon(
                        imageVector = if (selectedAsset.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (selectedAsset.isFavorite) PrimaryBlue else OnDarkTextMuted
                    )
                }
            }
        }

        // Beginner Quick Summary Card (Core Requirement)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "خلاصه وضعیت تحلیل تکنیکال (ویژه مبتدیان)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnDarkTextPrimary
                        )
                    }
                    SignalBadge(signalType = analysis.overallSignal, large = true)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "💡 پیشنهاد معامله هوشمند:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = analysis.recommendedActionFa,
                            fontSize = 13.sp,
                            color = OnDarkTextPrimary,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stop Loss & Take Profit Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Stop Loss
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SellRed.copy(alpha = 0.1f))
                            .border(1.dp, SellRed.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "حد ضرر (Stop-Loss)", fontSize = 11.sp, color = SellRed)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$${"%.2f".format(analysis.stopLoss)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SellRed
                            )
                        }
                    }

                    // Take Profit 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BuyGreen.copy(alpha = 0.1f))
                            .border(1.dp, BuyGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "حد سود ۱ (Take-Profit)", fontSize = 11.sp, color = BuyGreen)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$${"%.2f".format(analysis.takeProfit1)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BuyGreen
                            )
                        }
                    }

                    // Take Profit 2
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BuyGreen.copy(alpha = 0.1f))
                            .border(1.dp, BuyGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "حد سود ۲ (هدف اصلی)", fontSize = 11.sp, color = BuyGreen)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$${"%.2f".format(analysis.takeProfit2)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BuyGreen
                            )
                        }
                    }
                }
            }
        }

        // Signal Strength Meter
        SignalStrengthMeter(
            score = analysis.strengthScore,
            signalType = analysis.overallSignal
        )

        // Timeframe Matrix
        TimeframeMatrixRow(
            selectedTimeframe = selectedTimeframe,
            matrix = analysis.timeframeMatrix,
            onSelectTimeframe = { viewModel.selectTimeframe(it) }
        )

        // Detailed Technical Indicators Table
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "جزئیات اندیکاتورهای فنی و تاییدیه صعود/نزول",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnDarkTextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                analysis.indicators.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.5f)) {
                            Text(
                                text = item.nameFa,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnDarkTextPrimary
                            )
                            Text(
                                text = item.explanationFa,
                                fontSize = 11.sp,
                                color = OnDarkTextMuted
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = item.valueString,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnDarkTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            SignalBadge(signalType = item.signal, large = false)
                        }
                    }
                    if (index < analysis.indicators.size - 1) {
                        Divider(color = DarkCardBorder, thickness = 0.5.dp)
                    }
                }
            }
        }

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.saveCurrentSignal() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                Text(text = "ذخیره سیگنال", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.openPaperTrade("BUY", 1000.0) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = BuyGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                Text(text = "خرید مجازی (Paper)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            OutlinedButton(
                onClick = onNavigateToChart,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "مشاهده نمودار", fontSize = 12.sp, color = PrimaryBlue)
            }
        }

        // Disclaimer Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(NeutralYellow.copy(alpha = 0.1f))
                .border(1.dp, NeutralYellow.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = NeutralYellow)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "هشدار مسئولیت مالی: تمامی سیگنال‌ها بر اساس محاسبات الگوریتمی تولید شده‌اند و جنبه آموزشی دارند. مسئولیت سود یا زیان معامله بر عهده کاربر است.",
                    fontSize = 11.sp,
                    color = OnDarkTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
