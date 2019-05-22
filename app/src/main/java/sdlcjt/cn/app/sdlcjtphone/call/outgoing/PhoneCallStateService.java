package sdlcjt.cn.app.sdlcjtphone.call.outgoing;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by slantech on 2019/05/07 14:45
 */
public class PhoneCallStateService extends Service {
    private OutgoingCallState outgoingCallState;
    private OutgoingCallReciver outgoingCallReciver;
    //private MyRecorder recorder;

    @Override
    public void onCreate() {
        super.onCreate();

        //------以下应放在onStartCommand中，但2.3.5以下版本不会因service重新启动而重新调用--------
        //监听电话状态，如果是打入且接听 或者 打出 则开始自动录音
        //通话结束，保存文件到外部存储器上
        Log.d("Recorder", "正在监听中...");
        //recorder = new MyRecorder();
        outgoingCallState = new OutgoingCallState(this);
        //outgoingCallReciver = new OutgoingCallReciver(recorder);
        outgoingCallReciver = new OutgoingCallReciver();
        outgoingCallState.startListen();

        //去电
        IntentFilter outgoingCallFilter = new IntentFilter();
        outgoingCallFilter.addAction(OutgoingCallState.ForeGroundCallState.IDLE);
        outgoingCallFilter.addAction(OutgoingCallState.ForeGroundCallState.DIALING);
        outgoingCallFilter.addAction(OutgoingCallState.ForeGroundCallState.ALERTING);
        outgoingCallFilter.addAction(OutgoingCallState.ForeGroundCallState.ACTIVE);
        outgoingCallFilter.addAction(OutgoingCallState.ForeGroundCallState.DISCONNECTED);

        outgoingCallFilter.addAction("android.intent.action.PHONE_STATE");
        outgoingCallFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");

        //注册接收者
        registerReceiver(outgoingCallReciver, outgoingCallFilter);


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(outgoingCallReciver);
        Log.d("Recorder", "已关闭电话监听服务");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

}
