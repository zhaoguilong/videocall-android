package com.src.videocall.easemobvideocall.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConference;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.src.videocall.easemobvideocall.DemoApplication;
import com.src.videocall.easemobvideocall.R;
import com.src.videocall.easemobvideocall.utils.ConferenceInfo;
import com.src.videocall.easemobvideocall.utils.PreferenceManager;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hyphenate.EMError.CALL_TALKER_ISFULL;


public class LoginActivity extends AppCompatActivity {
    private  final String TAG = this.getClass().getSimpleName();
    public static final int REQUEST_CODE_SETNICK = 1;
    private EditText roomnameEditText;
    private EditText passwordEditText;
    private String username;
    private String currentRoomname;
    private String currentPassword;
    private String accessToken;
    private EMConferenceManager.EMConferenceRole  conferenceRole;
    private String password = "123";
    final  private String regEx="[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？-]";
    private Button btn_anchor;
    private Button btn_audience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         btn_anchor = (Button)findViewById(R.id.btn_anchor);
         btn_audience = (Button)findViewById(R.id.btn_audience);

        roomnameEditText = (EditText) findViewById(R.id.roomname);
        passwordEditText = (EditText) findViewById(R.id.password);
        if(ConferenceInfo.getInstance().getRoomname() != null){
            roomnameEditText.setText(ConferenceInfo.getInstance().getRoomname());
        }
        if(ConferenceInfo.getInstance().getPassword() != null){
            passwordEditText.setText(ConferenceInfo.getInstance().getPassword());
        }

