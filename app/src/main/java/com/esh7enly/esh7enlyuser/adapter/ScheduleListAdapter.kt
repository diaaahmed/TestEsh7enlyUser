package com.esh7enly.esh7enlyuser.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esh7enly.domain.entity.scedulelistresponse.Data
import com.esh7enly.esh7enlyuser.databinding.ScheduleListLayoutBinding
import com.esh7enly.esh7enlyuser.util.Constants

class ScheduleListAdapter(var listData: List<Data>) :
    RecyclerView.Adapter<ScheduleListAdapter.ScheduleListViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScheduleListAdapter.ScheduleListViewHolder {
        return ScheduleListViewHolder(
            ScheduleListLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ScheduleListAdapter.ScheduleListViewHolder,
        position: Int
    ) {
        holder.bind(listData, position)
    }

    override fun getItemCount() = listData.size

    inner class ScheduleListViewHolder(private var binding: ScheduleListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: List<Data>, position: Int) = with(binding)
        {
            if (Constants.LANG == Constants.AR) {
                serviceName.text = data[position].name_ar
            } else {
                serviceName.text = data[position].name_en
            }
            scheduleDay.text = data[position].schedule_date.toString()
        }
    }
}