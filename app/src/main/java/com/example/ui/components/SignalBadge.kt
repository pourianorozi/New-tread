package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.SignalType

@Composable
fun SignalBadge(
    signalType: SignalType,
    modifier: Modifier = Modifier,
    large: Boolean = false
) {
    val icon = when (signalType) {
        SignalType.STRONG_BUY, SignalType.BUY -> Icons.Default.ArrowUpward
        SignalType.NEUTRAL -> Icons.Default.Remove
        SignalType.SELL, SignalType.STRONG_SELL -> Icons.Default.ArrowDownward
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(signalType.containerColor)
            .padding(
                horizontal = if (large) 12.dp else 8.dp,
                vertical = if (large) 6.dp else 4.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = signalType.color,
                modifier = Modifier.size(if (large) 18.dp else 14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = signalType.labelFa,
                color = signalType.color,
                fontSize = if (large) 14.sp else 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
