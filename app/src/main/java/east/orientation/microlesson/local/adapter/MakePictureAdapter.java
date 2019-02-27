package east.orientation.microlesson.local.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.entity.LocalMedia;
import com.stroke.view.InkCanvas;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import east.orientation.microlesson.R;

/**
 * @author ljq
 * @date 2018/12/13
 * @description
 */

public class MakePictureAdapter extends CommonAdapter<LocalMedia> {

    public MakePictureAdapter(Context context, int layoutId, List<LocalMedia> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, LocalMedia localMedia, int position) {
        //Glide.with(holder.getConvertView()).load(localMedia.getPath()).into((ImageView) holder.getView(R.id.iv_show));
        InkCanvas inkCanvas = holder.getView(R.id.ink_canvas);
        inkCanvas.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Log.e("tag"," position: "+position +"height "+inkCanvas.getCanvasHeight()+" -path- "+localMedia.getPath());
                if (!TextUtils.isEmpty(localMedia.getPath())) {
                    inkCanvas.drawPicBackGround(localMedia.getPath(),inkCanvas.getCanvasWidth(),inkCanvas.getCanvasHeight());
                }
                inkCanvas.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
}
