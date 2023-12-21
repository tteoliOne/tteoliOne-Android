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

class HomePartProductDialog(
     val onClick:(String?, String?, String)->Unit
) : DialogFragment(),DatePickerDialog.OnDateSetListener {
    // 뷰 바인딩 정의
    private var _binding: DialogPartProductBinding? = null
    private val binding get() = _binding!!

    private var text: String? = null

    private var isStart = false
    private var dateType = 0
    private var sort = "createAt-asc"
    private var startDate : LocalDate? = null
    private var endDate : LocalDate? = null

    private var searchStartDate : String? = null
    private var searchEndDate: String? = null


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


        // 조회 버튼 클릭
        clickSearchButton()

        // 기간 선택
        clickDateButton()

        // 정렬순서 -  과거순 클릭
        binding.pastTextView.setOnClickListener {
            binding.latestView.isVisible=false
            binding.pastView.isVisible = true
            sort = "createAt-desc"
        }

        // 정렬순서 - 최신순 클릭
        binding.latestTextView.setOnClickListener {
            binding.pastView.isVisible=false
            binding.latestView.isVisible = true
            sort = "createAt-asc"
        }

        binding.dateStartTextView.text = "${endDate?.year}.${endDate?.monthValue}.${endDate?.dayOfMonth}"
        binding.dateEndTextView.text = "${endDate?.year}.${endDate?.monthValue}.${endDate?.dayOfMonth}"

        binding.dateStartTextView.setOnClickListener {
            isStart = true
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            context?.let {
                DatePickerDialog(
                    it,
                    this,
                    year,
                    month,
                    day
                ).show()
            }
        }

        binding.dateEndTextView.setOnClickListener {
            isStart = false
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            context?.let {
                DatePickerDialog(
                    it,
                    this,
                    year,
                    month,
                    day
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
            startDate = null
            endDate = null
        }

        // 기간선택 - 1일 칩 클릭
        binding.dateTodayButton.setOnClickListener {
            cleanChipButton()
            binding.dateTodayButton.isChecked = true
            appearDateInput(false)
            startDate = endDate?.minusDays(1)

            Log.e("currentDate",startDate.toString())
        }

        // 기간선택 - 일주일 칩 클릭
        binding.dateWeekButton.setOnClickListener {
            cleanChipButton()
            binding.dateWeekButton.isChecked = true
            appearDateInput(false)
            startDate = endDate?.minusDays(7)
        }

        // 기간선택 - 1개월 칩 클릭
        binding.dateMonthButton.setOnClickListener {
            cleanChipButton()
            binding.dateMonthButton.isChecked = true
            appearDateInput(false)
            startDate = endDate?.minusMonths(1)
        }

        // 기간선택 - 3개월 칩 클릭
        binding.dateTreeMonthButton.setOnClickListener {
            cleanChipButton()
            binding.dateTreeMonthButton.isChecked = true
            appearDateInput(false)
            startDate = endDate?.minusMonths(3)
        }

        // 기간선택 - 6개월 칩 클릭
        binding.dateSixMonthButton.setOnClickListener {
            cleanChipButton()
            binding.dateSixMonthButton.isChecked = true
            appearDateInput(false)
            startDate = endDate?.minusMonths(6)
        }

        // 기간선택 - 직접 입력 칩 클릭
        binding.dateDirectInputButton.setOnClickListener {
            // 칩 버튼 초기화
            cleanChipButton()
            binding.dateDirectInputButton.isChecked = true

            appearDateInput(true)

        }
    }

    // 직접입력칸 온오프 함수
    private fun appearDateInput(isInput: Boolean) {
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
        endDate = LocalDate.now()
    }

    // 조회 버튼 클릭 함수
    private fun clickSearchButton() {
        binding.searchButton.setOnClickListener {

            if (startDate !=null && endDate != null){
                searchStartDate = String.format("%d%02d%02d",startDate?.year,startDate?.monthValue,startDate?.dayOfMonth)
                searchEndDate = String.format("%d%02d%02d",endDate?.year,endDate?.monthValue,endDate?.dayOfMonth)
            }
            onClick(searchStartDate, searchEndDate, sort)
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
            startDate = LocalDate.of(year, month + 1, dayOfMonth)
        }
        else{
            binding.dateEndTextView.text = "$year.${month + 1}.$dayOfMonth"
            endDate = LocalDate.of(year, month + 1, dayOfMonth)
        }

    }
}
