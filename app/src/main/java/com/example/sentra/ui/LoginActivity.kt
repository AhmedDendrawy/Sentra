package com.example.sentra.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (TokenManager.getToken(this) != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login)


        val etEmail = findViewById<TextInputEditText>(R.id.editTextEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.editTextPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.loginButton)
        val tvSignUp = findViewById<TextView>(R.id.tvCreateAccount)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        val emailInputLayout = findViewById<TextInputLayout>(R.id.EmailInputLayout)
        val passwordInputLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)


        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot password coming soon!", Toast.LENGTH_SHORT).show()
        }

        // 4. اللوجيك الأساسي لزرار تسجيل الدخول
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // تصفير الأخطاء القديمة
            emailInputLayout.error = null
            passwordInputLayout.error = null
            var isValid = true

            // التحقق من صحة البيانات (Validation)
            if (email.isEmpty()) {
                emailInputLayout.error = "Please enter your email"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.error = "Please enter a valid email address"
                isValid = false
            }

            if (password.isEmpty()) {
                passwordInputLayout.error = "Please enter your password"
                isValid = false
            }

            // لو البيانات فيها مشكلة، وقف هنا وماتكملش
            if (!isValid) return@setOnClickListener

            // تعطيل الشاشة عشان اليوزر مايدوسش مرتين
            setLoadingState(btnLogin, etEmail, etPassword, true)

            // 5. جلب توكن فايربيز (FCM) الأول
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                val fcmToken = if (task.isSuccessful) {
                    val token = task.result
                    Log.d("SENTRA_FCM", "FCM Token fetched successfully: $token")
                    token
                } else {
                    Log.w("SENTRA_FCM", "Fetching FCM token failed", task.exception)
                    "" // لو فشل، بنبعت فاضي عشان اللوجين مايوقفش
                }

                // 6. إرسال الطلب للسيرفر في الخلفية (Coroutines)
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val request = LoginRequest(email, password, fcmToken)
                        val response = RetrofitClient.getApiService(this@LoginActivity).loginUser(request)

                        withContext(Dispatchers.Main) {
                            // إرجاع الشاشة لحالتها الطبيعية
                            setLoadingState(btnLogin, etEmail, etPassword, false)

                            if (response.isSuccessful && response.body() != null) {
                                val loginData = response.body()!!

                                // 🌟 حفظ التوكن الأساسي وبيانات اليوزر
                                TokenManager.saveUserData(
                                    this@LoginActivity,
                                    loginData.accessToken,
                                    loginData.name,
                                    loginData.email
                                )

                                // 🌟 حفظ توكن التجديد (Refresh Token)
                                TokenManager.saveRefreshToken(
                                    this@LoginActivity,
                                    loginData.refreshToken
                                )

                                Toast.makeText(this@LoginActivity, "Welcome ${loginData.name}!", Toast.LENGTH_SHORT).show()

                                // الانتقال للشاشة الرئيسية
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish() // قفل شاشة اللوجين عشان اليوزر مايرجعلهاش بزرار الـ Back

                            } else {
                                // معالجة خطأ الباك إند (لو الباسوورد غلط مثلاً)
                                var backendErrorMsg = "Login failed"
                                try {
                                    val errorBodyString = response.errorBody()?.string()
                                    if (errorBodyString != null) {
                                        val jsonObject = JSONObject(errorBodyString)
                                        if (jsonObject.has("message")) {
                                            backendErrorMsg = jsonObject.getString("message")
                                        }
                                    }
                                } catch (parseException: Exception) {
                                    backendErrorMsg = "Server error code: ${response.code()}"
                                }

                                Toast.makeText(this@LoginActivity, backendErrorMsg, Toast.LENGTH_LONG).show()
                                emailInputLayout.error = "Check credentials"
                                passwordInputLayout.error = "Check credentials"
                            }
                        }
                    } catch (e: Exception) {
                        // معالجة أخطاء الإنترنت والاتصال
                        withContext(Dispatchers.Main) {
                            setLoadingState(btnLogin, etEmail, etPassword, false)
                            Toast.makeText(this@LoginActivity, "Connection Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    // دالة مساعدة لتفعيل وتعطيل الشاشة وقت التحميل
    private fun setLoadingState(button: MaterialButton, emailField: TextInputEditText, passField: TextInputEditText, isLoading: Boolean) {
        if (isLoading) {
            button.isEnabled = false
            button.text = "Logging in..."
            emailField.isEnabled = false
            passField.isEnabled = false
        } else {
            button.isEnabled = true
            button.text = "Login"
            emailField.isEnabled = true
            passField.isEnabled = true
        }
    }
}