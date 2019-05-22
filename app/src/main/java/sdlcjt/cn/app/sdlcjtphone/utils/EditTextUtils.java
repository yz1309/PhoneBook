package sdlcjt.cn.app.sdlcjtphone.utils;

import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.EditText;

import java.lang.reflect.Field;

/**
 * Created by slantech on 2019/05/11 14:06
 */
public class EditTextUtils {
    /**
     * 输入框删除逻辑 可以根据光标的选择进行删除了
     */
    public static void deleteTextSelect(EditText editText) {

        if (editText.hasFocus()) {

            String num = editText.getText().toString().trim();

            int index = editText.getSelectionStart() - 1;

            if (num.length() > 0
                    && editText.getSelectionStart() != 0) {
                StringBuffer sb = new StringBuffer(num);
                sb.replace(index, index + 1, "");
                editText.setText(sb.toString());
                editText.setSelection(index);
                sb.setLength(0);
            }

        }

    }

    /**
     * 输入框的输入逻辑 可以根据光标的选择输入了
     *
     * @param text 要插入的内容
     */
    public static void inputText(EditText editText, String text) {

        if (editText.hasFocus()) {

            String num = editText.getText().toString().trim();

            int index = editText.getSelectionStart();

            if (TextUtils.isEmpty(num)) {
                editText.setText(text);
            } else if (num.length() == index) {
                editText.setText(num + text);
            } else {
                // 判断下，如果输入已经达到了最大值，不做操作。否则会把已输入的内容吃掉
                if (getMaxLength(editText) == num.length())
                    return;

                StringBuffer sb = new StringBuffer(num);
                sb.replace(index, index + 1, text + sb.substring(index, index + 1));
                editText.setText(sb.toString());
                sb.setLength(0);

            }

            if (index > num.length() - 1)
                editText.setSelection(editText.length());
            else
                editText.setSelection(index + 1);

        }

    }

    /**
     * 获取  输入框的maxLength
     *
     * @param et
     * @return
     */
    public static int getMaxLength(EditText et) {

        int length = 0;

        try {

            InputFilter[] inputFilters = et.getFilters();

            for (InputFilter filter : inputFilters) {

                Class<?> c = filter.getClass();

                if (c.getName().equals("android.text.InputFilter$LengthFilter")) {

                    Field[] f = c.getDeclaredFields();

                    for (Field field : f) {

                        if (field.getName().equals("mMax")) {

                            field.setAccessible(true);

                            length = (Integer) field.get(filter);

                        }

                    }

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return length;

    }
}
