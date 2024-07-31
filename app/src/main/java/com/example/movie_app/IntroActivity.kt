package com.example.movie_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.movie_app.LoginActivity
import com.example.movie_app.R

class IntroActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnSkip: Button
    private lateinit var btnNext: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        viewPager = findViewById(R.id.viewPager)// ViewPager2 초기화
        btnSkip = findViewById(R.id.btnSkip)// Skip 버튼 초기화
        btnNext = findViewById(R.id.btnNext)// Next 버튼 초기화

        viewPager.adapter = IntroPagerAdapter(this)// ViewPager2에 어댑터 설정

        btnSkip.setOnClickListener {
            navigateToLogin()// Skip 버튼 클릭 시 로그인 화면으로 이동
        }

        btnNext.setOnClickListener {
            if (viewPager.currentItem < 2) {
                viewPager.currentItem = viewPager.currentItem + 1// 다음 페이지로 이동
            } else {
                navigateToLogin()// 마지막 페이지에서 로그인 화면으로 이동
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()// IntroActivity를 종료하여 뒤로 가기 시 다시 나타나지 않도록 한다.
    }
}
