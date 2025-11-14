package com.example.fitness.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ExerciseLibraryItem(
    val id: Int,
    val name: String,
    val category: String? = null
) : Parcelable

@Parcelize
data class ExerciseEntry(
    val exerciseId: Int,
    var exerciseName: String,
    val setNumber: Int,
    val kg: Float,
    val reps: Int,
    val note: String? = null,
    val rating: Int? = null
) : Parcelable

@Parcelize
data class TrainingSession(
    val id: String = UUID.randomUUID().toString(), // GUID
    val trainingNumber: Int,
    val date: String, // yyyy/mm/dd
    val exercises: MutableList<ExerciseEntry>
) : Parcelable

@Parcelize
data class TrainingData(
    val exerciseLibrary: MutableList<ExerciseLibraryItem> = mutableListOf(),
    val trainings: MutableList<TrainingSession> = mutableListOf()
) : Parcelable
