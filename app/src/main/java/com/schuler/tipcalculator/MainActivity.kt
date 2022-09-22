package com.schuler.tipcalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.schuler.tipcalculator.components.InputField
import com.schuler.tipcalculator.ui.theme.TipCalculatorTheme
import com.schuler.tipcalculator.util.calculateTotalPerPerson
import com.schuler.tipcalculator.util.calculateTotalTip
import com.schuler.tipcalculator.widgets.RoundIconButton


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
//                TopHeader()
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit){
    TipCalculatorTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            color = MaterialTheme.colors.background
        ) {
            content()
        }
    }
}


@Composable
fun TopHeader(totalPerPerson: Double = 130.0){
    Surface(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
            .height(150.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
//          .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}


@Preview
@Composable
fun MainContent(){
    val splitByState = remember {
        mutableStateOf(1)
    }
    val tipAmountState = remember {
        mutableStateOf(0.00)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
        Column(modifier = Modifier.padding(12.dp)){
            BillForm(
                splitByState = splitByState,

                tipAmountState = tipAmountState,
                totalPerPersonState = totalPerPersonState
            ){billAmt->
            Log.d("AMT", "MainContent: Total per person = ${billAmt.toDouble()}")

            }
        }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValueChange: (String) -> Unit = {},

){
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.value * 100).toInt()

    TopHeader(totalPerPerson = totalPerPersonState.value)

    Surface(
        modifier = modifier
            .padding( 2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ){
        Column(
            modifier = Modifier.padding(all = 6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ){
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if (!validState) return@KeyboardActions

                    onValueChange(totalBillState.value.trim())
//                        onValueChange( "%.2f".format(totalPerPerson))
                    totalPerPersonState.value = calculateTotalPerPerson(
                        totalBill = totalBillState.value.toDouble(),
                        tipPercentage = sliderPositionState.value,
                        splitBy = splitByState.value
                    )

                    keyboardController?.hide()
                }
            )
            if (validState){
            // Split Row
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(
                            alignment = Alignment.CenterVertically
                        )
                    )
                    Spacer(modifier = Modifier.width(120.dp))

                    Row (
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End,
                    ){
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                if (splitByState.value > 1){
                                    splitByState.value --
                                    totalPerPersonState.value = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        tipPercentage = sliderPositionState.value,
                                        splitBy = splitByState.value
                                    )
                                }
                            }
                        )

                        Text(
                            text = "${splitByState.value}",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )

                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value < range.last) {
                                    splitByState.value ++
                                    totalPerPersonState.value = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        tipPercentage = sliderPositionState.value,
                                        splitBy = splitByState.value
                                    )
                                }
                            }
                        )
                    }
                }
            // Tip Row
            Row(
                modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)
            ){
                Text(
                    text = "Tip",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.width(200.dp))

                Text(
                    text = "$ ${"%.2f".format(tipAmountState.value)}",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
            }
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){

                Text(text = "$tipPercentage %")

                Spacer(modifier = Modifier.height(14.dp))

                // Slider
                Slider(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    value = sliderPositionState.value,
                    onValueChange = {newVal ->
                        sliderPositionState.value = newVal
                        tipAmountState.value = calculateTotalTip(
                            totalBill = totalBillState.value.toDouble(),
                            tipPercentage = newVal
                        )
                        totalPerPersonState.value = calculateTotalPerPerson(
                            totalBill = totalBillState.value.toDouble(),
                            tipPercentage = newVal,
                            splitBy = splitByState.value
                        )
                    },
                    steps = 9,
                )
            }

            } else {
                Box(){

                }
            }
        }
    }
}


//@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipCalculatorTheme {
        MyApp {
            Text(text = "Hello again!")
        }
    }
}