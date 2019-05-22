package sdlcjt.cn.app.sdlcjtphone.call.phonecallui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.czt.mp3recorder.MP3Recorder;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import sdlcjt.cn.app.sdlcjtphone.R;
import sdlcjt.cn.app.sdlcjtphone.entity.StatusResultEvent;
import sdlcjt.cn.app.sdlcjtphone.utils.CalendarTool;

import static sdlcjt.cn.app.sdlcjtphone.call.listenphonecall.CallListenerService.formatPhoneNumber;


/**
 * description: 接打电话界面，使用该 activity 提供电话管理的界面
 * 拨打电话流程 MO
 * https://blog.csdn.net/michael_yt/article/details/53748915
 * 来电流程 MT
 * https://blog.csdn.net/michael_yt/article/details/53992566
 * Call 状态
 * https://blog.csdn.net/michael_yt/article/details/54647050?utm_source=itdadao&utm_medium=referral
 * 录音
 * http://www.jizhuomi.com/android/example/354.html
 * 录音文件名:svoicecall/对方号码_年月日时分秒毫秒.mp3
 * <p>
 * 录音文件和通话记录上传
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class PhoneCallActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_call_number_label)
    TextView tvCallNumberLabel;
    @BindView(R.id.tv_call_number)
    TextView tvCallNumber;
    @BindView(R.id.rl_user_info)
    RelativeLayout rlUserInfo;
    @BindView(R.id.tv_phone_calling_time)
    TextView tvCallingTime;
    @BindView(R.id.tv_phone_hf)
    TextView tvPhoneHf;
    @BindView(R.id.tv_phone_hang_up)
    TextView tvHangUp;
    @BindView(R.id.tv_phone_pick_up)
    TextView tvPickUp;


    private PhoneCallManager phoneCallManager;
    private PhoneCallService.CallType callType;
    private String phoneNumber;

    private Timer onGoingCallTimer;
    private Timer onOutGoingCallTimer;
    private int callingTime;
    private String vociePath = "";
    private MP3Recorder mRecorder;

    public static void actionStart(Context context, String phoneNumber,
                                   PhoneCallService.CallType callType) {
        Intent intent = new Intent(context, PhoneCallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, callType);
        intent.putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNumber);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_call);
        ButterKnife.bind(this);

        initData();

        initView();
    }

    private void initData() {
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/svoicecall");
        if (!path.exists())
            path.mkdirs();

        vociePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/svoicecall";

        phoneCallManager = new PhoneCallManager(this);
        onGoingCallTimer = new Timer();
        onOutGoingCallTimer = new Timer();
        if (getIntent() != null) {
            phoneNumber = getIntent().getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            callType = (PhoneCallService.CallType) getIntent().getSerializableExtra(Intent.EXTRA_MIME_TYPES);
        }
    }

    private void initView() {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //hide navigationBar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        tvCallNumber.setText(formatPhoneNumber(phoneNumber));
        tvPickUp.setOnClickListener(this);
        tvHangUp.setOnClickListener(this);

        // 打进的电话
        if (callType == PhoneCallService.CallType.CALL_IN) {
            tvCallNumberLabel.setText("来电号码");
            tvPickUp.setVisibility(View.VISIBLE);
        }
        // 打出的电话
        else if (callType == PhoneCallService.CallType.CALL_OUT) {
            tvCallNumberLabel.setText("呼叫号码");
            tvPickUp.setVisibility(View.GONE);
            phoneCallManager.openSpeaker();
            // TODO 开始录音
            recorder_Audio();
            /*onGoingCallTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {

                        }
                    });
                }
            }, 0, 1000);*/
        }
    }

    public void recorder_Audio() {
        try {
            mRecorder = new MP3Recorder(new File(vociePath, phoneNumber + "_" + CalendarTool.getCurrentDate(CalendarTool.dateFormatYMDHMSSSS2) + ".mp3"));
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_phone_pick_up) {
            recorder_Audio();
            phoneCallManager.answer();
            tvPickUp.setVisibility(View.GONE);
            tvCallingTime.setVisibility(View.VISIBLE);
            onGoingCallTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            callingTime++;
                            tvCallingTime.setText("通话中：" + getCallingTime());
                        }
                    });
                }
            }, 0, 1000);
        } else if (v.getId() == R.id.tv_phone_hang_up) {
            phoneCallManager.disconnect();
            stopTimer();
        } else if (v.getId() == R.id.tv_phone_hf) {
            if (phoneCallManager != null) {
                if (phoneCallManager.isOpenSpeaker()) {
                    phoneCallManager.closeSpeaker();
                    tvPhoneHf.setTextColor(getResources().getColor(R.color.gray));
                }
                else {
                    phoneCallManager.openSpeaker();
                    tvPhoneHf.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        }
    }

    private void goCallFinishPage() {
        Intent intent = new Intent(this, CallFinishActivity.class);
        startActivity(intent);

    }

    private String getCallingTime() {
        int minute = callingTime / 60;
        int second = callingTime % 60;
        return (minute < 10 ? "0" + minute : minute) +
                ":" +
                (second < 10 ? "0" + second : second);
    }

    private void stopTimer() {
        if (onGoingCallTimer != null) {
            onGoingCallTimer.cancel();
        }

        callingTime = 0;

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mRecorder != null)
                mRecorder.stop();
            phoneCallManager.destroy();

            EventBus.getDefault().post(new StatusResultEvent(200));
        } catch (Exception ex) {
        }
        goCallFinishPage();
    }
}
