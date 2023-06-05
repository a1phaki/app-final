package com.example.appFinal

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.res.ResourcesCompat
import com.example.appFinal.databinding.FragmentNotificationPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationPage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var  auth: FirebaseAuth
    private lateinit var  binding: FragmentNotificationPageBinding


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
        // Inflate the layout for this fragment using Data Binding
        binding = FragmentNotificationPageBinding.inflate(inflater, container, false)

        // Access the root view of the inflated layout
        val rootView: View = binding.root


        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        val database = Firebase.database("https://register-945ad-default-rtdb.asia-southeast1.firebasedatabase.app")
        val requestRef = database.getReference("friendRequest")
        val email = user?.email
        val emailAsNode = email?.replace(".", "_") //將user的email的.取代成＿（firebase的節點不能有.)
        val myRef = database.getReference("users")
        if (emailAsNode != null) {
            myRef.child(emailAsNode).child("name").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // 在這裡處理一次性數據讀取的邏輯
                    binding.username.text = dataSnapshot.value.toString()
                    val layout = rootView.findViewById<LinearLayout>(R.id.container)

                    requestRef.child(binding.username.text.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (childSnapshot in dataSnapshot.children) {
                                val senderName = childSnapshot.child("senderName").value as String

                                //顯示好友邀請
                                val senderNameTextView = TextView(requireContext())
                                senderNameTextView.text = senderName +" sent you a friend request."
                                senderNameTextView.setTextColor(Color.BLACK) // 設置文字顏色為黑色
                                val usernameTextSize = 20f // 設置使用者名稱的字體大小
                                senderNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, usernameTextSize)
                                val font = ResourcesCompat.getFont(requireContext(), R.font.roboto)
                                senderNameTextView.typeface = font
                                val usernameLayoutParams = LinearLayout.LayoutParams( //設置文字位置
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                usernameLayoutParams.setMargins(90, 20, 0, 30)
                                senderNameTextView.layoutParams = usernameLayoutParams

                                val buttonTextSize = 15f//按鈕內文字字體大小

                                //確認按鈕
                                val checkBtn = Button(requireContext())
                                checkBtn.text = "Confirm"
                                checkBtn.setBackgroundResource(R.drawable.oval_button_blue_bg)
                                checkBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonTextSize)
                                val checkBtnLayoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                checkBtnLayoutParams.setMargins(80, 10, 0, 0)
                                checkBtn.layoutParams = checkBtnLayoutParams

                                //確認後文字
                                val check = TextView(requireContext())
                                check.text = "Confirmed"
                                check.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                                check.setTextColor(Color.BLACK) // 設置文字顏色為黑色
                                check.typeface = font
                                val btntextLayoutParams = LinearLayout.LayoutParams( //設置文字位置
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                btntextLayoutParams.setMargins(100, 20, 0, 30)
                                check.layoutParams = btntextLayoutParams

                                //拒絕按鈕
                                val refuseBtn = Button(requireContext())
                                refuseBtn.text = "Delete"
                                refuseBtn.setBackgroundResource(R.drawable.oval_button_gray_bg)
                                refuseBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonTextSize)
                                val refuseBtnLayoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                refuseBtnLayoutParams.setMargins(40, 10, 0, 0) // 設置上邊距為 20 像素
                                refuseBtn.layoutParams = refuseBtnLayoutParams

                                //拒絕文字
                                val refuse = TextView(requireContext())
                                refuse.text = "Request removed"
                                refuse.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                                refuse.setTextColor(Color.BLACK) // 設置文字顏色為黑色
                                refuse.typeface = font
                                refuse.layoutParams = btntextLayoutParams

                                checkBtn.setOnClickListener{
                                    val friendRef = database.getReference("friend")
                                    friendRef.child(binding.username.text.toString()).child(senderName).setValue(senderName)
                                    friendRef.child(senderName).child(binding.username.text.toString()).setValue(binding.username.text.toString())
                                }

                                refuseBtn.setOnClickListener{
                                    requestRef.child(binding.username.text.toString()).child(senderName).removeValue()
                                }

                                // 按讚次數和按讚按鈕的容器
                                val btnContainer = LinearLayout(requireContext())
                                btnContainer.orientation = LinearLayout.HORIZONTAL
                                btnContainer.layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                btnContainer.addView(checkBtn)
                                btnContainer.addView(refuseBtn)

                                // 創建貼文區塊容器
                                val postContainer = LinearLayout(requireContext())
                                postContainer.orientation = LinearLayout.VERTICAL
                                postContainer.addView(senderNameTextView)
                                postContainer.addView(btnContainer)
                                postContainer.setBackgroundResource(R.drawable.post_bg) // 設定貼文區塊的背景

                                // 設定 TextView 的 LayoutParams
                                val params = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                params.height = 350 // 設定高度為 400 像素
                                params.setMargins(0, 10, 0, 10)
                                postContainer.layoutParams = params
                                postContainer.setPadding(0, 0, 0, 0) // 設置容器的內邊距

                                layout.addView(postContainer, 0) // 將貼文容器加入到指定的 LinearLayout 中並指定 index 為 0
                                checkBtn.setOnClickListener{
                                    val friendRef=database.getReference("friend")
                                    requestRef.child(binding.username.text.toString()).child(senderName).removeValue()
                                    friendRef.child(binding.username.text.toString()).child(senderName).setValue(senderName)
                                    friendRef.child(senderName).child(binding.username.text.toString()).setValue(binding.username.text.toString())
                                    layout.removeView(postContainer)
                                    btnContainer.removeView(checkBtn)
                                    btnContainer.removeView(refuseBtn)
                                    btnContainer.addView(check)
                                    layout.addView(postContainer)
                                }

                                refuseBtn.setOnClickListener{
                                    requestRef.child(binding.username.text.toString()).child(senderName).removeValue()
                                    layout.removeView(postContainer)
                                    btnContainer.removeView(checkBtn)
                                    btnContainer.removeView(refuseBtn)
                                    btnContainer.addView(refuse)
                                    layout.addView(postContainer)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            println("Failed to read value: ${error.message}")
                        }
                    })
                    // 在這裡使用獲得的值進行相應的操作
                    // 例如更新界面、處理數據等
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
         * @return A new instance of fragment NotificationPage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}