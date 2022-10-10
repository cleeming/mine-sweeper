package com.example.minesweeper.view

data class MineSweeperField (var type: Int, var minesAround: Int,
                             var isFlagged: Boolean, var wasClicked: Boolean,
                             var isRevealed: Boolean, var visited: Boolean) {

}