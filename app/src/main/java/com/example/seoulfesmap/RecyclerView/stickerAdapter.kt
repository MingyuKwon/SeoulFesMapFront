package com.example.seoulfesmap.RecyclerView

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.R
import com.example.seoulfesmap.appStaticData.Companion.stickerItems
import com.example.seoulfesmap.databinding.FilterbuttonBinding
import com.example.seoulfesmap.databinding.StickerContainerBinding

interface clickInterface
{
    fun OnItemClick(position: Int)
}
class stickerAdapter(var items: ArrayList<FestivalData>, var callback : clickInterface) : RecyclerView.Adapter<stickerAdapter.MyViewHolder>(){

    val images = arrayOf(R.drawable.newbee, R.drawable.movie, R.drawable.opera, R.drawable.music, R.drawable.korea_music,
        R.drawable.exihibition, R.drawable.education, R.drawable.guitar, R.drawable.grandslam)

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
            return stickerItems.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            changeViewBorderColor(holder.binding.imageView3, getBorderColorForItem(position))
            holder.binding.imageView3.setOnClickListener {
                callback.OnItemClick(position)
            }
            if(stickerItems[position] == "none")
            {
                val drawable = ContextCompat.getDrawable(holder.itemView.context, R.drawable.baseline_cross_24)
                holder.binding.imageView3.setImageDrawable(drawable)
            }else
            {
                if(stickerItems[position] == "newbee")
                {
                    holder.binding.imageView3.setImageResource(images[0])
                }
                if(stickerItems[position] == "movie")
                {
                    holder.binding.imageView3.setImageResource(images[1])
                }
                if(stickerItems[position] == "opera")
                {
                    holder.binding.imageView3.setImageResource(images[2])
                }
                if(stickerItems[position] == "music")
                {
                    holder.binding.imageView3.setImageResource(images[3])
                }
                if(stickerItems[position] == "koreanmusic")
                {
                    holder.binding.imageView3.setImageResource(images[4])
                }
                if(stickerItems[position] == "exhibition")
                {
                    holder.binding.imageView3.setImageResource(images[5])
                }
                if(stickerItems[position] == "education")
                {
                    holder.binding.imageView3.setImageResource(images[6])
                }
                if(stickerItems[position] == "guitar")
                {
                    holder.binding.imageView3.setImageResource(images[7])
                }

                if(stickerItems[position] == "grandslam")
                {
                    holder.binding.imageView3.setImageResource(images[8])
                }
            }
        }
        fun changeViewBorderColor(view: View, newColor: Int) {
            val background = view.background
            if (background is GradientDrawable) {
                background.setStroke(5, newColor) // borderWidth는 테두리의 두께입니다.
            }
        }

        fun seethroghListforStickerExplicit()
        {
            notifyDataSetChanged()
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

