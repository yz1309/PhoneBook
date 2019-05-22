package sdlcjt.cn.app.sdlcjtphone;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ezy.assist.compat.SettingsCompat;
import pub.devrel.easypermissions.EasyPermissions;
import sdlcjt.cn.app.sdlcjtphone.call.listenphonecall.CallListenerService;
import sdlcjt.cn.app.sdlcjtphone.call.outgoing.PhoneCallStateService;
import sdlcjt.cn.app.sdlcjtphone.callrecord.CallRecordListActivity;
import sdlcjt.cn.app.sdlcjtphone.contact.ContactListActivity;
import sdlcjt.cn.app.sdlcjtphone.dialpad.DialpadFragment;
import sdlcjt.cn.app.sdlcjtphone.sms.SMSListActivity;

/**
 * description:
 */
public class TestActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    private static final String[] PHONE_EXTERNAL_STORAG_CAMERA_LOCATION = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };
    private static final int PHONE_EXTERNAL_STORAG_CAMERA_LOCATION_Code = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        ButterKnife.bind(this);

        checkPermission();

        initView();
    }

    private void checkPermission() {
        if (hasPHONE_EXTERNAL_STORAG_CAMERA_LOCATIONPermission()) {
        } else {
            new MaterialDialog.Builder(this).title("请先单击权限")
                    .customView(R.layout.sys_permission_layout, true)
                    .positiveText("我知道了")
                    .autoDismiss(false)
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            EasyPermissions.requestPermissions(TestActivity.this, "请先单击权限", PHONE_EXTERNAL_STORAG_CAMERA_LOCATION_Code,
                                    PHONE_EXTERNAL_STORAG_CAMERA_LOCATION);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    /**
     * 检查是否所有权限都有了
     */
    private boolean hasPHONE_EXTERNAL_STORAG_CAMERA_LOCATIONPermission() {
        return EasyPermissions.hasPermissions(this, PHONE_EXTERNAL_STORAG_CAMERA_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.e("Perssion", "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.e("Perssion", "onPermissionsDenied:" + requestCode + ":" + perms.size());
        checkPermission();
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        /*if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }*/
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        Log.e("Perssion", "onRationaleAccepted:" + requestCode);
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        Log.e("Perssion", "onRationaleDenied:" + requestCode);
    }

    private void initView() {

    }

    private void askForDrawOverlay() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setTitle("允许显示悬浮框")
                .setMessage("为了使电话监听服务正常工作，必须允许这项权限")
                .setPositiveButton("去设置", (dialog, which) -> {
                    openDrawOverlaySettings();
                    dialog.dismiss();
                })
                .setNegativeButton("稍后再说", (dialog, which) -> dialog.dismiss());

        alertDialog.show();
    }

    /**
     * 跳转悬浮窗管理设置界面
     */
    private void openDrawOverlaySettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M 以上引导用户去系统设置中打开允许悬浮窗
            // 使用反射是为了用尽可能少的代码保证在大部分机型上都可用
            try {
                Context context = this;
                Class clazz = Settings.class;
                Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
                Intent intent = new Intent(field.get(null).toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "请在悬浮窗管理中打开权限", Toast.LENGTH_LONG).show();
            }
        } else {
            // 6.0 以下则直接使用 SettingsCompat 中提供的接口
            SettingsCompat.manageDrawOverlays(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 是否为默认拨号
        if (isDefaultPhoneCallApp()) {
            setMonitorPermission();
        } else {
            setDefaultCallApp();
        }
    }

    private void setDefaultCallApp() {
        // Android M 以上的系统发起将本应用设为默认电话应用的请求
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
            intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                    getPackageName());
            startActivity(intent);

        } else {
            Toast.makeText(TestActivity.this, "Android 6.0 以上才支持修改默认电话应用！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Android M 以上检查是否是系统默认电话应用
     */
    public boolean isDefaultPhoneCallApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TelecomManager manger = (TelecomManager) getSystemService(TELECOM_SERVICE);
            if (manger != null) {
                String name = manger.getDefaultDialerPackage();
                return name.equals(getPackageName());
            }
        }
        return false;
    }

    private void setMonitorPermission() {
        // 使用使用 SettingsCompat 检查是否开启了权限
        if (!SettingsCompat.canDrawOverlays(TestActivity.this)) {
            askForDrawOverlay();
            return;
        } else {
            Intent callListener = new Intent(TestActivity.this, CallListenerService.class);
            Intent phoneStateServie = new Intent(TestActivity.this, PhoneCallStateService.class);

            startService(callListener);
            startService(phoneStateServie);
        }
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @OnClick({R.id.fl_call,   R.id.btn_contact, R.id.btn_contact_call_list, R.id.btn_sms_list})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_call:
                showDialpadFragment();
                break;
            case R.id.btn_contact:
                startActivity(new Intent(this, ContactListActivity.class));
                break;
            case R.id.btn_contact_call_list:
                startActivity(new Intent(this, CallRecordListActivity.class));
                break;
            case R.id.btn_sms_list:
                startActivity(new Intent(this, SMSListActivity.class));
                break;
        }
    }

    private void showDialpadFragment() {
        dialtactsContainer.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.dialtacts_container, DialpadFragment.newInstance()).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialtactsContainer.setVisibility(View.GONE);
        }
        return false;
    }

    @BindView(R.id.dialtacts_frame)
    FrameLayout dialtactsFrame;
    @BindView(R.id.dialtacts_container)
    FrameLayout dialtactsContainer;
    @BindView(R.id.btn_contact_call_list)
    Button btnContactCallList;
    @BindView(R.id.btn_sms_list)
    Button btnSmsList;
    @BindView(R.id.btn_contact)
    Button btnContact;
}
