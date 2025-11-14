package com.example.fitness

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.databinding.ItemTrainingDetailBinding
import com.example.fitness.models.ExerciseEntry

class TrainingDetailAdapter(private val exercises: List<ExerciseEntry>) : RecyclerView.Adapter<TrainingDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrainingDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.binding.textExerciseName.text = exercise.exerciseName
        holder.binding.textSetDetails.text = "Set ${exercise.setNumber}: ${exercise.kg}kg x ${exercise.reps} reps"
    }

    override fun getItemCount() = exercises.size

    class ViewHolder(val binding: ItemTrainingDetailBinding) : RecyclerView.ViewHolder(binding.root)
}