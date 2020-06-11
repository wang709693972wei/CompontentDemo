package com.basemodule.net;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.basemodule.BaseApplication;
import com.basemodule.R;
import com.basemodule.ww_test.net.LoginService;
import com.basemodule.ww_test.net.MessageCodeEntity;
import com.basemodule.ww_test.net.ZNetwork;
import com.basemodule.ww_test.net.ZResponse;
import com.basemodule.ww_test.net.utils.Callback;
import com.basemodule.ww_test.net.utils.ZNetworkCallback;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;


public class NewNetActivity extends RxAppCompatActivity {
    private TextView text;
    private Button button1, go;
    // https://www.wanandroid.com/article/list/0/json?cid=60


    /*使用bindToLifecycle()
    https://www.jianshu.com/p/8311410de676
以Activity为例，在Activity中使用bindToLifecycle()方法，完成Observable发布的事件和当前的组件绑定，实现生命周期同步。从而实现当前组件生命周期结束时，自动取消对Observable订阅，代码如下：
*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        initViews();
        // 当执行onDestory()时， 自动解除订阅
      /*  Observable.interval(3, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.w("TAG", "Unsubscribing subscription from onCreate()");
                    }
                })
                //bindUntilEvent()，内部传入指定生命周期参数
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                //  .compose(this.<Long>bindToLifecycle())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        Log.w("TAG", "Started in onCreate(), running until onDestory(): " + num);
                    }
                });
*/
    }

    protected void initViews() {
        text = findViewById(R.id.text);
        button1 = findViewById(R.id.button1);
        go = findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   testDestoryRequest();
                //  startRequest();
                startRequestGet();
            }
        });

    }

    /**
     * 测试关闭Activity 后 自动取消订阅
     */
    private void testDestoryRequest() {
        Observable observable = Observable.interval(3, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.w("TAG", "Unsubscribing subscription from onCreate()");
                    }
                });
        ZNetwork.with(this)
                .api(observable)
                .callback(new Callback<Long>() {
                    @Override
                    public void onNext(Long response) {
                        Log.w("TAG", "next" + response);

                    }
                });

    }


    private void startRequestPost() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("phone", "18565851235");
     /*   Observable<ZResponse<MessageCodeEntity>> observable
                = ZNetwork.getService(LoginService.class)
                .login(params);
*/
        ZNetwork.with(this)
                .api(ZNetwork.getService(LoginService.class).login(params))
                .callback(new ZNetworkCallback<ZResponse<MessageCodeEntity>>() {

                    @Override
                    public void onBusinessSuccess(ZResponse<MessageCodeEntity> response) {
                        if (response.data != null) {
                            text.setText("" + new Gson().toJson(response.data));
                            Toast.makeText(BaseApplication.getInstance(), response.data.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BaseApplication.getInstance(), "登录成功", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onBusinessError(int errorCode, String errorMessage) {
                        text.setText(errorCode + " " + errorMessage);
                        Toast.makeText(BaseApplication.getInstance(), errorMessage, Toast.LENGTH_SHORT).show();
                    }

                });

    }

    private void startRequestGet() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("beLikeMemberId", "0");
        params.put("likeMemberId", "1256981313");

        ZNetwork.with(this)
                .api(ZNetwork.getService(LoginService.class).template(params))
                .callback(new ZNetworkCallback<ZResponse<String>>() {

                    @Override
                    public void onBusinessSuccess(ZResponse<String> response) {
                        if (response.data != null) {
                            text.setText("" + new Gson().toJson(response.data));
                            Toast.makeText(BaseApplication.getInstance(), response.data.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BaseApplication.getInstance(), "登录成功", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onBusinessError(int errorCode, String errorMessage) {
                        text.setText(errorCode + " " + errorMessage);
                        Toast.makeText(BaseApplication.getInstance(), errorMessage, Toast.LENGTH_SHORT).show();
                    }

                });

    }

}


