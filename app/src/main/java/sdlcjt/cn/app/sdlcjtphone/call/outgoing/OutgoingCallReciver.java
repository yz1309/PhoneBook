package sdlcjt.cn.app.sdlcjtphone.call.outgoing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by slantech on 2019/05/07 14:44
 */
public class OutgoingCallReciver extends BroadcastReceiver {
    static final String TAG = "Recorder";
    // private MyRecorder recorder;

    public OutgoingCallReciver() {
        // recorder = new MyRecorder();
    }

//    public  OutgoingCallReciver (MyRecorder recorder) {
//        this.recorder = recorder;
//    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        String phoneState = intent.getAction();
        Log.d(TAG, phoneState);
        if (phoneState.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);//拨出号码
            //recorder.setPhoneNumber(phoneNum);
            //recorder.setIsCommingNumber(false);
            Log.d(TAG, "设置为去电状态");
            Log.d(TAG, "去电状态 呼叫：" + phoneNum);
        }

        // TODO 以下部分 都没有起作用
        if (phoneState.equals(OutgoingCallState.ForeGroundCallState.DIALING)) {
            Log.d(TAG, "正在拨号...");
        }

        if (phoneState.equals(OutgoingCallState.ForeGroundCallState.ALERTING)) {
            Log.d(TAG, "正在呼叫...");
        }

        if (phoneState.equals(OutgoingCallState.ForeGroundCallState.ACTIVE)) {
            Log.d(TAG, "去电已接通");
//            if (!recorder.isCommingNumber() && !recorder.isStarted()) {
//                Log.d(TAG, "去电已接通 启动录音机");
//                recorder.start();
//
//            }
        }

        if (phoneState.equals(OutgoingCallState.ForeGroundCallState.DISCONNECTED)) {
            Log.d(TAG, "已挂断");
//            if (!recorder.isCommingNumber() && recorder.isStarted()) {
//                Log.d(TAG, "已挂断 关闭录音机");
//                recorder.stop();
//            }
        }
    }

}
