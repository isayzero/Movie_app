package com.example.movie_app

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var txtName: TextView
    lateinit var txtGender: TextView
    lateinit var txtId: TextView
    lateinit var txtEmail: TextView

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        txtName = findViewById(R.id.name) // 이름 텍스트 뷰 초기화
        txtGender = findViewById(R.id.gender) // 성별 텍스트 뷰 초기화
        txtId = findViewById(R.id.id) // ID 텍스트 뷰 초기화
        txtEmail = findViewById(R.id.email) // 이메일 텍스트 뷰 초기화

        // SharedPreferences에서 사용자 정보 읽기
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "No ID provided")
        val userName = sharedPreferences.getString("userName", "No Name")
        val userGender = sharedPreferences.getString("userGender", "No Gender")
        val userEmail = sharedPreferences.getString("userEmail", "No Email")

        Log.d("ProfileActivity", "UserId: $userId, UserName: $userName, UserGender: $userGender, UserEmail: $userEmail")

        txtId.text = userId // ID 설정
        txtName.text = userName // 이름 설정
        txtGender.text = userGender // 성별 설정
        txtEmail.text = userEmail // 이메일 설정
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent) // 홈으로 이동
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
