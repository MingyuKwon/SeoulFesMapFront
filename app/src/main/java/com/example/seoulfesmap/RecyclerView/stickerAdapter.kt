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
            seethroghListforSticker()
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
            seethroghListforSticker()
            notifyDataSetChanged()
        }

        private fun seethroghListforSticker()
        {
            var moviecount = 0
            var operacount = 0
            var musicount = 0
            var koreamusiccount = 0
            var exhibitioncount = 0
            var educationcount = 0
            var guitarcount = 0

            for(a in items)
            {
                if(a.category == "콘서트"
                    || a.category == "무용"
                    || a.category == "클래식")
                {
                    musicount++
                }else if(a.category == "뮤지컬/오페라"
                    || a.category == "연극")
                {
                    operacount++
                }else if(a.category == "국악")
                {
                    koreamusiccount++
                }else if(a.category == "전시/미술")
                {
                    exhibitioncount++
                }else if(a.category == "교육/체험")
                {
                    educationcount++
                }else if(a.category == "영화")
                {
                    moviecount++
                }else
                {
                    guitarcount++
                }
            }

            var stickercount = 1

            if(moviecount > 2)
            {
                stickerItems.set(stickercount, "movie")
                stickercount++
            }
            if(operacount > 2)
            {
                stickerItems.set(stickercount, "opera")
                stickercount++
            }
            if(musicount > 2)
            {
                stickerItems.set(stickercount, "music")
                stickercount++

            }
            if(koreamusiccount > 2)
            {
                stickerItems.set(stickercount, "koreanmusic")
                stickercount++

            }
            if(exhibitioncount > 2)
            {
                stickerItems.set(stickercount, "exhibition")
                stickercount++

            }
            if(educationcount > 2)
            {
                stickerItems.set(stickercount, "education")
                stickercount++

            }
            if(guitarcount > 2)
            {
                stickerItems.set(stickercount, "guitar")
                stickercount++

            }

            if(stickercount == 8)
            {
                stickerItems.set(stickercount, "grandslam")
                stickercount++
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

