package com.example.fitness

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitness.databinding.ActivityActiveTrainingBinding
import com.example.fitness.models.ExerciseEntry
import com.example.fitness.models.TrainingSession
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ActiveTrainingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActiveTrainingBinding
    private lateinit var jsonHelper: JsonHelper
    private val currentExerciseEntries = mutableListOf<ExerciseEntry>()
    private val groupedExercises = mutableListOf<GroupedExercise>()
    private lateinit var adapter: ActiveExercisesAdapter
    private val selectedDate = Calendar.getInstance()
    private val TAG = "ActiveTrainingActivity"

    private val logSetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val data = result.data
                val loggedSet = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    data?.getParcelableExtra(LogSetActivity.EXTRA_LOGGED_SET, ExerciseEntry::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    data?.getParcelableExtra(LogSetActivity.EXTRA_LOGGED_SET)
                }

                if (loggedSet != null) {
                    updateExercises(loggedSet)
                } else {
                    Log.e(TAG, "Received null set from LogSetActivity")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing result from LogSetActivity", e)
            }
        }
    }

    private val selectExerciseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val data = result.data
                val exerciseId = data?.getIntExtra(SelectExerciseActivity.EXTRA_EXERCISE_ID, -1) ?: -1
                val exerciseName = data?.getStringExtra(SelectExerciseActivity.EXTRA_EXERCISE_NAME) ?: ""

                if (exerciseId != -1 && exerciseName.isNotEmpty()) {
                    val existingGroup = groupedExercises.find { it.exerciseId == exerciseId }
                    if (existingGroup == null) {
                        val newGroup = GroupedExercise(exerciseId, exerciseName, emptyList())
                        groupedExercises.add(newGroup)
                        adapter.notifyItemInserted(groupedExercises.size - 1)
                    }
                    launchLogSetActivity(exerciseId, exerciseName)
                } else {
                    Log.e(TAG, "Invalid exercise data received from SelectExerciseActivity")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing result from SelectExerciseActivity", e)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jsonHelper = JsonHelper(this)

        setupRecyclerView()
        setupClickListeners()
        updateDateDisplay()
    }

    private fun setupRecyclerView() {
        adapter = ActiveExercisesAdapter(
            groupedExercises,
            onAddSetClicked = { exerciseId, exerciseName ->
                launchLogSetActivity(exerciseId, exerciseName)
            },
            onDuplicateSetClicked = { exerciseId ->
                duplicateLastSet(exerciseId)
            },
            onDeleteExerciseClicked = { exerciseId ->
                deleteExercise(exerciseId)
            }
        )
        binding.recyclerViewActiveExercises.adapter = adapter
        binding.recyclerViewActiveExercises.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        binding.buttonAddExerciseToSession.setOnClickListener {
            val intent = Intent(this, SelectExerciseActivity::class.java)
            selectExerciseLauncher.launch(intent)
        }

        binding.buttonFinishWorkout.setOnClickListener {
            finishWorkout()
        }

        binding.buttonChangeDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, month)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateDisplay()
        }

        DatePickerDialog(
            this,
            dateSetListener,
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun launchLogSetActivity(exerciseId: Int, exerciseName: String) {
        try {
            val setNumber = (currentExerciseEntries.filter { it.exerciseId == exerciseId }.maxOfOrNull { it.setNumber } ?: 0) + 1
            val intent = Intent(this, LogSetActivity::class.java).apply {
                putExtra(LogSetActivity.EXTRA_EXERCISE_ID, exerciseId)
                putExtra(LogSetActivity.EXTRA_EXERCISE_NAME, exerciseName)
                putExtra(LogSetActivity.EXTRA_SET_NUMBER, setNumber)
            }
            logSetLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch LogSetActivity", e)
        }
    }

    private fun updateExercises(loggedSet: ExerciseEntry) {
        currentExerciseEntries.add(loggedSet)

        val groupIndex = groupedExercises.indexOfFirst { it.exerciseId == loggedSet.exerciseId }
        if (groupIndex != -1) {
            val oldGroup = groupedExercises[groupIndex]
            val newSets = oldGroup.sets + loggedSet
            val newGroup = oldGroup.copy(sets = newSets.sortedBy { it.setNumber })
            groupedExercises[groupIndex] = newGroup
            adapter.notifyItemChanged(groupIndex)
        }
    }

    private fun duplicateLastSet(exerciseId: Int) {
        val lastSet = currentExerciseEntries.filter { it.exerciseId == exerciseId }.lastOrNull()
        if (lastSet != null) {
            val newSetNumber = lastSet.setNumber + 1
            val newSet = lastSet.copy(setNumber = newSetNumber, rating = null, note = null)
            updateExercises(newSet)
        }
    }

    private fun deleteExercise(exerciseId: Int) {
        val groupIndex = groupedExercises.indexOfFirst { it.exerciseId == exerciseId }
        if (groupIndex != -1) {
            groupedExercises.removeAt(groupIndex)
            currentExerciseEntries.removeAll { it.exerciseId == exerciseId }
            adapter.notifyItemRemoved(groupIndex)
        }
    }

    private fun updateDateDisplay() {
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        binding.textActiveTrainingDate.text = sdf.format(selectedDate.time)
    }

    private fun finishWorkout() {
        try {
            val trainingData = jsonHelper.readTrainingData()
            val nextTrainingNumber = (trainingData.trainings.maxOfOrNull { it.trainingNumber } ?: 0) + 1

            val newSession = TrainingSession(
                trainingNumber = nextTrainingNumber,
                date = binding.textActiveTrainingDate.text.toString(),
                exercises = currentExerciseEntries
            )

            trainingData.trainings.add(newSession)
            jsonHelper.writeTrainingData(trainingData)

            setResult(Activity.RESULT_OK)
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to finish workout", e)
        }
    }
}
