package sdlcjt.cn.app.sdlcjtphone.contact.adapter;

import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import sdlcjt.cn.app.sdlcjtphone.R;
import sdlcjt.cn.app.sdlcjtphone.contact.bean.Person;
import sdlcjt.cn.app.sdlcjtphone.utils.AndroidsUtils;

/**
 * Created by slantech on 2019/05/17 17:40
 */
public class CallContactPhoneAdapter extends BaseQuickAdapter<Person, BaseViewHolder> {
    private String search = "";

    public CallContactPhoneAdapter(int layoutResId) {
        super(layoutResId);
    }

    public CallContactPhoneAdapter(int layoutResId, String search) {
        super(layoutResId);
        this.search = search;
    }

    @Override
    protected void convert(BaseViewHolder helper, Person item) {
        if (!TextUtils.isEmpty(item.getName())) {
            helper.setText(R.id.tv_contact_phone_list_item_name, item.getName());
            helper.setText(R.id.tv_contact_phone_list_item_phone, Html.fromHtml(item.getPhonenum().get(0).replace(search, "<font color='#149d73'>" + search + "</font>")));
            helper.setVisible(R.id.tv_contact_phone_list_item_phone, true);
        } else {
            helper.setText(R.id.tv_contact_phone_list_item_name, Html.fromHtml(item.getPhonenum().get(0).replace(search, "<font color='#149d73'>" + search + "</font>")));
            helper.setVisible(R.id.tv_contact_phone_list_item_phone, false);
        }

        helper.setOnClickListener(R.id.iv_contact_phone_list_item_call, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidsUtils.callPhone((Activity) mContext, item.getPhonenum().get(0));
            }
        });
    }
}
