package com.demo.sharingapp.domain.home.product

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.sharingapp.login.data.Products
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private var userId = SharedPreferencesData.getLongData(getApplication(), Constants.USER_ID)
    private var accessToken = SharedPreferencesData.getData(getApplication(), Constants.ACCESS_TOKEN)
    private var refreshToken = SharedPreferencesData.getData(getApplication(), Constants.REFRESH_TOKEN)

    // 경도
    private val _currentId = MutableLiveData<Long>()
    val currentId: LiveData<Long>
        get() = _currentId

    // 상품이미지
    private val _currentImageList = MutableLiveData<List<MultipartBody.Part>>()
    val currentImageList: LiveData<List<MultipartBody.Part>>
        get() = _currentImageList

    // 상품 제목
    private val _currentImageTitle = MutableLiveData<String>()
    val currentImageTitle: LiveData<String>
        get() = _currentImageTitle

    // 구입 가격
    private val _currentBuyPrice = MutableLiveData<Int>()
    val currentBuyPrice: LiveData<Int>
        get() = _currentBuyPrice

    // 구입 수량
    private val _currentBuyCount = MutableLiveData<Int>()
    val currentBuyCount: LiveData<Int>
        get() = _currentBuyCount

    // 공유 가격
    private val _currentSharePrice = MutableLiveData<Int>()
    val currentSharePrice: LiveData<Int>
        get() = _currentSharePrice

    // 공매 일자
    private val _currentBuyDate = MutableLiveData<String>()
    val currentBuyDate: LiveData<String>
        get() = _currentBuyDate

    // 공유 수량
    private val _currentShareCount = MutableLiveData<Int>()
    val currentShareCount: LiveData<Int>
        get() = _currentShareCount

    // 카테고리 Id
    private val _currentCategoryId = MutableLiveData<Int>()
    val currentCategoryId: LiveData<Int>
        get() = _currentCategoryId

    // 상세설명
    private val _currentDescription = MutableLiveData<String>()
    val currentDescription: LiveData<String>
        get() = _currentDescription

    // 경도
    private val _currentLongitude = MutableLiveData<Double>()
    val currentLongitude: LiveData<Double>
        get() = _currentLongitude

    // 경도
    private val _currentLatitude = MutableLiveData<Double>()
    val currentLatitude: LiveData<Double>
        get() = _currentLatitude

    // 영수증
    private val _currentReceipt = MutableLiveData<MultipartBody.Part>()
    val currentReceipt: LiveData<MultipartBody.Part>
        get() = _currentReceipt

    // 제품 데이터 업데이트 함수
    fun updateProductImage(
        title: String,
        buyPrice: Int,
        buyCount: Int,
        sharePrice: Int,
        shareCount: Int,
        description: String,
        longitude: Double,
        latitude: Double,
        categoryId: Int,
        buyDate: String,
    ) {
        this._currentImageTitle.value = title
        this._currentBuyPrice.value = buyPrice
        this._currentBuyCount.value = buyCount
        this._currentSharePrice.value = sharePrice
        this._currentShareCount.value = shareCount
        this._currentDescription.value = description
        this._currentLongitude.value = longitude
        this._currentLatitude.value = latitude
        this._currentCategoryId.value = categoryId
        this._currentBuyDate.value = buyDate
    }

    fun updateProductImage(productsImage: List<MultipartBody.Part>){
        Log.e("updata","상품 업데이트")
       this._currentImageList.value = productsImage
    }

    // 영수증 이미지 데이터 파일로 받기
    fun updateReceipt(receipt: MultipartBody.Part) {
        this._currentReceipt.value = receipt
    }

    // 상품 id 데이터  받기
    fun updateId(productId:Long) {
        this._currentId.value = productId
    }


    // 상품 등록하는 데이터 받기
    fun postProduct() {

        val products = Products(userId = userId,
            categoryId = currentCategoryId.value ?: return,
            title = currentImageTitle.value ?: return,
            buyPrice = currentBuyPrice.value ?: return,
            buyCount = currentBuyCount.value ?: return,
            sharePrice = currentSharePrice.value ?: return,
            shareCount = currentShareCount.value ?: return,
            buyDate = currentBuyDate.value ?: return,
            description = currentDescription.value ?: return,
            longitude = currentLongitude.value ?: return,
            latitude = currentLatitude.value ?: return)


        if (currentReceipt.value != null && currentImageList.value != null) {
            RetrofitManager.instance.postProduct(context = getApplication(),accessToken = accessToken, request = products,
                receipt = currentReceipt.value!!,
                photos = currentImageList.value!!)

        }


    }
    // 상품 등록하는 데이터 받기
    fun putProductModify() {

        val products = Products(userId = userId,
            categoryId = currentCategoryId.value ?: return,
            title = currentImageTitle.value ?: return,
            buyPrice = currentBuyPrice.value ?: return,
            buyCount = currentBuyCount.value ?: return,
            sharePrice = currentSharePrice.value ?: return,
            shareCount = currentShareCount.value ?: return,
            buyDate = currentBuyDate.value ?: return,
            description = currentDescription.value ?: return,
            longitude = currentLongitude.value ?: return,
            latitude = currentLatitude.value ?: return)


        if (currentReceipt.value != null && currentImageList.value != null) {
            RetrofitManager.instance.putProductModify(context = getApplication(),accessToken = accessToken, productsId = currentId.value!! ,request = products,
                receipt = currentReceipt.value!!,
                photos = currentImageList.value!!)

        }


    }

}