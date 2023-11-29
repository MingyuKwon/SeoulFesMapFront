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

            initStickerRecyclerView()
            initVisitRecyclerView()
            initChallengeRecyclerView()
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

    fun initStickerRecyclerView() {

        stickeradapter = stickerAdapter(appStaticData.visitedFesDatalist, object : clickInterface {
            override fun OnItemClick(position: Int) {
                val exlainText =  arrayOf("로그인에 성공했습니다","영화 카테고리 축제 3개 이상을 방문하였습니다","오페라/뮤지컬 카테고리 축제 3개 이상을 방문하였습니다"
                    ,"음악 카테고리 축제 3개 이상을 방문하였습니다","국악 카테고리 축제 3개 이상을 방문하였습니다","전시회 카테고리 축제 3개 이상을 방문하였습니다",
                    "교육 카테고리 축제 3개 이상을 방문하였습니다","기타 카테고리 축제 3개 이상을 방문하였습니다","모든 스티커를 수집하였습니다",)
                if(stickeradapter.stickerItems[position] != "none")
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