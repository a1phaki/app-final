package com.example.appFinal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomePage.newInstance] factory method to
 * create an instance of this fragment.
 */

class HomePage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var  auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun getRecentlyViewedPages(): List<String> {
        val jsonString = sharedPreferences.getString("recently_viewed_pages_list", "[]")
        return if (!jsonString.isNullOrEmpty()) {
            val typeToken = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(jsonString, typeToken)
        } else {
            emptyList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username=view.findViewById<TextView>(R.id.username)

        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        val database = Firebase.database("https://register-945ad-default-rtdb.asia-southeast1.firebasedatabase.app")
        val email = user?.email
        val emailAsNode = email?.replace(".", "_") //將user的email的.取代成＿（firebase的節點不能有.)
        val myRef = database.getReference("users")
        if (emailAsNode != null) {
            myRef.child(emailAsNode).child("name").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // 在這裡處理一次性數據讀取的邏輯
                    username.text = dataSnapshot.value.toString()

                    // 在這裡使用獲得的值進行相應的操作
                    // 例如更新界面、處理數據等
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // 讀取數據取消時的處理邏輯
                }
            })
        }
        val pages = getRecentlyViewedPages().toList()

        val imageView2 = view.findViewById<ImageView>(R.id.imageView2)
        imageView2.setOnClickListener {
            val imgname2 = "com.example.appFinal." + pages[2].capitalize()
            val intent = Intent(requireContext(), Class.forName(imgname2))
            startActivity(intent)
        }

        val imageView1 = view.findViewById<ImageView>(R.id.imageView1)
        imageView1.setOnClickListener {
            val imgname1 = "com.example.appFinal." + pages[1].capitalize()
            val intent = Intent(requireContext(), Class.forName(imgname1))
            startActivity(intent)
        }

        val imageView0 = view.findViewById<ImageView>(R.id.imageView0)
        imageView0.setOnClickListener {
            val imgname0 = "com.example.appFinal." + pages[0].capitalize()
            val intent = Intent(requireContext(), Class.forName(imgname0))
            startActivity(intent)
        }

        val logout = view.findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            auth.signOut()
            val intent =Intent(requireContext(),MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val pages = getRecentlyViewedPages().toList()
        updateTextViews(pages)
    }

    private fun updateTextViews(pages: List<String>) {
        if(pages.size==1){
            val imgname0 = pages[0]
            val resourceId0 = requireContext().resources.getIdentifier(imgname0, "drawable", requireContext().packageName)
            val imageView0 = requireView().findViewById<ImageView>(R.id.imageView0)
            imageView0.setImageResource(resourceId0)
        }
        if (pages.size==2){
            val imgname1 = pages[1]
            val resourceId1 = requireContext().resources.getIdentifier(imgname1, "drawable", requireContext().packageName)
            val imageView1 = requireView().findViewById<ImageView>(R.id.imageView1)
            imageView1.setImageResource(resourceId1)

            val imgname0 = pages[0]
            val resourceId0 = requireContext().resources.getIdentifier(imgname0, "drawable", requireContext().packageName)
            val imageView0 = requireView().findViewById<ImageView>(R.id.imageView0)
            imageView0.setImageResource(resourceId0)
        }
        if (pages.size == 3) {
            val imgname2 = pages[2]
            val resourceId2 = requireContext().resources.getIdentifier(imgname2, "drawable", requireContext().packageName)
            val imageView2 = requireView().findViewById<ImageView>(R.id.imageView2)
            imageView2.setImageResource(resourceId2)

            val imgname1 = pages[1]
            val resourceId1 = requireContext().resources.getIdentifier(imgname1, "drawable", requireContext().packageName)
            val imageView1 = requireView().findViewById<ImageView>(R.id.imageView1)
            imageView1.setImageResource(resourceId1)

            val imgname0 = pages[0]
            val resourceId0 = requireContext().resources.getIdentifier(imgname0, "drawable", requireContext().packageName)
            val imageView0 = requireView().findViewById<ImageView>(R.id.imageView0)
            imageView0.setImageResource(resourceId0)

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GamePage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomePage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}