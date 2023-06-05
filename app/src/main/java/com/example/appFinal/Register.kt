package com.example.appFinal

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.appFinal.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class Register : AppCompatActivity() {

    //初始化變數
    private lateinit var phoneNumberET : EditText
    private lateinit var mAuth : FirebaseAuth
    private lateinit var number : String
    private lateinit var otp: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var date: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //返回起始畫面
        binding.backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //呼叫id
        phoneNumberET = findViewById(R.id.phoneEditTextNumber)
        mAuth = FirebaseAuth.getInstance()
        val database = Firebase.database("https://register-945ad-default-rtdb.asia-southeast1.firebasedatabase.app")
        val myRef = database.getReference("users")
        date = findViewById(R.id.Date)

        //跳轉至登入畫面
        binding.textview7.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth

        //OTPBtn點擊事件
        binding.sendOTPBtn.setOnClickListener {
            number = phoneNumberET.text.trim().toString() //獲取電話號碼
            if(number.isNotEmpty()){
                if (number.length == 10){
                    number = "+886$number"

                    //寄送OTP
                    startPhoneNumberVerification(number)

                    // 設定 resendTextView 的可見性為可見
                    binding.resendTextView.visibility = View.VISIBLE
                }else{
                    Snackbar.make(binding.root, "Please enter correct phone number", Snackbar.LENGTH_LONG).show() //phone格式不正確，顯示錯誤訊息給使用者
                }
            }else{
                Snackbar.make(binding.root, "Please enter phone number", Snackbar.LENGTH_LONG).show() //沒輸入phone，顯示錯誤訊息給使用者
            }
        }

        //再次寄送OTP
        binding.resendTextView.setOnClickListener{
            resendVerificationCode()
        }

        //取得當前日期
        val currentDate = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = formatter.format(currentDate.time)
        date.setText(dateString)

        //點擊輸入生日
        binding.Date.setOnClickListener{
            val year = currentDate.get(Calendar.YEAR)
            val month = currentDate.get(Calendar.MONTH)
            val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

            val datePickerDialog = DatePickerDialog(
                this@Register,
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, selectedYear)
                    selectedDate.set(Calendar.MONTH, selectedMonth)
                    selectedDate.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)

                    // 檢查生日是否合理
                    if (isDateValid(selectedDate)) {
                        val selectedDateString = formatter.format(selectedDate.time)
                        date.setText(selectedDateString)

                        // 将焦点转移到下一个EditText
                        binding.phoneEditTextNumber.requestFocus()
                    } else {
                        // 顯示錯誤訊息給使用者
                        Snackbar.make(binding.root, "Please enter correct birthday", Snackbar.LENGTH_LONG).show()
                    }
                },
                year, month, dayOfMonth
            )
            datePickerDialog.show()
        }

        //輸入密碼時可切換可見性
        val passwordET: EditText = findViewById(R.id.password)
        val visibilityImageView: ImageView = findViewById(R.id.visible)

        // 初始設定密碼欄位的輸入類型為「密碼文字」
        passwordET.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        passwordET.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.visible.setOnClickListener {
            val currentTransformationMethod = passwordET.transformationMethod
            if (currentTransformationMethod is PasswordTransformationMethod) {
                // 切換為可見文字
                passwordET.transformationMethod = HideReturnsTransformationMethod.getInstance()
                visibilityImageView.setImageResource(R.drawable.visible)
            } else {
                // 切換為密碼文字
                passwordET.transformationMethod = PasswordTransformationMethod.getInstance()
                visibilityImageView.setImageResource(R.drawable.invisible)
            }
            // 移動光標到最後
            passwordET.setSelection(passwordET.text.length)
        }

        //跳轉editText焦點
        binding.email.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }
        binding.password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }
        binding.username.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }
        binding.phoneEditTextNumber.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }

        //按下註冊按鈕後取得email and password
        binding.RegisterBtn.setOnClickListener{
            val typedOTP = binding.otpEditTextNumber.text.toString()
            val email = binding.email.text.toString().toLowerCase()
            val password = binding.password.text.toString()
            val number = phoneNumberET.text.trim().toString()
            val date = binding.Date.text.toString()
            val name = binding.username.text.toString()

            val users = HashMap<String, Any>()
            users["name"] = name
            users["phone"] = number
            users["date"] = date
            val emailAsNode = email?.replace(".", "_")//將user的email的.取代成＿（firebase的節點不能有.)

            // 檢查是否輸入email
            if (email.isEmpty()) {
                Snackbar.make(binding.root, "Please enter email", Snackbar.LENGTH_LONG).show() //沒輸入email，顯示錯誤訊息給使用者
                return@setOnClickListener
            }

            // 檢查使用者email格式正確
            if (!isEmailValid(email)) {
                Snackbar.make(binding.root, "Please enter correct email", Snackbar.LENGTH_LONG).show() //email格式不正確，顯示錯誤訊息給使用者
                return@setOnClickListener
            }

            // 檢查是否輸入密碼
            if (password.isEmpty()) {
                Snackbar.make(binding.root, "Please enter password", Snackbar.LENGTH_LONG).show() //沒輸入password，顯示錯誤訊息給使用者
                return@setOnClickListener
            }

            // 檢查使用者password格式正確
            if (!isPasswordValid(password)) {
                Snackbar.make(binding.root, "Please ensure that your password contains a minimum of 8 characters, including at least one uppercase letter, one lowercase letter, and one digit", Snackbar.LENGTH_LONG).show() //password格式不正確，顯示錯誤訊息給使用者
                return@setOnClickListener
            }

            // 檢查是否輸入使用者名稱
            if (name.isEmpty()) {
                Snackbar.make(binding.root, "Please enter user name", Snackbar.LENGTH_LONG).show() //沒輸入user name，顯示錯誤訊息給使用者
                return@setOnClickListener
            }

            // 檢查使用者名稱是否重複
            findUserByName(name) { isUserExists ->
                if (isUserExists) {
                    Snackbar.make(binding.root, "User name already exists", Snackbar.LENGTH_LONG).show()
                } else {
                    // 執行相應的邏輯
                }
            }

            // 檢查是否輸入電話號碼
            if (number.isEmpty()) {
                Snackbar.make(binding.root, "Please enter phone number", Snackbar.LENGTH_LONG).show() //沒輸入phone，顯示錯誤訊息給使用者
                return@setOnClickListener
            }

            //檢查電話號碼格式正確
            if (number.length != 10) {
                Snackbar.make(binding.root, "Please enter correct phone number", Snackbar.LENGTH_LONG).show() //phone格式不正確，顯示錯誤訊息給使用者
                return@setOnClickListener
            }

            //檢查OTP格式正確
            if(typedOTP.length == 6){
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(otp, typedOTP)
                signInWithPhoneAuthCredential(credential)

                //auth內有一個create user using email and password 的函數
                auth.createUserWithEmailAndPassword(email,password) //放入email and password
                    .addOnCompleteListener{

                        //判斷是否成功放入
                        if(it.isSuccessful){
                            Snackbar.make(binding.root, "Register successfully", Snackbar.LENGTH_LONG).show() //顯示註冊成功
                            if (emailAsNode != null) {
                                myRef.child(emailAsNode).setValue(users)
                            }
                            finish()
                        }else{
                            Snackbar.make(binding.root, "Register failed", Snackbar.LENGTH_LONG).show() //顯示註冊失敗
                        }
                    }
            }else{
                Snackbar.make(binding.root, "Please enter correct OTP", Snackbar.LENGTH_LONG).show() //OTP格式不正確，顯示錯誤訊息給使用者
            }
        }
    }

    //檢查email格式函式
    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    //檢查password是否滿足大寫、小寫字母與數字同時存在且需要八碼
    private fun isPasswordValid(password: String): Boolean {
        val pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$".toRegex()
        return password.matches(pattern)
    }

    private fun findUserByName(name: String, callback: (Boolean) -> Unit) {
        val database = Firebase.database("https://register-945ad-default-rtdb.asia-southeast1.firebasedatabase.app")
        val userRef = database.getReference("users")
        val query = userRef.orderByChild("name").equalTo(name)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val isUserExists = dataSnapshot.exists()
                callback(isUserExists)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 讀取資料失敗，顯示錯誤訊息
                Toast.makeText(applicationContext, "資料庫錯誤: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 檢查生日是否合理的函式
    private fun isDateValid(selectedDate: Calendar): Boolean {
        val currentDate = Calendar.getInstance()

        // 檢查日期是否在有效範圍內（例如限制在1900年至今）
        val minDate = Calendar.getInstance()
        minDate.set(1900, 0, 1)
        val maxDate = Calendar.getInstance()
        if (selectedDate.before(minDate) || selectedDate.after(maxDate)) {
            return false
        }
        // 檢查生日是否未來日期
        if (selectedDate.after(currentDate)) {
            return false
        }
        // 可以加入其他檢查項目，例如特殊日期條件或格式檢查
        return true
    }

    //隱藏鍵盤
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.username.windowToken, 0)
    }

    //寄送OTP函式
    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    //重新寄送OTP函式
    private fun resendVerificationCode(){
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this , "Authenticate Successfully" , Toast.LENGTH_SHORT).show()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    //回條函數
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        //驗證完成
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        //驗證失敗
        override fun onVerificationFailed(e: FirebaseException) {

            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    Log.d("TAG", "onVerificationFailed: $e") // 無效請求
                    Snackbar.make(binding.root, "Invalid verification request", Snackbar.LENGTH_LONG).show() //顯示錯誤訊息給使用者
                }
                is FirebaseTooManyRequestsException -> {
                    Log.d("TAG", "onVerificationFailed: $e") // 配額已用完
                    Snackbar.make(binding.root, "SMS quota exceeded", Snackbar.LENGTH_LONG).show() //顯示錯誤訊息給使用者
                }
                else -> {
                    Log.d("TAG", "onVerificationFailed: $e") // 其他驗證失敗情況
                    Snackbar.make(binding.root, "Verification failed", Snackbar.LENGTH_LONG).show() //顯示錯誤訊息給使用者
                }
            }
            // 在此处执行其他逻辑，例如重新设置UI
            // ...
        }

        //發送代碼
        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            otp = verificationId
            resendToken = token
        }
    }
}