package com.example.heartpoint

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class EditPasswordActivity : AppCompatActivity() {

    private lateinit var etOldPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var btnUpdatePassword: Button

    // 取得 FirebaseAuth 實例
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_password)

        val btnBack: ImageView = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish() // 返回到上一頁
        }

        etOldPassword = findViewById(R.id.et_old_password)
        etNewPassword = findViewById(R.id.et_new_password)
        btnUpdatePassword = findViewById(R.id.btn_update_password)

        // 點擊「更新密碼」按鈕
        btnUpdatePassword.setOnClickListener {
            val oldPassword = etOldPassword.text.toString()
            val newPassword = etNewPassword.text.toString()

            if (oldPassword.isEmpty()) {
                Toast.makeText(this, "請輸入舊密碼", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPassword.isEmpty()) {
                Toast.makeText(this, "請輸入新密碼", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "使用者未登入", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 先透過舊密碼重新驗證使用者
            reAuthenticateUser(currentUser, oldPassword) { reAuthSuccess ->
                if (!reAuthSuccess) {
                    Toast.makeText(this, "舊密碼錯誤，請重新輸入", Toast.LENGTH_SHORT).show()
                } else {
                    // 重新驗證成功，再更新新密碼
                    updatePassword(currentUser, newPassword)
                }
            }
        }
    }

    /**
     * 使用舊密碼重新驗證使用者
     */
    private fun reAuthenticateUser(user: FirebaseUser, oldPassword: String, callback: (Boolean) -> Unit) {
        val email = user.email
        if (email.isNullOrEmpty()) {
            callback(false)
            return
        }

        val credential = EmailAuthProvider.getCredential(email, oldPassword)
        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    /**
     * 更新新密碼
     */
    private fun updatePassword(user: FirebaseUser, newPassword: String) {
        user.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "密碼更新成功", Toast.LENGTH_SHORT).show()
                    // 更新完成後，回到上一頁
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "更新密碼失敗：${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
