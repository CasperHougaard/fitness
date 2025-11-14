package com.example.fitness

import android.content.Context
import android.util.Log
import com.example.fitness.models.TrainingData
import com.google.gson.Gson
import java.io.File

class JsonHelper(private val context: Context) {

    private val gson = Gson()
    private val file = File(context.filesDir, "training_data.json")
    private val TAG = "JsonHelper"

    fun readTrainingData(): TrainingData {
        if (!file.exists()) {
            return TrainingData()
        }
        return try {
            val json = file.readText()
            gson.fromJson(json, TrainingData::class.java) ?: TrainingData()
        } catch (e: Exception) {
            Log.e(TAG, "Error reading or parsing training_data.json. Backing up and creating a new data file.", e)
            // If the file is corrupt, create a backup and start with a fresh one.
            try {
                val backupFile = File(context.filesDir, "training_data.json.bak.${System.currentTimeMillis()}")
                file.renameTo(backupFile)
            } catch (backupEx: Exception) {
                Log.e(TAG, "Could not back up corrupt file.", backupEx)
            }
            TrainingData()
        }
    }

    fun writeTrainingData(trainingData: TrainingData) {
        try {
            val json = gson.toJson(trainingData)
            file.writeText(json)
        } catch (e: Exception) {
            Log.e(TAG, "Error writing to training_data.json", e)
        }
    }
}
