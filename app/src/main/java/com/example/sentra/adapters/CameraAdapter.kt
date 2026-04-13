package com.example.sentra.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.model.CameraItem
import com.example.sentra.R

// 1. ضيفنا (private val onItemClick: (CameraItem) -> Unit)
class CameraAdapter(
    private val cameraList: List<CameraItem>,
    private val onItemClick: (CameraItem) -> Unit
) : RecyclerView.Adapter<CameraAdapter.CameraViewHolder>() {

    class CameraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCameraName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvLastIncident: TextView = itemView.findViewById(R.id.tvLastIncident)
        val imgStatus: ImageView = itemView.findViewById(R.id.imgStatus) // تأكد من الـ ID في XML
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_camera, parent, false)
        return CameraViewHolder(view)
    }

    override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
        val item = cameraList[position]

        holder.tvName.text = item.name
        holder.tvLocation.text = item.location

        // Use the status from the backend instead of the old lastIncident
        holder.tvLastIncident.text = "Status: ${item.status}"

        // The backend returns a string for status (e.g., "Active", "Offline")
        if (item.status.equals("Active", ignoreCase = true)) {
            holder.imgStatus.setImageResource(R.drawable.statues_online)
        } else {
            holder.imgStatus.setImageResource(R.drawable.statues_offline)
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = cameraList.size
}