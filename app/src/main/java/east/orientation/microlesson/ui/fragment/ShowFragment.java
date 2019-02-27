package east.orientation.microlesson.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import east.orientation.microlesson.R;
import east.orientation.microlesson.local.Common;
import east.orientation.microlesson.local.adapter.ProjectAdapter;
import east.orientation.microlesson.local.dao.Project;
import east.orientation.microlesson.mvp.contract.ShowContract;
import east.orientation.microlesson.mvp.presenter.ShowPresenter;
import east.orientation.microlesson.ui.activity.GPlayActivity;
import east.orientation.microlesson.ui.fragment.base.BaseFragment;
import east.orientation.microlesson.utils.RxToast;

/**
 * @author ljq
 * @date 2018/11/27
 */

public class ShowFragment extends BaseFragment<ShowContract.View,ShowContract.Presenter> implements ShowContract.View {
    @BindView(R.id.tv_user)
    TextView mTvUser;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.rv_project)
    RecyclerView mRvProjects;

    private CommonAdapter<Project> mCommonAdapter;
    private EmptyWrapper<Project> mEmptyWrapper;
    private List<Project> mProjects;

    public static ShowFragment newInstance() {

        Bundle args = new Bundle();

        ShowFragment fragment = new ShowFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_show;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        // 初始化recyclerView
        initRecycler();
        // 初始化RefreshLayout
        initRefresh();
    }

    private void initRecycler() {
        mProjects = new ArrayList<>();
        mCommonAdapter = new ProjectAdapter(_mActivity,R.layout.layout_project_item,mProjects);
        mCommonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Intent intent = new Intent();
                intent.putExtra(Common.KEY_PROJECTS,mProjects.get(position));
                intent.setClass(_mActivity,GPlayActivity.class);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                new AlertDialog.Builder(_mActivity)
                        .setTitle("Tips")
                        .setMessage("是否删除？")
                        .setPositiveButton("确认",(dialog, which) -> presenter.deleteProject(mProjects.get(position),position))
                        .setNegativeButton("取消",(dialog, which) -> dialog.dismiss())
                        .show();
                return false;
            }
        });

        mEmptyWrapper = new EmptyWrapper<>(mCommonAdapter);
        mEmptyWrapper.setEmptyView(R.layout.default_empty_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(_mActivity,2);
        mRvProjects.setLayoutManager(gridLayoutManager);
        mRvProjects.setHasFixedSize(true);
        mRvProjects.setAdapter(mEmptyWrapper);
    }

    private void initRefresh() {
        mRefreshLayout.setOnRefreshListener(()-> {
            // 刷新监听
            presenter.getProject(mProjects);
            mRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    protected void initEventAndData() {
        presenter.actionGetUser(_mActivity);
        presenter.getProject(mProjects);
    }

    @Override
    public void updateUser(String name) {
        mTvUser.setText(name);
    }

    @Override
    public void updateList() {
        presenter.getProject(mProjects);
    }

    @Override
    public void showDeleteMsg(String msg) {
        RxToast.showToast(msg);
    }

    @Override
    public void removeItem(int position) {
        mProjects.remove(position);
        mEmptyWrapper.notifyItemRemoved(position);
        if (position != mProjects.size()) {
            mEmptyWrapper.notifyItemRangeChanged(position, mProjects.size() - position);
        } else {
            mEmptyWrapper.notifyItemRangeChanged(mProjects.size()-1, 1);
        }
    }

    @Override
    public void notifyChange() {
        mEmptyWrapper.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShowContract.Presenter createPresenter() {
        return new ShowPresenter();
    }
}
