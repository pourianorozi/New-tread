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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.TechnicalAnalysisEngine
import com.example.domain.model.AssetCategory
import com.example.domain.model.TimeFrame
import com.example.ui.components.SignalBadge
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
fun WatchlistScreen(
    viewModel: TradingViewModel,
    onSelectAsset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredAssets by viewModel.filteredAssets.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedTimeframe by viewModel.selectedTimeframe.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("جستجوی بیت‌کوین، طلا، سهام...", color = OnDarkTextMuted, fontSize = 13.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = PrimaryBlue) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = DarkCardBorder,
                focusedTextColor = OnDarkTextPrimary,
                unfocusedTextColor = OnDarkTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category filter chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                CategoryChip(
                    title = "همه نمادها",
                    isSelected = selectedCategory == null,
                    onClick = { viewModel.selectCategory(null) }
                )
            }
            items(AssetCategory.entries) { cat ->
                CategoryChip(
                    title = cat.displayNameFa,
                    isSelected = selectedCategory == cat,
                    onClick = { viewModel.selectCategory(cat) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Asset List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredAssets, key = { it.symbol }) { asset ->
                val analysis = TechnicalAnalysisEngine.analyze(asset, selectedTimeframe)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectAsset(asset)
                            onSelectAsset()
                        },
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.toggleFavorite(asset) }) {
                                Icon(
                                    imageVector = if (asset.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = null,
                                    tint = if (asset.isFavorite) PrimaryBlue else OnDarkTextMuted
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = asset.nameFa,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnDarkTextPrimary
                                )
                                Text(
                                    text = asset.symbol,
                                    fontSize = 12.sp,
                                    color = OnDarkTextMuted
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$${"%.2f".format(asset.currentPrice)}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnDarkTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val isPositive = asset.change24h >= 0
                                Text(
                                    text = "${if (isPositive) "+" else ""}${"%.2f".format(asset.change24h)}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPositive) BuyGreen else SellRed
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            SignalBadge(signalType = analysis.overallSignal, large = false)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) PrimaryBlue else DarkSurface)
            .border(1.dp, if (isSelected) PrimaryBlue else DarkCardBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) OnDarkTextPrimary else OnDarkTextSecondary
        )
    }
}
