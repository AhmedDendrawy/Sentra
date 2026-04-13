package com.example.sentra.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sentra.R
import com.example.sentra.model.AddCameraRequest
import com.example.sentra.api.RetrofitClient
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AddCameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_camera)

        val etName = findViewById<EditText>(R.id.etCameraName)
        val etIp = findViewById<EditText>(R.id.etCameraIp)
        val etLocation = findViewById<EditText>(R.id.etLocation)
        val btnSave = findViewById<MaterialButton>(R.id.btnSaveCamera)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val location = etLocation.text.toString().trim()
            val streamUrl = etIp.text.toString().trim()

            if (name.isEmpty() || location.isEmpty() || streamUrl.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSave.isEnabled = false
            btnSave.text = "Adding..."

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // هنا بنستخدم الـ Request الجديد اللي بيكلم السيرفر
                    val request = AddCameraRequest(name, location, streamUrl)
                    val response = RetrofitClient.getApiService(this@AddCameraActivity).addCamera(request)

                    withContext(Dispatchers.Main) {
                        btnSave.isEnabled = true
                        btnSave.text = "Add Camera"

                        if (response.isSuccessful) {
                            Toast.makeText(this@AddCameraActivity, "Camera added successfully!", Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            var backendErrorMsg = "Failed to add camera"
                            try {
                                val errorBodyString = response.errorBody()?.string()
                                if (errorBodyString != null) {
                                    val jsonObject = JSONObject(errorBodyString)
                                    if (jsonObject.has("message")) {
                                        backendErrorMsg = jsonObject.getString("message")
                                    } else if (jsonObject.has("errors")) {
                                        backendErrorMsg = jsonObject.getString("errors")
                                    }
                                }
                            } catch (e: Exception) {
                                backendErrorMsg = "Server error code: ${response.code()}"
                            }
                            Toast.makeText(this@AddCameraActivity, backendErrorMsg, Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        btnSave.isEnabled = true
                        btnSave.text = "Add Camera"
                        Toast.makeText(this@AddCameraActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}