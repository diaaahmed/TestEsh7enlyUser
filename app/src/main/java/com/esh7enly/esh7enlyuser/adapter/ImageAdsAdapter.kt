package com.esh7enly.esh7enlyuser.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esh7enly.domain.entity.imageadsresponse.DataX

import com.esh7enly.esh7enlyuser.databinding.AdsSliderImageBinding
import com.esh7enly.esh7enlyuser.util.NetworkUtils
import com.esh7enly.esh7enlyuser.util.Utils


class ImageAdsAdapter(private val images:List<DataX>):
    RecyclerView.Adapter<ImageAdsAdapter.ImageAdsViewHolder>()
{

    class ImageAdsViewHolder(private var binding: AdsSliderImageBinding):
        RecyclerView.ViewHolder(binding.root)
    {
            fun bind(images: List<DataX>, position: Int) = with(binding)
            {

                Utils.displayImageOriginalFromCache(binding.root.context,imageViewAdsSlider,images[position].banner
                    ,
                    NetworkUtils.isConnectedWifi(binding.root.context))
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