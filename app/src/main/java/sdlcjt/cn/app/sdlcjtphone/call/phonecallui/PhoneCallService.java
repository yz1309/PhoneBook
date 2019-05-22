package sdlcjt.cn.app.sdlcjtphone.call.phonecallui;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

import sdlcjt.cn.app.sdlcjtphone.ActivityStack;


/**
 * description: 监听电话通信状态的服务，实现该类的同时必须提供电话管理的UI
 * STATE_NEW : 新创建的call的状态
 * STATE_DIALING ： call当前正在外拨但是还没有连接上
 * STATE_RINGING ： call当前正处于来电状态，但是没有连接上
 * STATE_HOLDING ： call当前处于hold状态
 * STATE_ACTIVE ： call处于活动状态支持互相通话
 * STATE_DISCONNECTED ： 当前的call释放了所有资源处于断开连接状态
 * STATE_SELECT_PHONE_ACCOUNT ： 等待用户选择phoneaccount
 * STATE_PRE_DIAL_WAIT ：也是等待用户选择phoneaccount
 * STATE_CONNECTING ： 外拨的初始状态，如果成功会转换成STATE_DIALING 否则转换成STATE_DISCONNECTED
 * STATE_DISCONNECTING ： 正处于断开连接状态
 * STATE_PULLING_CALL ： 表示一个连接正处于从远端连接拉到本地连接的一个状态（比如有两个设备但是共用一个号码）
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class PhoneCallService extends InCallService {

    private Call.Callback callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);

            Log.e("PhoneCallService", "onStateChanged="+state);
            switch (state) {
                case Call.STATE_ACTIVE: {
                    Log.e("PhoneCallService", "Call.STATE_ACTIVE");
                    break;
                }

                case Call.STATE_DISCONNECTED: {
                    ActivityStack.getInstance().finishActivity(PhoneCallActivity.class);
                    break;
                }
            }
        }
    };

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);

        call.registerCallback(callback);
        PhoneCallManager.call = call;
        CallType callType = null;

        if (call.getState() == Call.STATE_RINGING) {//CALL_STATE_RINGING 电话进来时
            callType = CallType.CALL_IN;
            Log.e("PhoneCallService","电话进来时");
        } else if (call.getState() == Call.STATE_CONNECTING) {//外拨的初始状态，如果成功会转换成STATE_DIALING 否则转换成STATE_DISCONNECTED
            callType = CallType.CALL_OUT;
            Log.e("PhoneCallService","外拨的初始状态");
        }

        if (callType != null) {
            Call.Details details = call.getDetails();
            String phoneNumber = details.getHandle().getSchemeSpecificPart();
            PhoneCallActivity.actionStart(this, phoneNumber, callType);
        }
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);

        call.unregisterCallback(callback);
        PhoneCallManager.call = null;
    }

    public enum CallType {
        CALL_IN,
        CALL_OUT,
    }
}
