package com.example.minesweeper.model

import com.example.minesweeper.MainActivity
import com.example.minesweeper.view.MineSweeperField
import kotlin.random.Random

object MineSweeperModel {

    lateinit var fieldMatrix: Array<Array<MineSweeperField>>

    var randX = 0
    var randY = 0
    var mineCounter =  0
    val mineXCoords: MutableList<Int> = mutableListOf()
    val mineYCoords: MutableList<Int> = mutableListOf()

    fun initGameArea (size: Int) {
        fieldMatrix = Array(size){Array(size) {MineSweeperField(0,0, false, false, false, false)} }
    }

    fun generateMines() {
        mineCounter = 0
        while (mineCounter != MainActivity.mines) {
            val random = Random(System.currentTimeMillis())
            randX = random.nextInt(0, (MainActivity.gameSize - 1))
            randY = random.nextInt(0, (MainActivity.gameSize - 1))

            setMineCoords(randX,randY)
        }
    }

    private fun setMineCoords (randX: Int, randY: Int) {
        if (fieldMatrix[MineSweeperModel.randX][MineSweeperModel.randY].type != 9) {
            fieldMatrix[MineSweeperModel.randX][MineSweeperModel.randY].type = 9
            mineXCoords.add(MineSweeperModel.randX)
            mineYCoords.add(MineSweeperModel.randY)
            mineCounter ++
        }
    }

    private fun findNeighbour(mineX: Int, mineY: Int) {
        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) {continue}
                if ((mineX+i) in 0 until MainActivity.gameSize &&
                    (mineY+j) in 0 until MainActivity.gameSize) {
                    if (fieldMatrix[mineX+i][mineY+j].type != 9) {
                        fieldMatrix[mineX+i][mineY+j].type ++
                    }
                }
            }
        }
    }

    private fun incrementFlaggedMines (fieldX: Int, fieldY: Int, count: Int): Int {
        var retCnt = count
        if (fieldX in 0 until MainActivity.gameSize &&
            fieldY in 0 until MainActivity.gameSize &&
            fieldMatrix[fieldX][fieldY].type == 9 &&
            fieldMatrix[fieldX][fieldY].isFlagged) {
            retCnt++
        }

        return retCnt
    }

    fun flaggedMinesAround (fieldX: Int, fieldY: Int): Int {
        var counter = 0
        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) {continue}
                counter = incrementFlaggedMines(fieldX+i, fieldY+j, counter)
            }
        }
        return counter
    }

    fun setAllNeighbours() {
        for (i in 0 until mineXCoords.size) {
            findNeighbour(mineXCoords[i], mineYCoords[i])
        }
    }

    fun resetField(i: Int, j: Int) {
        fieldMatrix[i][j].type = 0
        fieldMatrix[i][j].minesAround = 0
        fieldMatrix[i][j].isFlagged = false
        fieldMatrix[i][j].wasClicked = false
        fieldMatrix[i][j].isRevealed = false
        fieldMatrix[i][j].visited = false
    }

    fun resetModel() {
        initGameArea(MainActivity.gameSize)
        for (i in 0 until MainActivity.gameSize) {
            for (j in 0 until MainActivity.gameSize) {
                resetField(i,j)
            }
        }
        mineXCoords.clear()
        mineYCoords.clear()
        generateMines()
        setAllNeighbours()
    }
}