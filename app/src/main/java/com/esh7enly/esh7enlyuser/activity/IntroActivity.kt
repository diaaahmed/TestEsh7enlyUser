package com.esh7enly.esh7enlyuser.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.adapter.IntroAdapter
import com.esh7enly.esh7enlyuser.adapter.OnBoardingItem
import com.esh7enly.esh7enlyuser.adapter.OnBoardingItemAdapter
import com.esh7enly.esh7enlyuser.databinding.ActivityIntroBinding
import com.esh7enly.esh7enlyuser.util.NavigateToActivity

class IntroActivity : AppCompatActivity()
{
    private val ui by lazy{
        ActivityIntroBinding.inflate(layoutInflater)
    }

    private lateinit var onBoardingItemAdapter: OnBoardingItemAdapter

    private lateinit var pagerAdapter: IntroAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        ui.imageNext.setOnClickListener {

            if(ui.viewPager.currentItem +1 < onBoardingItemAdapter.itemCount)
            {
                ui.viewPager.currentItem +=1
            }
            else
            {
                NavigateToActivity.navigateToIntroCreateAccountActivity(this)
            }
        }

       setOnBoardingItems()
    }

    private fun setOnBoardingItems()
    {
        val listImages =  listOf(
            OnBoardingItem(R.drawable.onboard_onboard1),
            OnBoardingItem(R.drawable.onboard_onboard2),
            OnBoardingItem(R.drawable.onboard_onboard3),
        )


        pagerAdapter = IntroAdapter(this,listImages)

        onBoardingItemAdapter = OnBoardingItemAdapter(
            listOf(
                OnBoardingItem(R.drawable.onboard_onboard1),
                OnBoardingItem(R.drawable.onboard_onboard2),
                OnBoardingItem(R.drawable.onboard_onboard3),
            )
        )

       // ui.viewPager.adapter = onBoardingItemAdapter
       ui.viewPager.adapter = pagerAdapter
    }
}