package com.shenzhen532.qunkong;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import com.shenzhen532.qunkong.media.h264data;
import com.shenzhen532.qunkong.screen.ScreenRecord;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by wei on 16-9-18.
 * <p>
 * 完全透明 只是用于弹出权限申请的窗而已
 *
 * 这个类的用于service需要在manifest中配置 intent-filter action
 */
public class ScreenShotActivity extends Activity {

    public static final int REQUEST_MEDIA_PROJECTION = 0x2893;
    private MediaProjectionManager mMediaProjectionManager;
    private ScreenRecord mScreenRecord;
    private static int queuesize = 30;
    public static ArrayBlockingQueue<h264data> h264Queue = new ArrayBlockingQueue<>(queuesize);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        setTheme(android.R.style.Theme_Dialog);//这个在这里设置 之后导致 的问题是 背景很黑
        super.onCreate(savedInstanceState);

        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        //如下代码 只是想 启动一个透明的Activity 而上一个activity又不被pause
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0f);

        requestScreenShot();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();




        mScreenRecord.release();
    }

    public void requestScreenShot() {
        if (Build.VERSION.SDK_INT >= 21) {
            startActivityForResult(createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        } else {
            toast("版本过低,无法截屏");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Intent createScreenCaptureIntent() {
        //这里用media_projection代替Context.MEDIA_PROJECTION_SERVICE 是防止低于21 api编译不过
        return mMediaProjectionManager.createScreenCaptureIntent();
    }

    private void toast(String str) {
        Toast.makeText(ScreenShotActivity.this, str, Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION: {
                if (resultCode == RESULT_OK && data != null) {
                    Log.i("data","1111111111111111");

//                    Shotter shotter = new Shotter(ScreenShotActivity.this, resultCode, data);
//                    shotter.startScreenShot(new Shotter.OnShotListener() {
//                        @Override
//                        public void onFinish() {
//                            toast("shot finish!");
//                            finish(); // don't forget finish activity
//                        }
//                    });
                    MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
                    if(mediaProjection == null){
                        Toast.makeText(this,"程序发生错误:MediaProjection@1",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mScreenRecord = new ScreenRecord(this,mediaProjection);
                    mScreenRecord.start();




                } else if (resultCode == RESULT_CANCELED) {
                    toast("shot cancel , please give permission.");
                } else {
                    toast("unknow exceptions!");
                }
            }
        }
    }

    public static void putData(byte[] buffer, int type,long ts) {
        if (h264Queue.size() >= queuesize) {
            h264Queue.poll();
        }
        h264data data = new h264data();
        data.data = buffer;
        data.type = type;
        data.ts = ts;
        h264Queue.add(data);
    }


    public static void sendData(byte[] buffer){

        TcpClient.sendTcpByte(buffer);
    }


}