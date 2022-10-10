package com.example.minesweeper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.minesweeper.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    companion object {
        var gameSize = 9
        var flagging = false
        var mines = 10
        var exploded = false
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tglFR.setOnClickListener() {
            flagging = binding.tglFR.isChecked
        }

        binding.btnReset.setOnClickListener() {
            binding.mineSweeper.resetGame()
        }

        binding.btnSmall.setOnClickListener() {
            gameSize = 6
            mines = 6
            binding.mineSweeper.resetGame()
        }
        binding.btnMedium.setOnClickListener() {
            gameSize = 9
            mines = 10
            binding.mineSweeper.resetGame()
        }
        binding.btnLarge.setOnClickListener() {
            gameSize = 14
            mines = 30
            binding.mineSweeper.resetGame()
        }
    }
    fun youWon() {
        Snackbar.make(binding.root,
            "You Won!",
            Snackbar.LENGTH_LONG).show()
    }
    fun youLost() {
        Snackbar.make(binding.root,
            "Uh oh! You exploded a mine",
            Snackbar.LENGTH_LONG).show()
    }
}
