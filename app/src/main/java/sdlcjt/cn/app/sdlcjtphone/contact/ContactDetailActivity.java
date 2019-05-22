package sdlcjt.cn.app.sdlcjtphone.contact;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sdlcjt.cn.app.sdlcjtphone.R;
import sdlcjt.cn.app.sdlcjtphone.callrecord.CallRecordListActivity;
import sdlcjt.cn.app.sdlcjtphone.contact.adapter.ContactPhoneAdapter;
import sdlcjt.cn.app.sdlcjtphone.contact.bean.Person;
import sdlcjt.cn.app.sdlcjtphone.contact.bean.PersonResultEvent;
import sdlcjt.cn.app.sdlcjtphone.entity.StatusResultEvent;
import sdlcjt.cn.app.sdlcjtphone.utils.AndroidsUtils;

/**
 * 联系人详情
 */
public class ContactDetailActivity extends AppCompatActivity {
    private ContactPhoneAdapter mAdapter;
    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_detail_activity);
        ButterKnife.bind(this);

        person = (Person) getIntent().getSerializableExtra(Person.Serial_Name);

        if (person == null)
            finish();
        initListView();
        initData();
    }

    private void initListView() {
        mAdapter = new ContactPhoneAdapter(R.layout.contact_phone_list_item);
        rvContactDetailPhone.setLayoutManager(new LinearLayoutManager(this));
        rvContactDetailPhone.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AndroidsUtils.callPhone(ContactDetailActivity.this, mAdapter.getItem(position));
            }
        });
    }

    private void initData() {
        Bitmap bitmap = AndroidsUtils.byte2Bitmap(person.getPics());
        if (bitmap != null) {
            ivHeader.setImageBitmap(bitmap);
        }

        tvContactDetailName.setText(person.getName());
        mAdapter.setNewData(person.getPhonenum());
    }

    @OnClick({R.id.ll_back, R.id.ll_del, R.id.ll_edit, R.id.ll_contact_detail_view_call_reacord})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.ll_del:
                del();
                break;
            case R.id.ll_edit:
                Intent intent = new Intent(ContactDetailActivity.this, ContactEditActivity.class);
                intent.putExtra(Person.Serial_Name, person);
                startActivity(intent);
                break;
            case R.id.ll_contact_detail_view_call_reacord:
                intent = new Intent(ContactDetailActivity.this, CallRecordListActivity.class);
                intent.putExtra(Person.Serial_Name, person);
                startActivity(intent);
                break;
        }
    }

    private void del() {
        new MaterialDialog.Builder(this)
                .title("温馨提示")
                .content("确认删除")
                .positiveText("确认")
                .negativeText("取消")
                .negativeColorRes(R.color.gray)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delData();
                    }
                }).show();
    }

    public void delData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO 先删除子表
                    ArrayList<ContentProviderOperation> opsRawContact = new ArrayList<>();
                    opsRawContact.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                            .withSelection("raw_contact_id = ? ", new String[]{String.valueOf(person.getRaw_contact_id())})
                            .build());

                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, opsRawContact);


                    // TODO 再删除主表
                    ArrayList<ContentProviderOperation> opsData = new ArrayList<>();
                    opsData.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                            .withSelection("_id = ? ", new String[]{String.valueOf(person.getRaw_contact_id())})
                            .build());
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, opsData);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new StatusResultEvent(1));
                            Toast.makeText(ContactDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ContactDetailActivity.this, "删除失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPersonResultEvent(PersonResultEvent event) {
        if (event == null) return;
        if (event.person != null) {
            person = event.person;
            initData();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }


    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.iv_header)
    RoundedImageView ivHeader;
    @BindView(R.id.rv_contact_detail_phone)
    RecyclerView rvContactDetailPhone;
    @BindView(R.id.tv_contact_detail_name)
    TextView tvContactDetailName;

}
