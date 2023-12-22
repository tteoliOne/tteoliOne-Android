package com.demo.sharingapp.domain.home

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.demo.sharingapp.databinding.DialogPartProductBinding
import com.demo.sharingapp.databinding.DialogSignupBinding
import java.time.LocalDate
import java.util.*

class HomePartProductDialog( sort: String,
                             searchStartDate: LocalDate?,
                             searchEndDate: LocalDate?,
                             dateType: Int,
     val onClick:(LocalDate?, LocalDate?, String, Int)->Unit
) : DialogFragment(),DatePickerDialog.OnDateSetListener {
    // 뷰 바인딩 정의
    private var _binding: DialogPartProductBinding? = null
    private val binding get() = _binding!!

    private var text: String? = null

    private var isStart = false
    private var dateType = dateType

    private var sort = sort
    private var searchStartDate : LocalDate? = searchStartDate
    private var searchEndDate: LocalDate? = searchEndDate

    private val currentDate = LocalDate.now()


    init {
        this.text = text
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DialogPartProductBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))



        //
        cleanChipButton()
        when(dateType){
            0 -> binding.dateAllButton.isChecked = true
            1 -> binding.dateTodayButton.isChecked = true
            2 -> binding.dateWeekButton.isChecked = true
            3 -> binding.dateMonthButton.isChecked = true
            4 -> binding.dateTreeMonthButton.isChecked = true
            5 -> binding.dateSixMonthButton.isChecked = true
            6 -> {
                binding.dateDirectInputButton.isChecked = true

                appearDateInput(true)

            }
        }
        if (sort == "createAt-desc"){
            binding.pastView.isVisible=false
            binding.latestView.isVisible = true
        }else{
            binding.latestView.isVisible=false
            binding.pastView.isVisible = true
        }


        // 조회 버튼 클릭
        clickSearchButton()

        // 기간 선택
        clickDateButton()

        // 정렬순서 -  과거순 클릭
        binding.pastTextView.setOnClickListener {
            binding.latestView.isVisible=false
            binding.pastView.isVisible = true
            sort = "createAt-asc"
        }

        // 정렬순서 - 최신순 클릭
        binding.latestTextView.setOnClickListener {
            binding.pastView.isVisible=false
            binding.latestView.isVisible = true
            sort = "createAt-desc"
        }



        binding.dateStartTextView.setOnClickListener {
            isStart = true
            val date = searchStartDate ?: currentDate
            context?.let {
                DatePickerDialog(
                    it,
                    this,
                    date.year,
                    date.monthValue - 1,
                    date.dayOfMonth
                ).show()
            }
        }

        binding.dateEndTextView.setOnClickListener {
            isStart = false
            val date = searchEndDate ?: currentDate
            context?.let {
                DatePickerDialog(
                    it,
                    this,
                    date.year,
                    date.monthValue - 1,
                    date.dayOfMonth
                ).show()
            }
        }

        return view
    }



    // 기간 선택 함수
    private fun clickDateButton() {

        // 기간선택 - 전체 칩 클릭
        binding.dateAllButton.setOnClickListener {
            cleanChipButton()
            binding.dateAllButton.isChecked = true
            appearDateInput(false)
            searchStartDate = null
            searchEndDate = null
            dateType = 0
        }

        // 기간선택 - 1일 칩 클릭
        binding.dateTodayButton.setOnClickListener {
            cleanChipButton()
            binding.dateTodayButton.isChecked = true
            appearDateInput(false)
            searchEndDate = currentDate
            searchStartDate = searchEndDate?.minusDays(1)

            dateType = 1
        }

        // 기간선택 - 일주일 칩 클릭
        binding.dateWeekButton.setOnClickListener {
            cleanChipButton()
            binding.dateWeekButton.isChecked = true
            appearDateInput(false)
            searchEndDate = currentDate
            searchStartDate = searchEndDate?.minusDays(7)

            dateType = 2
        }

        // 기간선택 - 1개월 칩 클릭
        binding.dateMonthButton.setOnClickListener {
            cleanChipButton()
            binding.dateMonthButton.isChecked = true
            appearDateInput(false)
            searchEndDate = currentDate
            searchStartDate = searchEndDate?.minusMonths(1)

            dateType = 3
        }

        // 기간선택 - 3개월 칩 클릭
        binding.dateTreeMonthButton.setOnClickListener {
            cleanChipButton()
            binding.dateTreeMonthButton.isChecked = true
            appearDateInput(false)
            searchEndDate = currentDate
            searchStartDate = searchEndDate?.minusMonths(3)

            dateType = 4
        }

        // 기간선택 - 6개월 칩 클릭
        binding.dateSixMonthButton.setOnClickListener {
            cleanChipButton()
            binding.dateSixMonthButton.isChecked = true
            appearDateInput(false)
            searchEndDate = currentDate
            searchStartDate = searchEndDate?.minusMonths(6)

            dateType = 5
        }

        // 기간선택 - 직접 입력 칩 클릭
        binding.dateDirectInputButton.setOnClickListener {
            // 칩 버튼 초기화
            cleanChipButton()
            binding.dateDirectInputButton.isChecked = true

            appearDateInput(true)

            dateType = 6

        }
    }

    // 직접입력칸 온오프 함수
    private fun appearDateInput(isInput: Boolean) {
        if (searchStartDate != null){
            binding.dateStartTextView.text = "${searchStartDate?.year}.${searchStartDate?.monthValue}.${searchStartDate?.dayOfMonth}"
            binding.dateEndTextView.text = "${searchEndDate?.year}.${searchEndDate?.monthValue}.${searchEndDate?.dayOfMonth}"
        }else{
            binding.dateStartTextView.text = "${currentDate?.year}.${currentDate?.monthValue}.${currentDate?.dayOfMonth}"
            binding.dateEndTextView.text = "${currentDate?.year}.${currentDate?.monthValue}.${currentDate?.dayOfMonth}"
        }

        binding.dateStartTextView.isVisible = isInput
        binding.dateEndTextView.isVisible = isInput
        binding.fromTextView.isVisible = isInput
    }

    // 칩 버튼 초기화
    private fun cleanChipButton() {
        binding.dateAllButton.isChecked = false
        binding.dateTodayButton.isChecked = false
        binding.dateWeekButton.isChecked = false
        binding.dateMonthButton.isChecked = false
        binding.dateTreeMonthButton.isChecked = false
        binding.dateSixMonthButton.isChecked = false
        binding.dateDirectInputButton.isChecked = false


    }

    // 조회 버튼 클릭 함수
    private fun clickSearchButton() {
        binding.searchButton.setOnClickListener {

            if (searchStartDate !=null && searchEndDate != null){
                searchStartDate = searchStartDate
                searchEndDate = searchEndDate
            }
            onClick(searchStartDate, searchEndDate, sort, dateType)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        if (isStart){
            binding.dateStartTextView.text = "$year.${month + 1}.$dayOfMonth"
            searchStartDate = LocalDate.of(year, month + 1, dayOfMonth)
        }
        else{
            binding.dateEndTextView.text = "$year.${month + 1}.$dayOfMonth"
            searchEndDate = LocalDate.of(year, month + 1, dayOfMonth)
        }

    }
}
