package com.esh7enly.esh7enlyuser.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.esh7enly.domain.entity.ImageAdsModel
import com.esh7enly.domain.entity.imageadsresponse.DataX

import com.esh7enly.esh7enlyuser.databinding.AdsSliderImageBinding
import com.esh7enly.esh7enlyuser.util.NetworkUtils
import com.esh7enly.esh7enlyuser.util.Utils


class ImageAdsAdapter(val viewPager2: ViewPager2, private val images:List<DataX>):
    RecyclerView.Adapter<ImageAdsAdapter.ImageAdsViewHolder>()
{

    class ImageAdsViewHolder(private var binding: AdsSliderImageBinding):
        RecyclerView.ViewHolder(binding.root)
    {
            fun bind(images: List<DataX>, position: Int) = with(binding)
            {

//                imageViewAdsSlider.setOnClickListener {
//                    Log.d("TAG", "diaa adsView click: ${images[position].adsLink}")
//                }

                Utils.displayImageOriginalFromCache(binding.root.context,imageViewAdsSlider,images[position].banner
                    ,
                    NetworkUtils.isConnectedWifi(binding.root.context))

//                Glide.with(binding.root.context)
//                    .load(images[position].banner)
//                    .into(imageViewAdsSlider)

            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdsViewHolder
    {
        return ImageAdsViewHolder(
            AdsSliderImageBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int  = images.size

    override fun onBindViewHolder(holder: ImageAdsViewHolder, position: Int)
    {
        holder.bind(images,position)
    }
}