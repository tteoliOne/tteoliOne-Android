package com.demo.sharingapp.domain.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import com.demo.sharingapp.R
import com.demo.sharingapp.retrofit.RetrofitManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class ReportEtcBottomSheet(val chatNo: Long, private val opponentId: Long,private val onSuccess:()->Unit): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.report_etc_bottom_sheet, container, false)
    }


    // 테마 설정
    override fun getTheme(): Int {
        return R.style.BottomSheetStyleDialogTheme
    }

    // 액션 설정
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val descriptionEditText = view?.findViewById<TextInputEditText>(R.id.descriptionEditText)
        val completeBtn = view?.findViewById<ConstraintLayout>(R.id.completeButton)
        val scrollView = view?.findViewById<NestedScrollView>(R.id.scrollView)


        descriptionEditText?.setOnClickListener {
            scrollView?.post{
                scrollView.smoothScrollTo(0, completeBtn?.bottom!!)
            }
        }


        completeBtn?.setOnClickListener {
            val data = descriptionEditText?.text.toString()
            dismiss()
            RetrofitManager.instance.postReport(this.requireContext(), "chat",chatNo,"etc", opponentId = opponentId, content = data){
                if(it == 0){
                    onSuccess()
                }
            }
        }


    }
}