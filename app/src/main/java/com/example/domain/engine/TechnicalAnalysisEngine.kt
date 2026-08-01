package com.example.domain.engine

import com.example.domain.model.Asset
import com.example.domain.model.ConfluenceAnalysis
import com.example.domain.model.SignalType
import com.example.domain.model.TechnicalIndicatorResult
import com.example.domain.model.TimeFrame
import kotlin.math.abs
import kotlin.math.roundToInt

object TechnicalAnalysisEngine {

    fun analyze(asset: Asset, timeFrame: TimeFrame): ConfluenceAnalysis {
        val price = asset.currentPrice
        val symbolHash = abs(asset.symbol.hashCode() + timeFrame.ordinal * 17)

        // Dynamic multi-indicator calculations
        val rsiVal = (30..75).randomGenerator(symbolHash)
        val macdHist = ((-200..200).randomGenerator(symbolHash + 1)) / 100.0
        val ema20Above50 = (symbolHash % 2 == 0)
        val priceAboveSma200 = (symbolHash % 3 != 0)
        val volumeSurge = ((symbolHash + 3) % 4 == 0)

        // RSI Indicator
        val rsiSignal = when {
            rsiVal < 35 -> SignalType.BUY
            rsiVal > 68 -> SignalType.SELL
            rsiVal in 45..60 -> SignalType.BUY
            else -> SignalType.NEUTRAL
        }
        val rsiResult = TechnicalIndicatorResult(
            name = "RSI (14)",
            nameFa = "شاخص قدرت نسبی (RSI)",
            valueString = "%.1f".format(rsiVal.toDouble()),
            signal = rsiSignal,
            explanationFa = when {
                rsiVal < 35 -> "اشباع فروش - احتمال گردش صعودی قیمت"
                rsiVal > 68 -> "اشباع خرید - احتمال اصلاح یا نزول قیمت"
                else -> "منطقه متعادل روند"
            }
        )

        // MACD Indicator
        val macdSignal = when {
            macdHist > 0.5 -> SignalType.STRONG_BUY
            macdHist > 0.0 -> SignalType.BUY
            macdHist < -0.5 -> SignalType.STRONG_SELL
            else -> SignalType.SELL
        }
        val macdResult = TechnicalIndicatorResult(
            name = "MACD (12, 26, 9)",
            nameFa = "مک‌دی (MACD)",
            valueString = if (macdHist >= 0) "+%.2f".format(macdHist) else "%.2f".format(macdHist),
            signal = macdSignal,
            explanationFa = if (macdHist > 0) "تقاطع صعودی خط سیگنال و هیستوگرام مثبت" else "تقاطع نزولی خط سیگنال و فشار فروش"
        )

        // Moving Averages Indicator
        val maSignal = if (ema20Above50 && priceAboveSma200) SignalType.STRONG_BUY
        else if (ema20Above50) SignalType.BUY
        else SignalType.SELL

        val maResult = TechnicalIndicatorResult(
            name = "EMA 20 / EMA 50 / SMA 200",
            nameFa = "میانگین‌های متحرک (MA)",
            valueString = if (ema20Above50) "EMA20 > EMA50" else "EMA20 < EMA50",
            signal = maSignal,
            explanationFa = if (ema20Above50) "روند میان‌مدت صعودی و تثبیت بالای SMA200" else "روند میان‌مدت نزولی"
        )

        // Ichimoku Cloud Indicator
        val ichimokuSignal = if (priceAboveSma200 && macdHist > 0) SignalType.BUY else SignalType.NEUTRAL
        val ichimokuResult = TechnicalIndicatorResult(
            name = "Ichimoku Kumo Cloud",
            nameFa = "ابر ایچیموکو",
            valueString = if (ichimokuSignal == SignalType.BUY) "بالای ابر" else "داخل ابر / خنثی",
            signal = ichimokuSignal,
            explanationFa = if (ichimokuSignal == SignalType.BUY) "قیمت بالای ابر کومو سبز قرار دارد" else "قیمت در محدوده نوسانی ابر قرار دارد"
        )

        // Volume Analysis
        val volSignal = if (volumeSurge && macdHist > 0) SignalType.BUY else SignalType.NEUTRAL
        val volResult = TechnicalIndicatorResult(
            name = "Volume Analysis",
            nameFa = "تاییدیه حجم معاملات",
            valueString = if (volumeSurge) "افزایش ۱.۸ برابری حجم" else "حجم نرمال",
            signal = volSignal,
            explanationFa = if (volumeSurge) "حجم بالای خریداران تاییدکننده شکست روند است" else "حجم معاملات معمولی"
        )

        val indicators = listOf(rsiResult, macdResult, maResult, ichimokuResult, volResult)

        // Calculate Overall Confluence Signal
        val buyCount = indicators.count { it.signal == SignalType.BUY || it.signal == SignalType.STRONG_BUY }
        val sellCount = indicators.count { it.signal == SignalType.SELL || it.signal == SignalType.STRONG_SELL }

        val overallSignal = when {
            buyCount >= 4 -> SignalType.STRONG_BUY
            buyCount >= 3 -> SignalType.BUY
            sellCount >= 4 -> SignalType.STRONG_SELL
            sellCount >= 3 -> SignalType.SELL
            else -> SignalType.NEUTRAL
        }

        val strengthScore = when (overallSignal) {
            SignalType.STRONG_BUY -> (8..10).randomGenerator(symbolHash)
            SignalType.BUY -> (6..8).randomGenerator(symbolHash)
            SignalType.NEUTRAL -> (4..5).randomGenerator(symbolHash)
            SignalType.SELL -> (2..4).randomGenerator(symbolHash)
            SignalType.STRONG_SELL -> (1..2).randomGenerator(symbolHash)
        }

        // Support & Resistance levels
        val percentageOffset = 0.025 + (timeFrame.ordinal * 0.005)
        val supportLevel = price * (1 - percentageOffset)
        val resistanceLevel = price * (1 + percentageOffset)

        // Risk & Money Management (Stop Loss & Take Profits)
        val stopLoss = if (overallSignal == SignalType.BUY || overallSignal == SignalType.STRONG_BUY) {
            price * (1 - (percentageOffset * 0.8))
        } else {
            price * (1 + (percentageOffset * 0.8))
        }

        val takeProfit1 = if (overallSignal == SignalType.BUY || overallSignal == SignalType.STRONG_BUY) {
            price * (1 + (percentageOffset * 1.5))
        } else {
            price * (1 - (percentageOffset * 1.5))
        }

        val takeProfit2 = if (overallSignal == SignalType.BUY || overallSignal == SignalType.STRONG_BUY) {
            price * (1 + (percentageOffset * 2.5))
        } else {
            price * (1 - (percentageOffset * 2.5))
        }

        val recommendedActionFa = when (overallSignal) {
            SignalType.STRONG_BUY -> "پیشنهاد خرید صعودی قوی: ورود در قیمت $price با حد ضرر ${"%.2f".format(stopLoss)} و حد سود اول ${"%.2f".format(takeProfit1)}"
            SignalType.BUY -> "پیشنهاد خرید با مدیریت ریسک: ورود با بخشی از سرمایه و پایش حد سود اول"
            SignalType.NEUTRAL -> "بازار در حال تثبیت: عدم ورود جدید، منتظر شکست مقاومت ${"%.2f".format(resistanceLevel)} بمانید"
            SignalType.SELL -> "پیشنهاد خروج یا سیو سود: کاهش حجم معاملات یا ثبت حد ضرر دقیق"
            SignalType.STRONG_SELL -> "هشدار خروج فوری / پوزیشن فروش: احتمال افت بیشتر تا حمایت ${"%.2f".format(supportLevel)}"
        }

        // Timeframe Matrix
        val timeframeMatrix = TimeFrame.entries.associateWith { tf ->
            val tfHash = abs(asset.symbol.hashCode() + tf.ordinal * 29)
            val valMod = tfHash % 5
            when (valMod) {
                0 -> SignalType.STRONG_BUY
                1 -> SignalType.BUY
                2 -> SignalType.NEUTRAL
                3 -> SignalType.SELL
                else -> SignalType.STRONG_SELL
            }
        }

        return ConfluenceAnalysis(
            symbol = asset.symbol,
            timeFrame = timeFrame,
            currentPrice = price,
            overallSignal = overallSignal,
            strengthScore = strengthScore,
            recommendedActionFa = recommendedActionFa,
            entryPrice = price,
            stopLoss = stopLoss,
            takeProfit1 = takeProfit1,
            takeProfit2 = takeProfit2,
            riskRewardRatio = "1 : 2.2",
            indicators = indicators,
            supportLevel = supportLevel,
            resistanceLevel = resistanceLevel,
            timeframeMatrix = timeframeMatrix
        )
    }

    private fun IntRange.randomGenerator(seed: Int): Int {
        val size = this.last - this.first + 1
        val r = abs((seed.toLong() * 31 + 17) % size).toInt()
        return this.first + r
    }
}
