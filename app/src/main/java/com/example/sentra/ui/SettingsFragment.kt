package com.example.sentra.ui // تأكد من مسار الباكيدج بتاعك

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView // 🌟 ضفنا ده
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.sentra.ui.LoginActivity
import com.example.sentra.ui.ManageCamerasActivity
import com.example.sentra.R
import com.example.sentra.api.TokenManager // تأكد إن ده مسار الـ TokenManager الصح عندك
import com.example.sentra.data.CamerasRepository
import com.example.sentra.ui.ChangePassword
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = view.findViewById<TextView>(R.id.tvUserEmail)


        val name = TokenManager.getUserName(requireContext()) ?: "User"
        val email = TokenManager.getUserEmail(requireContext()) ?: "user@example.com"


        tvUserName.text = name
        tvUserEmail.text = email



        val switchNotif = view.findViewById<SwitchMaterial>(R.id.switchNotifications)
        val switchSounds = view.findViewById<SwitchMaterial>(R.id.switchSounds)

        val btnManageCameras = view.findViewById<ConstraintLayout>(R.id.btnManageCameras)
        val btnChangePassword = view.findViewById<ConstraintLayout>(R.id.btnChangePassword)

        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogOut)

        // --- 3. برمجة الـ Switches ---
        switchNotif.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(context, "Notifications Enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Notifications Disabled", Toast.LENGTH_SHORT).show()
            }
        }

        switchSounds.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Sounds ON" else "Sounds OFF"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        // --- 4. برمجة أزرار التنقل ---
        btnManageCameras.setOnClickListener {
            val intent = Intent(requireContext(), ManageCamerasActivity::class.java)
            startActivity(intent)
        }

        btnChangePassword.setOnClickListener {
            val intent = Intent(requireContext(), ChangePassword::class.java)
            startActivity(intent)
        }

        // --- 5. برمجة زر الخروج (Logout) ---
        btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Logging Out...", Toast.LENGTH_SHORT).show()

            // 1. Clear user token and data
            TokenManager.clearData(requireContext())

            // 2. Clear cameras from RAM so the next user starts fresh
            CamerasRepository.camerasList.clear()

            // 3. Navigate back to LoginActivity and prevent going back
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}