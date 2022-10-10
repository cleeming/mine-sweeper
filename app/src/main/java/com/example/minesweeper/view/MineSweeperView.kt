package com.example.minesweeper.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.minesweeper.MainActivity
import com.example.minesweeper.R
import com.example.minesweeper.model.MineSweeperModel
import java.util.*
import kotlin.collections.ArrayList

class MineSweeperView (context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    init {
        MineSweeperModel.initGameArea(MainActivity.gameSize)
        MineSweeperModel.generateMines()
        MineSweeperModel.setAllNeighbours()
    }

    var bitStart = BitmapFactory.decodeResource(resources, R.drawable.startsprite)
    var bitFlag = BitmapFactory.decodeResource(resources, R.drawable.flag)
    var bitOne = BitmapFactory.decodeResource(resources, R.drawable.num1)
    var bitTwo = BitmapFactory.decodeResource(resources, R.drawable.numtwo)
    var bitThree = BitmapFactory.decodeResource(resources, R.drawable.numthree)
    var bitFour = BitmapFactory.decodeResource(resources, R.drawable.numfour)
    var bitFive = BitmapFactory.decodeResource(resources, R.drawable.numfive)
    var bitSix = BitmapFactory.decodeResource(resources, R.drawable.numsix)
    var bitSeven = BitmapFactory.decodeResource(resources, R.drawable.numseven)
    var bitEight = BitmapFactory.decodeResource(resources, R.drawable.numeight)
    var bitEmpty = BitmapFactory.decodeResource(resources, R.drawable.emptyfield)
    var bitMine = BitmapFactory.decodeResource(resources, R.drawable.bomb)

    val bitMaps = arrayOf(bitEmpty,bitOne,bitTwo,bitThree,bitFour,bitFive,bitSix,bitSeven,bitEight,bitMine,bitFlag,bitStart)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        resizeBitmaps()
    }

    private fun resizeBitmaps() {
        for (i in 0..11) {
            bitMaps[i] = Bitmap.createScaledBitmap(bitMaps[i],
                width/MainActivity.gameSize,height/MainActivity.gameSize,false)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBoard(canvas)
    }

    // Every time we touch the screen the x and y coordinates are refreshed
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!MainActivity.exploded) {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                val fX = event.x.toInt() / (width / MainActivity.gameSize)
                val fY = event.y.toInt() / (height / MainActivity.gameSize)
                if (fX < MainActivity.gameSize && fY < MainActivity.gameSize) {
                    MineSweeperModel.fieldMatrix[fX][fY].wasClicked = true
                    onFlag(fX,fY)
                    onReveal(fX,fY)
                }
                invalidate()
            }
            checkWin()
        }
        return true
    }

    fun checkWin() {
        var flaggedMines = 0
        for (i in 0 until MineSweeperModel.mineXCoords.size) {
            if (MineSweeperModel.fieldMatrix[MineSweeperModel.mineXCoords[i]][MineSweeperModel.mineYCoords[i]].isFlagged) {
                flaggedMines ++
            }
        }
        if (flaggedMines == MineSweeperModel.mineXCoords.size) {
            (context as MainActivity).youWon()
        }
    }

    private fun onReveal (fX: Int, fY: Int) {
        if (!MainActivity.flagging) {
            if (MineSweeperModel.fieldMatrix[fX][fY].type == 0) {
                expandEmpty(fX,fY)
            }
            if (MineSweeperModel.fieldMatrix[fX][fY].type == 9 && !MainActivity.flagging && !MineSweeperModel.fieldMatrix[fX][fY].isFlagged) {
                explodeMine()
                MainActivity.exploded = true
                (context as MainActivity).youLost()
            }
            else if (MineSweeperModel.fieldMatrix[fX][fY].isRevealed && MineSweeperModel.flaggedMinesAround(fX,fY) == MineSweeperModel.fieldMatrix[fX][fY].type) {
                revealNeighbours(fX,fY)
            }
        }
    }

    private fun explodeMine() {
        for (i in 0 until MainActivity.mines) {
            MineSweeperModel.fieldMatrix[MineSweeperModel.mineXCoords[i]][MineSweeperModel.mineYCoords[i]].wasClicked = true
            MineSweeperModel.fieldMatrix[MineSweeperModel.mineXCoords[i]][MineSweeperModel.mineYCoords[i]].isRevealed = true
            MineSweeperModel.fieldMatrix[MineSweeperModel.mineXCoords[i]][MineSweeperModel.mineYCoords[i]].isFlagged = false
        }
        invalidate()
    }

    private fun revealNeighbours (fX: Int, fY: Int) {
        //if (number of bombs neighbouring fx, fy != type) return
        for (i in -1..1) {
            for (j in -1..1) {
                if ((fX+i) in 0 until MainActivity.gameSize &&
                    (fY+j) in 0 until MainActivity.gameSize) {
                    neighbourEmptyOrFlag(fX+i, fY+j)
                }
            }
        }
    }

    private fun neighbourEmptyOrFlag (fX: Int,fY: Int) {
        if (MineSweeperModel.fieldMatrix[fX][fY].isFlagged) {
            return
        }
        else{
            if (MineSweeperModel.fieldMatrix[fX][fY].type == 0) {
                expandEmpty(fX,fY)
            }
            MineSweeperModel.fieldMatrix[fX][fY].wasClicked = true
            MineSweeperModel.fieldMatrix[fX][fY].isRevealed = true
        }
    }

    private fun onFlag (fX: Int, fY: Int) {
        if (MainActivity.flagging) {
            if (MineSweeperModel.fieldMatrix[fX][fY].isFlagged) {
                MineSweeperModel.fieldMatrix[fX][fY].wasClicked = false
                MineSweeperModel.fieldMatrix[fX][fY].isFlagged =  false
            }
            else {
                MineSweeperModel.fieldMatrix[fX][fY].isFlagged = true
            }
        }
    }

    private fun drawBoard (canvas: Canvas) {
        for (i in 0 until MainActivity.gameSize) {
            for (j in 0 until MainActivity.gameSize) {
                // If the field at i,j was clicked
                if (MineSweeperModel.fieldMatrix[i][j].wasClicked) {
                    // draw the correct bitmap at i,j
                    canvas?.drawBitmap(fieldClicked(i, j),
                        i.toFloat() * (width/MainActivity.gameSize),
                        j.toFloat() * (width/MainActivity.gameSize), null)
                }
                else {
                    canvas?.drawBitmap(bitMaps[11],
                        i.toFloat() * (width/MainActivity.gameSize),
                        j.toFloat() * (width/MainActivity.gameSize), null)
                }
            }
        }
    }

    private fun expandEmpty (fieldX: Int, fieldY: Int) {
        val stack = Stack<ArrayList<Int>>()
        stack.push(arrayListOf(fieldX,fieldY))
        while (!stack.isEmpty()) {
            val currentField = stack.pop()
            val currX = currentField[0]
            val currY = currentField[1]
            checkEmptyNeighbour(currX, currY, stack)
        }
    }

    private fun checkEmptyNeighbour (currX: Int, currY: Int, stack: Stack<ArrayList<Int>>) {
        for (i in -1..1) {
            for (j in -1..1) {
                if (j == 0 && i == 0) {continue}
                val nbourX = currX + i
                val nbourY = currY + j
                if ((nbourX) in 0 until MainActivity.gameSize &&
                    (nbourY) in 0 until MainActivity.gameSize &&
                    !MineSweeperModel.fieldMatrix[nbourX][nbourY].wasClicked) {
                        pushEmptyNeighbour(nbourX, nbourY, stack)
                }
            }
        }
    }

    private fun pushEmptyNeighbour (nbourX: Int, nbourY: Int, stack: Stack<ArrayList<Int>>) {
        if (MineSweeperModel.fieldMatrix[nbourX][nbourY].type == 0) {
            stack.push(arrayListOf(nbourX,nbourY))
        }
        MineSweeperModel.fieldMatrix[nbourX][nbourY].wasClicked = true
        MineSweeperModel.fieldMatrix[nbourX][nbourY].isRevealed = true
    }

    private fun fieldClicked (i: Int, j: Int): Bitmap {
        if (MineSweeperModel.fieldMatrix[i][j].isFlagged &&
            !MineSweeperModel.fieldMatrix[i][j].isRevealed) {
            return bitMaps[10]
        }
        else {
            MineSweeperModel.fieldMatrix[i][j].isRevealed = true
            return bitMaps[MineSweeperModel.fieldMatrix[i][j].type]
        }
    }

    public fun resetGame() {
        resizeBitmaps()
        MineSweeperModel.resetModel()
        MainActivity.exploded = false
        invalidate()
    }
}
