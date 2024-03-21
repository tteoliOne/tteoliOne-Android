package com.demo.sharingapp.domain.report


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.demo.sharingapp.R
import com.demo.sharingapp.retrofit.RetrofitManager

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReportBottomSheet( val chatNo: Long) : BottomSheetDialogFragment() {

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
            RetrofitManager.instance.postReport(this.requireContext(), "chat",chatNo,"spam")
        }
        imageViolenceBtn?.setOnClickListener{
            Log.e("aa","imageViolenceBtn")
        }
        informationBtn?.setOnClickListener{
            Log.e("aa","informationBtn")
        }
        ectButton?.setOnClickListener{
            Log.e("aa","ectButton")
        }



    }






}

