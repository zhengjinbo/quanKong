package com.shenzhen532.qunkong.media;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;


import com.shenzhen532.qunkong.ScreenShotActivity;
import com.shenzhen532.qunkong.screen.Constant;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by zpf on 2018/3/7.
 */

public class VideoMediaCodec extends MediaCodecBase {

    private final static String TAG = "VideoMediaCodec";


    private Surface mSurface;
    private long startTime = 0;
    private int TIMEOUT_USEC = 12000;
    public byte[] configbyte;

    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test1.h264";
    private BufferedOutputStream outputStream;
    FileOutputStream outStream;
    private void createfile(){
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * **/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public VideoMediaCodec(){
        //createfile();
        prepare();
    }

    public Surface getSurface(){
        return mSurface;
    }

    public void isRun(boolean isR){
        this.isRun = isR;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void prepare(){
        try{
            MediaFormat format = MediaFormat.createVideoFormat(Constant.MIME_TYPE, Constant.VIDEO_WIDTH, Constant.VIDEO_HEIGHT);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_BIT_RATE, Constant.VIDEO_BITRATE);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, Constant.VIDEO_FRAMERATE);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, Constant.VIDEO_IFRAME_INTER);
            mEncoder = MediaCodec.createEncoderByType(Constant.MIME_TYPE);
            mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mSurface = mEncoder.createInputSurface();
            mEncoder.start();
        }catch (IOException e){

        }
    }

    @Override
    public void release() {
        this.isRun = false;

    }


    /**
     * 获取h264数据
     * **/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void getBuffer(){
        try
        {
            while(isRun){
                if(mEncoder == null)
                    break;

                MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
                int outputBufferIndex  = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
                while (outputBufferIndex >= 0){
                    ByteBuffer outputBuffer = mEncoder.getOutputBuffers()[outputBufferIndex];
                    byte[] outData = new byte[mBufferInfo.size];
                    outputBuffer.get(outData);
                    if(mBufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG){
                        configbyte = new byte[mBufferInfo.size];
                        configbyte = outData;
                    }
//                    else{
//                        MainActivity.putData(outData,2,mBufferInfo.presentationTimeUs*1000L);
//                    }

                    else if(mBufferInfo.flags == 1){
                        byte[] keyframe = new byte[mBufferInfo.size + configbyte.length];
                        System.arraycopy(configbyte, 0, keyframe, 0, configbyte.length);
                        System.arraycopy(outData, 0, keyframe, configbyte.length, outData.length);
                        Log.i("keyframe",keyframe.toString());
                        ScreenShotActivity.sendData(keyframe);
                  //      writeBytesToFile(keyframe);
                      //  ScreenShotActivity.putData(keyframe,1,mBufferInfo.presentationTimeUs*1000L);
//                        if(outputStream != null){
//                            outputStream.write(keyframe, 0, keyframe.length);
//                        }
                    }else{
                        Log.i("keyframe1",outData.toString());
                        ScreenShotActivity.sendData(outData);
                    //    ScreenShotActivity.putData(outData,2,mBufferInfo.presentationTimeUs*1000L);
//                        if(outputStream != null){
//                            outputStream.write(outData, 0, outData.length);
//                        }
                    }
                    mEncoder.releaseOutputBuffer(outputBufferIndex, false);
                    outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
                }
            }
        }
        catch (Exception e){

        }
        try {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        } catch (Exception e){
            e.printStackTrace();
        }
//        try {
//            outputStream.flush();
//            outputStream.close();
//            outputStream = null;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public static void writeBytesToFile(byte[] bs) throws IOException {

        OutputStream out = new FileOutputStream("/storage/sdcard0/screenShot");
        InputStream is = new ByteArrayInputStream(bs);
        byte[] buff = new byte[1024];
        int len = 0;
        while((len=is.read(buff))!=-1){
            out.write(buff, 0, len);
        }
        is.close();
        out.close();
    }

}
