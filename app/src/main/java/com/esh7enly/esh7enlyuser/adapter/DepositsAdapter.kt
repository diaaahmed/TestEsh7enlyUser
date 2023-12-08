package com.esh7enly.esh7enlyuser.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.esh7enly.domain.entity.depositsresponse.DataX
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.DepositClick
import com.esh7enly.esh7enlyuser.databinding.DepositItemLayoutBinding


class DepositsAdapter  :
    RecyclerView.Adapter<DepositsAdapter.NewTransactionViewHolder>() {

    private var transactionEntityList = mutableListOf<DataX>()

    inner class NewTransactionViewHolder(private var binding: DepositItemLayoutBinding)
        :RecyclerView.ViewHolder(binding.root)
    {
            @SuppressLint("SetTextI18n")
            fun bind(transactionEntity: List<DataX>, position: Int) = with(binding)
            {
                val createdAt = transactionEntity[position].created_at

                val month = createdAt.split(" ")
                val monthA = month[0].split("-")
                val day = monthA[2]
                val finalMonth = monthA[1]

                val replaceMonth = finalMonth.replace("01",binding.root.resources.getString(R.string.jan))
                    .replace("02",binding.root.resources.getString(R.string.feb))
                    .replace("03",binding.root.resources.getString(R.string.mar))
                    .replace("04",binding.root.resources.getString(R.string.abr))
                    .replace("05",binding.root.resources.getString(R.string.may))
                    .replace("06",binding.root.resources.getString(R.string.jun))
                    .replace("07",binding.root.resources.getString(R.string.july))
                    .replace("08",binding.root.resources.getString(R.string.aug))
                    .replace("09",binding.root.resources.getString(R.string.sep))
                    .replace("10",binding.root.resources.getString(R.string.oct))
                    .replace("11",binding.root.resources.getString(R.string.nov))
                    .replace("12",binding.root.resources.getString(R.string.dec))


                dayNumber.text = day
                monthNumber.text = replaceMonth

                if(transactionEntity[position].type == "card")
                {
                    tvService.text = binding.root.resources.getString(R.string.visa)

                }
                else
                {
                    tvService.text = binding.root.resources.getString(R.string.cash)

                }
                tvId.text = binding.root.resources.getString(R.string.deposit_id)+transactionEntity[position].id.toString()

                tvDate.text = transactionEntity[position].amount + binding.root.resources.getString(R.string.egp)
                when(transactionEntity[position].status)
                {
                    "SUCCESSFUL" -> {
                        tvStatus.text = binding.root.resources.getString(R.string.print_succes)
                        tvStatus.setTextColor(ContextCompat.getColor(root.context,R.color.green_new))
                        tvService.setTextColor(ContextCompat.getColor(root.context,R.color.green_new))
                        viewDepositStatus.setBackgroundColor(ContextCompat.getColor(root.context,R.color.green_new))
                    }

                    "PENDING" -> {
                        tvStatus.text = binding.root.resources.getString(R.string.print_pending)
                        tvStatus.setTextColor(ContextCompat.getColor(root.context,R.color.transactions_status))
                        tvService.setTextColor(ContextCompat.getColor(root.context,R.color.transactions_status))
                        viewDepositStatus.setBackgroundColor(ContextCompat.getColor(root.context,R.color.transactions_status))

                    }

                    "FAILED" -> {
                        tvStatus.text = binding.root.resources.getString(R.string.print_failed)
                        tvStatus.setTextColor(ContextCompat.getColor(root.context,R.color.orange_900))
                        tvService.setTextColor(ContextCompat.getColor(root.context,R.color.orange_900))
                        viewDepositStatus.setBackgroundColor(ContextCompat.getColor(root.context,R.color.orange_900))

                    }
                }

            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewTransactionViewHolder {
        return NewTransactionViewHolder(
            DepositItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int = transactionEntityList.size

    override fun onBindViewHolder(holder: NewTransactionViewHolder, position: Int)
    {
        holder.bind(transactionEntityList,position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTransactionEntity(transactionEntity: List<DataX>)
    {
        transactionEntityList.addAll(transactionEntity)
        notifyDataSetChanged()
    }

}