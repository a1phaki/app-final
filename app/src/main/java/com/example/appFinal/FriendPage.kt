package com.example.appFinal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.appFinal.databinding.FragmentFriendPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FriendPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendPage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var  auth: FirebaseAuth
    private lateinit var  binding: FragmentFriendPageBinding

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
        binding = FragmentFriendPageBinding.inflate(inflater, container, false)

        // Access the root view of the inflated layout
        val rootView: View = binding.root

        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        val database = Firebase.database("https://register-945ad-default-rtdb.asia-southeast1.firebasedatabase.app")
        val friendRef = database.getReference("friend")
        val email = user?.email
        val emailAsNode = email?.replace(".", "_") //將user的email的.取代成＿（firebase的節點不能有.)
        val myRef = database.getReference("users")
        if (emailAsNode != null) {
            myRef.child(emailAsNode).child("name").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // 在這裡處理一次性數據讀取的邏輯
                    binding.username.text = dataSnapshot.value.toString()

                    val layout = rootView.findViewById<LinearLayout>(R.id.container)
                    friendRef.child(binding.username.text.toString()).addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (childSnapshot in dataSnapshot.children) {

                                val friendname = childSnapshot.value as? String

                                val friendnameTextView = TextView(requireContext())
                                friendnameTextView.text = friendname
                                friendnameTextView.setTextColor(Color.BLACK) // 設置文字顏色為黑色
                                val usernameTextSize = 25f // 設置使用者名稱的字體大小
                                friendnameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, usernameTextSize)
                                val font = ResourcesCompat.getFont(requireContext(), R.font.roboto)// 設置使用者名稱的字體型態
                                friendnameTextView.typeface = font
                                val usernameLayoutParams = LinearLayout.LayoutParams( //設置文字位置
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                usernameLayoutParams.setMargins(90, 30, 0, 30) // 設置上邊距為 20 像素
                                friendnameTextView.layoutParams = usernameLayoutParams

                                // 聊天室按鈕
                                val chatButton = ImageButton(requireContext())
                                chatButton.setImageResource(R.drawable.chat)
                                chatButton.setBackgroundResource(android.R.color.transparent)
                                val chatBtnLayoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                chatBtnLayoutParams.setMargins(600, 15, 0, 0) // 設置上邊距為 20 像素
                                chatButton.layoutParams = chatBtnLayoutParams

                                // 創建區塊容器
                                val postContainer = LinearLayout(requireContext())
                                postContainer.orientation = LinearLayout.HORIZONTAL
                                postContainer.setBackgroundResource(R.drawable.post_bg) // 設定貼文區塊的背景
                                postContainer.addView(friendnameTextView)
                                postContainer.addView(chatButton)

                                // 設定 TextView 的 LayoutParams
                                val params = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                params.height = 200 // 設定高度為 400 像素
                                params.setMargins(0, 10, 0, 10)
                                postContainer.layoutParams = params
                                postContainer.setPadding(0, 0, 0, 0) // 設置容器的內邊距

                                layout.addView(postContainer, 0) // 將貼文容器加入到指定的 LinearLayout 中並指定 index 為 0
                            }

                        }
                        override fun onCancelled(error: DatabaseError) {
                            println("Failed to read value: ${error.message}")
                        }
                    })
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 讀取數據取消時的處理邏輯
                }
                })
        }
        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FriendPage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FriendPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val friend = view.findViewById<ImageButton>(R.id.FriendBtn)
        // 设置点击事件等操作
        friend.setOnClickListener {
            val intent = Intent(requireContext(), AddFriend::class.java)
            startActivity(intent)
        }
    }
}