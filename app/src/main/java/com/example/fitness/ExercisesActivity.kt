package com.example.fitness

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitness.databinding.ActivityExercisesBinding
import com.example.fitness.models.ExerciseLibraryItem

class ExercisesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExercisesBinding
    private lateinit var jsonHelper: JsonHelper
    private lateinit var adapter: ExerciseLibraryAdapter

    private val addExerciseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // An exercise was either created or added from default. We just need to reload the list.
            loadExercises()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExercisesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jsonHelper = JsonHelper(this)

        setupRecyclerView()
        loadExercises()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.buttonAddExercise.setOnClickListener {
            // Launch in create mode (no ID passed)
            val intent = Intent(this, EditExerciseActivity::class.java)
            addExerciseLauncher.launch(intent)
        }

        binding.buttonAddFromDefault.setOnClickListener {
            val intent = Intent(this, SelectDefaultExerciseActivity::class.java)
            addExerciseLauncher.launch(intent)
        }

        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = ExerciseLibraryAdapter(
            emptyList(),
            onEditClicked = { exercise ->
                // Launch in edit mode (pass the exercise ID)
                val intent = Intent(this, EditExerciseActivity::class.java).apply {
                    putExtra(EditExerciseActivity.EXTRA_EXERCISE_ID, exercise.id)
                    putExtra(EditExerciseActivity.EXTRA_EXERCISE_NAME, exercise.name)
                }
                addExerciseLauncher.launch(intent)
            }
        )
        binding.recyclerViewExercises.adapter = adapter
        binding.recyclerViewExercises.layoutManager = LinearLayoutManager(this)
    }

    private fun loadExercises() {
        val trainingData = jsonHelper.readTrainingData()
        adapter.updateExercises(trainingData.exerciseLibrary)
    }
}
