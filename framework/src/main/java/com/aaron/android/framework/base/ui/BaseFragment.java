package com.aaron.android.framework.base.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.aaron.android.framework.utils.PopupUtils;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * Created on 2017/6/1.
 *
 * @author aaron.huang
 * @version 1.0.0
 */
public abstract class BaseFragment extends Fragment {

    public final String TAG = getClass().getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isEventTarget() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    protected void startActivity(Class<?> cls) {
        if (getActivity() == null) {
            return;
        }
        startActivity(new Intent(getActivity(), cls));
    }
    @Override
    public void onDestroy() {
        if (isEventTarget() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    protected void postEvent(Object object) {
        EventBus.getDefault().post(object);
    }

    protected boolean isEventTarget() {
        return false;
    }

    public void showToast(String message) {
        PopupUtils.showToast(getActivity(), message);
    }

    public void showToast(int resId) {
        PopupUtils.showToast(getActivity(), resId);
    }
}
