package com.example.heartpoint

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.et_email)
        val passwordEditText = findViewById<EditText>(R.id.et_password)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val registerButton = findViewById<Button>(R.id.btn_register)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "請填寫所有欄位", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(email, password)
            } else {
                Toast.makeText(this, "請填寫所有欄位", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "登入成功！", Toast.LENGTH_SHORT).show()

                    // 跳轉到主頁 (MainActivity)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // 結束當前的 LoginActivity，防止用戶按返回鍵返回到登入頁面
                } else {
                    Toast.makeText(this, "登入失敗：${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // 新增用戶文件到 Firestore
                        addUserToFirestore(userId)
                    } else {
                        Toast.makeText(this, "註冊成功，但無法獲取用戶 ID", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "註冊失敗：${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToFirestore(userId: String) {
        val userData = mapOf(
            "point" to 30, // 預設點數為 30
            "createdAt" to System.currentTimeMillis() // 記錄註冊時間
        )

        db.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "註冊成功，請重新登入", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "創建用戶資料失敗：${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
