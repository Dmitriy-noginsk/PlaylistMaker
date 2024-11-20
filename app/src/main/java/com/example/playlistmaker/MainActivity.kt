package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
//import android.widget.Button
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }
}

class LibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
    }
}

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<MaterialButton>(R.id.btn_search).setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btn_library).setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btn_settings).setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}


//val btnSearch = findViewById<MaterialButton>(R.id.btn_search)
//val btnLibrary = findViewById<MaterialButton>(R.id.btn_library)
//val btnSettings = findViewById<MaterialButton>(R.id.btn_settings)

//val buttonClickListener = object : View.OnClickListener {
//override fun onClick(v: View?) {
//when (v?.id) {
//R.id.btn_search -> {
//Toast.makeText(this@MainActivity, "Нажали на Поиск!", Toast.LENGTH_SHORT).show()
//}
//R.id.btn_library -> {
//Toast.makeText(this@MainActivity, "Нажали на Библиотеку!", Toast.LENGTH_SHORT).show()
//}
// R.id.btn_settings -> {
//Toast.makeText(this@MainActivity, "Нажали на Настройки!", Toast.LENGTH_SHORT).show()
// }
//}
//}
//}

//btnSearch.setOnClickListener(buttonClickListener)
//btnLibrary.setOnClickListener(buttonClickListener)
//btnSettings.setOnClickListener(buttonClickListener)
//}


//override fun onCreate(savedInstanceState: Bundle?) {
//super.onCreate(savedInstanceState)
//setContentView(R.layout.activity_main)

//findViewById<Button>(R.id.btn_settings).setOnClickListener {
//startActivity(Intent(this, SettingActivity::class.java))
//}
//}
