package east.orientation.microlesson.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import east.orientation.microlesson.mvp.contract.DraftContract;
import east.orientation.microlesson.mvp.presenter.DraftPresenter;
import east.orientation.microlesson.ui.activity.GPlayActivity;
import east.orientation.microlesson.ui.fragment.base.BaseFragment;

/**
 * @author ljq
 * @date 2018/12/11
 * @description
 */

public class DraftBoxFragment extends BaseFragment<DraftContract.View,DraftContract.Presenter> implements DraftContract.View {

    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.rv_drafts)
    RecyclerView mRvDrafts;

    private CommonAdapter<Project> mCommonAdapter;
    private EmptyWrapper<Project> mEmptyWrapper;
    private List<Project> mProjects;

    public static DraftBoxFragment newInstance() {
        
        Bundle args = new Bundle();
        
        DraftBoxFragment fragment = new DraftBoxFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_draft;
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
                intent.setClass(_mActivity, GPlayActivity.class);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        mEmptyWrapper = new EmptyWrapper<>(mCommonAdapter);
        mEmptyWrapper.setEmptyView(R.layout.default_empty_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(_mActivity,2);
        mRvDrafts.setLayoutManager(gridLayoutManager);
        mRvDrafts.setHasFixedSize(true);
        mRvDrafts.setAdapter(mEmptyWrapper);
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
        presenter.getProject(mProjects);
    }

    @Override
    public void notifyChange() {
        mEmptyWrapper.notifyDataSetChanged();
    }

    @Override
    public DraftContract.Presenter createPresenter() {
        return new DraftPresenter();
    }
}
