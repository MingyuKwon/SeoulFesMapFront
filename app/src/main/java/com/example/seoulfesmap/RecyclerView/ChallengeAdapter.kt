package com.example.seoulfesmap.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.databinding.ChallengeviewcontainerBinding

class ChallengeAdapter()
    : RecyclerView.Adapter<ChallengeAdapter.MyViewHolder>(){

    inner class MyViewHolder(val binding: com.example.seoulfesmap.databinding.ChallengeviewcontainerBinding) : RecyclerView.ViewHolder(binding.root)
    {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view : ChallengeviewcontainerBinding = ChallengeviewcontainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appStaticData.challengeData.totlaList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {

            val challName = appStaticData.challengeData.totlaList.get(position)
            challengeTitle.text = appStaticData.challengeData.titleMap.get(challName)
            challengedescription.text = appStaticData.challengeData.descriptionMap.get(challName)

            if(appStaticData.challengeData.clearedList.contains(challName))
            {
                challengeImage.setImageResource(appStaticData.challengeData.imageMap.get(challName)!!)

            }else
            {
                challengeImage.setImageResource(appStaticData.challengeData.unableimageMap.get(challName)!!)

            }

            challengeProgress.max = 100

        }
    }


}