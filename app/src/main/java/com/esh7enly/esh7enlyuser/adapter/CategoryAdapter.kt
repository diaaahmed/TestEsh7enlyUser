package com.esh7enly.esh7enlyuser.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.esh7enly.domain.entity.categoriesNew.CategoryData
import com.esh7enly.domain.entity.userservices.*
import com.esh7enly.esh7enlyuser.click.CategoryClick
import com.esh7enly.esh7enlyuser.databinding.ItemLayoutBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.NetworkUtils
import com.esh7enly.esh7enlyuser.util.Utils

class CategoryAdapter (val click: CategoryClick) :
    ListAdapter<CategoryData, CategoryAdapter.ViewHolder>(CategoryDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val itemBinding: ItemLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        @SuppressLint("CheckResult")

        fun bind(service: CategoryData) = with(itemBinding)
        {

            Utils.displayImageOriginalFromCache(root.context,categoryIcon,service.icon
            ,NetworkUtils.isConnectedWifi(root.context))

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

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryData>()
    {
        override fun areItemsTheSame(
            oldItem: CategoryData,
            newItem: CategoryData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: CategoryData,
            newItem: CategoryData
        ): Boolean {
            return oldItem == newItem
        }
    }
}