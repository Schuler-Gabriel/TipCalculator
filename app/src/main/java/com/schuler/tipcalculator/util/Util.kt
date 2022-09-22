package com.schuler.tipcalculator.util

fun calculateTotalTip(
    totalBill: Double,
    tipPercentage: Float
): Double {

    return  if (totalBill.toString().isNotEmpty() && totalBill > 1)
        totalBill * tipPercentage
    else 0.0
}


fun calculateTotalPerPerson(
    totalBill: Double,
    tipPercentage: Float,
    splitBy: Int
): Double {
    val bill = calculateTotalTip(
        totalBill = totalBill,
        tipPercentage = tipPercentage
    ) + totalBill

    return (bill / splitBy)
}