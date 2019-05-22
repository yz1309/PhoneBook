package sdlcjt.cn.app.sdlcjtphone.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pub.devrel.easypermissions.EasyPermissions;
import sdlcjt.cn.app.sdlcjtphone.R;
import sdlcjt.cn.app.sdlcjtphone.callrecord.CallRecordAdapter;
import sdlcjt.cn.app.sdlcjtphone.callrecord.CallRecordInfo;
import sdlcjt.cn.app.sdlcjtphone.entity.StatusResultEvent;
import sdlcjt.cn.app.sdlcjtphone.utils.AndroidsUtils;
import sdlcjt.cn.app.sdlcjtphone.utils.CalendarTool;
import sdlcjt.cn.app.sdlcjtphone.utils.ULogger;


public class TabCallLogFragment extends Fragment {
    Unbinder unbinder;
    private CallRecordAdapter mAdapter;
    private ArrayList<String> phone;
    private static final String[] PHONE_EXTERNAL_STORAG_CAMERA_LOCATION = {
            Manifest.permission.READ_CALL_LOG
    };

    public TabCallLogFragment() {
    }

    public static TabCallLogFragment newInstance() {
        TabCallLogFragment fragment = new TabCallLogFragment();
        return fragment;
    }

    public static TabCallLogFragment newInstance(ArrayList<String> phone) {
        TabCallLogFragment fragment = new TabCallLogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("phone", phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            phone = getArguments().getStringArrayList("phone");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_call_log_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initListView();
        getCallHistoryList();
    }

    private void initListView() {
        mAdapter = new CallRecordAdapter(R.layout.call_record_item);
        rvCallRecordList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCallRecordList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AndroidsUtils.callPhone(getActivity(), ((CallRecordInfo) mAdapter.getItem(position)).getNumber());
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
        if (!EasyPermissions.hasPermissions(getActivity(), PHONE_EXTERNAL_STORAG_CAMERA_LOCATION)) {
            ULogger.e("权限获取失败");
            return;
        }
        List<CallRecordInfo> list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String selections = null;
                    String[] selectArgs = null;
                    /*if (!TextUtils.isEmpty(phone)){
                        selections = CallLog.Calls.NUMBER + " = ?";
                        selectArgs = new String[]{phone};
                    }*/
                    Cursor cs = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, //系统方式获取通讯录存储地址
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
                            Cursor cursorRawContactId = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                                    new String[]{"raw_contact_id", "data1", "mimetype"},
                                    "data1 = ? and mimetype = ? ", new String[]{callNumber, "vnd.android.cursor.item/phone_v2"}, null);
                            if (cursorRawContactId.getCount() > 0) {
                                cursorRawContactId.moveToFirst();
                                String raw_contact_id = cursorRawContactId.getString(cursorRawContactId.getColumnIndex("raw_contact_id"));

                                Cursor cursorName = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                                        new String[]{"raw_contact_id", "data1", "mimetype"},
                                        "raw_contact_id = ? and mimetype = ? ", new String[]{raw_contact_id, "vnd.android.cursor.item/name"}, null);
                                if (cursorName.getCount() > 0) {
                                    cursorName.moveToFirst();
                                    callName = cursorName.getString(cursorName.getColumnIndex("data1"));
                                }
                                cursorName.close();
                            }
                            else {
                                callName = "";
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.setNewData(list);
                                }
                            });
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "通话记录获取失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }

    private String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStatusResultEvent(StatusResultEvent event) {
        if (event == null) return;
        if (event.status == 200) {
            getCallHistoryList();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @BindView(R.id.rv_call_record_list)
    RecyclerView rvCallRecordList;
}
