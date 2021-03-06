package com.aaron.android.framework.base.widget.refresh;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaron.android.framework.base.mvp.presenter.BasePresenter;
import com.aaron.android.framework.base.widget.recycleview.RecyclerItemDecoration;
import com.aaron.common.utils.ListUtils;
import com.aaron.android.framework.R;
import com.aaron.android.framework.base.widget.recycleview.BaseRecycleViewAdapter;

import java.util.List;

/**
 * Created on 16/8/24.
 *
 * @author aaron.huang
 * @version 1.0.0
 */
public abstract class NetworkSwipeRecyclerRefreshPagerLoaderFragment<T extends BasePresenter> extends BaseSwipeRefreshPagerLoaderFragment<T> {
    protected RecyclerView mRecyclerView;
    protected BaseRecycleViewAdapter mRecyclerViewAdapter;
    protected View mFooterView;

    @Override
    protected View createContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRecyclerView = createRecyclerView();
        if (mRecyclerView == null) {
            getDefaultRecyclerView();
        }
        mFooterView = inflater.inflate(R.layout.layout_network_footer, mRecyclerView, false);
        return mRecyclerView;
    }

    protected RecyclerView createRecyclerView() {
        return null;
    }

    protected void getDefaultRecyclerView() {
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isSlideToBottom(recyclerView) && allowPullUp()) {
                    loadNextPage();
                }
            }
        });
    }

    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        return !ViewCompat.canScrollVertically(recyclerView, 1);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setRecyclerViewPadding(int left, int top, int right, int bottom) {
        mRecyclerView.setPadding(left, top, right, bottom);
        mRecyclerView.setClipToPadding(false);
    }

    protected void setDivider(@DrawableRes int drawableId) {
        mRecyclerView.addItemDecoration(new RecyclerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, drawableId));
    }

    /**
     * 数据回调后，通知ListView更新
     *
     * @param listData DataListExtraResult
     */
    public void updateListView(List listData) {
        if (mRecyclerViewAdapter != null) {
            if (ListUtils.isEmpty(listData)) {
                if (isRequestHomePage()) {
                    clearListViewContent();
                    getStateView().setState(StateView.State.NO_DATA);
                } else {
                    setTotalPage(getCurrentPage());
                    mRecyclerViewAdapter.addFooterView(mFooterView);
                }
            } else {
                getStateView().setState(StateView.State.SUCCESS);
                if (isRequestHomePage()) {
                    mRecyclerViewAdapter.setData(listData);
                } else {
                    mRecyclerViewAdapter.addData(listData);
                }
                setTotalPage(getCurrentPage() + 1);
                mRecyclerViewAdapter.removeFooterView(mFooterView);
                mRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    protected void clearListViewContent() {
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.setData(null);
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    protected void setNoNextPageFooterView(View view) {
        mFooterView = view;
    }


    /**
     * @return 获取ListView适配器
     */
    public RecyclerView.Adapter getRecyclerAdapter() {
        return mRecyclerViewAdapter;
    }

    /**
     * 设置ListView适配器Adapter
     *
     * @param adapter BaseAdapter
     */
    public void setRecyclerAdapter(BaseRecycleViewAdapter adapter) {
        mRecyclerViewAdapter = adapter;
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    public void addRecyclerItemDecoration(RecyclerView.ItemDecoration decoration){
        mRecyclerView.addItemDecoration(decoration);
    }
}
