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
            title = it.getString(ARG_TITLE)
            description = it.getString(ARG_DESCRIPTION)
            imageResId = it.getInt(ARG_IMAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.intro_slide, container, false)

        val slideTitle = view.findViewById<TextView>(R.id.slideTitle)
        val slideDescription = view.findViewById<TextView>(R.id.slideDescription)
        val slideImage = view.findViewById<ImageView>(R.id.slideImage)

        slideTitle.text = title
        slideDescription.text = description
        slideImage.setImageResource(imageResId)

        return view
    }

    companion object {
        fun newInstance(title: String, description: String, imageResId: Int): IntroFragment {
            val fragment = IntroFragment()
            val bundle = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_DESCRIPTION, description)
                putInt(ARG_IMAGE, imageResId)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}
