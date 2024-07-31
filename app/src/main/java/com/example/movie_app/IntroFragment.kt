package com.example.movie_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

private const val ARG_TITLE = "title"
private const val ARG_DESCRIPTION = "description"
private const val ARG_IMAGE = "image"

class IntroFragment : Fragment() {

    private var title: String? = null
    private var description: String? = null
    private var imageResId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)// 프래그먼트의 제목을 설정하는 부분이다.
            description = it.getString(ARG_DESCRIPTION)// 프래그먼트의 설명을 설정하는 부분이다.
            imageResId = it.getInt(ARG_IMAGE)// 프래그먼트의 이미지를 설정하는 부분이다.
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트의 레이아웃을 인플레이트하는 부분이다.
        val view = inflater.inflate(R.layout.intro_slide, container, false)

        val slideTitle = view.findViewById<TextView>(R.id.slideTitle)// 제목 텍스트뷰를 초기화하는 부분이다.
        val slideDescription = view.findViewById<TextView>(R.id.slideDescription)// 설명 텍스트뷰를 초기화하는 부분이다.
        val slideImage = view.findViewById<ImageView>(R.id.slideImage)// 이미지뷰를 초기화하는 부분이다.

        slideTitle.text = title // 제목을 설정하는 부분이다.
        slideDescription.text = description // 설명을 설정하는 부분이다.
        slideImage.setImageResource(imageResId) // 이미지를 설정하는 부분이다.

        return view
    }

    companion object {
        // 새로운 인스턴스를 생성하는 메서드이다.
        fun newInstance(title: String, description: String, imageResId: Int): IntroFragment {
            val fragment = IntroFragment()
            val bundle = Bundle().apply {
                putString(ARG_TITLE, title)// 번들에 제목을 추가하는 부분이다.
                putString(ARG_DESCRIPTION, description)// 번들에 설명을 추가하는 부분이다.
                putInt(ARG_IMAGE, imageResId)// 번들에 이미지를 추가하는 부분이다.
            }
            fragment.arguments = bundle
            return fragment// 프래그먼트를 반환하는 부분이다.
        }
    }
}
