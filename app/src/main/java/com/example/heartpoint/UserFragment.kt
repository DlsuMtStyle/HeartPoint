package com.example.heartpoint

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 獲取 TextView
        val tvUsername = view.findViewById<TextView>(R.id.tv_username)

        // 獲取當前用戶 Email 並顯示
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val email = currentUser.email
            if (!email.isNullOrEmpty()) {
                tvUsername.text = email // 顯示 Email
            } else {
                tvUsername.text = "無法獲取 Email"
            }
        } else {
            tvUsername.text = "用戶未登錄"
        }

        val editIcon = view.findViewById<ImageView>(R.id.img_edit_username)
        editIcon.setOnClickListener {
            val intent = Intent(requireContext(), EditUserInfoActivity::class.java)
            startActivity(intent)
        }

        // 初始化登出按鈕
        val btnLogout: TextView = view.findViewById(R.id.tv_logout)

        btnLogout.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            auth.signOut() // 執行登出操作

            // 跳轉至登入頁面
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(requireContext(), "您已成功登出", Toast.LENGTH_SHORT).show()
        }

        // 初始化禮物盒按鈕
        val btnGift: TextView = view.findViewById(R.id.tv_gift_box)
        btnGift.setOnClickListener {
            val intent = Intent(requireContext(), GiftActivity::class.java)
            startActivity(intent) // 跳轉到禮物盒介面
        }

        // 初始化禮物兌換紀錄按鈕
        val btnRedeemRecord: TextView = view.findViewById(R.id.tv_exchange_history)
        btnRedeemRecord.setOnClickListener {
            val intent = Intent(requireContext(), GiftRedeemRecordActivity::class.java)
            startActivity(intent) // 跳轉到禮物盒介面
        }

        // 初始化活動報名紀錄按鈕
        val btnEventApplyRecord: TextView = view.findViewById(R.id.tv_event_registration_history)
        btnEventApplyRecord.setOnClickListener {
            val intent = Intent(requireContext(), EventRecordActivity::class.java)
            startActivity(intent) // 跳轉到禮物盒介面
        }
    }
}