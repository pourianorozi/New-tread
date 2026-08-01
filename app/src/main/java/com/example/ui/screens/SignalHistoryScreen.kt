package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.PaperTrade
import com.example.domain.model.SignalType
import com.example.ui.components.SignalBadge
import com.example.ui.theme.BuyGreen
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.OnDarkTextMuted
import com.example.ui.theme.OnDarkTextPrimary
import com.example.ui.theme.OnDarkTextSecondary
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SellRed
import com.example.ui.viewmodel.TradingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SignalHistoryScreen(
    viewModel: TradingViewModel,
    modifier: Modifier = Modifier
) {
    val signalHistory by viewModel.signalHistory.collectAsState()
    val paperTrades by viewModel.paperTrades.collectAsState()
    val paperBalance by viewModel.paperBalance.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("تاریخچه سیگنال‌ها", "سبد معاملات مجازی (Paper)")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = DarkSurface,
            contentColor = PrimaryBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = PrimaryBlue
                )
            },
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) PrimaryBlue else OnDarkTextSecondary
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTabIndex == 0) {
            // Signal History Tab
            if (signalHistory.isEmpty()) {
                EmptyStateView("هنوز سیگنالی ثبت نشده است.", "از صفحه اصلی می‌توانید سیگنال‌های تحلیلی را ذخیره کنید.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(signalHistory, key = { it.id }) { log ->
                        val signalType = try {
                            SignalType.valueOf(log.signalType)
                        } catch (e: Exception) {
                            SignalType.NEUTRAL
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = log.symbolNameFa, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnDarkTextPrimary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "(${log.symbol})", fontSize = 12.sp, color = OnDarkTextMuted)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = log.timeframe,
                                            fontSize = 11.sp,
                                            color = PrimaryBlue,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(PrimaryBlue.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    SignalBadge(signalType = signalType, large = false)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "قیمت ورود: $${"%.2f".format(log.entryPrice)}", fontSize = 12.sp, color = OnDarkTextSecondary)
                                    Text(text = "حد ضرر: $${"%.2f".format(log.stopLoss)}", fontSize = 12.sp, color = SellRed)
                                    Text(text = "هدف اول: $${"%.2f".format(log.takeProfit1)}", fontSize = 12.sp, color = BuyGreen)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = formatDate(log.timestamp),
                                    fontSize = 10.sp,
                                    color = OnDarkTextMuted
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Paper Trading Tab
            Column(modifier = Modifier.fillMaxSize()) {
                // Portfolio Summary Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(14.dp)
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
                                Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = BuyGreen)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "موجودی حساب مجازی (Paper)", fontSize = 13.sp, color = OnDarkTextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$${"%.2f".format(paperBalance)} USDT",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BuyGreen
                            )
                        }

                        val openCount = paperTrades.count { it.isOpen }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "پوزیشن‌های فعال:", fontSize = 12.sp, color = OnDarkTextMuted)
                            Text(text = "$openCount معامله", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnDarkTextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (paperTrades.isEmpty()) {
                    EmptyStateView("معامله مجازی فعال یا بسته‌شده‌ای ندارید.", "از صفحه نمودار یا تحلیل می‌توانید معاملات مجازی بدون ریسک باز کنید.")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(paperTrades, key = { it.id }) { trade ->
                            PaperTradeItemCard(
                                trade = trade,
                                onCloseTrade = { viewModel.closePaperTrade(trade) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaperTradeItemCard(
    trade: PaperTrade,
    onCloseTrade: () -> Unit
) {
    val isBuy = trade.tradeType == "BUY" || trade.tradeType == "خرید"
    val isProfit = trade.pnlUsdt >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isBuy) "خرید (LONG)" else "فروش (SHORT)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isBuy) BuyGreen else SellRed,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isBuy) BuyGreen.copy(alpha = 0.15f) else SellRed.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${trade.symbolNameFa} (${trade.symbol})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnDarkTextPrimary)
                }

                if (trade.isOpen) {
                    Button(
                        onClick = onCloseTrade,
                        colors = ButtonDefaults.buttonColors(containerColor = SellRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.padding(end = 2.dp))
                        Text(text = "بستن معامله", fontSize = 11.sp, color = Color.White)
                    }
                } else {
                    Text(
                        text = "${if (isProfit) "+" else ""}${"%.2f".format(trade.pnlUsdt)} USDT (${"%.2f".format(trade.pnlPercent)}%)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isProfit) BuyGreen else SellRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "حجم: $${"%.0f".format(trade.amountUsdt)}", fontSize = 12.sp, color = OnDarkTextSecondary)
                Text(text = "قیمت ورود: $${"%.2f".format(trade.entryPrice)}", fontSize = 12.sp, color = OnDarkTextSecondary)
                Text(text = "حد سود: $${"%.2f".format(trade.takeProfit)}", fontSize = 12.sp, color = BuyGreen)
            }
        }
    }
}

@Composable
private fun EmptyStateView(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.History, contentDescription = null, tint = OnDarkTextMuted)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnDarkTextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 12.sp, color = OnDarkTextMuted, lineHeight = 18.sp)
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
