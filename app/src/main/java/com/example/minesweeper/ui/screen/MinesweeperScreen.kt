package com.example.minesweeper.ui.screen

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minesweeper.R

@Composable
fun MinesweeperScreen(
    modifier: Modifier,
    viewModel: MinesweeperModel = viewModel()
) {
    val board=viewModel.board
    val win=viewModel.win
    val fin=viewModel.fin
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text= stringResource(R.string.minesweeper),
            modifier=Modifier.padding(bottom=10.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Checkbox(
                checked=viewModel.flagModeToggled,
                onCheckedChange = {
                    viewModel.switchFlagMode()
                    Log.d("FlagMode", "Flag mode toggled: ${viewModel.flagModeToggled}")
                }
            )
            Text(
                text = stringResource(R.string.toggle_flag_mode),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        if(win&&!fin){
            MinesweeperBoard(
                board = board,
                flagModeToggled=viewModel.flagModeToggled,
                onCellClick = { boardCell ->
                    if(!viewModel.flagModeToggled){
                        viewModel.onCellClicked(boardCell)
                        Log.d("CellClick", "Normal cell click at ${boardCell.row}, ${boardCell.col}")
                    }else{
                        viewModel.onCellFlagClicked(boardCell)
                        Log.d("FlagClick", "Flag cell click at ${boardCell.row}, ${boardCell.col}")
                    }
                })
        }else if(!win){
            Text(text = stringResource(R.string.you_lose))
            Button(onClick = { viewModel.resetGame() }) {
                Text(text = stringResource(R.string.reset_game))
            }
        }else if(fin){
            Text(text = stringResource(R.string.you_win))
        }
    }
}

@Composable
fun MinesweeperBoard(
    board: Array<Array<Field?>>,
    flagModeToggled: Boolean,
    onCellClick: (BoardCell)->Unit
){
    Log.d("MinesweeperBoard", "Board Composed - Flag Mode: $flagModeToggled")
    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .aspectRatio(1.0f)
            .pointerInput(key1 = Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        Log.d(
                            "TAG_CLICK",
                            "Click: ${offset.x} ${offset.y}"
                        )

                        val row = (offset.y / (size.height / 5)).toInt()
                        val col = (offset.x / (size.width / 5)).toInt()

                        onCellClick(BoardCell(row, col))

                    }
                )
            }
    ) {
        drawRect(
            Color.Gray,
            topLeft = Offset.Zero,
            size = size
        )
        val gridSize = size.minDimension
        val cellSize = gridSize / 5

        for (i in 0 until 5) {
            for (j in 0 until 5) {
                val field = board[i][j]
                if(field==null){
                    continue
                }else if(field.wasClicked&&field.type==FieldType.Bomb){
                    drawRect(
                        color = Color.Red,
                        topLeft = Offset(x = j * cellSize, y = i * cellSize),
                        size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                    )
                }else if(field.wasClicked&&field.type==FieldType.Number){
                    drawRect(
                        color = Color.Blue,
                        topLeft = Offset(x = j * cellSize, y = i * cellSize),
                        size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                    )
                }else if(field.wasClicked&&field.type==FieldType.Empty){
                    drawRect(
                        color = Color.Green,
                        topLeft = Offset(x = j * cellSize, y = i * cellSize),
                        size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                    )
                }else if(field.wasClicked&&field.type==FieldType.Flag){
                    drawRect(
                        color = Color.Gray,
                        topLeft = Offset(x = j * cellSize, y = i * cellSize),
                        size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                    )
                    drawCircle(
                        color = Color.Yellow,
                        center = Offset(x = j * cellSize + cellSize / 2, y = i * cellSize + cellSize / 2),
                        radius = cellSize / 4,
                        style = Stroke(width = 5f)
                    )
                }
            }
        }

        for (i in 1 until 5) {
            drawLine(
                color = Color.Black,
                strokeWidth = 5f,
                start = Offset(cellSize * i, 0f),
                end = Offset(cellSize * i, gridSize)
            )
            drawLine(
                color = Color.Black,
                strokeWidth = 5f,
                start = Offset(0f, cellSize * i),
                end = Offset(gridSize, cellSize * i)
            )
        }
    }
}