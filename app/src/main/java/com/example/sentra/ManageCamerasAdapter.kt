package com.example.sentra

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class ManageCamerasAdapter(
    private val cameras: List<CameraItem>,
    private val onEditClick: (CameraItem) -> Unit,
    private val onDeleteClick: (CameraItem) -> Unit
) : RecyclerView.Adapter<ManageCamerasAdapter.ManageViewHolder>() {

    class ManageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCameraName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvUrl: TextView = itemView.findViewById(R.id.tvUrl)
        val btnEdit: MaterialButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_manage_camera, parent, false)
        return ManageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ManageViewHolder, position: Int) {
        val item = cameras[position]
        holder.tvName.text = item.name
        holder.tvLocation.text = item.location
        // هنا بنحط الرابط الحقيقي أو رابط افتراضي للعرض
        holder.tvUrl.text = "rtsp://192.168.1.10${position}:554/stream"

        holder.btnEdit.setOnClickListener { onEditClick(item) }
        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount() = cameras.size
}