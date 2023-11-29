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

class RecyclerAdapter(var items: ArrayList<FestivalData>, var isVisited : Boolean = false)
    : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    var filteredList: List<FestivalData> = items

    fun refillData(newitems: ArrayList<FestivalData>)
    {
        filteredList = newitems
        notifyDataSetChanged()
    }

    fun filter(categortType: String, dateStart: String, dateEnd: String) {
        filteredList = when {
            categortType == "전체" -> items
            else -> {
                if(categortType == "음악")
                {
                    items.filter { it.category == "콘서트"
                            || it.category == "무용"
                            || it.category == "클래식"}
                }else if(categortType == "뮤지컬/오페라")
                {
                    items.filter { it.category == "뮤지컬/오페라"
                            || it.category == "연극"}

                }else if(categortType == "국악")
                {
                    items.filter { it.category == "국악" }

                }else if(categortType == "전시/미술")
                {
                    items.filter { it.category == "전시/미술" }

                }else if(categortType == "교육/체험")
                {
                    items.filter { it.category == "교육/체험" }

                }else if(categortType == "영화")
                {
                    items.filter { it.category == "영화" }

                }else
                {
                    items.filter {
                        it.category != "콘서트"
                                && it.category != "무용"
                                && it.category != "클래식"
                                && it.category != "뮤지컬/오페라"
                                && it.category != "연극"
                                && it.category != "국악"
                                && it.category != "전시/미술"
                                && it.category != "교육/체험"
                                && it.category != "영화" }

                }

            }

        }

        filteredList = when {
            dateStart.isEmpty()  -> filteredList
            else -> filteredList.filter { item->
                val startDate =  LocalDate.parse(dateStart)
                val festivalEnd = item.FesEndDate

                startDate.isBefore(festivalEnd) || startDate.isEqual(festivalEnd)
            }
        }

        filteredList = when {
            dateEnd.isEmpty()  -> filteredList
            else -> filteredList.filter { item->
                val endDate =  LocalDate.parse(dateEnd)
                val festivalStart = item.FesStartDate

                endDate.isAfter(festivalStart) || endDate.isEqual(festivalStart)
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
                itemClickListener?.OnItemClick(absoluteAdapterPosition)
            }

            binding.VieTotal.setOnLongClickListener{
                itemLongClickListener?.OnItemLongClick(absoluteAdapterPosition)!!
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
        if(isVisited) {
            holder.binding.FesDate.text =
                filteredList[position].visitedDate!!.substring(0, 10)
        }
        else{
            holder.binding.FesDate.text = filteredList[position].FesStartDate?.toString() + " ~ "  + filteredList[position].FesEndDate?.toString()
        }
    }

}