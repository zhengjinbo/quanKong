package com.shenzhen532.qunkong;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shenzhen532.qunkong.service.MyAccessibilityService;
import com.shenzhen532.qunkong.utils.RootCmd;
import com.shenzhen532.qunkong.utils.ToastUtils;

public class WeiChatAllFunAty extends FragmentActivity {
    public static String likeFriendName = "";
    public static int likeNum;
    public static boolean isNewTask = false;
    public static int friendCircleFunType;
    public static WeiChatAllFunAty atyInstance;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weichat_allfun);
        atyInstance = this;

        //一键登录
        findViewById(R.id.btn_wechat_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.dialog_wechat_login, null);
                showAccountDialog(view, 0);

            }
        });

        //发朋友圈
        findViewById(R.id.btn_send_friend_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.dialog_send_friend_circle, null);
                showAccountDialog(view, 1);

            }
        });

        //自动点赞
        findViewById(R.id.btn_auto_prise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adbLikeFriendCircle("จุ๊บjingo", 2);
            }
        });

        //自动评论
        findViewById(R.id.btn_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adbFriendCircleComment("ุ๊บjingo", 2);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        atyInstance = null;
    }

    private void showAccountDialog(final View view, final int flag) {
        AlertDialog.Builder weichatInputDialog = new AlertDialog.Builder(WeiChatAllFunAty.this, R.style.MyDialog);
        weichatInputDialog.setView(view);
        final AlertDialog alertDialog = weichatInputDialog.create();
        alertDialog.show();


        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnConfirm = view.findViewById(R.id.btn_confirm);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (flag) {
                    case 0: //一键登录
                        EditText etAccount = view.findViewById(R.id.et_wechat_account);
                        EditText etPassword = view.findViewById(R.id.et_wechat_password);
                        String account = etAccount.getText().toString();
                        String password = etPassword.getText().toString();
                        ToastUtils.showToast(WeiChatAllFunAty.this, "正在执行任务，请等待");
                        adbLoginWeChat(account, password);
                        break;
                    case 1:  //发朋友圈
                        EditText etContent = view.findViewById(R.id.et_friend_circle_content);
                        EditText etPictureNum = view.findViewById(R.id.et_friend_image_num);
                        String content = etContent.getText().toString();
                        String num = etPictureNum.getText().toString();
                        int picNum;
                        if (!TextUtils.isEmpty(num)) {
                            picNum = Integer.valueOf(num);
                        } else {
                            picNum = 0;
                        }
                        if (TextUtils.isEmpty(content)) {
                            adbSendFriendCircle("测试", picNum);
                        } else {
                            adbSendFriendCircle(content, picNum);
                        }


                        break;
                    default:
                        break;
                }
                alertDialog.dismiss();

            }
        });


    }

    /**
     * 发朋友圈
     *
     * @param content 发送的内容
     * @param num     发送图片张数
     */
    private void adbSendFriendCircle(final String content, int num) {
        //剪切板
        copyToClipboard(content);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String commend = "am start com.tencent.mm/com.tencent.mm.ui.LauncherUI";
                    RootCmd.execRootCmd(commend);
                    Thread.sleep(1000);
                    RootCmd.execRootCmd("input tap 35 100");  //左上角返回第一次
                    Thread.sleep(500);
                    RootCmd.execRootCmd("input tap 35 100");//左上角返回第二次
                    Thread.sleep(1000);
                    RootCmd.execRootCmd("input tap 453 1309");  //点击发现
                    Thread.sleep(1500);
                    RootCmd.execRootCmd("input tap 435 186");  //点击朋友圈
                    Thread.sleep(1500);
                    String openSend = "input swipe 663 95 663 95 2000";
                    RootCmd.execRootCmd(openSend);
                    Thread.sleep(1500);
//                    String input= "am broadcast -a ADB_INPUT_TEXT --es msg '\u4f60\u597d\u5417'";
//                    RootCmd.execRootCmd(input);
                    String longClick = "input swipe 120 180 120 180 1500";
                    RootCmd.execRootCmd(longClick);
                    Thread.sleep(1000);
                    String clip = "input tap 72 300";
                    RootCmd.execRootCmd(clip);  //粘贴
                    Thread.sleep(1000);
                    String send = "input tap 640 88";  //发表
                    RootCmd.execRootCmd(send);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    /**
     * 朋友圈点赞
     *
     * @param nickName 备注称呼
     * @param num      点赞数量
     */
    private void adbLikeFriendCircle(String nickName, int num) {
        likeFriendName = nickName;
        likeNum = num;
        isNewTask = true;
        friendCircleFunType = 1;
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String commend = "am start com.tencent.mm/com.tencent.mm.ui.LauncherUI";
                    RootCmd.execRootCmd(commend);
                    Thread.sleep(2000);
                    String back1 = "input tap 35 100";
                    RootCmd.execRootCmd(back1);
                    Thread.sleep(1000);
                    String back2 = "input tap 35 100";
                    RootCmd.execRootCmd(back2);
                    Thread.sleep(1000);
                    String openFind = "input tap 453 1309";
                    RootCmd.execRootCmd(openFind);
                    Thread.sleep(1500);
                    String openFriend = "input tap 435 186";
                    RootCmd.execRootCmd(openFriend); //打开朋友圈

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    /**
     * 朋友圈评论
     *
     * @param nickName 备注称呼
     * @param num      从最新的说说开始，评论多少条
     */
    private void adbFriendCircleComment(String nickName, int num) {
        likeFriendName = nickName;
        likeNum = num;
        isNewTask = true;
        friendCircleFunType = 2;
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String commend = "am start com.tencent.mm/com.tencent.mm.ui.LauncherUI";
                    RootCmd.execRootCmd(commend);
                    Thread.sleep(2000);
                    String back1 = "input tap 35 100";
                    RootCmd.execRootCmd(back1);
                    Thread.sleep(1000);
                    String back2 = "input tap 35 100";
                    RootCmd.execRootCmd(back2);
                    Thread.sleep(1000);
                    String openFind = "input tap 453 1309";
                    RootCmd.execRootCmd(openFind);
                    Thread.sleep(1500);
                    String openFriend = "input tap 435 186";
                    RootCmd.execRootCmd(openFriend);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    /**
     * 自动登录微信
     *
     * @param account
     * @param password
     */
    private void adbLoginWeChat(final String account, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String commend = "am start com.tencent.mm/com.tencent.mm.ui.LauncherUI";
                    RootCmd.execRootCmd(commend);
                    Thread.sleep(8000);
                    RootCmd.execRootCmd("input tap 175 1260"); //点击登录按钮
                    Thread.sleep(1000);
                    RootCmd.execRootCmd("input tap 187 580");  //使用其他登录方式按钮
                    Thread.sleep(1000);
                    RootCmd.execRootCmd("input tap 200 1220");  //用微信号/qq号/邮箱登录
                    Thread.sleep(1000);
                    RootCmd.execRootCmd("am broadcast -a ADB_INPUT_TEXT --es msg " + "1814701557");
                    //   RootCmd.execRootCmd("input text 1183945397");
                    Thread.sleep(1000);
                    RootCmd.execRootCmd("input tap 270 480");
                    Thread.sleep(1000);
                    RootCmd.execRootCmd("am broadcast -a ADB_INPUT_TEXT --es msg " + "2Zhengjinbo.");
                    Thread.sleep(1000);
                    RootCmd.execRootCmd("input tap 347 714");
                    Thread.sleep(7000);
                    //起点（139,680）--》终点（560,680）  时间1000ms
                    RootCmd.execRootCmd("input swipe 139 680 560 680 1000");  //滑块验证
                    Thread.sleep(3000);
                    RootCmd.execRootCmd("input tap 347 714");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public void copyToClipboard(String text) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);

    }

}
