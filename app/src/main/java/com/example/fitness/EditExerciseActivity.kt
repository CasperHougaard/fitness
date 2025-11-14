package com.example.fitness

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fitness.databinding.ActivityEditExerciseBinding
import com.example.fitness.models.ExerciseLibraryItem

class EditExerciseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditExerciseBinding
    private lateinit var jsonHelper: JsonHelper
    private var exerciseId: Int = -1

    companion object {
        const val EXTRA_EXERCISE_ID = "extra_exercise_id"
        const val EXTRA_EXERCISE_NAME = "extra_exercise_name"
        const val EXTRA_NEW_EXERCISE = "extra_new_exercise"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jsonHelper = JsonHelper(this)
        exerciseId = intent.getIntExtra(EXTRA_EXERCISE_ID, -1)

        if (exerciseId != -1) {
            // Edit mode
            binding.textEditExerciseTitle.text = "Edit Exercise"
            val exerciseName = intent.getStringExtra(EXTRA_EXERCISE_NAME)
            binding.editTextExerciseName.setText(exerciseName)
        } else {
            // Create mode
            binding.textEditExerciseTitle.text = "Create New Exercise"
        }

        binding.buttonSaveExercise.setOnClickListener {
            saveExercise()
        }

        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun saveExercise() {
        val newName = binding.editTextExerciseName.text.toString().trim()
        if (newName.isEmpty()) {
            binding.editTextExerciseName.error = "Exercise name cannot be empty"
            return
        }

        val trainingData = jsonHelper.readTrainingData()

        if (exerciseId != -1) {
            // Update existing exercise
            val existingExercise = trainingData.exerciseLibrary.find { it.id == exerciseId }
            if (existingExercise != null) {
                // Update name in the library
                val updatedExercise = existingExercise.copy(name = newName)
                val index = trainingData.exerciseLibrary.indexOf(existingExercise)
                trainingData.exerciseLibrary[index] = updatedExercise

                // Update name in all past training sessions for data integrity
                trainingData.trainings.forEach { session ->
                    session.exercises.forEach { entry ->
                        if (entry.exerciseId == exerciseId) {
                            val entryIndex = session.exercises.indexOf(entry)
                            session.exercises[entryIndex].copy(exerciseName = newName)
                        }
                    }
                }
            }
        } else {
            // Create new exercise
            val nextId = (trainingData.exerciseLibrary.maxOfOrNull { it.id } ?: 0) + 1
            val newExercise = ExerciseLibraryItem(id = nextId, name = newName)
            trainingData.exerciseLibrary.add(newExercise)
            exerciseId = nextId // So we can return it
        }

        jsonHelper.writeTrainingData(trainingData)

        val resultIntent = Intent().apply {
            putExtra(EXTRA_EXERCISE_ID, exerciseId)
            putExtra(EXTRA_EXERCISE_NAME, newName)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
