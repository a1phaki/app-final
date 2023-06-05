package com.example.appFinal

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.example.appFinal.databinding.ActivityZeldaBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.ktx.database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Zelda : AppCompatActivity() {

    //初始化
    private lateinit var  auth: FirebaseAuth
    private lateinit var  binding: ActivityZeldaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityZeldaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.refreshBtn.setOnClickListener {
            recreate()
        }

        //回上一頁
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
        }

        //跳轉至討論區
        binding.chatBtn.setOnClickListener {
            startActivity(Intent(this, ZeldaChat::class.java))
        }

        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        val database = Firebase.database("https://register-945ad-default-rtdb.asia-southeast1.firebasedatabase.app")
        val userRef = database.getReference("Zelda")
        var i = 1
        var j = 0
        val email = user?.email
        val emailAsNode = email?.replace(".", "_") //將user的email的.取代成＿（firebase的節點不能有.)
        val myRef = database.getReference("users")
        if (emailAsNode != null) {
            myRef.child(emailAsNode).child("name").addListenerForSingleValueEvent(object :ValueEventListener{
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
        val sortedQuery = userRef.child("articles").orderByChild("number")
        sortedQuery.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val values = snapshot.value as? HashMap<String, Any>

                if (values != null) {
                    val content = values["content"] as? String // 紀錄文章內容
                    val username = values["username"] as? String // 紀錄username
                    val number = values["number"] as? Long
                    val date = values["date"] as? String // 記錄日期
                    val likes = values["likes"] as? Long // 記錄點贊次數

                    //文章區塊顯示容器
                    val contentContainer = LinearLayout(this@Zelda)
                    contentContainer.orientation = LinearLayout.VERTICAL
                    contentContainer.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    //顯示使用者名稱
                    val usernameTextView = TextView(this@Zelda)
                    usernameTextView.text = username
                    usernameTextView.setTextColor(Color.BLACK) // 設置文字顏色為黑色
                    val usernameTextSize = 25f // 設置使用者名稱的字體大小
                    usernameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, usernameTextSize)
                    val font = ResourcesCompat.getFont(this@Zelda, R.font.roboto)// 設置使用者名稱的字體型態
                    usernameTextView.typeface = font
                    contentContainer.addView(usernameTextView)
                    val usernameLayoutParams = LinearLayout.LayoutParams( //設置文字位置
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    usernameLayoutParams.setMargins(120, 20, 0, 0) // 設置上邊距為 20 像素
                    usernameTextView.layoutParams = usernameLayoutParams

                    // 文章內容
                    val contentTextView = TextView(this@Zelda)
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
                    contentLayoutParams.setMargins(80, 10, 0, 0) // 設置上邊距為 20 像素
                    contentTextView.layoutParams = contentLayoutParams

                    // 按讚次數和按讚按鈕的容器
                    val likesContainer = LinearLayout(this@Zelda)
                    likesContainer.orientation = LinearLayout.HORIZONTAL
                    likesContainer.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    // 按讚次數
                    val likeTextview = TextView(this@Zelda)
                    likeTextview.text = likes.toString()
                    likeTextview.setTextColor(Color.BLACK) // 設置文字顏色為黑色
                    val likeTextSize = 20f // 設置文章的字體大小
                    likeTextview.setTextSize(TypedValue.COMPLEX_UNIT_SP, likeTextSize)
                    likeTextview.typeface = font
                    // 設置 likeTextview 的其他屬性
                    val likeLayoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    likeLayoutParams.setMargins(10, 35, 0, 0) // 設置上邊距為 20 像素
                    likeTextview.layoutParams = likeLayoutParams

                    // 按讚按鈕
                    val likeButton = ImageButton(this@Zelda)
                    var isLiked = false
                    likeButton.setImageResource(R.drawable.like)
                    likeButton.setBackgroundResource(android.R.color.transparent)
                    val likeBtnLayoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    likeBtnLayoutParams.setMargins(80, 10, 0, 0) // 設置上邊距為 20 像素
                    likeButton.layoutParams = likeBtnLayoutParams

                    likesContainer.addView(likeButton)
                    likesContainer.addView(likeTextview)
                    val articleRef = userRef.child("articles").child("articles$i")

                    likeButton.setOnClickListener {
                        if (!isLiked) {
                            articleRef.runTransaction(object : Transaction.Handler {
                                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                                    val likes = mutableData.child("likes").getValue(Long::class.java) ?: 0
                                    mutableData.child("likes").value = likes + 1
                                    return Transaction.success(mutableData)
                                }

                                override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                                    if (databaseError != null) {
                                        // 处理事务过程中的错误
                                    } else if (committed) {
                                        // 事务成功提交，可以执行相关操作
                                        likeTextview.text = (likeTextview.text.toString().toInt() + 1).toString()
                                        isLiked = true
                                        likeButton.setImageResource(R.drawable.like_filled)

                                    } else {
                                        // 事务被中止，可以处理中止情况
                                    }
                                }
                            })
                        } else {
                            articleRef.runTransaction(object : Transaction.Handler {
                                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                                    val likes = mutableData.child("likes").getValue(Long::class.java) ?: 0
                                    mutableData.child("likes").value = likes - 1
                                    return Transaction.success(mutableData)
                                }

                                override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                                    if (databaseError != null) {
                                        // 处理事务过程中的错误
                                    } else if (committed) {
                                        // 事务成功提交，可以执行相关操作
                                        likeTextview.text = (likeTextview.text.toString().toInt() - 1).toString()
                                        isLiked = false
                                        likeButton.setImageResource(R.drawable.like)

                                    } else {
                                        // 事务被中止，可以处理中止情况
                                    }
                                }
                            })
                        }
                    }

                    //顯示發文時間
                    val dateTextView = TextView(this@Zelda)
                    dateTextView.text = date
                    dateTextView.setTextColor(Color.BLACK) // 設置文字顏色為黑色
                    val dateTextSize = 15f // 設置文章的字體大小
                    dateTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, dateTextSize)
                    dateTextView.typeface = font
                    // 設置 dateTextView 的其他屬性
                    likesContainer.addView(dateTextView)
                    val dateLayoutParams = LinearLayout.LayoutParams( //設置文字位置
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    dateLayoutParams.setMargins(500, 30, 0, 0) // 設置上邊距為 20 像素
                    dateTextView.layoutParams = dateLayoutParams

                    // 分隔線
                    val separatorView = View(this@Zelda)
                    val separatorParams = LinearLayout.LayoutParams(
                        1300,
                        1 // 設定分隔線高度為 1 像素
                    )
                    separatorParams.gravity = Gravity.CENTER_HORIZONTAL // 設定分隔線水平置中
                    separatorView.setBackgroundColor(Color.DKGRAY) // 設定分隔線顏色為黑色

                    // 創建貼文區塊容器
                    val postContainer = LinearLayout(this@Zelda)
                    postContainer.orientation = LinearLayout.VERTICAL
                    postContainer.addView(contentContainer)
                    postContainer.addView(separatorView, separatorParams)
                    postContainer.addView(likesContainer)
                    postContainer.setBackgroundResource(R.drawable.post_bg) // 設定貼文區塊的背景

                    // 設定 TextView 的 LayoutParams
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.height = 400 // 設定高度為 400 像素
                    params.setMargins(0, 10, 0, 10)
                    postContainer.layoutParams = params
                    postContainer.setPadding(0, 0, 0, 0) // 設置容器的內邊距

                    layout.addView(postContainer, 0) // 將貼文容器加入到指定的 LinearLayout 中並指定 index 為 0
                    i = i + 1 // 紀錄跑的迴圈次數
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // 尚未實作
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented") // 尚未實作
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented") // 尚未實作
            }

            override fun onCancelled(error: DatabaseError) {
                // 尚未實作
            }
        })

        val chatLayout = layoutInflater.inflate(R.layout.chat,null)
        val bottomSheet = chatLayout.findViewById<ConstraintLayout>(R.id.bottomsheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        val sendArticle = findViewById<ImageButton>(R.id.postBtn)
        val dialog = BottomSheetDialog(this)

        sendArticle.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            dialog.setContentView(chatLayout)
            dialog.show()
        }

        val close = chatLayout.findViewById<ImageView>(R.id.close)

        close.setOnClickListener {
            dialog.dismiss()
        }

        val send = chatLayout.findViewById<Button>(R.id.post)
        val text = chatLayout.findViewById<EditText>(R.id.sendarticle_true)

        // 設置送出按鈕的初始狀態為禁用
        send.isEnabled = false

        // 監聽輸入框文字變化事件
        text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 在文字變化前執行的邏輯，這裡不需要執行任何操作
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 在文字變化時執行的邏輯
                val inputText = s?.toString()?.trim() // 獲取輸入框文字並移除首尾空格

                // 檢查文字是否為空
                if (inputText != null) {
                    send.isEnabled = inputText.isNotEmpty()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // 在文字變化後執行的邏輯，這裡不需要執行任何操作
            }
        })

        // 送出按鈕點擊事件
        send.setOnClickListener{
            val article = text.text.toString()
            val currentTime = Date()

            // 設定時間的格式
            val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

            // 將時間格式化成字串
            val formattedTime = dateFormat.format(currentTime)

            //設定articles為一整組的資料email,number,content,date
            val articles = HashMap<String, Any>()
            articles["username"] = binding.username.text.toString()
            articles["number"] = i
            articles["content"] = article
            articles["date"] = formattedTime
            articles["likes"] = j

            //在articles的節點下加入子節點articles"i"並設定值為articles
            userRef.child("articles").child("articles"+i).setValue(articles)

            // 清空輸入框
            text.text.clear()
        }
    }
}