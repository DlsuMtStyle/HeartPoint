package com.example.heartpoint

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class EditUserInfoActivity : AppCompatActivity() {

    private lateinit var tvEditEmail: TextView
    private lateinit var tvEditPassword: TextView
    private lateinit var tvDeleteUser: TextView

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_info)

        // 初始化視圖
        tvEditEmail = findViewById(R.id.tv_edit_email)
        tvEditPassword = findViewById(R.id.tv_edit_pw)
        tvDeleteUser = findViewById(R.id.tv_delete_user)

        val btnBack: ImageView = findViewById(R.id.iv_back)
        btnBack.setOnClickListener {
            finish() // 返回到上一頁
        }

        // 點擊修改 Email
        tvEditEmail.setOnClickListener {
            val intent = Intent(this, EditEmailActivity::class.java)
            startActivity(intent)
        }

        // 點擊修改密碼
        tvEditPassword.setOnClickListener {
            val intent = Intent(this, EditPasswordActivity::class.java)
            startActivity(intent)
        }

        // 點擊刪除帳號
        tvDeleteUser.setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    private fun showDeleteAccountDialog() {
        val currentUser: FirebaseUser? = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "使用者未登入，無法刪除帳號", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("刪除帳號")
            .setMessage("您確定要刪除帳號嗎？此操作無法復原。")
            .setPositiveButton("確認刪除") { _, _ ->
                deleteUserAccount(currentUser)
            }
            .setNegativeButton("取消", null)
            .create()

        dialog.show()
    }

    private fun deleteUserAccount(user: FirebaseUser) {
        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "帳號已成功刪除", Toast.LENGTH_SHORT).show()
                    // 跳轉至登入註冊頁面
                    val intent = Intent(this, LoginActivity::class.java) // 替換為登入註冊頁面的 Activity
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "刪除帳號失敗，請稍後再試"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

}
