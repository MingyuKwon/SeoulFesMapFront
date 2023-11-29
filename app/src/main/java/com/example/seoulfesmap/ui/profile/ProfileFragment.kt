package com.example.seoulfesmap.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.seoulfesmap.Data.RetrofitClient
import com.example.seoulfesmap.RecyclerView.clickInterface
import com.example.seoulfesmap.RecyclerView.stickerAdapter
import com.example.seoulfesmap.StartActivity
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.appStaticData.Companion.resetStickerItem
import com.example.seoulfesmap.appStaticData.Companion.stickerItems
import com.example.seoulfesmap.databinding.FragmentProfileBinding
import com.example.seoulfesmap.isGuest
import com.example.seoulfesmap.loginNaver
import com.example.seoulfesmap.ui.NewActivity.ChallengeActivity
import com.example.seoulfesmap.ui.NewActivity.VisitedFestivalActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.navercorp.nid.NaverIdLoginSDK
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    lateinit var stickeradapter: stickerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val notificationsViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            RetrofitClient.initVisitedFes()
        }

        if (isGuest == false) {
            binding.ProfileName.text = appStaticData.USER?.name
            binding.ProfileExplain.text = appStaticData.USER?.email

            seethroghListforSticker()
            initStickerRecyclerView()
            initVisitRecyclerView()
            initChallengeRecyclerView()
            initUserLevel()
        } else {
            binding.ProfileName.text = "Guest"
            binding.ProfileExplain.text = "게스트 모드로 접속 중입니다."
        }

        val btnLogout: ImageButton = binding.logoutBtn
        if (isGuest == false) {
            if(loginNaver == true) {
                btnLogout.setOnClickListener {
                    NaverLogout()
                }
            }
            else { // Google
                btnLogout.setOnClickListener {
                    GoogleLogout()
                }
            }

        }else
        {
            btnLogout.setOnClickListener {
                movetoStartActivity()
            }
        }

        if(isGuest)
        {
            binding.visitedConatiner.visibility = View.GONE
            binding.challengeConatiner.visibility = View.GONE
            binding.textView4.visibility = View.GONE
            binding.ProfileLevel.visibility = View.GONE
            binding.progressBar2.visibility = View.GONE

        }

        val root: View = binding.root
        return root
    }

    fun movetoStartActivity()
    {
        val intent = Intent(requireContext(), StartActivity()::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    fun countLevel(expp : Float) : Float
    {
        var level = 1
        var exp = expp
        val levelStandard = arrayListOf<Int>(100,200,400,800,1600,3200,6400)

        while(exp >= levelStandard.get(level-1))
        {
            exp -= levelStandard.get(level-1)
            level++
        }

        return level + exp / levelStandard.get(level-1)
    }


    fun initUserLevel() {
        Log.e("UserLEvel", appStaticData.challengeData.userLevel.toString())
        val result = countLevel(appStaticData.challengeData.userLevel.toFloat())
        val level = result.toInt()
        val exp = ((result - level) * 100 ).toInt()

        binding.ProfileLevel.text = level.toString()
        binding.progressBar2.max = 100
        binding.progressBar2.progress = exp
    }

    private fun seethroghListforSticker()
    {
        resetStickerItem()

        var moviecount = 0
        var operacount = 0
        var musicount = 0
        var koreamusiccount = 0
        var exhibitioncount = 0
        var educationcount = 0
        var guitarcount = 0

        for(a in appStaticData.visitedFesDatalist)
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

    fun initStickerRecyclerView() {

        stickeradapter = stickerAdapter(appStaticData.visitedFesDatalist, object : clickInterface {
            override fun OnItemClick(position: Int) {
                val exlainText =  arrayOf("로그인에 성공했습니다","영화 카테고리 축제 3개 이상을 방문하였습니다","오페라/뮤지컬 카테고리 축제 3개 이상을 방문하였습니다"
                    ,"음악 카테고리 축제 3개 이상을 방문하였습니다","국악 카테고리 축제 3개 이상을 방문하였습니다","전시회 카테고리 축제 3개 이상을 방문하였습니다",
                    "교육 카테고리 축제 3개 이상을 방문하였습니다","기타 카테고리 축제 3개 이상을 방문하였습니다","모든 스티커를 수집하였습니다",)
                if(stickerItems[position] != "none")
                    Toast.makeText(requireContext(), exlainText[position], Toast.LENGTH_SHORT).show()
            }

        })
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.stickerrecyclerview.setLayoutManager(layoutManager)
        binding.stickerrecyclerview.adapter = stickeradapter
    }

    fun initVisitRecyclerView() {

        binding.visitedConatiner.setOnClickListener{
            moveToVisitedActivity()
        }

        if(appStaticData.visitedFesDatalist.size < 1) return
        Glide.with(requireActivity())
            .load(appStaticData.visitedFesDatalist.get(0).imageResourceUrl)
            .into(binding.profileVisitedImage)

        binding.profileVisitedFesTitle.text = appStaticData.visitedFesDatalist.get(0).FesTitle
        binding.profileVisitedFesLocaiton.text = appStaticData.visitedFesDatalist.get(0).FesLocation
        binding.profileVisitedFesDate.text = appStaticData.visitedFesDatalist.get(0).visitedDate?.substring(0,10)

        if(appStaticData.visitedFesDatalist.size < 2) return
        Glide.with(requireActivity())
            .load(appStaticData.visitedFesDatalist.get(1).imageResourceUrl)
            .into(binding.profileVisitedImageSmall1)

        if(appStaticData.visitedFesDatalist.size < 3) return
        Glide.with(requireActivity())
            .load(appStaticData.visitedFesDatalist.get(2).imageResourceUrl)
            .into(binding.profileVisitedImageSmall2)

    }

    fun initChallengeRecyclerView() {

        binding.challengeConatiner.setOnClickListener{
            moveToChallengeActivity()
        }

        val progressRate = appStaticData.challengeData.clearedList.size * 10
        binding.challengeDone.text = appStaticData.challengeData.clearedList.size.toString() + "/10 (" + progressRate + "%)"
        binding.challengeProgress.max = 100
        binding.challengeProgress.progress = progressRate


        if(appStaticData.challengeData.clearedList.size < 1) return
        binding.challengeImage.setImageResource(appStaticData.challengeData.imageMap.get(appStaticData.challengeData.clearedList.get(0))!!)
        binding.challengeTitle.text = appStaticData.challengeData.titleMap.get(appStaticData.challengeData.clearedList.get(0))!!
        binding.challengedescription.text = appStaticData.challengeData.descriptionMap.get(appStaticData.challengeData.clearedList.get(0))!!

        if(appStaticData.challengeData.clearedList.size < 2) return
        binding.challengeImageSmall1.setImageResource(appStaticData.challengeData.imageMap.get(appStaticData.challengeData.clearedList.get(1))!!)

        if(appStaticData.challengeData.clearedList.size < 3) return
        binding.challengeImageSmall2.setImageResource(appStaticData.challengeData.imageMap.get(appStaticData.challengeData.clearedList.get(2))!!)


    }

    private fun moveToVisitedActivity()
    {
        val intent = Intent(requireContext(), VisitedFestivalActivity::class.java)
        startActivity(intent)
    }

    private fun moveToChallengeActivity()
    {
        val intent = Intent(requireContext(), ChallengeActivity::class.java)
        startActivity(intent)
    }


    private fun NaverLogout() {
        isGuest = true
        NaverIdLoginSDK.logout()

        movetoStartActivity()
    }

    private fun GoogleLogout() {
        isGuest = true
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        mGoogleSignInClient.signOut()

        movetoStartActivity()
    }


}