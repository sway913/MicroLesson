package east.orientation.microlesson.local.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import east.orientation.microlesson.R;
import east.orientation.microlesson.local.dao.Project;
import east.orientation.microlesson.utils.RxUtils;
import east.orientation.microlesson.utils.VideoUtil;
import io.reactivex.Observable;

/**
 * @author ljq
 * @date 2018/11/27
 */

public class ProjectAdapter extends CommonAdapter<Project> {


    public ProjectAdapter(Context context, int layoutId, List<Project> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, Project project, int position) {
        holder.setText(R.id.tv_user,project.getUserName());
        holder.setText(R.id.tv_title,project.getTitle());

        Observable.create(emitter -> {
            if (!TextUtils.isEmpty(project.getThumbnail())) {
                Bitmap bitmap = VideoUtil.getVideoThumbnail(project.getThumbnail(),1000*1000);
                if (bitmap != null) emitter.onNext(bitmap);
            }
        }).compose(RxUtils.rxSchedulerHelper()).subscribe(o -> {
            Glide.with(holder.getConvertView()).load(o).into((ImageView) holder.getView(R.id.iv));
        });
    }
}
