package com.example.fitness

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.fitness.databinding.ActivityLogSetBinding
import com.example.fitness.models.ExerciseEntry

class LogSetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogSetBinding
    private var selectedRating: Int = 0
    private lateinit var ratingTiles: List<TextView>

    companion object {
        const val EXTRA_EXERCISE_ID = "extra_exercise_id"
        const val EXTRA_EXERCISE_NAME = "extra_exercise_name"
        const val EXTRA_SET_NUMBER = "extra_set_number"
        const val EXTRA_LOGGED_SET = "extra_logged_set"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val exerciseName = intent.getStringExtra(EXTRA_EXERCISE_NAME) ?: "Exercise"
        binding.textLogSetTitle.text = "Log Set for $exerciseName"

        setupRatingTiles()

        binding.buttonSaveSet.setOnClickListener {
            saveSet()
        }

        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRatingTiles() {
        ratingTiles = listOf(
            binding.tile1,
            binding.tile2,
            binding.tile3,
            binding.tile4,
            binding.tile5
        )

        ratingTiles.forEachIndexed { index, tile ->
            tile.setOnClickListener {
                selectedRating = index + 1
                updateRatingTiles()
            }
        }
    }

    private fun updateRatingTiles() {
        ratingTiles.forEachIndexed { index, tile ->
            if (index < selectedRating) {
                tile.background = ContextCompat.getDrawable(this, R.drawable.tile_selected)
                tile.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                tile.background = ContextCompat.getDrawable(this, R.drawable.tile_unselected)
                tile.setTextColor(ContextCompat.getColor(this, R.color.fitness_text_primary))
            }
        }
    }

    private fun saveSet() {
        val kg = binding.editTextKg.text.toString().toFloatOrNull() ?: 0f
        val reps = binding.editTextReps.text.toString().toIntOrNull() ?: 0
        val note = binding.editTextNote.text.toString()

        val exerciseId = intent.getIntExtra(EXTRA_EXERCISE_ID, -1)
        val exerciseName = intent.getStringExtra(EXTRA_EXERCISE_NAME) ?: ""
        val setNumber = intent.getIntExtra(EXTRA_SET_NUMBER, 1)

        val newEntry = ExerciseEntry(
            exerciseId = exerciseId,
            exerciseName = exerciseName,
            setNumber = setNumber,
            kg = kg,
            reps = reps,
            note = note.takeIf { it.isNotBlank() },
            rating = selectedRating.takeIf { it > 0 }
        )

        val resultIntent = Intent().apply {
            putExtra(EXTRA_LOGGED_SET, newEntry)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
