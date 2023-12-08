package com.esh7enly.esh7enlyuser.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esh7enly.esh7enlyuser.databinding.OnBoardingItemBinding

class OnBoardingItemAdapter(private var onBoardingItems:List<OnBoardingItem>):
    RecyclerView.Adapter<OnBoardingItemAdapter.onBoardingItemViewHolder>()
{
    inner class onBoardingItemViewHolder(private val binding:OnBoardingItemBinding)
        :RecyclerView.ViewHolder(binding.root)
    {

            fun bind(onBoardingItem: OnBoardingItem) = with(binding)
            {
                imageOnBoarding.setImageResource(onBoardingItem.onBoardingImage)
              //  txtTitle.text = onBoardingItem.onBoardingTitle
               // txtDescription.text = onBoardingItem.onBoardingDescription
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): onBoardingItemViewHolder {

        val itemBinding = OnBoardingItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false)
        return onBoardingItemViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = onBoardingItems.size

    override fun onBindViewHolder(holder: onBoardingItemViewHolder, position: Int)
    {
        holder.bind(onBoardingItems[position])
    }
}