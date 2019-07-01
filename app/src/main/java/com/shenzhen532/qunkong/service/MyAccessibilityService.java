package com.shenzhen532.qunkong.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.Gson;
import com.shenzhen532.qunkong.WeiChatAllFunAty;
import com.shenzhen532.qunkong.utils.RootCmd;

import java.util.ArrayList;
import java.util.List;

public class MyAccessibilityService extends AccessibilityService {
    AccessibilityNodeInfo mNodeInfo;
    public String currentWindowName="";
    private boolean isWeChatPermission = false;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String className = (String) event.getClassName();

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            mNodeInfo= event.getSource();
            if (className!=null){
                currentWindowName = className;
                Log.e("MyAccessibilityService",className);
                if (currentWindowName.equals("com.tencent.mm.app.WeChatSplashActivity")){
                    isWeChatPermission = true;
                }

                if (currentWindowName.equals("com.android.packageinstaller.permission.ui.GrantPermissionsActivity")){
                    RootCmd.execRootCmd("input tap 550 1260");
                }

                if (currentWindowName.equals("com.tencent.mm.ui.widget.a.c")){
                    RootCmd.execRootCmd("input tap 250 820");
                }

                if (currentWindowName.equals("com.tencent.mm.plugin.sns.ui.SnsLongMsgUI")){
                    RootCmd.execRootCmd("input tap 346 1240");
                }

                //微信朋友圈界面
                if (className.equals("com.tencent.mm.plugin.sns.ui.SnsTimeLineUI")){
                    if (WeiChatAllFunAty.atyInstance!=null){
                        if (WeiChatAllFunAty.isNewTask){
                            if (event.getSource()!=null){
                                int count = 0;
                                AccessibilityNodeInfo lastNodeInfo = null;
                                for (int k = 0; k < 10; k++) {  //十页
                                    mNodeInfo = event.getSource();
                                    List<AccessibilityNodeInfo> allThisFriend = new ArrayList<>();
                                    List<AccessibilityNodeInfo> infoFriend = event.getSource().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b6e");
                                    if (infoFriend != null && infoFriend.size() > 0) {
                                        for (int i = 0; i < infoFriend.size(); i++) {
                                            Log.e("infoFriend["+i+"]==", new Gson().toJson(infoFriend.get(i)));
                                            AccessibilityNodeInfo accessibilityNodeInfo = infoFriend.get(i);
                                            Log.e("控件名称["+i+"]==",accessibilityNodeInfo.getText().toString());
                                            List<AccessibilityNodeInfo> thisFriend = infoFriend.get(i).findAccessibilityNodeInfosByText(WeiChatAllFunAty.likeFriendName);
                                            if (thisFriend != null && thisFriend.size() > 0) {
                                                allThisFriend.add(thisFriend.get(0));
                                            }
                                        }
                                        // 由于在页面分割处的朋友圈会被识别两次，此方法用于去重
                                        if (lastNodeInfo!=null){
                                            if (allThisFriend.size()>0){
                                                Log.i("comment1",lastNodeInfo.toString());
                                                Log.i("comment2",allThisFriend.get(0).toString());

                                                Rect rectAllFirst = new Rect();
                                                allThisFriend.get(0).getBoundsInParent(rectAllFirst);
                                                Rect rectLast = new Rect();
                                                lastNodeInfo.getBoundsInParent(rectLast);
                                                if (rectAllFirst.equals(rectLast)){

                                                    allThisFriend.remove(0);
                                                }
                                            }
                                        }


                                        for (int i = 0; i < allThisFriend.size(); i++) {
                                            if (count>WeiChatAllFunAty.likeNum-1){
                                                break;
                                            }
                                            lastNodeInfo = allThisFriend.get(i);
                                            AccessibilityNodeInfo parent = allThisFriend.get(i).getParent();
                                            List<AccessibilityNodeInfo> infoMenu = parent.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/eho");
                                            if (infoMenu!=null && infoMenu.size()>0){
                                                infoMenu.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                try {
                                                    Thread.sleep(800);
                                                    if (WeiChatAllFunAty.friendCircleFunType == 1){ //点赞
                                                        List<AccessibilityNodeInfo> likeInfo = mNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ehb");
                                                        if (likeInfo!=null && likeInfo.size()>0){
                                                            for (int j = 0; j < likeInfo.size(); j++) {
                                                                if (likeInfo.get(j).getText().equals("赞")){
                                                                    likeInfo.get(j).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                                    count++;
                                                                    Thread.sleep(500);
                                                                    break;
                                                                }
                                                            }

                                                        }
                                                    }else if (WeiChatAllFunAty.friendCircleFunType == 2){  //评论
                                                        List<AccessibilityNodeInfo> commentInfo = mNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ehe");//评论
                                                        if (commentInfo!=null && commentInfo.size()>0){
                                                            commentInfo.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                            count++;
                                                            Thread.sleep(500);
                                                            List<AccessibilityNodeInfo> commentEtInfo = mNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/eif");//输入框
                                                            if (commentEtInfo!=null && commentEtInfo.size()>0){

                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                    Bundle arguments = new Bundle();
                                                                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "ha~ha~ha~");
                                                                    commentEtInfo.get(0).performAction(AccessibilityNodeInfo.FOCUS_INPUT);
                                                                    commentEtInfo.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                                                                    try {
                                                                        Thread.sleep(1000);
                                                                        List<AccessibilityNodeInfo> sendInfo = event.getSource().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/eih");//发送
                                                                        if (sendInfo!=null && sendInfo.size()>0){
                                                                            sendInfo.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                                            Thread.sleep(300);
                                                                        }
                                                                    } catch (InterruptedException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                        }



                                                    }


                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }


                                        }
                                        Log.i("count",count+"");
                                        if (count>WeiChatAllFunAty.likeNum-1){
                                            WeiChatAllFunAty.isNewTask = false;
                                            break;
                                        }

                                        try {
                                            AccessibilityNodeInfo infoList= infoFriend.get(0).getParent();
                                            if (infoList!=null){
                                                AccessibilityNodeInfo listParent= infoList.getParent();

                                                listParent.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                                                Thread.sleep(2000);

                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }




                                    }
                                }
                            }



                        }
                    }


                }
                // 当前最前端的窗口是软键盘
//                if (className.equals("android.inputmethodservice.SoftInputWindow")){
////                    if (WeiChatAllFunAty.atyInstance!=null){
////                        if (WeiChatAllFunAty.isNewTask){
//                            if (event.getSource()!=null){
//                                Log.i("comment","111111111111111");
//
//
//
//
//                            }
//                       // }
//                  //  }
//                }

            }
        }


    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 模拟点击事件
     * @param x 横坐标
     * @param y 纵坐标
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void dispatchGestureView(final int x, final int y) {
        Point position = new Point(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(position.x, position.y);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0L, 1000L));
        GestureDescription gesture = builder.build();
        dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);

            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);

            }
        }, null);
    }

    /**
     * 模拟滑动事件
     * @param x 横坐标
     * @param y 纵坐标
     * @param xMove 横坐标移动的距离
     * @param yMove 纵坐标移动的距离
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void dispatchScroolView(final int x, final int y,int xMove,int yMove) {

        Point position = new Point(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(position.x, position.y);
        p.lineTo(position.x-xMove,position.y-yMove);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0L, 1000L));
        GestureDescription gesture = builder.build();
        dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);

            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);

            }
        }, null);
    }


}
