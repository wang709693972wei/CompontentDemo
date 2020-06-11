package com.basemodule.net;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basemodule.BaseApplication;
import com.basemodule.R;
import com.basemodule.utils.GlideUtils;
import com.basemodule.ww_test.net.LoginService;
import com.basemodule.ww_test.net.MessageCodeEntity;
import com.basemodule.ww_test.net.ZNetwork;
import com.basemodule.ww_test.net.ZResponse;
import com.basemodule.ww_test.net.fileLoad.MediaUploadResponse;
import com.basemodule.ww_test.net.fileLoad.callback.ZUploadCallback;
import com.basemodule.ww_test.net.fileLoad.upload.entity.UploadInfo;
import com.basemodule.ww_test.net.utils.Callback;
import com.basemodule.ww_test.net.utils.GlideEngine;
import com.basemodule.ww_test.net.utils.ZNetworkCallback;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import okhttp3.RequestBody;


public class NewNetActivity extends RxAppCompatActivity {
    private TextView text;
    private Button button1, go;
    // https://www.wanandroid.com/article/list/0/json?cid=60
    ImageView image;

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
        image = findViewById(R.id.image);
        text = findViewById(R.id.text);
        button1 = findViewById(R.id.button1);
        button1.setText("图片选择");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(NewNetActivity.this)
                        .openGallery(PictureMimeType.ofImage())
                        .loadImageEngine(GlideEngine.createGlideEngine()) // Please refer to the Demo GlideEngine.java
                        .forResult(PictureConfig.CHOOSE_REQUEST);

            }
        });
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // onResult Callback
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    String path = selectList.get(0).getPath();
                    Log.w("TAG", "--path-" + path);
                    GlideUtils.loadImage(NewNetActivity.this, image, path);
                    uploadVideo(path);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 上传视频
     *
     * @param mediaPath 文件路径
     */
    public void uploadVideo(String mediaPath) {
        if (mediaPath == null || TextUtils.isEmpty(mediaPath)) {
            return;
        }
        File file = new File(mediaPath);
        if (!file.exists()) {
            Log.w("TAG", "文件不存在");
            return;
        }
        UploadInfo.FileAndParamName fileAndParamName = new UploadInfo.FileAndParamName(file, "multipartFile");
        UploadInfo<ZResponse<MediaUploadResponse>> uploadInfo = new UploadInfo<ZResponse<MediaUploadResponse>>(fileAndParamName) {
            @Override
            public Observable<ZResponse<MediaUploadResponse>> getApi(HashMap<String, RequestBody> params) {
                Log.w("TAG", "---params-" + new Gson().toJson(params));
                return ZNetwork.getUploadService(LoginService.class).upload(params);
            }
        };
      /*  HashMap<String, Object> params = new HashMap<>();
        params.put("memberId", "" + 1256981313);
        params.put("multipartFile", file);
        */
        ZNetwork.with(this)
                .upload(uploadInfo)
                .callback(new ZUploadCallback<ZResponse<MediaUploadResponse>>() {
                    @Override
                    public void onBegin() {
                    }

                    @Override
                    public void onProgress(int index, long allProgress, long allTotalProgress,
                                           long currentOneProgress, long currentOneTotalProgress, boolean done) {
                        // view.onProgress((int) ((float) allProgress / allTotalProgress * 100));
                    }

                    @Override
                    public void onBusinessSuccess(ZResponse<MediaUploadResponse> response) {
                        Bundle bundle = new Bundle();
                        if (response.data != null) {

                        }
                    }

                    @Override
                    public void onEnd() {
                        //view.hideLoading();
                    }
                });
    }

}


