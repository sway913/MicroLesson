package east.orientation.microlesson.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
import east.orientation.microlesson.mvp.contract.OnlineContract;
import east.orientation.microlesson.mvp.presenter.OnlinePresenter;
import east.orientation.microlesson.ui.activity.GPlayActivity;
import east.orientation.microlesson.ui.fragment.base.BaseFragment;
import east.orientation.microlesson.utils.RxToast;

/**
 * @author ljq
 * @date 2019/1/18
 * @description
 */

public class OnlineFragment extends BaseFragment<OnlineContract.View,OnlineContract.Presenter> implements OnlineContract.View  {
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.rv_project)
    RecyclerView mRvProjects;

    private CommonAdapter<Project> mCommonAdapter;
    private EmptyWrapper<Project> mEmptyWrapper;

    private List<Project> mProjects;

    public static OnlineFragment newInstance() {

        Bundle args = new Bundle();

        OnlineFragment fragment = new OnlineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_online;
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
                presenter.down(mProjects.get(position));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
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
            presenter.refresh(mProjects);
            mRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    protected void initEventAndData() {
        presenter.refresh(mProjects);
    }

    @Override
    public void notifyChanged() {
        mEmptyWrapper.notifyDataSetChanged();
    }

    @Override
    public void showMessage(String msg) {
        RxToast.showToast(msg);
    }

    @Override
    public void downSuccess(Project project) {
//        Intent intent = new Intent();
//        intent.putExtra(Common.KEY_PROJECTS,project);
//        intent.setClass(_mActivity,PlayActivity.class);
//        startActivity(intent);

        Intent intent = new Intent();
        intent.putExtra(Common.KEY_PROJECTS,project);
        intent.setClass(_mActivity,GPlayActivity.class);
        startActivity(intent);
    }

    @NonNull
    @Override
    public OnlineContract.Presenter createPresenter() {
        return new OnlinePresenter();
    }
}
