package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.SignalType
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.OnDarkTextSecondary

@Composable
fun SignalStrengthMeter(
    score: Int, // 1 to 10
    signalType: SignalType,
    modifier: Modifier = Modifier
) {
    val progress = score / 10f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "قدرت سیگنال و قدرت همگرایی:",
                style = MaterialTheme.typography.bodyMedium,
                color = OnDarkTextSecondary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$score / ۱۰",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = signalType.color
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when {
                        score >= 8 -> "(بسیار قوی)"
                        score >= 6 -> "(قوی)"
                        score >= 4 -> "(متوسط)"
                        else -> "(ضعیف)"
                    },
                    fontSize = 12.sp,
                    color = OnDarkTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFF0F172A))
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = signalType.color,
                trackColor = Color.Transparent,
            )
        }
    }
}
