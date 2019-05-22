package sdlcjt.cn.app.sdlcjtphone.callrecord;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sdlcjt.cn.app.sdlcjtphone.R;
import sdlcjt.cn.app.sdlcjtphone.contact.bean.Person;
import sdlcjt.cn.app.sdlcjtphone.utils.AndroidsUtils;
import sdlcjt.cn.app.sdlcjtphone.utils.CalendarTool;

/**
 * 通话记录
 */
public class CallRecordListActivity extends AppCompatActivity {

    private CallRecordAdapter mAdapter;
    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_record_list_activity);
        ButterKnife.bind(this);

        person = (Person) getIntent().getSerializableExtra(Person.Serial_Name);
        if (person != null && !TextUtils.isEmpty(person.getName())) {
            tvTitle.setText(person.getName() + getString(R.string.tab_call_log));
        }

        initListView();
        getCallHistoryList();
    }

    private void initListView() {
        mAdapter = new CallRecordAdapter(R.layout.call_record_item);
        rvCallRecordList.setLayoutManager(new LinearLayoutManager(this));
        rvCallRecordList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AndroidsUtils.callPhone(CallRecordListActivity.this, ((CallRecordInfo) mAdapter.getItem(position)).getNumber());
            }
        });
    }

    /**
     * 利用系统CallLog获取通话历史记录
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public void getCallHistoryList() {
        List<CallRecordInfo> list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String selections = null;
                    String[] selectArgs = null;
                    if (person.getPhonenum() != null && person.getPhonenum().size() > 0) {
                        StringBuffer sb = new StringBuffer();

                        for (int i = 0; i < person.getPhonenum().size(); i++) {
                            if (i == person.getPhonenum().size() - 1) {
                                sb.append(person.getPhonenum().get(i));
                            } else {
                                sb.append(person.getPhonenum().get(i) + ",");
                            }
                        }
                        selections = CallLog.Calls.NUMBER + " in (" + sb.toString() + ")";
                    }
                    Cursor cs = getContentResolver().query(CallLog.Calls.CONTENT_URI, //系统方式获取通讯录存储地址
                            new String[]{
                                    CallLog.Calls.CACHED_NAME,  //姓名
                                    CallLog.Calls.NUMBER,    //号码
                                    CallLog.Calls.TYPE,  //呼入/呼出(2)/未接
                                    CallLog.Calls.DATE,  //拨打时间
                                    CallLog.Calls.DURATION,   //通话时长
                            }, selections, selectArgs, CallLog.Calls.DEFAULT_SORT_ORDER);

                    if (cs != null && cs.getCount() > 0) {
                        cs.moveToFirst(); // 游标移动到第一项
                        for (int j = 0; j < cs.getCount(); j++) {
                            cs.moveToPosition(j);
                            String callName = cs.getString(0);  //名称
                            String callNumber = cs.getString(1);  //号码

                            //如果名字为空，在通讯录查询一次有没有对应联系人

                            // 通过手机号码和mimetypes查询得到raw_contact_id
                            // 通过raw_contact_id得到data1的值
                            Cursor cursorRawContactId = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                                    new String[]{"raw_contact_id", "data1", "mimetype"},
                                    "data1 = ? and mimetype = ? ", new String[]{callNumber, "vnd.android.cursor.item/phone_v2"}, null);
                            if (cursorRawContactId.getCount() > 0) {
                                cursorRawContactId.moveToFirst();
                                String raw_contact_id = cursorRawContactId.getString(cursorRawContactId.getColumnIndex("raw_contact_id"));

                                Cursor cursorName = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                                        new String[]{"raw_contact_id", "data1", "mimetype"},
                                        "raw_contact_id = ? and mimetype = ? ", new String[]{raw_contact_id, "vnd.android.cursor.item/name"}, null);
                                if (cursorName.getCount() > 0) {
                                    cursorName.moveToFirst();
                                    callName = cursorName.getString(cursorName.getColumnIndex("data1"));
                                }
                                cursorName.close();
                            }
                            cursorRawContactId.close();


                            /**
                             * 以下方法有点问题，某些查不出来名称显示1
                             */
                                /*// ContactsContract.CommonDataKinds.Phone.CONTENT_URI data表
                                Cursor cursorName = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},
                                        ContactsContract.CommonDataKinds.Phone.NUMBER + "='" + callNumber + "'", null, null);
                                if (cursorName.getCount() > 0) {
                                    cursorName.moveToFirst();
                                    callName = cursorName.getString(cursorName.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                                }
                                cursorName.close();*/

                            //通话类型
                            int callType = Integer.parseInt(cs.getString(2));
                            String callTypeStr = getTypeStr(callType);
                            //拨打时间
                            String callDateStr = CalendarTool.getStringByFormat(cs.getString(3), CalendarTool.dateFormatYMDHMSSSS);

                            //通话时长
                            int callDuration = Integer.parseInt(cs.getString(4));
                            int min = callDuration / 60;
                            int sec = callDuration % 60;
                            String callDurationStr = "";
                            if (sec > 0) {
                                if (min > 0) {
                                    callDurationStr = min + "分" + sec + "秒";
                                } else {
                                    callDurationStr = sec + "秒";
                                }
                            }
                            CallRecordInfo info = new CallRecordInfo();
                            info.setName(callName);
                            info.setNumber(callNumber);
                            info.setType(callTypeStr);
                            info.setDate(callDateStr);
                            info.setDuration(callDurationStr);
                            info.setDate(callDateStr);
                            list.add(info);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.setNewData(list);
                                }
                            });
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CallRecordListActivity.this, "通话记录获取失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();


    }

    public static long[] secondNum2Time(String timeStr) {
        long[] longs = new long[]{0, 0, 0};
        if (TextUtils.isEmpty(timeStr)) return longs;
        long time = Long.parseLong(timeStr);
        long hour = time / 3600;
        long minute = time / 60 % 60;
        long second = time % 60;
        longs[0] = hour;
        longs[1] = minute;
        longs[2] = second;
        return longs;
    }

    @OnClick({R.id.ll_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                finish();
                break;
        }
    }

    public String getTypeStr(int type) {
        if (CallLog.Calls.INCOMING_TYPE == type) {
            return "来电";
        } else if (CallLog.Calls.OUTGOING_TYPE == type) {
            return "去电";
        } else if (CallLog.Calls.MISSED_TYPE == type) {
            return "未接";
        } else if (CallLog.Calls.VOICEMAIL_TYPE == type) {
            return "语音邮件";
        } else if (CallLog.Calls.REJECTED_TYPE == type) {
            return "拒绝";
        } else if (CallLog.Calls.BLOCKED_TYPE == type) {
            return "阻止";
        } else {
            return "未知";
        }
    }

    @BindView(R.id.rv_call_record_list)
    RecyclerView rvCallRecordList;
    @BindView(R.id.tv_title)
    TextView tvTitle;
}
