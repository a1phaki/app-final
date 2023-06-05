package com.example.appFinal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GamePage.newInstance] factory method to
 * create an instance of this fragment.
 */
class GamePage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        return inflater.inflate(R.layout.fragment_game_page, container, false)
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
            GamePage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val zelda = view.findViewById<ImageView>(R.id.imageView1)
        val pokemon = view.findViewById<ImageView>(R.id.imageView2)
        val hogwarts = view.findViewById<ImageView>(R.id.imageView3)
        val godofwar = view.findViewById<ImageView>(R.id.imageView4)
        val eldenring = view.findViewById<ImageView>(R.id.imageView5)

        // 设置点击事件等操作
        zelda.setOnClickListener {
            val intent = Intent(requireContext(), Zelda::class.java)
            addPageToRecentlyViewed("zelda")
            startActivity(intent)
        }
        pokemon.setOnClickListener {
            val intent = Intent(requireContext(), Pokemon::class.java)
            addPageToRecentlyViewed("pokemon")
            startActivity(intent)
        }
        hogwarts.setOnClickListener {
            val intent = Intent(requireContext(), Hogwarts::class.java)
            addPageToRecentlyViewed("hogwarts")
            startActivity(intent)
        }
        godofwar.setOnClickListener {
            val intent = Intent(requireContext(), Godofwar::class.java)
            addPageToRecentlyViewed("godofwar")
            startActivity(intent)
        }
        eldenring.setOnClickListener {
            val intent = Intent(requireContext(), Eldenring::class.java)
            addPageToRecentlyViewed("eldenring")
            startActivity(intent)
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
    private fun addPageToRecentlyViewed(page: String) {
        // 读取已储存的页面名称列表
        val pages = getRecentlyViewedPages().toMutableList()
        println(pages)

        if(!pages.contains(page)){  //  判斷page是否已存在pages中
            pages.add(page)
        }

        if (pages.size > 3) {
            pages.removeAt(0)  // 限制列表大小为 3
        }
        // 保存更新后的页面名称列表
        val jsonString = Gson().toJson(pages)
        sharedPreferences.edit().putString("recently_viewed_pages_list", jsonString).apply()
        println(pages)
    }

}


