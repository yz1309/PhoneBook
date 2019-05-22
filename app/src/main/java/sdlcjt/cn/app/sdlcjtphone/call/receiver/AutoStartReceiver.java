package sdlcjt.cn.app.sdlcjtphone.call.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import sdlcjt.cn.app.sdlcjtphone.call.listenphonecall.CallListenerService;

/**
 * 自动启动
 * Created by slantech on 2019/05/07 12:34
 */
public class AutoStartReceiver extends BroadcastReceiver {

    public static final String AUTO_START_RECEIVER = "sdlcjt.cn.app.sdlcjtphone.autostart_action";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AutoStartReceiver", "onReceive");

        if (!CallListenerService.isRunning)
            startCallShowService(context, intent);
    }

    private void startCallShowService(Context context, Intent intent) {
        intent.setClass(context, CallListenerService.class);
        context.startService(intent);
    }

}
