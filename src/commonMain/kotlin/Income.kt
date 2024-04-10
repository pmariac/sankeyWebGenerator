package io.data2viz.demo

data class IncomeFlow(
	val fromIndex: Int,
	val toIndex: Int,
	val amount: Double
)

data class Income(
	val index: Int,
	val name: String,
	val flows: List<IncomeFlow>
)

fun String.clean() = this.trimStart().trimEnd()

data class StringIncome(val from:String, val to: String, val amount:Double)
