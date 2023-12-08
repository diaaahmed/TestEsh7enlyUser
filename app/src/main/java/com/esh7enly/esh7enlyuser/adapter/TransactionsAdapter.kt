package com.esh7enly.esh7enlyuser.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.esh7enly.domain.entity.TransactionEntity
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.TransactionClick
import com.esh7enly.esh7enlyuser.databinding.TransactionItemBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.NetworkUtils
import com.esh7enly.esh7enlyuser.util.Utils


class TransactionsAdapter (val click: TransactionClick) :
    ListAdapter<TransactionEntity, TransactionsAdapter.ViewHolder>(TransactionDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = TransactionItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val itemBinding: TransactionItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        @SuppressLint("CheckResult", "SetTextI18n")

        fun bind(response: TransactionEntity) = with(itemBinding)
        {

            tvId.text = itemBinding.root.resources.getString(R.string.trans_id_title) + response.id

            tvAmount.text = response.amount + itemBinding.root.resources.getString(R.string.egp)
            tvDate.text = response.createdAt
            tvService.text = response.provider.name + response.service.name

            if(response.service.icon == null)
            {
                image.setImageResource(R.drawable.logo)
            }
            else
            {
                Utils.displayImageOriginalFromCache(root.context,
                image,response.service.icon,NetworkUtils.isConnectedWifi(root.context))
            }

            val type = response.type

            when(type)
            {
                Constants.DONE -> {
                    tvStatus.text = itemBinding.root.resources.getString(R.string.print_succes)
                    tvStatus.setTextColor(ContextCompat.getColor(root.context,R.color.green_800))
                }

                Constants.FAIL -> {
                    tvStatus.text = itemBinding.root.resources.getString(R.string.print_failed)
                    tvStatus.setTextColor(ContextCompat.getColor(root.context,R.color.red_900))

                }

                Constants.PENDING -> {
                    tvStatus.text = itemBinding.root.resources.getString(R.string.print_pending)
                    tvStatus.setTextColor(ContextCompat.getColor(root.context,R.color.colorAccent))

                }
            }

            root.setOnClickListener {
                click.click(response)
            }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<TransactionEntity>()
    {
        override fun areItemsTheSame(
            oldItem: TransactionEntity,
            newItem: TransactionEntity
        ): Boolean {
            return oldItem.id == newItem.id && oldItem.createdAt == newItem.createdAt
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: TransactionEntity,
            newItem: TransactionEntity
        ): Boolean {
            return oldItem == newItem
        }
    }

}