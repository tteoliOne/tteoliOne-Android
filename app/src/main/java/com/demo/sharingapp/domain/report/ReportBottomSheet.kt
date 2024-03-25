package com.demo.sharingapp.domain.report


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.demo.sharingapp.R
import com.demo.sharingapp.retrofit.RetrofitManager

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator

class ReportBottomSheet( val chatNo: Long, private val opponentId: Long, private val onSuccess:()->Unit) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        return inflater.inflate(R.layout.report_bottom_sheet, container, false)
    }

    // 테마 설정
    override fun getTheme(): Int {
        return R.style.BottomSheetStyleDialogTheme
    }

    // 액션 설정
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val spamBtn = view?.findViewById<ConstraintLayout>(R.id.spamButton)
        val imageViolenceBtn = view?.findViewById<ConstraintLayout>(R.id.imageViolenceButton)
        val informationBtn = view?.findViewById<ConstraintLayout>(R.id.informationButton)
        val ectButton = view?.findViewById<ConstraintLayout>(R.id.ectButton)



        spamBtn?.setOnClickListener{
            Log.e("aa","spam")
            dismiss()
            RetrofitManager.instance.postReport(this.requireContext(), "chat",chatNo,"spam", opponentId = opponentId){
                if(it == 0){
                    onSuccess()
                }
            }
        }
        imageViolenceBtn?.setOnClickListener{
            dismiss()
            RetrofitManager.instance.postReport(this.requireContext(), "chat",chatNo,"image-violence", opponentId = opponentId){
                if(it == 0){
                    onSuccess()
                }
            }
            Log.e("aa","imageViolenceBtn")

        }
        informationBtn?.setOnClickListener{
            Log.e("aa","informationBtn")
            dismiss()
            RetrofitManager.instance.postReport(this.requireContext(), "chat",chatNo,"information", opponentId = opponentId){
                if(it == 0){
                    onSuccess()
                }
            }
        }
        ectButton?.setOnClickListener{
            Log.e("aa","ectButton")
        }



    }






}

