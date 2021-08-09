package com.jclian.sudokuincompose

import android.content.Context
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.jclian.sudokuincompose.ui.theme.SudokuInComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SudokuInComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Sudoku(this@MainActivity)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SudokuInComposeTheme {
        Sudoku(null)
    }
}


@Composable
fun Sudoku(context: Context?) {
    val paint = Paint().asFrameworkPaint()
    var selected by remember {
        mutableStateOf<Offset?>(null)
    }
    var selectedKey by remember {
        mutableStateOf("")
    }
    var selectedNumber by remember {
        mutableStateOf(-1)
    }
    val initMap by remember {
        mutableStateOf(com.jclian.libsudoku.Sudoku.gen())
    }

    val answerMap by remember {
        mutableStateOf(hashMapOf<String, Int>())
    }

    Column {

        Row {

            Canvas(modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {

                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        if (it.y <= canvasWidth) {
                            // tap inside the grids
                            // 在九宫区域的点击事件
                            val numBlockWidth = canvasWidth / 9
                            val numBlockHeight = canvasWidth / 9
                            // calculate the key base x and y coordinates
                            // 根据x、y坐标计算计算选中的键值
                            val indexX = (it.x / numBlockWidth).toInt()
                            val indexY = (it.y / numBlockWidth).toInt()
                            selectedKey = "$indexX,$indexY"
                            context?.let {
                                if (BuildConfig.DEBUG) {
                                    Toast
                                        .makeText(
                                            context,
                                            "onTap: $selectedKey ",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            }
                            selected = it
                        }
                    })
                }) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val numBlockWidth = canvasWidth / 9
                val numBlockHeight = canvasWidth / 9

                val circleRadius = numBlockWidth * 0.8

                val dashPathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(numBlockWidth * 0.6f, numBlockWidth * 0.4f),
                    numBlockWidth * -0.2f
                )

                paint.apply {
                    textSize = numBlockWidth * 0.6f
                    isAntiAlias = true
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    color = android.graphics.Color.WHITE
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                val textHeight: Float = paint.descent() - paint.ascent()
                val textOffset: Float = textHeight / 2 - paint.descent()
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
                        val key = "$i,$j"
                        val left = i * numBlockWidth
                        val top = j * numBlockHeight
                        val rectF = RectF(left, top, left + numBlockWidth, top + numBlockHeight)

                        // pined num 初始化的值
                        var value = initMap[key]

                        // selected 判断点击是否在圆内
                        var circleColor = if (selectedKey == key) {
                            Color.Yellow
                        } else {
                            Color.Black
                        }

                        // draw circle 画圆
                        if (value != null || selectedKey == key) {
                            drawCircle(
                                center = Offset(
                                    x = (numBlockWidth * i + numBlockWidth / 2f).toFloat(),
                                    y = (numBlockWidth * j + numBlockWidth / 2f).toFloat()
                                ),
                                radius = circleRadius.toFloat() / 2f,
                                color = circleColor
                            )
                        }
                        paint.color = android.graphics.Color.WHITE
                        if (value == null) {
                            value = answerMap[key]
                            paint.color = android.graphics.Color.BLACK
                        }
                        // using native draw to draw. can't find any solution in Compose
                        // 使用原生的绘制方法绘制文本。在Compose中，暂时没有找到其他方案
                        // draw number 画数字
                        if (value != null) {
                            drawIntoCanvas {
                                it.nativeCanvas.drawText(
                                    "$value",
                                    rectF.centerX(),
                                    rectF.centerY() + textOffset,
                                    paint
                                )
                            }
                        }
                    }
                }

            }
        }

        for (i in 0..1) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (j in 1..5) {
                    val number = i * 5 + j
                    var txt = if (number >= 10) "X" else "$number"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    if (BuildConfig.DEBUG) {
                                        Toast
                                            .makeText(context, "$number", Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                    if (!initMap.containsKey(selectedKey)) {
                                        if (number != 10) {
                                            answerMap[selectedKey] = i * 5 + j
                                        } else {
                                            answerMap.remove(selectedKey)
                                        }
                                    }
                                })
                            }
                    ) {
                        Text(text = txt)
                    }
                }
            }

        }
    }

}