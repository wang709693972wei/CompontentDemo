package com.moudlea;

import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.android.common.base.BaseActivity;
import com.android.common.base.BasePresenter;
import com.android.common.router.ActivityPath;

/**
 * Created by wangwei on 2018/4/17.
 */

@Route(path = ActivityPath.AModuleActivity)
public class AModuleActivity extends BaseActivity {
    TextView txt;



    @Override
    protected void initViews() {
        txt = findViewById(R.id.txt);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int initLayout() {
        return R.layout.le_layout;
    }




    @Override
    protected void initData() {

    }
}
