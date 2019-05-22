package sdlcjt.cn.app.sdlcjtphone.contact.adapter;

import android.app.Activity;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import sdlcjt.cn.app.sdlcjtphone.R;
import sdlcjt.cn.app.sdlcjtphone.utils.AndroidsUtils;

/**
 * Created by slantech on 2019/05/17 17:40
 */
public class ContactPhoneAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public ContactPhoneAdapter(int layoutResId) {
        super(layoutResId);
    }


    @Override
    protected void convert(BaseViewHolder helper, String item) {

        helper.setText(R.id.tv_contact_phone_list_item_phone, item);

        helper.setOnClickListener(R.id.iv_contact_phone_list_item_call, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidsUtils.callPhone((Activity) mContext, item);
            }
        });
    }
}
