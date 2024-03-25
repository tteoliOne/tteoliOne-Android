package com.demo.sharingapp.domain.report

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.demo.sharingapp.R
import com.demo.sharingapp.retrofit.RetrofitManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReportCompleteBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        return inflater.inflate(R.layout.report_complete_bottom_sheet, container, false)
    }

    // 테마 설정
    override fun getTheme(): Int {
        return R.style.BottomSheetStyleDialogTheme
    }

    // 액션 설정
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val completeBtn = view?.findViewById<ConstraintLayout>(R.id.completeButton)
        completeBtn?.setOnClickListener {

            dismiss()
        }


    }

}