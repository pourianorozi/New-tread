package com.example.domain.model

data class TechnicalIndicatorResult(
    val name: String,
    val nameFa: String,
    val valueString: String,
    val signal: SignalType,
    val explanationFa: String
)

data class ConfluenceAnalysis(
    val symbol: String,
    val timeFrame: TimeFrame,
    val currentPrice: Double,
    val overallSignal: SignalType,
    val strengthScore: Int, // 1 - 10
    val recommendedActionFa: String,
    val entryPrice: Double,
    val stopLoss: Double,
    val takeProfit1: Double,
    val takeProfit2: Double,
    val riskRewardRatio: String,
    val indicators: List<TechnicalIndicatorResult>,
    val supportLevel: Double,
    val resistanceLevel: Double,
    val timeframeMatrix: Map<TimeFrame, SignalType>
)
