package com.example.seoulfesmap.RecyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.databinding.FesViewcontainerBinding
import java.util.ArrayList

class RecyclerAdapter(var items: ArrayList<FestivalData>)
    : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    // 이걸 호출하면 리사이클러 뷰 안의 아이템들이 우선도 오름차순으로 정렬이 됩니다. -> 초록색일 수록 위에, 빨간색 일 수록 아래에 깔리게 됩니다
    fun sortItemwithAscendingPriority()
    {
//        val comparator: Comparator<ToDo> = object : Comparator<ToDo> {
//            override fun compare(o1: ToDo?, o2: ToDo?): Int {
//
//                return if(o1!!.priority - o2!!.priority < 0) {
//                    -1
//                }else if(o1!!.priority - o2!!.priority > 0) {
//                    1
//                }else {
//                    0
//                }
//            }
//
//        }
//        items.sortWith(comparator)
//        notifyDataSetChanged()
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
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(items[position].imageResourceUrl)
            .into(holder.binding.FesImage)

        holder.binding.FesTitle.text = items[position].FesTitle
        holder.binding.FestLocation.text = items[position].FesLocation
        holder.binding.FesDate.text = items[position].FesStartDate?.toLocalDate().toString()
    }

}