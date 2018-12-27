package east.orientation.microlesson.ui.fragment;

import android.os.Bundle;

import east.orientation.microlesson.R;
import east.orientation.microlesson.mvp.contract.DraftContract;
import east.orientation.microlesson.mvp.presenter.DraftPresenter;
import east.orientation.microlesson.ui.fragment.base.BaseFragment;

/**
 * @author ljq
 * @date 2018/12/11
 * @description
 */

public class DraftBoxFragment extends BaseFragment<DraftContract.View,DraftContract.Presenter> implements DraftContract.View {

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
    protected void initEventAndData() {

    }

    @Override
    public DraftContract.Presenter createPresenter() {
        return new DraftPresenter();
    }
}
