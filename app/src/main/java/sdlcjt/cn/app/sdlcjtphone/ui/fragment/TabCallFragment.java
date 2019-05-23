package sdlcjt.cn.app.sdlcjtphone.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import sdlcjt.cn.app.sdlcjtphone.R;
import sdlcjt.cn.app.sdlcjtphone.contact.adapter.CallContactPhoneAdapter;
import sdlcjt.cn.app.sdlcjtphone.contact.bean.Person;
import sdlcjt.cn.app.sdlcjtphone.dialpad.DialpadFragment;
import sdlcjt.cn.app.sdlcjtphone.entity.StrResultEvent;
import sdlcjt.cn.app.sdlcjtphone.utils.AndroidsUtils;

import static com.lzy.okgo.utils.HttpUtils.runOnUiThread;


public class TabCallFragment extends Fragment {
    private String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
    Unbinder unbinder;
    CallContactPhoneAdapter mAdapter;

    public TabCallFragment() {
    }


    public static TabCallFragment newInstance() {
        TabCallFragment fragment = new TabCallFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_call_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getChildFragmentManager().beginTransaction().replace(R.id.dialtacts_container, DialpadFragment.newInstance()).commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStrResultEvent(StrResultEvent event) {
        if (event == null) return;
        if (!TextUtils.isEmpty(event.str)) {
            search(event.str);
        }
    }

    private void search(String str) {
        if (mAdapter != null) {
            mAdapter.setNewData(null);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                //data1存储各个记录的总数据，mimetype存放记录的类型，如电话、email等
                Cursor cursor2 = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{
                        "data1",
                        "mimetype"
                }, " data1 like ? ", new String[]{"%" + str + "%"}, null);
                List<Person> list = new ArrayList<>();
                while (cursor2.moveToNext()) {
                    String name = "";
                    List<String> phoneList = new ArrayList<>();
                    String phone = "";
                    String data = cursor2.getString(cursor2.getColumnIndex("data1"));
                    String mimetypeTemp = cursor2.getString(cursor2.getColumnIndex("mimetype"));
                    if (mimetypeTemp.equals("vnd.android.cursor.item/phone_v2")) {  //如果是电话
                        phone = data;
                        phoneList.add(phone);
                        Person person = new Person(name, phoneList);

                        //如果名字为空，在通讯录查询一次有没有对应联系人
                        // 通过手机号码和mimetypes查询得到raw_contact_id
                        // 通过raw_contact_id得到data1的值
                        Cursor cursorRawContactId = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                                new String[]{"raw_contact_id", "data1", "mimetype"},
                                "data1 = ? and mimetype = ? ", new String[]{phone, "vnd.android.cursor.item/phone_v2"}, null);
                        if (cursorRawContactId.getCount() > 0) {
                            cursorRawContactId.moveToFirst();
                            String raw_contact_id = cursorRawContactId.getString(cursorRawContactId.getColumnIndex("raw_contact_id"));

                            Cursor cursorName = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                                    new String[]{"raw_contact_id", "data1", "mimetype"},
                                    "raw_contact_id = ? and mimetype = ? ", new String[]{raw_contact_id, "vnd.android.cursor.item/name"}, null);
                            if (cursorName.getCount() > 0) {
                                cursorName.moveToFirst();
                                name = cursorName.getString(cursorName.getColumnIndex("data1"));
                            }
                            cursorName.close();
                        }
                        else {
                            name = "";
                        }
                        cursorRawContactId.close();
                        person.setName(name);
                        list.add(person);
                    }
                }

                cursor2.close();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter = new CallContactPhoneAdapter(R.layout.call_contact_phone_list_item, str);
                        rvTabCallContact.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rvTabCallContact.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                AndroidsUtils.callPhone(getActivity(), mAdapter.getItem(position).getPhonenum().get(0));
                            }
                        });
                        mAdapter.setNewData(list);
                    }
                });
            }
        }).start();

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @BindView(R.id.rv_tab_call_contact)
    RecyclerView rvTabCallContact;
}
