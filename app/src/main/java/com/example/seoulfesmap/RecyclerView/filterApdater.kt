package com.example.seoulfesmap.RecyclerView

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.seoulfesmap.R
import com.example.seoulfesmap.databinding.FilterbuttonBinding
import com.google.android.material.color.utilities.MaterialDynamicColors.background


class filterApdater(var items: ArrayList<String>,
    val itemClick : (position : Int) -> Unit)
    : RecyclerView.Adapter<filterApdater.MyViewHolder>(){

    interface OnItemClickListener{
        fun OnItemClick(position : Int)
    }

    var currentIndex = 0;

    var itemClickListener: OnItemClickListener? = object : filterApdater.OnItemClickListener{
        override fun OnItemClick(position: Int) {
            notifyDataSetChanged()
        }
    }

    inner class MyViewHolder(val binding: com.example.seoulfesmap.databinding.FilterbuttonBinding) : RecyclerView.ViewHolder(binding.root)
    {
        init{
            binding.textView.setOnClickListener{
                itemClickListener?.OnItemClick(adapterPosition)
                itemClick(absoluteAdapterPosition)
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
        changeViewBorderColor(holder.binding.textView, getBorderColorForItem(position));
        holder.binding.textView.text = items[position];

        if(currentIndex == position)
        {
            changeClickColor(holder.binding.textView, getBorderColorForItem(position))
        }else
        {
            changeClickColor(holder.binding.textView, -1)
        }
    }

    fun changeViewBorderColor(view: View, newColor: Int) {
        val background = view.background
        val borderWidth = 9
        if (background is GradientDrawable) {
            background.setStroke(borderWidth, newColor) // borderWidth는 테두리의 두께입니다.
        }
    }

    fun changeClickColor(view: View, newColor: Int) {
        val background = view.background as? GradientDrawable
        background?.let {
            // 색상 값이 유효하지 않은 경우 흰색으로 설정
            val color = if (newColor == -1) {
                Color.WHITE
            } else {
                val alpha = 70
                val red = Color.red(newColor)
                val green = Color.green(newColor)
                val blue = Color.blue(newColor)

                // 새로운 ARGB 값을 생성
                Color.argb(alpha, red, green, blue)
            }

            // 새 색상으로 내부 채우기
            it.setColor(color)
        }
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