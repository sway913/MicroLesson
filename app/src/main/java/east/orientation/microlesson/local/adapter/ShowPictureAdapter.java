package east.orientation.microlesson.local.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.entity.LocalMedia;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.recyclerview.utils.WrapperUtils;

import java.util.List;

import east.orientation.microlesson.R;

/**
 * @author ljq
 * @date 2018/12/12
 * @description
 */

public class ShowPictureAdapter extends CommonAdapter<LocalMedia> {

    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();

    public ShowPictureAdapter(Context context, int layoutId, List<LocalMedia> datas) {
        super(context, layoutId, datas);

    }

    @Override
    protected void convert(ViewHolder holder, LocalMedia localMedia, int position) {
        if (!TextUtils.isEmpty(localMedia.getCompressPath())) {
            Glide.with(mContext).load(localMedia.getCompressPath()).into((ImageView) holder.getView(R.id.iv_show));
        } else if (!TextUtils.isEmpty(localMedia.getPath())) {
            Glide.with(mContext).load(localMedia.getPath()).into((ImageView) holder.getView(R.id.iv_show));
        } else {
            Glide.with(mContext).load(R.drawable.sp_pic_background).into((ImageView) holder.getView(R.id.iv_show));
        }

        holder.getView(R.id.iv_delete).setOnClickListener(v -> {
            mDatas.remove(position);
            notifyItemRemoved(position);
            if (position != mDatas.size()) {
                notifyItemRangeChanged(position, mDatas.size() - position);
            } else {
                notifyItemRangeChanged(mDatas.size()-1, 1);
            }
        });
        if (mDatas.size() >= 9) {
            for (int i = BASE_ITEM_TYPE_FOOTER; i < mFootViews.size()+BASE_ITEM_TYPE_FOOTER; i++) {
                mFootViews.get(i).setVisibility(View.GONE);
            }
        } else {
            for (int i = BASE_ITEM_TYPE_FOOTER; i < mFootViews.size()+BASE_ITEM_TYPE_FOOTER; i++) {
                mFootViews.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null)
        {
            return ViewHolder.createViewHolder(parent.getContext(), mHeaderViews.get(viewType));

        } else if (mFootViews.get(viewType) != null)
        {
            return ViewHolder.createViewHolder(parent.getContext(), mFootViews.get(viewType));
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position)
    {
        if (isHeaderViewPos(position))
        {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position))
        {
            return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return super.getItemViewType(position - getHeadersCount());
    }

    private int getRealItemCount()
    {
        return super.getItemCount();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (isHeaderViewPos(position))
        {
            return;
        }
        if (isFooterViewPos(position))
        {
            return;
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount()
    {
        return getHeadersCount() + getFootersCount() + getRealItemCount();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position))
        {
            WrapperUtils.setFullSpan(holder);
        }
    }

    private boolean isHeaderViewPos(int position)
    {
        return position < getHeadersCount();
    }

    private boolean isFooterViewPos(int position)
    {
        return position >= getHeadersCount() + getRealItemCount();
    }


    public void addHeaderView(View view)
    {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addFootView(View view)
    {
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    public int getHeadersCount()
    {
        return mHeaderViews.size();
    }

    public int getFootersCount()
    {
        return mFootViews.size();
    }
}
