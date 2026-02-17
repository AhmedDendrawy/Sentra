package com.example.sentra.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sentra.R
import com.example.sentra.api.LoginRequest
import com.example.sentra.api.RetrofitClient
import com.example.sentra.api.TokenManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout // 🌟 ضفنا ده
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. فحص الدخول التلقائي (لو معاه توكن، دخله فوراً)
        if (TokenManager.getToken(this) != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // نقفل صفحة الـ Login
            return // نوقف قراءة باقي الكود
        }

        // لو ممعهوش توكن، نعرضله شاشة تسجيل الدخول
        setContentView(R.layout.activity_login)

        // 2. ربط العناصر بالـ IDs اللي في الـ XML
        val etEmail = findViewById<TextInputEditText>(R.id.editTextEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.editTextPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.loginButton)
        val tvSignUp = findViewById<TextView>(R.id.tvCreateAccount)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        // 🌟 ربط الـ Layouts عشان الـ Errors الحمراء 🌟
        val emailInputLayout = findViewById<TextInputLayout>(R.id.EmailInputLayout)
        val passwordInputLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)

        // 3. زرار الانتقال لصفحة إنشاء حساب
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        // زرار نسيت كلمة المرور
        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot password coming soon!", Toast.LENGTH_SHORT).show()
        }

        // 4. برمجة زرار تسجيل الدخول الأساسي
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // 🌟 تصفير أي أخطاء قديمة عشان المربع يرجع طبيعي 🌟
            emailInputLayout.error = null
            passwordInputLayout.error = null
            var isValid = true

            // 🌟 Validation محلي واحترافي 🌟
            if (email.isEmpty()) {
                emailInputLayout.error = "Please enter your email"
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.error = "Please enter a valid email address"
                isValid = false
            }

            if (password.isEmpty()) {
                passwordInputLayout.error = "Please enter your password"
                isValid = false
            }

            // لو في أي غلطة، نوقف الكود ومبنبعتش حاجة للسيرفر
            if (!isValid) return@setOnClickListener

            // 🌟 نقفل الزرار والخانات عشان اليوزر ميلعبش فيهم وقت التحميل 🌟
            btnLogin.isEnabled = false
            btnLogin.text = "Logging in..."
            etEmail.isEnabled = false
            etPassword.isEnabled = false

            // الاتصال بالـ API في الخلفية
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val request = LoginRequest(email, password)
                    val response = RetrofitClient.apiService.loginUser(request)

                    withContext(Dispatchers.Main) {
                        // 🌟 نرجع نفتح الزرار والخانات تاني 🌟
                        btnLogin.isEnabled = true
                        btnLogin.text = "Login"
                        etEmail.isEnabled = true
                        etPassword.isEnabled = true

                        if (response.isSuccessful && response.body() != null) {

                            // استلام البيانات وحفظها
                            val loginData = response.body()!!
                            TokenManager.saveUserData(
                                this@LoginActivity,
                                loginData.token,
                                loginData.name,
                                loginData.email
                            )

                            Toast.makeText(this@LoginActivity, "Welcome ${loginData.name}!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            // 🌟 السيرفر رفض: نخلي المربعات تنور أحمر 🌟
                            emailInputLayout.error = "Invalid email or password"
                            passwordInputLayout.error = "Invalid email or password"
                        }
                    }
                } catch (e: Exception) {
                    // مفيش نت أو السيرفر واقع
                    withContext(Dispatchers.Main) {
                        btnLogin.isEnabled = true
                        btnLogin.text = "Login"
                        etEmail.isEnabled = true
                        etPassword.isEnabled = true
                        Toast.makeText(this@LoginActivity, "Network Error. Check your connection", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}