package com.example.fitness

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.databinding.ItemHistoryBinding
import com.example.fitness.models.TrainingSession

class HistoryAdapter(private val trainings: List<TrainingSession>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val training = trainings[position]
        holder.binding.textTrainingTitle.text = "Training #${training.trainingNumber}"
        holder.binding.textTrainingDate.text = training.date

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TrainingDetailActivity::class.java).apply {
                putExtra(TrainingDetailActivity.EXTRA_TRAINING_SESSION, training)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = trainings.size

    class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)
}