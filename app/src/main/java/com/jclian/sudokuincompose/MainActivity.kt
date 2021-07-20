package com.jclian.sudokuincompose

import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jclian.sudokuincompose.ui.theme.SudokuInComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SudokuInComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SudokuInComposeTheme {
        Greeting("Android")
    }
}

@Preview(showBackground = true)
@Composable
fun Sudoku() {
    val paint = Paint().asFrameworkPaint()

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val numBlockWidth = canvasWidth / 9
        val numBlockHeight = canvasWidth / 9

        val circleRadius = numBlockWidth * 0.8

        val dashPathEffect = PathEffect.dashPathEffect(
            floatArrayOf(numBlockWidth * 0.6f, numBlockWidth * 0.4f),
            numBlockWidth * -0.2f
        )
        for (i in 0 until 9) {

            if (i != 0 && i != 9) {

                val lineColor = if (i == 3 || i == 6) Color.Black else Color.Blue
                val localPathEffect = if (i == 3 || i == 6) null else dashPathEffect
                drawLine(
                    start = Offset(x = numBlockWidth * i, y = 0f),
                    end = Offset(x = numBlockWidth * i, y = canvasWidth),
                    color = lineColor,
                    strokeWidth = 5F,
                    pathEffect = localPathEffect
                )

                drawLine(
                    start = Offset(x = 0f, y = numBlockHeight * i),
                    end = Offset(x = canvasWidth, y = numBlockHeight * i),
                    color = lineColor,
                    strokeWidth = 5F,
                    pathEffect = localPathEffect
                )
            }

            for (j in 0 until 9) {
                // draw circle 画圆
                drawCircle(
                    center = Offset(
                        x = (numBlockWidth * i + numBlockWidth / 2f).toFloat(),
                        y = (numBlockWidth * j + numBlockWidth / 2f).toFloat()
                    ),
                    radius = circleRadius.toFloat() / 2f,
                    color = Color.Black
                )

                // draw number 画数字

                paint.apply {
                    isAntiAlias = true
                    textSize = 24f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                paint.color = android.graphics.Color.WHITE

                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        "$j",
                        (numBlockWidth * i + numBlockWidth / 2f).toFloat(),
                        (numBlockWidth * j + numBlockWidth / 2f).toFloat(),
                        paint
                    )
                }
            }
        }

    }
}