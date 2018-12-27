package east.orientation.microlesson.local.adapter;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Collections;
import java.util.List;


/**
 * @author ljq
 * @date 2018/12/17
 * @description
 */

public class SortItemTouchHelperCallback<T> extends ItemTouchHelper.Callback {

    private RecyclerView.ViewHolder vh;
    private RecyclerView.Adapter mAdapter;
    private List<T> mDatas;
    private Context mContext;

    public SortItemTouchHelperCallback(Context context,RecyclerView.Adapter adapter, List<T> datas) {
        this.mContext = context;
        this.mAdapter = adapter;
        this.mDatas = datas;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag = 0;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager){
            dragFlag = ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
        }else if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            dragFlag = ItemTouchHelper.UP|ItemTouchHelper.DOWN;
        }
        return makeMovementFlags(dragFlag,0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //得到当拖拽的viewHolder的Position
        int fromPosition = viewHolder.getAdapterPosition();
        if (fromPosition >= mDatas.size()) return false;
        //拿到当前拖拽到的item的viewHolder
        int toPosition = target.getAdapterPosition();
        if (toPosition >= mDatas.size()) return false;

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mDatas, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mDatas, i, i - 1);
            }
        }
        mAdapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //侧滑删除可以使用；
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
    /**
     * 长按选中Item的时候开始调用
     * 长按高亮
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            pickUpAnimation(viewHolder.itemView);
            //获取系统震动服务//震动70毫秒
            Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(70);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 手指松开的时候还原高亮
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(0);
        mAdapter.notifyDataSetChanged();  //完成拖动后刷新适配器，这样拖动后删除就不会错乱
    }



    private void pickUpAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationZ", 5f, 50f);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(300);
        animator.start();
    }

    private void putDownAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationZ", 10f, 1f);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(300);
        animator.start();
    }
}
