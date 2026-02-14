import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.sentra.ui.LoginActivity
import com.example.sentra.ui.ManageCamerasActivity
import com.example.sentra.R
import com.example.sentra.databinding.ActivityChangePasswordBinding
import com.example.sentra.ui.ChangePassword
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ربط ملف XML بالكود
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 1. تعريف العناصر (Initialize Views) ---
        // لاحظ: استخدمنا IDs اللي كتبناها في XML

        val switchNotif = view.findViewById<SwitchMaterial>(R.id.switchNotifications)
        val switchSounds = view.findViewById<SwitchMaterial>(R.id.switchSounds)

        // الأزرار هنا عبارة عن ConstraintLayout لأننا صممنا الزرار بنفسنا
        val btnManageCameras = view.findViewById<ConstraintLayout>(R.id.btnManageCameras)
        val btnChangePassword = view.findViewById<ConstraintLayout>(R.id.btnChangePassword)

        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogOut)


        // --- 2. برمجة الـ Switches (Preferences) ---

        switchNotif.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(context, "Notifications Enabled", Toast.LENGTH_SHORT).show()
                // هنا ممكن تضيف كود لحفظ الإعدادات
            } else {
                Toast.makeText(context, "Notifications Disabled", Toast.LENGTH_SHORT).show()
            }
        }

        switchSounds.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Sounds ON" else "Sounds OFF"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }


        // --- 3. برمجة أزرار التنقل (Management) ---

        btnManageCameras.setOnClickListener {
            // كود الانتقال لصفحة الكاميرات
            val intent = Intent(requireContext(), ManageCamerasActivity::class.java)
            startActivity(intent)
        }

        btnChangePassword.setOnClickListener {

            val intent = Intent(requireContext(), ChangePassword::class.java)
            startActivity(intent)
        }


        // --- 4. برمجة زر الخروج (Logout) ---

        btnLogout.setOnClickListener {
            // هنا بتكتب كود مسح بيانات اليوزر والرجوع لصفحة الدخول
            Toast.makeText(context, "Logging Out...", Toast.LENGTH_SHORT).show()

            // مثال:
             val intent = Intent(activity, LoginActivity::class.java)
             startActivity(intent)
             activity?.finish()
        }

    }
}