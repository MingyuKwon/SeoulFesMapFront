package com.example.seoulfesmap.RecyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.databinding.FesViewcontainerBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.ArrayList

class RecyclerAdapter(var items: ArrayList<FestivalData>)
    : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    var filteredList: List<FestivalData> = items

    fun filter(categortType: String, dateStart: String, dateEnd: String) {
        filteredList = when {
            categortType == "전체" -> items
            else -> items.filter { it.category == categortType }
        }

        filteredList = when {
            dateStart.isEmpty()  -> filteredList
            else -> filteredList.filter { item->
                val startDate =  LocalDate.parse(dateStart)
                val festivalEnd = item.FesEndDate?.toLocalDate()

                startDate.isBefore(festivalEnd)
            }
        }

        filteredList = when {
            dateEnd.isEmpty()  -> filteredList
            else -> filteredList.filter { item->
                val endDate =  LocalDate.parse(dateEnd)
                val festivalStart = item.FesStartDate?.toLocalDate()

                endDate.isAfter(festivalStart)
            }
        }

        notifyDataSetChanged()
    }


    interface OnItemClickListener{
        fun OnItemClick(position : Int)
    }

    interface OnLongItemClickListener{
        fun OnItemLongClick(position : Int) : Boolean // 꾹 눌렀을 때 반응할 콜백 함수는 Boolean 값을 반환해 줘야 합니다
    }


    var itemClickListener: OnItemClickListener? = object : RecyclerAdapter.OnItemClickListener{
        override fun OnItemClick(position: Int) {
            Log.d("OnItemClick", "OnItemClick");
            notifyDataSetChanged()
        }
    }

    var itemLongClickListener : OnLongItemClickListener? = object : RecyclerAdapter.OnLongItemClickListener{
        override fun OnItemLongClick(position: Int): Boolean {

            Log.d("OnItemLongClick", "OnItemLongClick");
            notifyDataSetChanged()
            return  true
        }
    }


    inner class MyViewHolder(val binding: FesViewcontainerBinding) : RecyclerView.ViewHolder(binding.root)
    {
        init{
            binding.VieTotal.setOnClickListener{
                itemClickListener?.OnItemClick(adapterPosition)
            }

            binding.VieTotal.setOnLongClickListener{
                itemLongClickListener?.OnItemLongClick(adapterPosition)!!
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view : FesViewcontainerBinding = FesViewcontainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(filteredList[position].imageResourceUrl)
            .into(holder.binding.FesImage)

        holder.binding.FesTitle.text = filteredList[position].FesTitle
        holder.binding.FestLocation.text = filteredList[position].FesLocation
        holder.binding.FesDate.text = filteredList[position].FesStartDate?.toLocalDate().toString() + " ~ "  + filteredList[position].FesEndDate?.toLocalDate().toString()
    }

}