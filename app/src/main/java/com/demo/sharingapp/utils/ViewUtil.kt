package com.demo.sharingapp.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object ViewUtil {
    fun EditText.setOnEditorActionListener(action : Int, invoke: () -> Unit){
        setOnEditorActionListener { _, actionId, _ ->
            if(actionId == action){
                invoke()
                true
            }else{
                false
            }
        }
    }

    fun EditText.showKeyboard(){
        this.requestFocus()
        val inputMethodManger = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManger.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun EditText.showKeyboardDelay() {
        postDelayed({
            showKeyboard()
        },200)
    }

    fun EditText.hideKeyboard(){
        this.clearFocus()
        val inputMethodManger = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManger.hideSoftInputFromWindow(this.windowToken, 0)
    }
}