        EditText editText= (EditText)findViewById(R.id.roomname);
        editText.clearFocus();
        editText.setSelected(false);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
    }

    /*
    主播加入会议房间
     */
    public void addconference_anchor(View view){
        currentRoomname = roomnameEditText.getText().toString().trim();
        currentPassword = passwordEditText.getText().toString().trim();
        if(currentRoomname.length() == 0 && currentPassword.length() == 0){
            Toast.makeText(getApplicationContext(), "房间名或密码不允许为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(currentRoomname.length() < 3){
            Toast.makeText(getApplicationContext(), "房间名不能少于3位！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(currentPassword.length() < 3){
            Toast.makeText(getApplicationContext(), "密码不能少于3位！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(currentRoomname.length() > 18){
            Toast.makeText(getApplicationContext(), "房间名不能超过18位！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(currentPassword.length() > 18){
            Toast.makeText(getApplicationContext(), "房间密码不能超过18位！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isLegalChars(currentRoomname)){
            Toast.makeText(getApplicationContext(), "房间名不允许输入除数字、中文、英文、下划线或者减号以外的特殊字符!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isLegalChars(currentPassword)){
            Toast.makeText(getApplicationContext(), "密码不允许输入除数字、中文、英文、下划线或者减号以外的特殊字符!", Toast.LENGTH_SHORT).show();
            return;
        }

        conferenceRole = EMConferenceManager.EMConferenceRole.Talker;
        ConferenceInfo.getInstance().setCurrentrole(EMConferenceManager.EMConferenceRole.Talker);

        username = PreferenceManager.getInstance().getCurrentUsername();

        //btn_anchor.setActivated(false);
        //btn_audience.setActivated(false);

        if(username == null){
            register(view);
        }else{
            password = PreferenceManager.getInstance().getCurrentUserPassWord();
            login(view);
        }
        //register(view);
    }

    /*
    观众加入会议房间
     */
    public void addconference_audience(View view){
        currentRoomname = roomnameEditText.getText().toString().trim();
        currentPassword = passwordEditText.getText().toString().trim();

        if(currentRoomname.length() == 0 && currentPassword.length() == 0){
            Toast.makeText(getApplicationContext(), "房间名或密码不允许为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(currentRoomname.length() < 3){
            Toast.makeText(getApplicationContext(), "房间名不能少于3位！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(currentPassword.length() < 3){
            Toast.makeText(getApplicationContext(), "密码不能少于3位！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(currentRoomname.length() > 18){
            Toast.makeText(getApplicationContext(), "房间名不能超过18位！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(currentPassword.length() > 18){
            Toast.makeText(getApplicationContext(), "房间密码不能超过18位！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isLegalChars(currentRoomname)){
            Toast.makeText(getApplicationContext(), "房间名不允许输入除数字、中文、英文、下划线或者减号以外的特殊字符!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isLegalChars(currentPassword)){
            Toast.makeText(getApplicationContext(), "密码不允许输入除数字、中文、英文、下划线或者减号以外的特殊字符!", Toast.LENGTH_SHORT).show();
            return;
        }

        conferenceRole = EMConferenceManager.EMConferenceRole.Audience;
        ConferenceInfo.getInstance().setCurrentrole(EMConferenceManager.EMConferenceRole.Audience);

        username = PreferenceManager.getInstance().getCurrentUsername();
        if(username == null){
            register(view);
        }else{
            password = PreferenceManager.getInstance().getCurrentUserPassWord();
            login(view);
        }
        //register(view);
    }


    /**
     * 只能输入数字，字母，汉字和下划线
     * @param chars
     * @return
     */
    public static boolean isLegalChars(String chars){
        //String regex = "[\u4e00-\u9fa5\\w]+";
        String regex ="^[\\u4e00-\\u9fa5A-Za-z0-9_-]*$";
        boolean result = chars.matches(regex);
        return result;
    }


    /*
    自动注册一个账号
     */
    public void register(View view){
        new Thread(new Runnable() {
            public void run() {
                UUID uuid = UUID.randomUUID();
                String aa = "";//这里是将特殊字符换为aa字符串," "代表直接去掉
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(uuid.randomUUID().toString());//这里把想要替换的字符串传进来
                username = m.replaceAll(aa).trim();
                try {
                    // call method in SDK
                    EMClient.getInstance().createAccount(username, password);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //注册成功进行登录
                            PreferenceManager.getInstance().setCurrentUserName(username);
                            PreferenceManager.getInstance().setCurrentuserPassword(password);
                            login(view);
                        }
                    });
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            int errorCode=e.getErrorCode();
                            if(errorCode==EMError.NETWORK_ERROR){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_ALREADY_EXIST){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_AUTHENTICATION_FAILED){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_ILLEGAL_ARGUMENT){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.EXCEED_SERVICE_LIMIT){

                            }else{
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    /*
    登录IM账号
     */
    public void login(View view) {
        // call login method
        Log.d(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(username, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");
                accessToken = EMClient.getInstance().getAccessToken();
                //登录成功进入会议房间
                if(currentRoomname == null || currentPassword == null){
                    Toast.makeText(getApplicationContext(), "房间名和密码不允许为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                joinConference();
            }
            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }
            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /*
     加入一个聊天会议室
     */
    private void joinConference() {
        EMClient.getInstance().setDebugMode(true);
        ConferenceInfo.getInstance().Init();
        EMClient.getInstance().conferenceManager().set(accessToken,EMClient.getInstance().getOptions().getAppKey() ,username);
        EMClient.getInstance().conferenceManager().joinRoom(currentRoomname, currentPassword, conferenceRole, new EMValueCallBack<EMConference>(){
                    @Override
                    public void onSuccess(EMConference value) {
                         EMLog.e(TAG, "join  conference  failed  success");
                         Log.e("tag", "thread's name+"+Thread.currentThread().getName());
                         Log.e("tag", "before name = "+currentRoomname);
                         System.out.println("fangjianming前:" + currentRoomname);
                         ConferenceInfo.getInstance().setRoomname(currentRoomname);
                         ConferenceInfo.getInstance().setPassword(currentPassword);
                         ConferenceInfo.getInstance().setCurrentrole(value.getConferenceRole());
                         ConferenceInfo.getInstance().setConference(value);
                         Log.d(TAG, "Get ConferenceId:"+ value.getConferenceId());

                         Intent intent = new Intent(LoginActivity.this, ConferenceActivity.class);
                         startActivity(intent);
                         finish();
                    }
                    @Override
                    public void onError(final int error, final String errorMsg) {
                        EMLog.e(TAG, "join conference failed error " + error + ", msg " + errorMsg);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //主播已满判断
                                if(error == CALL_TALKER_ISFULL)
                                {
                                    takerFullDialogDisplay();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Join conference failed " + error + " " + errorMsg, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });
                    }
                });
    }

    /**
     * 主播已满提示对话框
     */
    public void takerFullDialogDisplay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(LoginActivity.this, R.layout.activity_talker_full, null);
        dialog.setView(dialogView);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER | Gravity.CENTER;
        dialog.show();

        final Button btn_ok = dialogView.findViewById(R.id.btn_ok);
        final Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                dialog.dismiss();

                EMLog.e(TAG, "talker is full , join conference as Audience");

                conferenceRole = EMConferenceManager.EMConferenceRole.Audience;
                ConferenceInfo.getInstance().setCurrentrole(EMConferenceManager.EMConferenceRole.Audience);
                joinConference();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //主播已满不加入会议
                EMLog.e(TAG, "talker is full , not join conference");
            }
        });
    }

    /*
    个人设置
     */
    public void personalSetting(View view){
        Intent intent = new Intent(LoginActivity.this,
                SettingActivity.class);
        startActivity(intent);
    }
}
