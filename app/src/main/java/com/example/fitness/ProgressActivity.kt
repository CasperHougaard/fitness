package com.example.fitness

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.fitness.databinding.ActivityProgressBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding
    private lateinit var jsonHelper: JsonHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "View Progress"

        jsonHelper = JsonHelper(this)

        setupSpinner()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupSpinner() {
        val trainingData = jsonHelper.readTrainingData()
        val exerciseNames = trainingData.trainings
            .flatMap { it.exercises }
            .map { it.exerciseName }
            .distinct()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, exerciseNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerExercise.adapter = adapter

        binding.spinnerExercise.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedExercise = exerciseNames[position]
                updateStatsForExercise(selectedExercise)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun updateStatsForExercise(exerciseName: String) {
        val allSets = mutableListOf<ExerciseSet>()
        val trainingData = jsonHelper.readTrainingData()
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        for (training in trainingData.trainings) {
            for (exercise in training.exercises) {
                if (exercise.exerciseName == exerciseName) {
                    allSets.add(ExerciseSet(training.date, exercise.setNumber, exercise.kg, exercise.reps))
                }
            }
        }

        allSets.sortBy {
            try {
                dateFormat.parse(it.date)
            } catch (e: Exception) {
                Date(0)
            }
        }

        calculateAndDisplayStats(allSets)
        setupChart(allSets, dateFormat)
    }

    private fun calculateAndDisplayStats(sets: List<ExerciseSet>) {
        if (sets.isEmpty()) {
            binding.textMaxWeight.text = "Max Weight: --"
            binding.textAvgWeight.text = "Avg Weight: --"
            binding.textTotalReps.text = "Total Reps: --"
            return
        }

        val maxWeight = sets.maxOfOrNull { it.kg } ?: 0f
        val totalReps = sets.sumOf { it.reps }
        val totalVolume = sets.sumOf { (it.kg * it.reps).toDouble() }
        val avgWeight = if (totalReps > 0) totalVolume / totalReps else 0.0

        binding.textMaxWeight.text = String.format(Locale.US, "Max Weight: %.1fkg", maxWeight)
        binding.textAvgWeight.text = String.format(Locale.US, "Avg Weight: %.1fkg", avgWeight)
        binding.textTotalReps.text = "Total Reps: $totalReps"
    }

    private fun setupChart(sets: List<ExerciseSet>, dateFormat: SimpleDateFormat) {
        val entries = mutableListOf<Entry>()

        sets.forEach {
            val date = try {
                dateFormat.parse(it.date)
            } catch (e: Exception) {
                null
            }

            if (date != null) {
                entries.add(Entry(date.time.toFloat(), it.kg))
            }
        }

        val dataSet = LineDataSet(entries, "Weight (kg)")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK
        dataSet.setCircleColor(Color.BLUE)
        dataSet.circleRadius = 4f
        dataSet.lineWidth = 2f

        val lineData = LineData(dataSet)
        binding.chart.data = lineData

        val xAxis = binding.chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return try {
                    val date = Date(value.toLong())
                    SimpleDateFormat("MM/dd", Locale.getDefault()).format(date)
                } catch (e: Exception) {
                    ""
                }
            }
        }
        xAxis.labelRotationAngle = -45f
        xAxis.setDrawGridLines(false)

        binding.chart.axisLeft.setDrawGridLines(false)
        binding.chart.axisRight.isEnabled = false

        binding.chart.description.isEnabled = false
        binding.chart.legend.isEnabled = true
        binding.chart.invalidate()
    }
}
