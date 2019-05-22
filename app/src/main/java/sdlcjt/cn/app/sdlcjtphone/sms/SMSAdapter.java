package sdlcjt.cn.app.sdlcjtphone.sms;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import sdlcjt.cn.app.sdlcjtphone.R;

/**
 * Created by slantech on 2019/05/09 13:34
 */
public class SMSAdapter extends BaseQuickAdapter<SMSInfo, BaseViewHolder> {
    public SMSAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SMSInfo item) {
        helper.setText(R.id.tv_sms_item_address, "电话：" + item.getAddress());
        helper.setText(R.id.tv_sms_item_person, "姓名：" + item.getPerson());
        helper.setText(R.id.tv_sms_item_type, "类型：" + item.getTypes());
        helper.setText(R.id.tv_sms_item_date, "日期：" + item.getDate());
        helper.setText(R.id.tv_sms_item_body, "内容：" + item.getBody());
    }


}
