package com.example.sentra.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sentra.R
import com.example.sentra.adapters.ManageCamerasAdapter
import com.example.sentra.api.RetrofitClient
import com.example.sentra.model.CameraItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManageCamerasActivity : AppCompatActivity() {

    private lateinit var adapter: ManageCamerasAdapter
    private lateinit var rvManageCameras: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private var camerasList = mutableListOf<CameraItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_cameras)

        rvManageCameras = findViewById(R.id.rvManageCameras)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        rvManageCameras.layoutManager = LinearLayoutManager(this)

        adapter = ManageCamerasAdapter(
            camerasList,
            onEditClick = { camera ->
                // Here we send the whole camera object to the Edit screen
                val intent = Intent(this, EditCameraActivity::class.java)
                intent.putExtra("CAMERA_DATA", camera)
                startActivity(intent)
            },
            onDeleteClick = { camera ->
                showDeleteConfirmation(camera)
            }
        )

        rvManageCameras.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // Always fetch fresh data when returning to this screen
        fetchCameras()
    }

    private fun fetchCameras() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.getApiService(this@ManageCamerasActivity).getCameras()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        camerasList.clear()
                        camerasList.addAll(response.body()!!)
                        adapter.notifyDataSetChanged()
                        updateEmptyState()
                    } else {
                        updateEmptyState()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    updateEmptyState()
                }
            }
        }
    }

    private fun showDeleteConfirmation(camera: CameraItem) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Camera")
        builder.setMessage("Are you sure you want to delete '${camera.name}'?")

        builder.setPositiveButton("Delete") { _, _ ->
            deleteCameraFromServer(camera)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(android.R.color.holo_red_dark))
    }

    private fun deleteCameraFromServer(camera: CameraItem) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.getApiService(this@ManageCamerasActivity)
                    .deleteCamera(camera.cameraId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ManageCamerasActivity, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                        fetchCameras() // Refresh the list after deleting
                    } else {
                        Toast.makeText(this@ManageCamerasActivity, "Failed to delete", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ManageCamerasActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateEmptyState() {
        if (camerasList.isEmpty()) {
            layoutEmptyState.visibility = View.VISIBLE
            rvManageCameras.visibility = View.GONE
        } else {
            layoutEmptyState.visibility = View.GONE
            rvManageCameras.visibility = View.VISIBLE
        }
    }
}