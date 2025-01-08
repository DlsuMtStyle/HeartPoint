package com.example.heartpoint

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class EditEmailActivity : AppCompatActivity() {

    private lateinit var etNewEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnUpdateEmail: Button

    // Firebase Auth 實例
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_email)

        val btnBack: ImageView = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish() // 返回到上一頁
        }

        etNewEmail = findViewById(R.id.et_new_email)
        etPassword = findViewById(R.id.et_password)
        btnUpdateEmail = findViewById(R.id.btn_update_email)

        // 按下「更新 Email (不驗證)」
        btnUpdateEmail.setOnClickListener {
            val newEmail = etNewEmail.text.toString().trim()
            val password = etPassword.text.toString()

            // 基本輸入檢查
            if (!isValidEmail(newEmail)) {
                Toast.makeText(this, "請輸入有效的 Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "請輸入密碼", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 確認使用者是否登入
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "使用者未登入", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 檢查是否為 Email/Password 登入
            if (!isUserSignedInWithPassword(currentUser)) {
                Toast.makeText(this, "此帳號並非 Email/Password 登入，無法直接修改 Email", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // 1) re-authenticate → 2) updateEmail (不寄送驗證信)
            reAuthenticateUser(currentUser, password) { success ->
                if (!success) {
                    Toast.makeText(this, "重新驗證失敗，請確認舊密碼是否正確", Toast.LENGTH_SHORT).show()
                } else {
                    updateEmailDirectly(currentUser, newEmail)
                }
            }
        }
    }

    /**
     * 重新驗證使用者（舊密碼）
     */
    private fun reAuthenticateUser(user: FirebaseUser, password: String, callback: (Boolean) -> Unit) {
        val email = user.email
        if (email.isNullOrEmpty()) {
            callback(false)
            return
        }
        val credential = EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    /**
     * 直接更新 Email，不寄送驗證信
     */
    private fun updateEmailDirectly(user: FirebaseUser, newEmail: String) {
        user.updateEmail(newEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 更新成功
                    Toast.makeText(
                        this,
                        "Email 已成功更新為：$newEmail",
                        Toast.LENGTH_LONG
                    ).show()

                    // ***** 加上這行，更新完後立即返回上一頁 *****
                    finish()

                } else {
                    // 更新失敗
                    Toast.makeText(
                        this,
                        "更新 Email 失敗：${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    /**
     * 檢查字串是否為有效 Email
     */
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * 檢查使用者登入方式是否為 Email/Password
     */
    private fun isUserSignedInWithPassword(user: FirebaseUser): Boolean {
        return user.providerData.any { it.providerId == EmailAuthProvider.PROVIDER_ID }
    }
}
