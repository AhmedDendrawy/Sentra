package com.example.sentra.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.R
import com.example.sentra.data.model.CameraItem
import com.example.sentra.databinding.ItemCameraBinding

class CameraAdapter(
    private val cameraList: MutableList<CameraItem>,
    private val onItemClick: (CameraItem) -> Unit
) : RecyclerView.Adapter<CameraAdapter.CameraViewHolder>() {

    class CameraViewHolder(val binding: ItemCameraBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
        val binding = ItemCameraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CameraViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
        val item = cameraList[position]

        holder.binding.tvCameraName.text = item.name
        holder.binding.tvLocation.text = item.location
        holder.binding.tvLastIncident.text = "Status: ${item.status}"

        if (item.status.equals("Active", ignoreCase = true)) {
            holder.binding.imgStatus.setImageResource(R.drawable.statues_online)
        } else {
            holder.binding.imgStatus.setImageResource(R.drawable.statues_offline)
        }

        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }

    fun updateData(newCameras: List<CameraItem>) {
        cameraList.clear()
        cameraList.addAll(newCameras)
        notifyDataSetChanged()
    }

    override fun getItemCount() = cameraList.size
}