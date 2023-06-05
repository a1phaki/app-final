package com.example.appFinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.appFinal.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignIn.setSize(SignInButton.SIZE_ICON_ONLY)

        binding.buttonSignIn.setOnClickListener {
            signIn()
        }

        binding.RegisterBtn.setOnClickListener{
            startActivity(Intent(this, Register::class.java))
        }

       binding.LoginBtn.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
        }
    }
    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("422298058550-ifrkk2i9sa3q77mvb2oiq639h1vt5sd8.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val email = account?.email
                val name = account?.displayName
                binding.name.text=name
                binding.email.text=email
                val users = HashMap<String, Any>()
                users["name"] = binding.name.text.toString()
                users["phone"] = ""
                users["date"] = ""
                val database = Firebase.database("https://login-4a224-default-rtdb.firebaseio.com")
                val myRef = database.getReference("users")
                val emailAsNode = email?.replace(".", "_")//將user的email的.取代成＿（firebase的節點不能有.)
                Toast.makeText(this, emailAsNode, Toast.LENGTH_SHORT).show()
                if (emailAsNode != null) {
                    myRef.child(emailAsNode).setValue(users)
                }

                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
            } catch (e: ApiException) {
                Log.i("givemepass", "signInResult:failed code=" + e.statusCode)
                Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.i("givemepass", "login fail")
            Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val RC_SIGN_IN = 100
    }
}