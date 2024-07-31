package com.example.movie_app

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movie_app.DBManager
import com.example.movie_app.MainActivity
import com.example.movie_app.R
import com.example.movie_app.RegisterActivity

class LoginActivity : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var edtId: EditText
    lateinit var edtPassword: EditText
    lateinit var btnLogin: Button
    lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbManager = DBManager(this, "personnel", null, 1) // DBManager 초기화

        edtId = findViewById(R.id.edtID) // 아이디 입력 필드 초기화
        edtPassword = findViewById(R.id.edtPassword) // 비밀번호 입력 필드 초기화
        btnLogin = findViewById(R.id.btnLogin) // 로그인 버튼 초기화
        btnRegister = findViewById(R.id.btnRegister) // 회원가입 버튼 초기화

        btnLogin.setOnClickListener {
            val userId = edtId.text.toString()
            val password = edtPassword.text.toString()

            if (isLoginSuccessful(userId, password)) { // 로그인 성공 여부를 확인
                navigateToMainPage() // 메인 페이지로 이동
            } else {
                Toast.makeText(this, "아이디나 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent) // 회원가입 페이지로 이동
        }
    }

    @SuppressLint("Range")
    private fun isLoginSuccessful(userId: String, password: String): Boolean {
        sqlitedb = dbManager.readableDatabase
        val cursor = sqlitedb.rawQuery(
            "SELECT * FROM personnel WHERE id = ? AND password = ?",
            arrayOf(userId, password)
        )
        val loginSuccessful = cursor.count > 0 // 로그인 성공 여부를 판단

        if (loginSuccessful) {
            val editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit()
            if (cursor.moveToFirst()) {
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val gender = cursor.getString(cursor.getColumnIndex("gender"))
                editor.putString("userId", userId)
                editor.putString("userName", name)
                editor.putString("userGender", gender)
            }
            editor.apply() // 로그인 성공 시 사용자 정보를 SharedPreferences에 저장
        }

        cursor.close()
        sqlitedb.close()
        return loginSuccessful
    }

    fun navigateToMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 로그인 후에는 로그인 액티비티를 스택에서 제거
    }
}
