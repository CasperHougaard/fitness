package com.example.fitness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {
    private lateinit var jsonHelper: JsonHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        jsonHelper = JsonHelper(this)

        findViewById<Button>(R.id.button_reset_data).setOnClickListener {
            showResetDataConfirmationDialog()
        }
    }

    private fun showResetDataConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Reset Data")
            .setMessage("Are you sure you want to reset all your data? This action cannot be undone.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Reset") { _, _ ->
                resetData()
            }
            .show()
    }

    private fun resetData() {
        jsonHelper.resetTrainingData()
    }
}