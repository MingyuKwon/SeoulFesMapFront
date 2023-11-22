package com.example.seoulfesmap.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.seoulfesmap.RecyclerView.stickerAdapter
import com.example.seoulfesmap.StartActivity
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.databinding.FragmentProfileBinding
import com.example.seoulfesmap.isGuest
import com.example.seoulfesmap.loginNaver
import com.example.seoulfesmap.ui.NewActivity.VisitedFestivalActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.navercorp.nid.NaverIdLoginSDK

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var stickerlist: ArrayList<String> = ArrayList()
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

        appStaticData.initVisitedFes()

        if (isGuest == false) {
            binding.ProfileName.text = appStaticData.USER?.name
            binding.ProfileExplain.text = appStaticData.USER?.email

            initStickerRecyclerView()
            initVisitRecyclerView()
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

        }

        val root: View = binding.root
        return root
    }

    fun initStickerRecyclerView() {
        stickerlist.add("none")
        stickerlist.add("none")
        stickerlist.add("none")
        stickerlist.add("none")
        stickerlist.add("none")


        stickeradapter = stickerAdapter(stickerlist)

        stickeradapter.itemClickListener = object : stickerAdapter.OnItemClickListener {
            override fun OnItemClick(position: Int) {
                val selectedCategory = stickeradapter.items[position]
                // 다른 RecyclerView의 데이터를 정렬하는 메서드 호출
                stickeradapter.notifyDataSetChanged()
            }
        }


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

    private fun moveToVisitedActivity()
    {
        val intent = Intent(requireContext(), VisitedFestivalActivity::class.java)
        startActivity(intent)
    }


    private fun NaverLogout() {
        isGuest = true
        NaverIdLoginSDK.logout()

        val intent = Intent(requireContext(), StartActivity()::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun GoogleLogout() {
        isGuest = true
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        mGoogleSignInClient.signOut()

        val intent = Intent(requireContext(), StartActivity()::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


}