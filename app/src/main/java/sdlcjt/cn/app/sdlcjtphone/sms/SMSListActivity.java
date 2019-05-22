package sdlcjt.cn.app.sdlcjtphone.sms;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sdlcjt.cn.app.sdlcjtphone.R;

/**
 * 短信记录
 */
public class SMSListActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_sms_list)
    RecyclerView rvSmsList;
    public static final int REQ_CODE_CONTACT = 1;
    private SMSAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_list_activity);
        ButterKnife.bind(this);

        initListView();

        checkSMSPermission();
    }

    /**
     * 检查申请短信权限
     */
    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            //未获取到读取短信权限
            //向系统申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS}, REQ_CODE_CONTACT);
        } else {
            getSmsInPhone();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //判断用户是否，同意 获取短信授权
        if (requestCode == REQ_CODE_CONTACT && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //获取到读取短信权限
            getSmsInPhone();
        } else {
            Toast.makeText(this, "未获取到短信权限", Toast.LENGTH_SHORT).show();
        }
    }

    private void initListView() {
        mAdapter = new SMSAdapter(R.layout.sms_item);
        rvSmsList.setLayoutManager(new LinearLayoutManager(this));
        rvSmsList.setAdapter(mAdapter);
    }

    public void getSmsInPhone() {
        Log.e("SMS", "开始获取短信内容");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<SMSInfo> list = new ArrayList<>();
                    final String SMS_URI_ALL = "content://sms/"; // 所有短信
                    final String SMS_URI_INBOX = "content://sms/inbox"; // 收件箱
                    final String SMS_URI_SEND = "content://sms/sent"; // 已发送
                    final String SMS_URI_DRAFT = "content://sms/draft"; // 草稿
                    final String SMS_URI_OUTBOX = "content://sms/outbox"; // 发件箱
                    final String SMS_URI_FAILED = "content://sms/failed"; // 发送失败
                    final String SMS_URI_QUEUED = "content://sms/queued"; // 待发送列表

                    Uri uri = Uri.parse(SMS_URI_ALL);
                    String[] projection = new String[]{"_id", "address", "person", "body", "date", "type",};
                    Cursor cur = getContentResolver().query(uri, projection, null,
                            null, "date desc"); // 获取手机内部短信
                    // 获取短信中最新的未读短信
                    if (cur != null && cur.getCount() > 0) {
                        cur.moveToFirst();
                        for (int i = 0; i < cur.getCount(); i++) {
                            cur.moveToPosition(i);
                            int index_Address = cur.getColumnIndex("address");
                            int index_Person = cur.getColumnIndex("person");
                            int index_Body = cur.getColumnIndex("body");
                            int index_Date = cur.getColumnIndex("date");
                            int index_Type = cur.getColumnIndex("type");

                            String strAddress = cur.getString(index_Address);
                            String strPerson = cur.getString(index_Person);

                            String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME};
                            //设置查询条件
                            String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "='" + strAddress + "'";
                            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    cols, selection, null, null);
                            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                            if (cursor.getCount() > 0) {
                                cursor.moveToFirst();
                                strPerson = cursor.getString(nameFieldColumnIndex);
                            }
                            cursor.close();


                            String strbody = cur.getString(index_Body);
                            long longDate = cur.getLong(index_Date);
                            int intType = cur.getInt(index_Type);

                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "yyyy-MM-dd hh:mm:ss");
                            Date d = new Date(longDate);
                            String strDate = dateFormat.format(d);

                            String strType = "";
                            if (intType == 1) {
                                strType = "接收";
                            } else if (intType == 2) {
                                strType = "发送";
                            } else if (intType == 3) {
                                strType = "草稿";
                            } else if (intType == 4) {
                                strType = "发件箱";
                            } else if (intType == 5) {
                                strType = "发送失败";
                            } else if (intType == 6) {
                                strType = "待发送列表";
                            } else if (intType == 0) {
                                strType = "所以短信";
                            } else {
                                strType = "null";
                            }

                            SMSInfo info = new SMSInfo();
                            info.setAddress(strAddress);
                            info.setPerson(strPerson);
                            info.setBody(strbody);
                            info.setDate(strDate);
                            info.setTypes(strType);
                            list.add(info);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setNewData(list);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SMSListActivity.this, "短信获取失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }
}
