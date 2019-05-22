package sdlcjt.cn.app.sdlcjtphone.contact;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import sdlcjt.cn.app.sdlcjtphone.R;
import sdlcjt.cn.app.sdlcjtphone.entity.StatusResultEvent;
import sdlcjt.cn.app.sdlcjtphone.ui.fragment.TabContactFragment;

/**
 * 通讯录
 * 先去取raw_contacts 表中的所有数据得到_id
 * 接着通过_id去data表总通过raw_contact_id=_id来获取数据
 * 新增 需要先给raw_contacts新增，接着再去data新增姓名和电话数据
 * 删除 通过raw_contact_id来删除data表数据
 * 修改 通过raw_contact_id和mimetype来修改
 */
public class ContactListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list_activity);
        ButterKnife.bind(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_contain, TabContactFragment.newInstance(true)).commit();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStatusResultEvent(StatusResultEvent event) {
        if (event == null) return;
        if (event.status == 100) {
            finish();
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

}
