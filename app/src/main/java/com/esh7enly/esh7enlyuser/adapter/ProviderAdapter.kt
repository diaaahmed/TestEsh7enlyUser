package com.esh7enly.esh7enlyuser.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.esh7enly.domain.entity.providersNew.ProviderData

import com.esh7enly.esh7enlyuser.click.ProviderClick
import com.esh7enly.esh7enlyuser.databinding.ProviderLayoutBinding
import com.esh7enly.esh7enlyuser.util.Constants


class ProviderAdapter (val click: ProviderClick) :
    ListAdapter<ProviderData, ProviderAdapter.ViewHolder>(ProviderDiffCallback()) {

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

        fun bind(provider: ProviderData) = with(itemBinding)
        {

            providerModel = provider

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

    class ProviderDiffCallback : DiffUtil.ItemCallback<ProviderData>()
    {
        override fun areItemsTheSame(
            oldItem: ProviderData,
            newItem: ProviderData
        ): Boolean {
            return oldItem.name_en == newItem.name_en
        }

        override fun areContentsTheSame(
            oldItem: ProviderData,
            newItem: ProviderData
        ): Boolean {
            return oldItem == newItem
        }
    }
}
