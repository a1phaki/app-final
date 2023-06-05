package com.example.appFinal

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appFinal.databinding.ActivityAddFriendBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class AddFriend : AppCompatActivity() {

    // 初始化
    private lateinit var binding: ActivityAddFriendBinding
    private  lateinit var  auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val user = Firebase.auth.currentUser

        val email = user?.email
        val emailAsNode = email?.replace(".", "_") //將user的email的.取代成＿（firebase的節點不能有.)
        val database = FirebaseDatabase.getInstance("https://register-945ad-default-rtdb.asia-southeast1.firebasedatabase.app")
        val userRef = database.getReference("users")
        val myname=findViewById<TextView>(R.id.name)
        val usernameEditText = findViewById<EditText>(R.id.username)
        if (emailAsNode != null) {
            userRef.child(emailAsNode).child("name").addValueEventListener(object :ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // 在這裡處理數據讀取的邏輯
                    val name = dataSnapshot.value.toString()

                    myname.text = name.toString()

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 讀取數據取消時的處理邏輯
                }
            })
        }

        binding.sendBtn.setOnClickListener {
            val username = usernameEditText.text.toString()
            searchAndSendFriendRequest(username)
        }

        //返回起始畫面
        binding.backBtn.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }

    private fun searchAndSendFriendRequest(username: String) {
        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        val currentUserId=user?.uid
        val database = FirebaseDatabase.getInstance("https://register-945ad-default-rtdb.asia-southeast1.firebasedatabase.app")
        val friendRequestRef = database.getReference("friendRequest")
        val friendRef = database.getReference("friend")
        val userRef = database.getReference("users")
        val query = userRef.orderByChild("name").equalTo(username)
        val myname=findViewById<TextView>(R.id.name)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 使用者存在，顯示提示訊息
                    Toast.makeText(applicationContext, "找到使用者 $username", Toast.LENGTH_SHORT).show()


                    friendRequestRef.child(username).child(myname.text.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // 目標用戶已經收到相同的邀請，不允許重複發送
                                Toast.makeText(applicationContext, "已經發送過邀請給該用戶", Toast.LENGTH_SHORT).show()

                            } else {

                                friendRef.child(myname.text.toString()).child(username).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        if (dataSnapshot.exists()){

                                            Toast.makeText(applicationContext, "已經成為好友", Toast.LENGTH_SHORT).show()

                                        } else {
                                            if (currentUserId != null) {
                                                // 建立好友邀請的數據
                                                val friendRequestData = HashMap<String, Any>()
                                                friendRequestData["senderId"] = currentUserId
                                                friendRequestData["senderName"] = myname.text


                                                // 更新好友邀請
                                                val updateData = HashMap<String, Any>()
                                                updateData[myname.text.toString()] = friendRequestData

                                                friendRequestRef.child(username).updateChildren(updateData).addOnSuccessListener {
                                                    Toast.makeText(applicationContext, "已發送好友邀請", Toast.LENGTH_SHORT).show()
                                                }
                                                    .addOnFailureListener { error -> Toast.makeText(applicationContext, "無法發送好友邀請: ${error.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                    }
                                })

                                // 目標用戶尚未收到相同的邀請，可以發送邀請
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // 讀取數據取消時的處理邏輯
                        }
                    }
                    )
                }else{
                    Toast.makeText(applicationContext, "找不到使用者 $username", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // 讀取資料失敗，顯示錯誤訊息
                Toast.makeText(applicationContext, "資料庫錯誤: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}