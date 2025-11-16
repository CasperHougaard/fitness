package com.example.fitness

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitness.databinding.ActivityTrainingDetailBinding
import com.example.fitness.models.ExerciseEntry
import com.example.fitness.models.GroupedExercise
import com.example.fitness.models.TrainingSession

class TrainingDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainingDetailBinding
    private lateinit var jsonHelper: JsonHelper
    private lateinit var trainingSession: TrainingSession

    private val editSetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val updatedEntry = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableExtra(EditSetActivity.EXTRA_EXERCISE_ENTRY, ExerciseEntry::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra(EditSetActivity.EXTRA_EXERCISE_ENTRY)
            }

            if (updatedEntry != null) {
                updateTrainingSession(updatedEntry)
            }
        }
    }

    companion object {
        const val EXTRA_TRAINING_SESSION = "extra_training_session"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jsonHelper = JsonHelper(this)

        val session = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TRAINING_SESSION, TrainingSession::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_TRAINING_SESSION)
        }

        if (session != null) {
            trainingSession = session
            title = "Training #${trainingSession.trainingNumber} - ${trainingSession.date}"
            setupRecyclerView()
            setupClickListeners()
        } else {
            title = "Training Details"
        }
    }

    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }

        binding.buttonDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Training")
                .setMessage("Are you sure you want to delete this training permanently?")
                .setPositiveButton("Delete") { _, _ ->
                    val trainingData = jsonHelper.readTrainingData()
                    val updatedTrainings = trainingData.trainings.toMutableList()
                    updatedTrainings.remove(trainingSession)
                    jsonHelper.writeTrainingData(trainingData.copy(trainings = updatedTrainings))
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun setupRecyclerView() {
        val groupedExercises = trainingSession.exercises.groupBy { it.exerciseId }.map { (exerciseId, sets) ->
            GroupedExercise(exerciseId, sets.first().exerciseName, sets)
        }

        binding.recyclerViewTrainingDetail.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTrainingDetail.adapter = TrainingDetailAdapter(groupedExercises) {
            val intent = Intent(this, EditSetActivity::class.java).apply {
                putExtra(EditSetActivity.EXTRA_EXERCISE_ENTRY, it)
            }
            editSetLauncher.launch(intent)
        }
    }

    private fun updateTrainingSession(updatedEntry: ExerciseEntry) {
        val exerciseIndex = trainingSession.exercises.indexOfFirst { it.setNumber == updatedEntry.setNumber && it.exerciseId == updatedEntry.exerciseId }
        if (exerciseIndex != -1) {
            trainingSession.exercises[exerciseIndex] = updatedEntry
            val trainingData = jsonHelper.readTrainingData()
            val sessionIndex = trainingData.trainings.indexOfFirst { it.id == trainingSession.id }
            if (sessionIndex != -1) {
                trainingData.trainings[sessionIndex] = trainingSession
                jsonHelper.writeTrainingData(trainingData)
                setupRecyclerView()
            }
        }
    }
}