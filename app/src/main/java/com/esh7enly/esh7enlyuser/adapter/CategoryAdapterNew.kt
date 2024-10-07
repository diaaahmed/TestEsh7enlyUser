package com.esh7enly.esh7enlyuser.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esh7enly.domain.entity.categoriesNew.CategoryData
import com.esh7enly.esh7enlyuser.click.CategoryClick
import com.esh7enly.esh7enlyuser.databinding.ItemLayoutBinding
import com.esh7enly.esh7enlyuser.util.Constants

class CategoryAdapterNew(private var listCategory:List<CategoryData>,val click: CategoryClick
): RecyclerView.Adapter<CategoryAdapterNew.CategoryViewHolderNew>()
{

    inner class CategoryViewHolderNew(private var binding: ItemLayoutBinding)
        :RecyclerView.ViewHolder(binding.root)
    {
        @SuppressLint("SetTextI18n")
        fun bind(category: List<CategoryData>, position: Int) = with(binding)
        {

            categoryModel = category[position]


            if(Constants.LANG == Constants.AR)
            {
                tvTitle.text = category[position].name_ar
            }
            else
            {
                tvTitle.text = category[position].name_en
            }

            root.setOnClickListener {
                click.click(category[position])
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolderNew {
        return CategoryViewHolderNew(
            ItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount() = listCategory.size

    override fun onBindViewHolder(holder: CategoryViewHolderNew, position: Int)
    {
        holder.bind(listCategory,position)
    }
}