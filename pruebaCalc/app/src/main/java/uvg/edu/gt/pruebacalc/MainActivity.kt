package uvg.edu.gt.pruebacalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uvg.edu.gt.pruebacalc.ui.theme.PruebaCalcTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PruebaCalcTheme {
                Calculator()
            }
        }
    }
}

@Composable
fun Calculator(
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("0") }
    var subtitle by remember { mutableStateOf("0") }

    Column {
        display(title, subtitle)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxWidth()
        ) {
            CalculatorButton(onClickedValue = { title = "0" }, text = "C")
            CalculatorButton(onClickedValue = { title = title + "(" }, text = "(")
            CalculatorButton(onClickedValue = { title = title + ")" }, text = ")")
            CalculatorButton(onClickedValue = { title = title.substring(0, title.length - 1) }, text = "<-")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxWidth()
        ) {
            CalculatorButton(onClickedValue = { title = title + "1" }, text = "1")
            CalculatorButton(onClickedValue = { title = title + "2" }, text = "2")
            CalculatorButton(onClickedValue = { title = title + "3" }, text = "3")
            CalculatorButton(onClickedValue = { title = title + "+" }, text = "+")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxWidth()
        ) {
            CalculatorButton(onClickedValue = { title = title + "4" }, text = "4")
            CalculatorButton(onClickedValue = { title = title + "5" }, text = "5")
            CalculatorButton(onClickedValue = { title = title + "6" }, text = "6")
            CalculatorButton(onClickedValue = { title = title + "-" }, text = "-")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxWidth()
        ) {
            CalculatorButton(onClickedValue = { title = title + "7" }, text = "7")
            CalculatorButton(onClickedValue = { title = title + "8" }, text = "8")
            CalculatorButton(onClickedValue = { title = title + "9" }, text = "9")
            CalculatorButton(onClickedValue = { title = title + "*" }, text = "*")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxWidth()
        ) {
            CalculatorButton(onClickedValue = { title = title + "." }, text = ".")
            CalculatorButton(onClickedValue = { title = title + "0" }, text = "0")
            CalculatorButton(onClickedValue = { subtitle = evaluateExpression(title) }, text = "=")
            CalculatorButton(onClickedValue = { title = title + "/" }, text = "/")
        }

    }
}

@Composable
fun CalculatorButton(
    onClickedValue: () -> Unit
    , text: String = ""
    , modifier: Modifier = Modifier
){
    Button(
        onClick = onClickedValue
        , modifier = modifier.padding(end = 10.dp)
    ) {
        Text(text = text)
    }
}

@Composable
fun display(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End
            )
        }
    }
}

fun evaluateExpression(expression: String): String {
    return try {
        val tokens = tokenize(expression)
        val result = parseExpression(tokens)
        result.toString()
    } catch (e: Exception) {
        "Error"
    }
}

fun tokenize(expression: String): List<String> {
    val tokens = mutableListOf<String>()
    var number = StringBuilder()
    for (i in expression.indices) {
        val char = expression[i]
        when (char) {
            in '0'..'9', '.' -> number.append(char)
            '+', '-', '*', '/', '(', ')' -> {
                if (number.isNotEmpty()) {
                    tokens.add(number.toString())
                    number = StringBuilder()
                }
                tokens.add(char.toString())
            }
            else -> throw IllegalArgumentException("Invalid character in expression")
        }
        // Handle implicit multiplication (e.g., 5(4+1) -> 5*(4+1))
        if (char == '(' && i > 0 && expression[i - 1] in '0'..'9') {
            tokens.add(tokens.size - 1, "*")
        }
    }
    if (number.isNotEmpty()) {
        tokens.add(number.toString())
    }
    return tokens
}

fun parseExpression(tokens: List<String>): Double {
    val values = mutableListOf<Double>()
    val operators = mutableListOf<Char>()

    var i = 0
    while (i < tokens.size) {
        when (val token = tokens[i]) {
            "(" -> {
                var j = i + 1
                var openBrackets = 1
                while (j < tokens.size && openBrackets > 0) {
                    if (tokens[j] == "(") openBrackets++
                    if (tokens[j] == ")") openBrackets--
                    j++
                }
                if (openBrackets != 0) throw IllegalArgumentException("Mismatched parentheses")
                values.add(parseExpression(tokens.subList(i + 1, j - 1)))
                i = j
            }
            ")" -> throw IllegalArgumentException("Mismatched parentheses")
            "+", "-", "*", "/" -> {
                while (operators.isNotEmpty() && precedence(operators.last()) >= precedence(token[0])) {
                    values.add(applyOperator(operators.removeAt(operators.size - 1), values.removeAt(values.size - 1), values.removeAt(values.size - 1)))
                }
                operators.add(token[0])
                i++
            }
            else -> {
                values.add(token.toDouble())
                i++
            }
        }
    }

    while (operators.isNotEmpty()) {
        values.add(applyOperator(operators.removeAt(operators.size - 1), values.removeAt(values.size - 1), values.removeAt(values.size - 1)))
    }

    return values.last()
}

fun precedence(operator: Char): Int {
    return when (operator) {
        '+', '-' -> 1
        '*', '/' -> 2
        else -> throw IllegalArgumentException("Invalid operator")
    }
}

fun applyOperator(operator: Char, b: Double, a: Double): Double {
    return when (operator) {
        '+' -> a + b
        '-' -> a - b
        '*' -> a * b
        '/' -> a / b
        else -> throw IllegalArgumentException("Invalid operator")
    }
}

@Preview(showBackground = true)
@Composable
fun RightAlignedCardPreview() {
    Calculator()
}
@Preview(showBackground = true)
@Composable
fun DisplayCalcPreview(){
    display("0", "o")
}

@Preview(showBackground = true)
@Composable
fun CalculatorButtonPreview(){
    CalculatorButton({}, "0")
}