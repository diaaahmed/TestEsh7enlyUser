package com.esh7enly.esh7enlyuser.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.esh7enly.domain.entity.userservices.*
import com.esh7enly.esh7enlyuser.click.ProviderClick
import com.esh7enly.esh7enlyuser.databinding.ProviderLayoutBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.NetworkUtils
import com.esh7enly.esh7enlyuser.util.Utils

class ProviderAdapter (val click: ProviderClick) :
    ListAdapter<Provider, ProviderAdapter.ViewHolder>(ProviderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ProviderLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val itemBinding: ProviderLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        @SuppressLint("CheckResult")

        fun bind(provider: Provider) = with(itemBinding)
        {
            Utils.displayImageOriginalFromCache(root.context,categoryIcon,provider.logo,
            NetworkUtils.isConnectedWifi(root.context))

            if(Constants.LANG == Constants.AR)
            {
                tvTitle.text = provider.name_ar
            }
            else
            {
                tvTitle.text = provider.name_en
            }

            root.setOnClickListener {
                click.click(provider)
            }
        }
    }

    class ProviderDiffCallback : DiffUtil.ItemCallback<Provider>()
    {
        override fun areItemsTheSame(
            oldItem: Provider,
            newItem: Provider
        ): Boolean {
            return oldItem.name_en == newItem.name_en
        }

        override fun areContentsTheSame(
            oldItem: Provider,
            newItem: Provider
        ): Boolean {
            return oldItem == newItem
        }
    }
}