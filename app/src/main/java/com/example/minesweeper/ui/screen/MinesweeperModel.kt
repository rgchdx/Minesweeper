package com.example.minesweeper.ui.screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class FieldType{
    Bomb, Empty, Flag, Number
}

data class BoardCell(val row: Int, val col: Int)

data class Field(
    var type: FieldType, var minesAround: Int, var wasClicked: Boolean)

class MinesweeperModel : ViewModel(){
    var board by mutableStateOf(
        Array(5){Array(5){null as Field?}}
    )
    var initialClick by mutableStateOf(true)
    var fin by mutableStateOf(false) //to see if we are finished with mapping everything out
    var win by mutableStateOf(true) //change to false if lose is called
    var bombClicked by mutableStateOf(false) //change to true in onCellClicked later
    var foundBombCount by mutableStateOf(0)
    var cellClickCount by mutableStateOf(0)
    var flagModeToggled by mutableStateOf(false)
    fun onCellClicked(cell: BoardCell) {
        //initial instantiation of the board. Will call startGame on first click
        if (initialClick) {
            startGame()
            initialClick = false
        }
        //new ifstatement so that we wont call startGame again on a click
        if (board[cell.row][cell.col]?.wasClicked == false) {
            //val newBoard = board.copyOf() update this later to get state changes
            val newBoard=board.copyOf()
            //Update copy so that we can insert this into the board state later
            newBoard[cell.row][cell.col] = newBoard[cell.row][cell.col]?.copy(wasClicked = true)
            //Handle bomb click
            //change win to false and handle this in the minesweeper screen later to make lose condition
            if (newBoard[cell.row][cell.col]?.type == FieldType.Bomb) {
                bombClicked = true
                win = false
            } else {
                //--EDGE CASE--
                //celclickcount incrementation so that if someone does not use a single flag
                //and fills everything out, they will win
                cellClickCount++
            }
            //Update the board state to trigger recomposition
            board = newBoard
            //Check this to get the win condition
            if (cellClickCount == 22) {
                win = true
                fin = true
            }
        }
    }

    fun onCellFlagClicked(cell: BoardCell){
        if (initialClick) {
            startGame()
            initialClick = false
        }
        if(!initialClick){
            val newBoard=board.copyOf()
            if(board[cell.row][cell.col]?.wasClicked == false){
                newBoard[cell.row][cell.col] = newBoard[cell.row][cell.col]?.copy(wasClicked = true)
                if(board[cell.row][cell.col]?.type==FieldType.Bomb){
                    newBoard[cell.row][cell.col]?.type=FieldType.Flag
                    foundBombCount++
                    Log.d("FLAGGED_CELL", "Cell ($cell.row, $cell.col) flagged as bomb")
                    if(foundBombCount==3){
                        win = true
                    }
                }
                board=newBoard
            }
        }
    }

    fun switchFlagMode(){
        flagModeToggled=!flagModeToggled
        Log.d("FlagMode", "Flag mode toggled: $flagModeToggled")
    }


    fun startGame(){
        board=Array(5){Array(5){null as Field?} }
        var numArray=Array(5){IntArray(5)}
        var num=0
        var surroundingBombs=0
        for(i in 0 until 5){
            for(j in 0 until 5){
                numArray[i][j]=num
                num++
            }
        }
        val bombPlacement1=(0..24).random()
        var bombPlacement2=(0..24).random()
        var bombPlacement3=(0..24).random()
        while(bombPlacement1==bombPlacement2){
            bombPlacement2=(0..24).random()
        }
        while(bombPlacement2==bombPlacement3){
            bombPlacement3=(0..24).random()
        }
        for(i in 0 until 5){
            for (j in 0 until 5){
                if(numArray[i][j]==bombPlacement1||numArray[i][j]==bombPlacement2||numArray[i][j]==bombPlacement3){
                    numArray[i][j]=-1
                }
            }
        }
        for(i in 0 until 5) {
            for (j in 0 until 5) {
                if (numArray[i][j] == -1) {
                    board[i][j] = Field(type= FieldType.Bomb, minesAround=0, wasClicked=false)
                }
            }
        }

        //create other cells of 1 2 and 3. Iterate through each cell and count how many bombs there are around and store that
        //As a variable.
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                var surroundingBombs = 0

                val directions = listOf(
                    Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
                    Pair(0, -1), /* current cell */ Pair(0, 1),
                    Pair(1, -1), Pair(1, 0), Pair(1, 1)
                )

                for (dir in directions) {
                    val newRow = i + dir.first
                    val newCol = j + dir.second

                    //Handle crash here maybe??
                    //Make sure the new row and column are within bounds
                    if (newRow in 0 until 5 && newCol in 0 until 5) {
                        if (board[newRow][newCol]?.type == FieldType.Bomb) {
                            surroundingBombs++
                        }
                    }
                }

                if (board[i][j]?.type != FieldType.Bomb) {
                    if (surroundingBombs > 0) {
                        board[i][j] = Field(type = FieldType.Number, minesAround = surroundingBombs, wasClicked = false)
                    } else {
                        board[i][j] = Field(type = FieldType.Empty, minesAround = 0, wasClicked = false)
                    }
                }
            }
        }
    }
    fun resetGame() {
        board = Array(5) { Array(5) { null as Field? } }
        initialClick = true
        fin = false
        win = true
        bombClicked = false
        cellClickCount = 0
        foundBombCount = 0
        flagModeToggled = false
    }

}
