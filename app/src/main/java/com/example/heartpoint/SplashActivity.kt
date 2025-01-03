package com.example.heartpoint

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 延遲一秒模擬啟動頁過渡效果
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 1000) // 延遲 1 秒
    }

    private fun checkUserStatus() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        currentUser?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 使用者有效，跳轉到主頁
                navigateToMain()
            } else {
                // 使用者無效或已被刪除
                auth.signOut()
                navigateToLogin()
            }
        } ?: run {
            // 未登入，跳轉到登入頁
            navigateToLogin()
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // 結束啟動頁
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish() // 結束啟動頁
    }
}
