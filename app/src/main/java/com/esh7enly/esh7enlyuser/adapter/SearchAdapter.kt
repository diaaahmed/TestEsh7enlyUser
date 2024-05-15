package com.esh7enly.esh7enlyuser.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.esh7enly.domain.entity.searchresponse.SearchData
import com.esh7enly.domain.entity.userservices.*
import com.esh7enly.esh7enlyuser.click.SearchClick
import com.esh7enly.esh7enlyuser.databinding.ServiceLayoutBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.NetworkUtils
import com.esh7enly.esh7enlyuser.util.Utils

class SearchAdapter (val click: SearchClick) :
    ListAdapter<SearchData, SearchAdapter.ViewHolder>(ServiceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ServiceLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val itemBinding: ServiceLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        @SuppressLint("CheckResult")

        fun bind(service: SearchData) = with(itemBinding)
        {
            Utils.displayImageOriginalFromCache(root.context,categoryIcon,service.icon,
                NetworkUtils.isConnectedWifi(root.context))


            if(Constants.LANG == Constants.AR)
            {
                tvTitle.text = service.name_ar
            }
            else
            {
                tvTitle.text = service.name_en
            }

            root.setOnClickListener {
                click.click(service)
            }

        }
    }

    class ServiceDiffCallback : DiffUtil.ItemCallback<SearchData>()
    {
        override fun areItemsTheSame(
            oldItem: SearchData,
            newItem: SearchData
        ): Boolean {
            return oldItem.name_en == newItem.name_en
        }

        override fun areContentsTheSame(
            oldItem: SearchData,
            newItem: SearchData
        ): Boolean {
            return oldItem == newItem
        }
    }
}