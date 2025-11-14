package com.example.fitness

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.fitness.databinding.ActivitySelectExerciseBinding
import com.example.fitness.models.ExerciseLibraryItem

class SelectExerciseActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectExerciseBinding
    private lateinit var jsonHelper: JsonHelper
    private lateinit var exercises: List<ExerciseLibraryItem>
    private lateinit var adapter: ArrayAdapter<String>

    companion object {
        const val EXTRA_EXERCISE_ID = "extra_exercise_id"
        const val EXTRA_EXERCISE_NAME = "extra_exercise_name"
    }

    private val createExerciseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // A new exercise was created and returned.
            // We can now forward this result back to the ActiveTrainingActivity.
            setResult(Activity.RESULT_OK, result.data)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jsonHelper = JsonHelper(this)
        loadExercises()

        binding.listViewSelectExercise.setOnItemClickListener { _, _, position, _ ->
            val selectedExercise = exercises[position]
            val resultIntent = Intent().apply {
                putExtra(EXTRA_EXERCISE_ID, selectedExercise.id)
                putExtra(EXTRA_EXERCISE_NAME, selectedExercise.name)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        binding.buttonCreateNewExercise.setOnClickListener {
            val intent = Intent(this, EditExerciseActivity::class.java)
            createExerciseLauncher.launch(intent)
        }

        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadExercises() {
        exercises = jsonHelper.readTrainingData().exerciseLibrary
        val exerciseNames = exercises.map { it.name }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, exerciseNames)
        binding.listViewSelectExercise.adapter = adapter
    }
}
