package com.example.fitness

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.fitness.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var jsonHelper: JsonHelper

    private val startWorkoutForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // This block is called when ActiveTrainingActivity finishes.
        // We can now update the stats on the main screen.
        updateStats()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        jsonHelper = JsonHelper(this)

        setupClickListeners()
        updateStats()
    }

    private fun setupClickListeners() {
        binding.buttonStartWorkout.setOnClickListener {
            val intent = Intent(this, ActiveTrainingActivity::class.java)
            startWorkoutForResult.launch(intent)
        }

        binding.buttonViewProgress.setOnClickListener {
            val intent = Intent(this, ProgressActivity::class.java)
            startActivity(intent)
        }

        binding.buttonViewHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.buttonExercises.setOnClickListener {
            val intent = Intent(this, ExercisesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateStats() {
        val trainingData = jsonHelper.readTrainingData()
        binding.textWorkoutsCount.text = trainingData.trainings.size.toString()

        // Dummy data for now
        binding.textMinutesCount.text = "0"
        binding.textCaloriesCount.text = "0"
    }
}
