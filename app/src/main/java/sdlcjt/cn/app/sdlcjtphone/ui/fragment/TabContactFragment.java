package sdlcjt.cn.app.sdlcjtphone.ui.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pub.devrel.easypermissions.EasyPermissions;
import sdlcjt.cn.app.sdlcjtphone.R;
import sdlcjt.cn.app.sdlcjtphone.contact.ContactDetailActivity;
import sdlcjt.cn.app.sdlcjtphone.contact.ContactEditActivity;
import sdlcjt.cn.app.sdlcjtphone.contact.ContactListActivity;
import sdlcjt.cn.app.sdlcjtphone.contact.adapter.ContactAdapter;
import sdlcjt.cn.app.sdlcjtphone.contact.bean.Person;
import sdlcjt.cn.app.sdlcjtphone.contact.view.WordsNavigation;
import sdlcjt.cn.app.sdlcjtphone.entity.StatusResultEvent;

import static com.lzy.okgo.utils.HttpUtils.runOnUiThread;


public class TabContactFragment extends Fragment implements
        WordsNavigation.onWordsChangeListener, AbsListView.OnScrollListener {

    Unbinder unbinder;
    private Handler handler;
    private List<Person> list;
    private Boolean isShowSearch = false;
    private TextView footerView;

    public TabContactFragment() {
    }

    private static final String[] PHONE_EXTERNAL_STORAG_CAMERA_LOCATION = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };

    public static TabContactFragment newInstance(Boolean isShowSearch) {
        TabContactFragment fragment = new TabContactFragment();
        Bundle args = new Bundle();
        args.putBoolean("isShowSearch", isShowSearch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isShowSearch = getArguments().getBoolean("isShowSearch", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_contact_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();

        initEvent();

        //初始化数据
        readAllContact();

        //设置列表点击滑动监听
        handler = new Handler();
        word.setOnWordsChangeListener(this);
    }

    private void initView() {
        if (isShowSearch) {
            llMainTop.setVisibility(View.GONE);
            llMainTopSearch.setVisibility(View.VISIBLE);
            tvContactNum.setVisibility(View.GONE);
        } else {
            llMainTop.setVisibility(View.VISIBLE);
            llMainTopSearch.setVisibility(View.GONE);
        }
    }

    private void initEvent() {
        etContactListSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                searchContact(s);
            }
        });
    }

    private void initListView(List<Person> tempList) {
        ContactAdapter adapter = new ContactAdapter(getActivity(), tempList);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
                intent.putExtra(Person.Serial_Name, tempList.get(i));
                startActivity(intent);
            }
        });
        if (!isShowSearch) {
            try {
                /*footerView = new TextView(getActivity());
                footerView.setTextColor(getResources().getColor(R.color.text_999));
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                footerView.setLayoutParams(lp);
                footerView.setGravity(Gravity.CENTER);
                footerView.setPadding(20, 20, 20, 20);
                footerView.setText(list.size() + "位联系人");*/

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //手指按下字母改变监听回调
    @Override
    public void wordsChange(String words) {
        updateWord(words);
        updateListView(words);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (list != null && list.size() > 0)
            //当滑动列表的时候，更新右侧字母列表的选中状态
            word.setTouchIndex(list.get(firstVisibleItem).getHeaderWord());
    }

    /**
     * @param words 首字母
     */
    private void updateListView(String words) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String headerWord = list.get(i).getHeaderWord();
                //将手指按下的字母与列表中相同字母开头的项找出来
                if (words.equals(headerWord)) {
                    //将列表选中哪一个
                    listView.setSelection(i);
                    //找到开头的一个即可
                    return;
                }
            }
        }

    }

    /**
     * 更新中央的字母提示
     *
     * @param words 首字母
     */
    private void updateWord(String words) {
        tv.setText(words);
        tv.setVisibility(View.VISIBLE);
        //清空之前的所有消息
        handler.removeCallbacksAndMessages(null);
        //1s后让tv隐藏
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv.setVisibility(View.GONE);
            }
        }, 500);
    }

    /**
     * 获取联系人的图片
     */
    public static byte[] getPhoto(final ContentResolver contentResolver, String contactId) {
        byte[] bytes = new byte[0];
        Cursor dataCursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                new String[]{"data15"},
                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
                        + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'",
                new String[]{String.valueOf(contactId)}, null);
        if (dataCursor != null) {
            if (dataCursor.getCount() > 0) {
                dataCursor.moveToFirst();
                bytes = dataCursor.getBlob(dataCursor.getColumnIndex("data15"));

            }
            dataCursor.close();
        }
        return bytes;
    }

    /**
     * 读取通讯录的全部的联系人需要先在raw_contact表中遍历id，并根据id到data表中获取数据
     */
    public void readAllContact() {
        if (!EasyPermissions.hasPermissions(getActivity(), PHONE_EXTERNAL_STORAG_CAMERA_LOCATION)) {
            return;
        }
        list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse("content://com.android.contacts/contacts"); //访问raw_contacts表
                ContentResolver resolver = getActivity().getContentResolver();
                Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
                while (cursor.moveToNext()) {
                    //获得id并且在data中寻找数据
                    int tempId = cursor.getInt(0);
                    uri = Uri.parse("content://com.android.contacts/contacts/" + tempId + "/data");
                    //data1存储各个记录的总数据，mimetype存放记录的类型，如电话、email等
                    Cursor cursor2 = resolver.query(uri, new String[]{
                            "data1",
                            "mimetype"
                    }, null, null, null);


                    byte[] bytes = getPhoto(getActivity().getContentResolver(), tempId + "");
                    String name = "";
                    List<String> phoneList = new ArrayList<>();
                    String phone = "";
                    String mimetypeforname = "";//data表的mimetype_id
                    String mimetypeforphone = "";//data表的mimetype_id

                    while (cursor2.moveToNext()) {
                        String data = cursor2.getString(cursor2.getColumnIndex("data1"));
                        String mimetypeTemp = cursor2.getString(cursor2.getColumnIndex("mimetype"));
                        if (mimetypeTemp.equals("vnd.android.cursor.item/name")) {       //如果是名字
                            name = data;
                            mimetypeforname = mimetypeTemp;
                        } else if (mimetypeTemp.equals("vnd.android.cursor.item/phone_v2")) {  //如果是电话
                            phone = data;
                            phoneList.add(phone);
                            mimetypeforphone = mimetypeTemp;
                        }
                    }
                    if (!TextUtils.isEmpty(phone)) {
                        Person person = new Person(tempId, mimetypeforname, mimetypeforphone, name, phoneList, bytes);
                        list.add(person);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //对集合排序
                        Collections.sort(list, new Comparator<Person>() {
                            @Override
                            public int compare(Person lhs, Person rhs) {
                                //根据拼音进行排序
                                return lhs.getHeaderWord().compareTo(rhs.getHeaderWord());
                            }
                        });
                        initListView(list);
                        tvContactNum.setText(list.size() + "位联系人");
                    }
                });
            }
        }).start();
    }

    private Boolean checkIsContainPhone(List<String> list, String key) {
        Boolean rtn = false;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).contains(key))
                    return true;
            }
        }
        return rtn;
    }

    public void searchContact(String search) {
        if (list == null || list.size() == 0)
            return;
        List<Person> newList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getName().contains(search) || checkIsContainPhone(list.get(i).getPhonenum(), search) || list.get(i).getHeaderWord().contains(search.toUpperCase())) {
                        newList.add(list.get(i));
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //对集合排序
                        Collections.sort(newList, new Comparator<Person>() {
                            @Override
                            public int compare(Person lhs, Person rhs) {
                                //根据拼音进行排序
                                return lhs.getHeaderWord().compareTo(rhs.getHeaderWord());
                            }
                        });
                        initListView(newList);
                    }
                });
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStatusResultEvent(StatusResultEvent event) {
        if (event == null) return;
        if (event.status == 1) {
            readAllContact();
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

    @OnClick({R.id.ll_back, R.id.ll_search, R.id.ll_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                EventBus.getDefault().post(new StatusResultEvent(100));
                break;
            case R.id.ll_search:
                startActivity(new Intent(getActivity(), ContactListActivity.class));
                break;
            case R.id.ll_add:
                Intent intent = new Intent(getActivity(), ContactEditActivity.class);
                startActivity(intent);
                break;
        }
    }


    @BindView(R.id.tv_contact_num)
    TextView tvContactNum;
    @BindView(R.id.ll_main_top_search)
    LinearLayout llMainTopSearch;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.et_contact_list_search)
    EditText etContactListSearch;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.ll_add)
    LinearLayout llAdd;
    @BindView(R.id.ll_main_top)
    LinearLayout llMainTop;
    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.words)
    WordsNavigation word;
    @BindView(R.id.tv_contact_tag)
    TextView tv;
}
