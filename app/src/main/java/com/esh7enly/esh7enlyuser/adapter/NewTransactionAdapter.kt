package com.esh7enly.esh7enlyuser.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.esh7enly.domain.entity.TransactionEntity
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.TransactionClick
import com.esh7enly.esh7enlyuser.databinding.TransactionItemBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.NetworkUtils
import com.esh7enly.esh7enlyuser.util.Utils

class NewTransactionAdapter (val click: TransactionClick) :
    RecyclerView.Adapter<NewTransactionAdapter.NewTransactionViewHolder>() {

    private var transactionEntityList = mutableListOf<TransactionEntity>()

    inner class NewTransactionViewHolder(private var binding: TransactionItemBinding)
        :RecyclerView.ViewHolder(binding.root)
    {

            @SuppressLint("SetTextI18n")
            fun bind(transactionEntity: List<TransactionEntity>, position: Int) = with(binding)
            {
                tvService.text = transactionEntity[position].service.name

                tvId.text = binding.root.resources.getString(R.string.trans_id_title) +
                        transactionEntity[position].id

                tvAmount.text = transactionEntity[position].amount + binding.root.resources.getString(R.string.egp)
                tvDate.text = transactionEntity[position].createdAt

                if(transactionEntity[0].service.icon == null)
                {
                    image.setImageResource(R.drawable.logo)
                }
                else
                {
                    Utils.displayImageOriginalFromCache(binding.root.context,
                        image,transactionEntity[position].service.icon,
                        NetworkUtils.isConnectedWifi(binding.root.context))
                }

                val type = transactionEntity[position].status

                when(type)
                {
                    Constants.DONE -> {
                        tvStatus.text = binding.root.resources.getString(R.string.print_succes)
                        tvStatus.setTextColor(ContextCompat.getColor(binding.root.context,R.color.green_new))
                        tvAmount.setTextColor(ContextCompat.getColor(binding.root.context,R.color.green_new))
                        viewStatus.setBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.green_new))
                    }

                    Constants.FAIL -> {
                        tvStatus.text = binding.root.resources.getString(R.string.print_failed)
                        tvStatus.setTextColor(ContextCompat.getColor(binding.root.context,R.color.transactions_status))
                        tvAmount.setTextColor(ContextCompat.getColor(binding.root.context,R.color.transactions_status))
                        viewStatus.setBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.transactions_status))

                    }

                    Constants.PENDING -> {
                        tvStatus.text = binding.root.resources.getString(R.string.print_pending)
                        tvStatus.setTextColor(ContextCompat.getColor(binding.root.context,R.color.orange_900))
                        tvAmount.setTextColor(ContextCompat.getColor(binding.root.context,R.color.orange_900))
                        viewStatus.setBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.orange_900))

                    }
                }

                binding.root.setOnClickListener {
                    click.click(transactionEntity[position])
                }
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewTransactionViewHolder {
        return NewTransactionViewHolder(TransactionItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int = transactionEntityList.size

    override fun onBindViewHolder(holder: NewTransactionViewHolder, position: Int) {
        holder.bind(transactionEntityList,position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTransactionEntity(transactionEntity: List<TransactionEntity>)
    {
        transactionEntityList.addAll(transactionEntity)
        notifyDataSetChanged()
    }

}