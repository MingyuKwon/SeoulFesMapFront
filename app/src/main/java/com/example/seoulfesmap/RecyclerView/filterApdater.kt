package com.example.seoulfesmap.RecyclerView

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.seoulfesmap.databinding.FilterbuttonBinding


class filterApdater(var items: ArrayList<String>)
    : RecyclerView.Adapter<filterApdater.MyViewHolder>(){

    interface OnItemClickListener{
        fun OnItemClick(position : Int)
    }


    var itemClickListener: OnItemClickListener? = object : filterApdater.OnItemClickListener{
        override fun OnItemClick(position: Int) {
            Log.d("OnItemClick", "OnItemClick");
            notifyDataSetChanged()
        }
    }


    inner class MyViewHolder(val binding: com.example.seoulfesmap.databinding.FilterbuttonBinding) : RecyclerView.ViewHolder(binding.root)
    {
        init{
            binding.textView.setOnClickListener{
                itemClickListener?.OnItemClick(adapterPosition)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view : FilterbuttonBinding = FilterbuttonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val background = getBorderDrawableForItem(position)
        holder.itemView.background = background
        holder.binding.textView.text = items[position];
    }


    private fun getBorderDrawableForItem(position: Int): Drawable? {
        // 테두리 두께 정의
        val borderWidth = 3 // 픽셀 단위

        // 테두리 색상을 결정하는 메서드 호출
        val borderColor = getBorderColorForItem(position)

        // GradientDrawable 생성 및 설정
        val borderDrawable = GradientDrawable()
        borderDrawable.shape = GradientDrawable.RECTANGLE
        borderDrawable.setStroke(borderWidth, borderColor) // 테두리 색상 설정
        borderDrawable.setColor(Color.WHITE) // 배경색 설정 (옵션)
        return borderDrawable
    }

    // 아이템 위치에 따라 색상 결정
    private fun getBorderColorForItem(position: Int): Int {
        // 예시 색상 배열
        val colors = intArrayOf(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.CYAN
        )
        // position에 따라 색상 순환
        return colors[position % colors.size]
    }
}