package com.example.fitness

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.models.ExerciseEntry

data class GroupedExercise(val exerciseId: Int, val exerciseName: String, val sets: List<ExerciseEntry>)

class ActiveExercisesAdapter(
    private val groupedExercises: List<GroupedExercise>,
    private val onAddSetClicked: (exerciseId: Int, exerciseName: String) -> Unit
) : RecyclerView.Adapter<ActiveExercisesAdapter.GroupedExerciseViewHolder>() {

    class GroupedExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseName: TextView = view.findViewById(R.id.text_exercise_name)
        val setsSummary: TextView = view.findViewById(R.id.text_sets_summary)
        val addSetButton: Button = view.findViewById(R.id.button_add_set)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupedExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_active_exercise, parent, false)
        return GroupedExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupedExerciseViewHolder, position: Int) {
        val groupedExercise = groupedExercises[position]
        holder.exerciseName.text = groupedExercise.exerciseName

        val setsText = groupedExercise.sets.joinToString("\n") { set ->
            "Set ${set.setNumber}: ${set.kg} kg x ${set.reps} reps"
        }
        holder.setsSummary.text = setsText.ifEmpty { "No sets logged yet." }

        holder.addSetButton.setOnClickListener {
            onAddSetClicked(groupedExercise.exerciseId, groupedExercise.exerciseName)
        }
    }

    override fun getItemCount() = groupedExercises.size
}
