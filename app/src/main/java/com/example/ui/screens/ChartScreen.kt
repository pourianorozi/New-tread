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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.TimeFrame
import com.example.ui.components.SignalBadge
import com.example.ui.components.TradingViewChartWebView
import com.example.ui.theme.BuyGreen
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.OnDarkTextMuted
import com.example.ui.theme.OnDarkTextPrimary
import com.example.ui.theme.OnDarkTextSecondary
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SellRed
import com.example.ui.viewmodel.TradingViewModel

@Composable
fun ChartScreen(
    viewModel: TradingViewModel,
    modifier: Modifier = Modifier
) {
    val allAssets by viewModel.allAssets.collectAsState()
    val selectedAsset by viewModel.selectedAsset.collectAsState()
    val selectedTimeframe by viewModel.selectedTimeframe.collectAsState()
    val analysis by viewModel.currentAnalysis.collectAsState()

    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {
        // Asset Picker & Price bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
                        .clickable { dropdownExpanded = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedAsset.nameFa} (${selectedAsset.symbol})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnDarkTextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = PrimaryBlue)
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.background(DarkSurface)
                ) {
                    allAssets.forEach { asset ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "${asset.nameFa} (${asset.symbol})", color = OnDarkTextPrimary)
                                    Text(text = "$${asset.currentPrice}", color = OnDarkTextSecondary)
                                }
                            },
                            onClick = {
                                viewModel.selectAsset(asset)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Price & Change Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$${"%.2f".format(selectedAsset.currentPrice)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnDarkTextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                val isPositive = selectedAsset.change24h >= 0
                Text(
                    text = "${if (isPositive) "+" else ""}${"%.2f".format(selectedAsset.change24h)}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) BuyGreen else SellRed
                )
            }
        }

        // Timeframe selector bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            items(TimeFrame.entries) { tf ->
                val isSelected = tf == selectedTimeframe
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) PrimaryBlue else DarkSurface)
                        .clickable { viewModel.selectTimeframe(tf) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = tf.labelFa,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else OnDarkTextSecondary
                    )
                }
            }
        }

        // Interactive TradingView Chart WebView
        TradingViewChartWebView(
            asset = selectedAsset,
            timeframe = selectedTimeframe,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Signal Overlay Summary bar below chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "سیگنال هوشمند تایم‌فریم ${selectedTimeframe.labelFa}:", fontSize = 11.sp, color = OnDarkTextMuted)
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SignalBadge(signalType = analysis.overallSignal, large = true)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "حد سود: $${"%.2f".format(analysis.takeProfit1)} | حد ضرر: $${"%.2f".format(analysis.stopLoss)}",
                        fontSize = 11.sp,
                        color = OnDarkTextSecondary
                    )
                }
            }

            // Paper Trade buttons
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { viewModel.openPaperTrade("BUY", 1000.0) },
                    colors = ButtonDefaults.buttonColors(containerColor = BuyGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "خرید", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { viewModel.openPaperTrade("SELL", 1000.0) },
                    colors = ButtonDefaults.buttonColors(containerColor = SellRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "فروش", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
