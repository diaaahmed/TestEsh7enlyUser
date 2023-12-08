package com.esh7enly.esh7enlyuser.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterViewFlipper
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide

import com.esh7enly.esh7enlyuser.R


class FlipperAdapter(
    private var mCtx: Context?,
    private var ads: List<Int>,
    adapterViewFlipper: AdapterViewFlipper
) : BaseAdapter()
{
    private var adapterViewFlipper: AdapterViewFlipper? = adapterViewFlipper

    override fun getCount(): Int
    {

        return ads.size
    }

    override fun getItem(p0: Int): Any?
    {
        return null
    }

    override fun getItemId(p0: Int): Long
    {
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View
    {
        val inflater = LayoutInflater.from(mCtx)

        val view: View = inflater.inflate(R.layout.app_ads_layout, null)
        val rightBtn = view.findViewById<TextView>(R.id.right_btn)
        val leftB = view.findViewById<TextView>(R.id.left_btn)

        leftB.setOnClickListener {
            if(position == 0 )
            {
                return@setOnClickListener
            }
            else
            {
                adapterViewFlipper?.showPrevious()
            }
        }

        rightBtn.setOnClickListener {
            if(position == ads.last())
            {
                return@setOnClickListener
            }
            else
            {
                adapterViewFlipper?.showNext()
            }
        }

        val imageView = view.findViewById<ImageView>(R.id.imageView)

        Glide.with(mCtx!!)
            .load(ads[position])
            .into(imageView)

        return view
    }
}