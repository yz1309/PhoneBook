package sdlcjt.cn.app.sdlcjtphone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ezy.assist.compat.SettingsCompat;
import pub.devrel.easypermissions.EasyPermissions;
import sdlcjt.cn.app.sdlcjtphone.call.listenphonecall.CallListenerService;
import sdlcjt.cn.app.sdlcjtphone.call.outgoing.PhoneCallStateService;
import sdlcjt.cn.app.sdlcjtphone.entity.StatusResultEvent;
import sdlcjt.cn.app.sdlcjtphone.entity.TabEntity;
import sdlcjt.cn.app.sdlcjtphone.ui.fragment.TabCallFragment;
import sdlcjt.cn.app.sdlcjtphone.ui.fragment.TabCallLogFragment;
import sdlcjt.cn.app.sdlcjtphone.ui.fragment.TabContactFragment;
import sdlcjt.cn.app.sdlcjtphone.ui.view.PagerAdapter;
import sdlcjt.cn.app.sdlcjtphone.utils.ULogger;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles;
    private int[] mIconUnselectIds = {
            R.mipmap.icon_call_normall, R.mipmap.icon_call_log_normal, R.mipmap.icon_contact_normal};
    private int[] mIconSelectIds = {
            R.mipmap.icon_call_pressed, R.mipmap.icon_call_log_pressed, R.mipmap.icon_contact_pressed};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private TabCallFragment tabCallFragment;
    private TabCallLogFragment tabCallLogFragment;
    private TabContactFragment tabContactFragment;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        initVP();
        checkPermission();
    }

    private void initVP() {
        mTitles = new String[]{getString(R.string.tab_call), getString(R.string.tab_call_log), getString(R.string.tab_contact)};
        tabCallFragment = TabCallFragment.newInstance();
        tabCallLogFragment = TabCallLogFragment.newInstance();
        tabContactFragment = TabContactFragment.newInstance(false);
        mFragments.add(tabCallFragment);
        mFragments.add(tabCallLogFragment);
        mFragments.add(tabContactFragment);

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        vpData.setOffscreenPageLimit(5);
        vpData.setAdapter(new PagerAdapter(getSupportFragmentManager(), mFragments, mTitles));
        tabBottom.setTabData(mTabEntities);
        // 设置消息数量
        //tabBottom.showMsg(0, mRandom.nextInt(100) + 1);
        tabBottom.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vpData.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        vpData.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tabBottom.setCurrentTab(position);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        vpData.setCurrentItem(0);
    }


    private void checkPermission() {
        if (hasPHONE_EXTERNAL_STORAG_CAMERA_LOCATIONPermission()) {
            ULogger.e("权限已获取完毕，通知");
            EventBus.getDefault().post(new StatusResultEvent(1));
            EventBus.getDefault().post(new StatusResultEvent(200));
        } else {
            new MaterialDialog.Builder(this).title("请先单击权限")
                    .customView(R.layout.sys_permission_layout, true)
                    .positiveText("去设置")
                    .autoDismiss(false)
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            EasyPermissions.requestPermissions(MainActivity.this, "请先单击权限", PHONE_EXTERNAL_STORAG_CAMERA_LOCATION_Code,
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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.e("Perssion", "onPermissionsGranted:" + requestCode + ":" + perms.size());
        if (PHONE_EXTERNAL_STORAG_CAMERA_LOCATION_Code == requestCode && perms.size() ==PHONE_EXTERNAL_STORAG_CAMERA_LOCATION.length ){
            EventBus.getDefault().post(new StatusResultEvent(1));
            EventBus.getDefault().post(new StatusResultEvent(200));
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.e("Perssion", "onPermissionsDenied:" + requestCode + ":" + perms.size());
        checkPermission();
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        Log.e("Perssion", "onRationaleAccepted:" + requestCode);
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        Log.e("Perssion", "onRationaleDenied:" + requestCode);
    }


    private void askForDrawOverlay() {
        new MaterialDialog.Builder(this).title("请先单击权限")
                .content("开启悬浮框使用权限")
                .positiveText("去设置")
                .autoDismiss(false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        openDrawOverlaySettings();
                        dialog.dismiss();
                    }
                })
                .show();
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
        if (hasPHONE_EXTERNAL_STORAG_CAMERA_LOCATIONPermission()) {
            // 是否为默认拨号
            if (isDefaultPhoneCallApp()) {
                setMonitorPermission();
            } else {
                setDefaultCallApp();
            }
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
            Toast.makeText(MainActivity.this, "Android 6.0 以上才支持修改默认电话应用！", Toast.LENGTH_LONG).show();
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
        if (!SettingsCompat.canDrawOverlays(MainActivity.this)) {
            askForDrawOverlay();
            return;
        } else {
            Intent callListener = new Intent(MainActivity.this, CallListenerService.class);
            Intent phoneStateServie = new Intent(MainActivity.this, PhoneCallStateService.class);

            startService(callListener);
            startService(phoneStateServie);
        }
    }

    @BindView(R.id.vp_data)
    ViewPager vpData;
    @BindView(R.id.tab_bottom)
    CommonTabLayout tabBottom;
}
