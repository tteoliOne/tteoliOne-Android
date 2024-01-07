package com.demo.sharingapp.domain.user.shareProductList

import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.demo.sharingapp.R
import kotlin.math.min
import kotlin.math.max

class SwipeHelper: ItemTouchHelper.Callback() {

    private var currentPosition: Int? = null
    private var previousPosition: Int? = null
    private var currentDx = 0f
    private var clamp = 0f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {

        val view = getItemView(viewHolder)
        clamp = view.width.toFloat()   // 아이템뷰 가로 길이의 비율로 clamp 설정도 가능!!!!
        // Drag와 Swipe 방향을 결정 Drag는 사용하지 않아 0, Swipe의 경우는 오른쪽에서 왼쪽으로만 가능하게 설정,   양방향 모두 하고 싶다면 'ItemTouchHelper.LEFT or ItemTouchHelper.Right'
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    // 요소와의 사용자 상호작용이 끝나고 애니메이션도 완료되면 ItemTouchHelper에 의해 호출됩니다.
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        //super.clearView(recyclerView, viewHolder)
        currentDx = 0f
        getDefaultUIUtil().clearView(getView(viewHolder))
        previousPosition = viewHolder.adapterPosition
    }

    // Called when the ViewHolder swiped or dragged by the ItemTouchHelper is changed =
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        //super.onSelectedChanged(viewHolder, actionState)
        viewHolder?.let {
            currentPosition = viewHolder.adapterPosition  // 현재 스와이프 한 아이템 위치
            getDefaultUIUtil().onSelected(getView(it))
        }
    }
    override fun onChildDraw( // 스와이프 동작 시 호출
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, // 스와이프를 시작한 터치 지점에서 얼만큼 좌우로 움직였는지! (왼쪽 = 음수, 오른쪽 = 양수)
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            Log.d("AppTest", "onChildDraw")

            val view = getView(viewHolder)
            val itemView = getItemView(viewHolder)

            val isClamped = getClamped(viewHolder as ShareProductListAdepter.ShareProductListViewHolder)

            val x = clampViewPositionHorizontal(view, dX, isClamped, isCurrentlyActive)
            //Log.d("AppTest", "dX : ${dX}, dY : ${dY}")

            currentDx = x
            Log.d("AppTest", "x : ${x}")

            getDefaultUIUtil().onDraw(
                c,
                recyclerView,
                view,
                x,     // dX 가 아닌 x 로 설정해야함!!!, x만큼 왼쪽으로 스와이프 뷰를 밀어서 그린다!
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }
    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        //return super.getSwipeEscapeVelocity(defaultValue)
        return defaultValue * 10
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        //return super.getSwipeThreshold(viewHolder)
        setClamped(viewHolder as ShareProductListAdepter.ShareProductListViewHolder, currentDx <= -clamp)
        return 2f
    }

    private fun clampViewPositionHorizontal(
        view: View,
        dX: Float,  //
        isClamped: Boolean,
        isCurrentlyActive: Boolean // 스와이프 중인지 , 손 떼면 false 된다
    ) : Float {
        // View의 가로 길이의 30% 만 스와이프 되도록
        val maxSwipe: Float = -view.width.toFloat()  // 음수 값!!,  xml 상에서 삭제 영역이 아이템뷰 width의 0.3 만큼 차지하도록 설정한 것과 맞추기 위함

        // RIGHT 방향으로 swipe 막기
        val right: Float = 0f

        val x = if (isClamped) {
            // View가 고정되었을 때 swipe되는 영역 제한
            if (isCurrentlyActive) dX - clamp else -clamp
            // 스와이프 된 고정 상태에서 dX - clamp 가 maxSwipe 이하인 상태에서 터치 유지 해제(왼쪽 스와이프 시도 중) -> maxSwipe 리턴
            // 스와이프 된 고정 상태에서 dx - clamp 가 maxSwipe 이상인 상태에서 터치 유지 해제(오른쪽 스와이프 시도 중) -> isClamped 에 false 값이 들어올 것임 -> currentDx가 -clamp 보다 커진다!
        } else {
            dX   // maxSwipe보다 더 많이 왼쪽으로 스와이프 해도 maxSwipe 까지만 스와이프 된다(이때 isClamped는 true 된다) / 밑에서 maxSwipe & x 중에 큰 값을 사용하기 때문
        }

        return min(max(maxSwipe, x), right) // right = 항상 0 이므로  min 함수에서 최대는 항상 0 값이 나온다 -> 스와이프를 통해 오른쪽으로 밀리는 일은 없다!!
    }

    private fun setClamped(viewHolder: ShareProductListAdepter.ShareProductListViewHolder, isClamped: Boolean){
        viewHolder.setClamped(isClamped)
    }

    private fun getView(viewHolder: RecyclerView.ViewHolder): View {
        return (viewHolder as ShareProductListAdepter.ShareProductListViewHolder).itemView.findViewById(
            R.id.productLayout) // 아이템뷰에서 스와이프 영역에 해당하는 뷰 가져오기
    }

    private fun getItemView(viewHolder: RecyclerView.ViewHolder): View {
        return (viewHolder as ShareProductListAdepter.ShareProductListViewHolder).itemView.findViewById(
            R.id.deleteButton) // 아이템뷰에서 스와이프 영역에 해당하는 뷰 가져오기
    }

    private fun getClamped(viewHolder: ShareProductListAdepter.ShareProductListViewHolder): Boolean{
        return viewHolder.getClamped()
    }
}