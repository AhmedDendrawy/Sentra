package com.example.sentra.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.data.model.CameraItem
import com.example.sentra.databinding.ItemManageCameraBinding

class ManageCamerasAdapter(
    private val cameras: List<CameraItem>,
    private val onEditClick: (CameraItem) -> Unit,
    private val onDeleteClick: (CameraItem) -> Unit
) : RecyclerView.Adapter<ManageCamerasAdapter.ManageViewHolder>() {

    class ManageViewHolder(val binding: ItemManageCameraBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageViewHolder {
        val binding = ItemManageCameraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ManageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ManageViewHolder, position: Int) {
        val item = cameras[position]

        holder.binding.tvCameraName.text = item.name
        holder.binding.tvLocation.text = item.location
        holder.binding.tvUrl.text = "rtsp://192.168.1.10${position}:554/stream"

        holder.binding.btnEdit.setOnClickListener { onEditClick(item) }
        holder.binding.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount() = cameras.size
}