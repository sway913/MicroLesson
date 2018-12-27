package east.orientation.microlesson.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;
import com.zhy.adapter.recyclerview.wrapper.LoadMoreWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import east.orientation.microlesson.R;
import east.orientation.microlesson.local.adapter.ProjectAdapter;
import east.orientation.microlesson.local.model.Project;
import east.orientation.microlesson.mvp.contract.ShowContract;
import east.orientation.microlesson.mvp.presenter.ShowPresenter;
import east.orientation.microlesson.ui.fragment.base.BaseFragment;

/**
 * @author ljq
 * @date 2018/11/27
 */

public class ShowFragment extends BaseFragment<ShowContract.View,ShowContract.Presenter> implements ShowContract.View {
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.rv_project)
    RecyclerView mRvProjects;

    private CommonAdapter<Project> mCommonAdapter;
    private EmptyWrapper<Project> mEmptyWrapper;
    private LoadMoreWrapper<Project> mLoadMoreWrapper;
    private List<Project> mProjects;

    public static ShowFragment newInstance() {

        Bundle args = new Bundle();

        ShowFragment fragment = new ShowFragment();
        fragment.setArguments(args);
        return fragment;
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
        mEmptyWrapper = new EmptyWrapper<>(mCommonAdapter);
        mEmptyWrapper.setEmptyView(R.layout.default_empty_view);

        mLoadMoreWrapper = new LoadMoreWrapper<>(mEmptyWrapper);
        mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
        mLoadMoreWrapper.setOnLoadMoreListener(()->{
            // 加载更多监听

        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(_mActivity,3);
        mRvProjects.setLayoutManager(gridLayoutManager);
        mRvProjects.setHasFixedSize(true);
        mRvProjects.setAdapter(mLoadMoreWrapper);
    }

    private void initRefresh() {
        mRefreshLayout.setOnRefreshListener(()->{
            // 刷新监听


            mRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_show;
    }

    @Override
    protected void initEventAndData() {

    }

    @NonNull
    @Override
    public ShowContract.Presenter createPresenter() {
        return new ShowPresenter();
    }
}
