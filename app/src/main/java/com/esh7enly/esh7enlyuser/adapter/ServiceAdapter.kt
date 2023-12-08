package com.esh7enly.esh7enlyuser.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.esh7enly.domain.entity.userservices.*
import com.esh7enly.esh7enlyuser.click.ServiceClick
import com.esh7enly.esh7enlyuser.databinding.ServiceLayoutBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.NetworkUtils
import com.esh7enly.esh7enlyuser.util.Utils

class ServiceAdapter (val click: ServiceClick) : ListAdapter<Service, ServiceAdapter.ViewHolder>(ServiceDiffCallback()) {

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

        fun bind(service: Service) = with(itemBinding)
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

    class ServiceDiffCallback : DiffUtil.ItemCallback<Service>()
    {
        override fun areItemsTheSame(
            oldItem: Service,
            newItem: Service
        ): Boolean {
            return oldItem.name_en == newItem.name_en
        }

        override fun areContentsTheSame(
            oldItem: Service,
            newItem: Service
        ): Boolean {
            return oldItem == newItem
        }
    }
}