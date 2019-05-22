package sdlcjt.cn.app.sdlcjtphone.call.outgoing;

import android.content.Context;
import android.util.Log;

/**
 * Created by slantech on 2019/05/07 14:42
 */
public class OutgoingCallState {
    Context ctx;

    public OutgoingCallState(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * 前台呼叫状态
     *
     * @author sdvdxl
     */
    public static final class ForeGroundCallState {
        public static final String DIALING =
                "sdlcjt.cn.app.sdlcjtphone.FORE_GROUND_DIALING";
        public static final String ALERTING =
                "sdlcjt.cn.app.sdlcjtphone.FORE_GROUND_ALERTING";
        public static final String ACTIVE =
                "sdlcjt.cn.app.sdlcjtphone.FORE_GROUND_ACTIVE";
        public static final String IDLE =
                "sdlcjt.cn.app.sdlcjtphone.FORE_GROUND_IDLE";
        public static final String DISCONNECTED =
                "sdlcjt.cn.app.sdlcjtphone.FORE_GROUND_DISCONNECTED";
    }

    /**
     * 开始监听呼出状态的转变，
     * 并在对应状态发送广播
     */
    public void startListen() {
        new ReadLog(ctx).start();
        Log.d("Recorder", "开始监听呼出状态的转变，并在对应状态发送广播");
    }
}
