package sdlcjt.cn.app.sdlcjtphone.contact.adapter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import sdlcjt.cn.app.sdlcjtphone.R;

/**
 * Created by slantech on 2019/05/17 17:40
 */
public class ContactPhoneAddAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public ContactPhoneAddAdapter(int layoutResId) {
        super(layoutResId);
    }


    @Override
    protected void convert(BaseViewHolder helper, String item) {
        EditText et = helper.getView(R.id.et_contact_phone_list_item_phone);
        helper.setOnClickListener(R.id.iv_contact_phone_list_item_phone_del, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getData().size() > 1) {
                    remove(helper.getLayoutPosition());
                } else {
                    Toast.makeText(mContext, "至少输入一条手机号码", Toast.LENGTH_SHORT).show();
                }
            }
        });
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    getData().set(helper.getLayoutPosition(), s.toString());
                    if (s.toString().length() > 0) {
                        Boolean isAdd = true;
                        int num = 0;
                        for (int i = 0; i < getData().size(); i++) {
                            if (TextUtils.isEmpty(getData().get(i))) {
                                num++;
                            }
                        }

                        if (num > 0)
                            isAdd = false;
                        if (isAdd) {
                            addData("");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        et.setText(item);

    }
}
