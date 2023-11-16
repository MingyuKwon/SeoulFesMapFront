package com.example.seoulfesmap.RecyclerView

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.seoulfesmap.R
import com.example.seoulfesmap.databinding.FilterbuttonBinding
import com.example.seoulfesmap.databinding.StickerContainerBinding

class stickerAdapter(var items: ArrayList<String>) : RecyclerView.Adapter<stickerAdapter.MyViewHolder>(){
        interface OnItemClickListener{
            fun OnItemClick(position : Int)
        }

        var itemClickListener: OnItemClickListener? = object : stickerAdapter.OnItemClickListener{
            override fun OnItemClick(position: Int) {
                notifyDataSetChanged()
            }
        }

        inner class MyViewHolder(val binding: com.example.seoulfesmap.databinding.StickerContainerBinding) : RecyclerView.ViewHolder(binding.root)
        {
            init{

            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view : StickerContainerBinding = StickerContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyViewHolder(view)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            changeViewBorderColor(holder.binding.root, getBorderColorForItem(position));
            if(items[position] == "none")
            {
                val drawable = ContextCompat.getDrawable(holder.itemView.context, R.drawable.baseline_cross_24)
                holder.binding.imageView3.setImageDrawable(drawable)
            }else
            {

            }
        }
        fun changeViewBorderColor(view: View, newColor: Int) {
            val background = view.background
            if (background is GradientDrawable) {
                background.setStroke(2, newColor) // borderWidth는 테두리의 두께입니다.
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

