package com.jclian.sudokuincompose

import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.jclian.sudokuincompose.ui.theme.SudokuInComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SudokuInComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Sudoku()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SudokuInComposeTheme {
        Sudoku()
    }
}


@Composable
fun Sudoku() {
    val context = LocalContext.current
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
    var initMap by remember {
        mutableStateOf(com.jclian.libsudoku.Sudoku.gen())
    }

    val hashmap = HashMap<String, Int>()
    var answerMap = remember(hashmap) {
        mutableStateMapOf(*hashmap.map { it.key to it.value }.toTypedArray())
    }

    var showNewGame by remember {
        mutableStateOf(false)
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
                            // ??????????????????????????????
                            val numBlockWidth = canvasWidth / 9
                            val numBlockHeight = canvasWidth / 9
                            // calculate the key base x and y coordinates
                            // ??????x???y?????????????????????????????????
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

                        // pined num ???????????????
                        var value = initMap[key]

                        // selected ???????????????????????????
                        var circleColor = if (selectedKey == key) {
                            Color.Yellow
                        } else {
                            Color.Black
                        }

                        // draw circle ??????
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
                        // ?????????????????????????????????????????????Compose????????????????????????????????????
                        // draw number ?????????
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
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .pointerInput(Unit) {
                            }, onClick = {

                            if (BuildConfig.DEBUG) {
//                                Toast
//                                    .makeText(context, "$number", Toast.LENGTH_SHORT)
//                                    .show()
                            }

                            if (!initMap.containsKey(selectedKey)) {
                                if (number != 10) {
                                    answerMap.set(selectedKey, number)

                                    if (answerMap.size + initMap.size >= 81) {
                                        // TODO: 2021/8/10 answer is fully filled
                                        var hashMap = hashMapOf<String, Int>()
                                        hashMap.putAll(initMap)
                                        hashMap.putAll(answerMap)
                                        val isSuccess =
                                            com.jclian.libsudoku.Sudoku.check(hashMap)
                                        if (isSuccess) {
                                            // Puzzle Solved.And show up the new game button
                                            Toast.makeText(
                                                context,
                                                "Solved",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            showNewGame = true
                                        }
                                    }
                                } else {
                                    answerMap.remove(selectedKey)
                                }
                            }

                        }) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = txt,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer(translationY = 25f),
                                color = Color.White,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                fontSize = 114.sp
                            )
                        }

                    }
                }
            }

        }

        if (showNewGame) {
            Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                Button(onClick = {
                    // new game. ??????????????????
                    Toast.makeText(context, "new game", Toast.LENGTH_SHORT).show()
                    initMap = com.jclian.libsudoku.Sudoku.gen()
                    answerMap.clear()
                }) {
                    Text(text = "Start New Game")
                }
            }
        }
    }

}