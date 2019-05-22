package sdlcjt.cn.app.sdlcjtphone.callrecord;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import sdlcjt.cn.app.sdlcjtphone.R;
import sdlcjt.cn.app.sdlcjtphone.utils.CalendarTool;

/**
 * Created by slantech on 2019/05/09 13:34
 */
public class CallRecordAdapter extends BaseQuickAdapter<CallRecordInfo, BaseViewHolder> {
    public CallRecordAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, CallRecordInfo item) {
        if (item.getType().equals("去电")) {
            helper.setVisible(R.id.iv_call_record_item_outgoing, true);
        } else {
            helper.setVisible(R.id.iv_call_record_item_outgoing, false);
        }
        helper.setText(R.id.tv_call_record_item_name, "");
        helper.setText(R.id.tv_call_record_item_phone, "");
        if (!TextUtils.isEmpty(item.getName())) {
            helper.setText(R.id.tv_call_record_item_name, item.getName());
            helper.setText(R.id.tv_call_record_item_phone, item.getNumber());
        } else {
            helper.setText(R.id.tv_call_record_item_name, item.getNumber());
        }
        helper.setText(R.id.tv_call_record_item_date, CalendarTool.formatDateStr2Desc(item.getDate(), CalendarTool.dateFormatYMDHMS));
    }


}
