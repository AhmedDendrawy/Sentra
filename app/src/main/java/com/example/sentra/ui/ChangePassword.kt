package com.example.sentra.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sentra.R

class ChangePassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
       val backBtn=findViewById<ImageView>(R.id.btnBack)
       val backTv=findViewById<TextView>(R.id.tvBackLabel)
        backBtn.setOnClickListener{finish()}
        backTv.setOnClickListener{finish()}
    }
}