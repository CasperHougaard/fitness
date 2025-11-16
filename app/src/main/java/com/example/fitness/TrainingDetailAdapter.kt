package com.example.fitness

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.databinding.ItemSetDetailBinding
import com.example.fitness.databinding.ItemGroupedExerciseBinding
import com.example.fitness.models.ExerciseEntry
import com.example.fitness.models.GroupedExercise

class TrainingDetailAdapter(
    private val groupedExercises: List<GroupedExercise>,
    private val onEditSetClicked: (ExerciseEntry) -> Unit
) : RecyclerView.Adapter<TrainingDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGroupedExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val groupedExercise = groupedExercises[position]
        holder.binding.textExerciseName.text = groupedExercise.exerciseName

        holder.binding.setsContainer.removeAllViews()

        for (set in groupedExercise.sets) {
            val setBinding = ItemSetDetailBinding.inflate(LayoutInflater.from(holder.itemView.context))
            setBinding.textSetDetails.text = "Set ${set.setNumber}: ${set.kg}kg x ${set.reps} reps"
            setBinding.buttonEditSet.setOnClickListener {
                onEditSetClicked(set)
            }
            holder.binding.setsContainer.addView(setBinding.root)
        }
    }

    override fun getItemCount() = groupedExercises.size

    class ViewHolder(val binding: ItemGroupedExerciseBinding) : RecyclerView.ViewHolder(binding.root)
}