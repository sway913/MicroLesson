package east.orientation.microlesson.local.adapter;

import android.content.Context;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import east.orientation.microlesson.local.model.Project;

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

    }
}
