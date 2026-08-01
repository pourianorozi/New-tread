package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.domain.model.Asset
import com.example.domain.model.TimeFrame
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TradingViewChartWebView(
    asset: Asset,
    timeframe: TimeFrame,
    modifier: Modifier = Modifier
) {
    val intervalCode = when (timeframe) {
        TimeFrame.M1 -> "1"
        TimeFrame.M5 -> "5"
        TimeFrame.M15 -> "15"
        TimeFrame.H1 -> "60"
        TimeFrame.H4 -> "240"
        TimeFrame.D1 -> "D"
        TimeFrame.W1 -> "W"
    }

    val htmlData = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <style>
                body, html {
                    margin: 0;
                    padding: 0;
                    width: 100%;
                    height: 100%;
                    background-color: #0B0E14;
                    overflow: hidden;
                }
                #tradingview_widget {
                    width: 100%;
                    height: 100%;
                }
            </style>
            <script type="text/javascript" src="https://s3.tradingview.com/tv.js"></script>
        </head>
        <body>
            <div id="tradingview_widget"></div>
            <script type="text/javascript">
                new TradingView.widget({
                    "autosize": true,
                    "symbol": "${asset.tvSymbol}",
                    "interval": "$intervalCode",
                    "timezone": "Asia/Tehran",
                    "theme": "dark",
                    "style": "1",
                    "locale": "fa_IR",
                    "toolbar_bg": "#0B0E14",
                    "enable_publishing": false,
                    "hide_side_toolbar": false,
                    "allow_symbol_change": false,
                    "container_id": "tradingview_widget",
                    "studies": [
                        "RSI@tv-basicstudies",
                        "MACD@tv-basicstudies",
                        "MASimple@tv-basicstudies"
                    ],
                    "overrides": {
                        "mainSeriesProperties.candleStyle.upColor": "#00E676",
                        "mainSeriesProperties.candleStyle.downColor": "#FF3355",
                        "mainSeriesProperties.candleStyle.drawWick": true,
                        "mainSeriesProperties.candleStyle.drawBorder": true,
                        "mainSeriesProperties.candleStyle.borderColor": "#378658",
                        "mainSeriesProperties.candleStyle.borderUpColor": "#00E676",
                        "mainSeriesProperties.candleStyle.borderDownColor": "#FF3355"
                    }
                });
            </script>
        </body>
        </html>
    """.trimIndent()

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
            .background(DarkSurface)
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }
                    loadDataWithBaseURL("https://s3.tradingview.com", htmlData, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL("https://s3.tradingview.com", htmlData, "text/html", "UTF-8", null)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
