package sdlcjt.cn.app.sdlcjtphone.contact.utils;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

/*
 * 描述:     得到指定汉字的拼音
 */
public class PinYinUtils {
    /**
     * 将hanzi转成拼音
     *
     * @param hanzi 汉字或字母
     * @return 拼音
     */
    public static String getPinyin(String hanzi) {
        StringBuilder sb = new StringBuilder();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //由于不能直接对多个汉子转换，只能对单个汉子转换
        char[] arr = hanzi.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (Character.isWhitespace(arr[i])) {
                continue;
            }
            try {
                String[] pinyinArr = PinyinHelper.toHanyuPinyinStringArray(arr[i], format);
                if (pinyinArr != null) {
                    sb.append(pinyinArr[0]);
                } else {
                    sb.append(arr[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //不是正确的汉字
                sb.append(arr[i]);
            }

        }
        return sb.toString();
    }

    /**
     * 判断一个字符串的首字符是否为字母
     *
     * @param s
     * @return
     */
    public static boolean checkStrFirstIsLetter(String s) {
        if (TextUtils.isEmpty(s))
            return false;
        char c = s.charAt(0);
        int i = (int) c;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否为汉字
     *
     * @param s
     * @return
     */
    public static boolean checkStrFirstIsHanZi(String s) {
        int n = 0;
        for (int i = 0; i < s.length(); i++) {
            n = (int) s.charAt(i);
            if ((19968 <= n && n < 40869)) {
                return true;
            }
        }
        return false;
    }
}
