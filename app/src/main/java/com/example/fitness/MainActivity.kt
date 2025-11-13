package com.example.fitness

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitness.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        updateStats()
    }

    private fun setupClickListeners() {
        binding.buttonStartWorkout.setOnClickListener {
            Toast.makeText(this, "Start Workout feature coming soon!", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to workout screen
        }

        binding.buttonViewProgress.setOnClickListener {
            Toast.makeText(this, "View Progress feature coming soon!", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to progress screen
        }

        binding.buttonExercises.setOnClickListener {
            Toast.makeText(this, "Exercises feature coming soon!", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to exercises screen
        }
    }

    private fun updateStats() {
        // TODO: Load actual stats from database/storage
        binding.textWorkoutsCount.text = "0"
        binding.textMinutesCount.text = "0"
        binding.textCaloriesCount.text = "0"
    }
}
