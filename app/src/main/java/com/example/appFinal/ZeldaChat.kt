package com.example.appFinal

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewTreeObserver
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.example.appFinal.databinding.ActivityZeldaChatBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ZeldaChat : AppCompatActivity() {

    //初始化
    private  lateinit var  auth: FirebaseAuth
    private  lateinit var  binding: ActivityZeldaChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityZeldaChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        val sendBtn = findViewById<ImageButton>(R.id.sendBtn)
        val database = Firebase.database("https://register-945ad-default-rtdb.asia-southeast1.firebasedatabase.app")
        val userRef = database.getReference("Zelda")
        var i = 1
        val email = user?.email
        val emailAsNode = email?.replace(".", "_")
        val myRef = database.getReference("users")
        if (emailAsNode != null) {
            myRef.child(emailAsNode).child("name").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // 在這裡處理一次性數據讀取的邏輯
                    binding.username.text = dataSnapshot.value.toString()

                    // 在這裡使用獲得的值進行相應的操作
                    // 例如更新界面、處理數據等
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // 讀取數據取消時的處理邏輯
                }
            })
        }

        val layout = findViewById<LinearLayout>(R.id.container)
        val sortedQuery = userRef.child("chats").orderByChild("number")

        sortedQuery.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val values = snapshot.value as? HashMap<String, Any>

                if (values != null) {

                    val content = values["content"] as? String // 紀錄文章內容
                    val username = values["username"] as? String // 紀錄username
                    val number = values["number"] as? Long
                    val date = values["date"] as? String // 記錄日期

                    // 使用者名稱和日期的容器
                    val nameContainer = LinearLayout(this@ZeldaChat)
                    nameContainer.orientation = LinearLayout.HORIZONTAL
                    nameContainer.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    //顯示使用者名稱
                    val usernameTextView = TextView(this@ZeldaChat)
                    usernameTextView.text = username
                    usernameTextView.setTextColor(Color.BLACK) // 設置文字顏色為黑色
                    val usernameTextSize = 20f // 設置使用者名稱的字體大小
                    usernameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, usernameTextSize)
                    val font = ResourcesCompat.getFont(this@ZeldaChat, R.font.roboto)// 設置使用者名稱的字體型態
                    usernameTextView.typeface = font
                    val usernameLayoutParams = LinearLayout.LayoutParams( //設置文字位置
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    usernameLayoutParams.setMargins(100, 10, 0, 0) // 設置上邊距為 20 像素
                    usernameTextView.layoutParams = usernameLayoutParams

                    //顯示發文時間
                    val dateTextView = TextView(this@ZeldaChat)
                    dateTextView.text = date
                    dateTextView.setTextColor(Color.BLACK) // 設置文字顏色為黑色
                    val dateTextSize = 10f // 設置文章的字體大小
                    dateTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, dateTextSize)
                    dateTextView.typeface = font
                    // 設置 dateTextView 的其他屬性
                    val dateLayoutParams = LinearLayout.LayoutParams( //設置文字位置
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    dateLayoutParams.setMargins(70, 5, 0, 0) // 設置上邊距為 20 像素
                    dateTextView.layoutParams = dateLayoutParams

                    nameContainer.addView(usernameTextView)
                    nameContainer.addView(dateTextView)

                    // 顯示訊息區塊的容器
                    val contentContainer = LinearLayout(this@ZeldaChat)
                    contentContainer.orientation = LinearLayout.VERTICAL
                    contentContainer.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    // 訊息內容
                    val contentTextView = TextView(this@ZeldaChat)
                    contentTextView.text = content
                    contentTextView.setTextColor(Color.BLACK) // 設置文字顏色為黑色
                    val contentTextSize = 20f // 設置文章的字體大小
                    contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentTextSize)
                    contentTextView.typeface = font
                    // 設置 contentTextView 的其他屬性
                    contentContainer.addView(contentTextView)
                    val contentLayoutParams = LinearLayout.LayoutParams( //設置文字位置
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    contentLayoutParams.setMargins(100, 10, 0, 0) // 設置上邊距為 20 像素
                    contentTextView.layoutParams = contentLayoutParams

                    // 創建訊息區塊容器
                    val postContainer = LinearLayout(this@ZeldaChat)
                    postContainer.orientation = LinearLayout.VERTICAL
                    postContainer.addView(nameContainer)
                    postContainer.addView(contentContainer)

                    // 設定 TextView 的 LayoutParams
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.height = 250 // 設定高度為 400 像素
                    params.setMargins(0, 10, 0, 10)
                    postContainer.layoutParams = params
                    postContainer.setPadding(0, 0, 0, 0) // 設置容器的內邊距

                    layout.addView(postContainer) // 將貼文容器加入到指定的 LinearLayout 中並指定 index 為 0
                    i = i + 1 // 紀錄跑的迴圈次數
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        //送出按鈕點擊事件
        sendBtn.setOnClickListener{
            val text = findViewById<EditText>(R.id.sendChat)
            val chat = text.text.toString()
            val currentTime = Date()
            val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()) // 設定時間的格式
            val formattedTime = dateFormat.format(currentTime) // 將時間格式化成字串

            if(chat.isNotEmpty()){
                //設定articles為一整組的資料email,number,content,date
                val chats = HashMap<String, Any>()
                chats["username"] = binding.username.text.toString()
                chats["number"] = i
                chats["content"] = chat
                chats["date"] = formattedTime

                //在articles的節點下加入子節點articles"i"並設定值為articles
                userRef.child("chats").child("chat"+i).setValue(chats)

                // 清空輸入框
                text.text.clear()
            }
        }

        //回上一頁
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, Zelda::class.java))
        }

        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        scrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                // 確保滾動視窗已經完全加載後再進行滾動
                scrollView.post {

                    // 滾動視窗到最底部
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                }
            }
        })
    }
}