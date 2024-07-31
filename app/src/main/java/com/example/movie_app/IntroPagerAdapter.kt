package com.example.movie_app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class IntroPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 3 // 뷰페이저의 총 페이지 수는 3개이다.

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> IntroFragment.newInstance(
                "영화 개봉일을 \n 한 눈에 보고싶으신가요?",
                "모든 영화의 개봉일을\n 한 곳에서 확인할 수 있습니다.",
                R.drawable.calendar1
            ) // 첫 번째 페이지의 프래그먼트를 생성한다.
            1 -> IntroFragment.newInstance(
                "최신 영화를 빠르게 검색하고 \n 즐겨찾기하세요!",
                "최신 영화 정보를 빠르게 찾아보고, \n 나중에 볼 영화를 즐겨찾기 할 수 있습니다.",
                R.drawable.search
            ) // 두 번째 페이지의 프래그먼트를 생성한다.
            else -> IntroFragment.newInstance(
                "Film is SWU 시작하기",
                "지금 바로 Film is SWU를 시작해보세요!",
                R.drawable.logo
            ) // 그 외의 경우(세 번째 페이지)의 프래그먼트를 생성한다.
        }
    }
}
