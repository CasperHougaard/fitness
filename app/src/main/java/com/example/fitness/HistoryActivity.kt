package com.example.fitness

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitness.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var jsonHelper: JsonHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Training History"

        jsonHelper = JsonHelper(this)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val trainingData = jsonHelper.readTrainingData()
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHistory.adapter = HistoryAdapter(trainingData.trainings.reversed())
    }
}