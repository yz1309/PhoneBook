package sdlcjt.cn.app.sdlcjtphone.dialpad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;
import sdlcjt.cn.app.sdlcjtphone.R;
import sdlcjt.cn.app.sdlcjtphone.contact.ContactEditActivity;
import sdlcjt.cn.app.sdlcjtphone.contact.bean.Person;
import sdlcjt.cn.app.sdlcjtphone.entity.StrResultEvent;
import sdlcjt.cn.app.sdlcjtphone.utils.AndroidsUtils;
import sdlcjt.cn.app.sdlcjtphone.utils.EditTextUtils;

/**
 * Created by slantech on 2019/05/11 9:54
 */
public class DialpadFragment extends Fragment {
    Unbinder unbinder;

    public static DialpadFragment newInstance() {
        DialpadFragment f = new DialpadFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialpad_fragment, container,
                false);
        unbinder = ButterKnife.bind(this, view);
        disableShowSoftInput(etDialpadFragNumber);
        initEvent();
        return view;
    }

    private void initEvent() {
        etDialpadFragNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                EventBus.getDefault().post(new StrResultEvent(s.toString()));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnLongClick({R.id.ll_dialpad_one_number10, R.id.ll_dialpad_one_number11, R.id
            .ll_dialpad_one_number12})
    public boolean onViewLongClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_dialpad_one_number10:
                addTxt(",");
                break;
            case R.id.ll_dialpad_one_number11:
                addTxt("+");
                break;
            case R.id.ll_dialpad_one_number12:
                addTxt(";");
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * 禁止Edittext弹出软件盘，光标依然正常显示。
     */
    public static void disableShowSoftInput(EditText editText) {
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(editText, false);
        } catch (Exception e) {
        }
    }

    @OnClick({
            R.id.iv_dialpad_frag_number_add,
            R.id.iv_dialpad_frag_number_call,
            R.id.iv_dialpad_frag_number_del, R.id.ll_dialpad_one_number1, R.id.ll_dialpad_one_number2, R.id.ll_dialpad_one_number3, R.id.ll_dialpad_one_number4, R.id.ll_dialpad_one_number5, R.id
            .ll_dialpad_one_number6, R.id.ll_dialpad_one_number7, R.id.ll_dialpad_one_number8, R.id.ll_dialpad_one_number9, R.id.ll_dialpad_one_number10, R.id.ll_dialpad_one_number11, R.id
            .ll_dialpad_one_number12})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_dialpad_frag_number_add:
                Intent intent = new Intent(getActivity(), ContactEditActivity.class);
                String s = etDialpadFragNumber.getText().toString();
                if (!TextUtils.isEmpty(s)) {
                    List<String> phonList = new ArrayList<>();
                    phonList.add(s);
                    Person person = new Person("", phonList);
                    intent.putExtra(sdlcjt.cn.app.sdlcjtphone.contact.bean.Person.Serial_Name, person);
                }
                startActivity(intent);
                break;
            case R.id.iv_dialpad_frag_number_call:
                callPhone();
                break;
            case R.id.iv_dialpad_frag_number_del:
                EditTextUtils.deleteTextSelect(etDialpadFragNumber);
                break;
            case R.id.ll_dialpad_one_number1:
                addTxt("1");
                break;
            case R.id.ll_dialpad_one_number2:
                addTxt("2");
                break;
            case R.id.ll_dialpad_one_number3:
                addTxt("3");
                break;
            case R.id.ll_dialpad_one_number4:
                addTxt("4");
                break;
            case R.id.ll_dialpad_one_number5:
                addTxt("5");
                break;
            case R.id.ll_dialpad_one_number6:
                addTxt("6");
                break;
            case R.id.ll_dialpad_one_number7:
                addTxt("7");
                break;
            case R.id.ll_dialpad_one_number8:
                addTxt("8");
                break;
            case R.id.ll_dialpad_one_number9:
                addTxt("9");
                break;
            case R.id.ll_dialpad_one_number10:
                addTxt("*");
                break;
            case R.id.ll_dialpad_one_number11:
                addTxt("0");
                break;
            case R.id.ll_dialpad_one_number12:
                addTxt("#");
                break;
        }
    }

    private void callPhone() {
        String phone = etDialpadFragNumber.getText().toString();
        if (TextUtils.isEmpty(phone))
            return;
        AndroidsUtils.callPhone(getActivity(), phone);
    }

    private void addTxt(String s) {
        etDialpadFragNumber.requestFocus();

        EditTextUtils.inputText(etDialpadFragNumber, s);
    }

    @BindView(R.id.et_dialpad_frag_number)
    EditText etDialpadFragNumber;
    @BindView(R.id.iv_dialpad_frag_number_del)
    ImageView ivDialpadFragNumberDel;

}
