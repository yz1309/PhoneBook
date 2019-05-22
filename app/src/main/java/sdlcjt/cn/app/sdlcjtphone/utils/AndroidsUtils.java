package sdlcjt.cn.app.sdlcjtphone.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static android.content.Intent.ACTION_CALL;

/**
 * Created by slantech on 2019/05/08 13:21
 */
public class AndroidsUtils {
    public static void callPhone(Activity activity, String phone) {
        // TODO 检查是否为拦截号码
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(activity, "请输入号码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.equals("18716388243")) {
            Toast.makeText(activity, "当前号码不允许拨打", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Intent intentCall = new Intent(ACTION_CALL, Uri.parse("tel:" + phone));//直接拨打电话
            activity.startActivity(intentCall);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap byte2Bitmap(byte[] data) {
        if (data != null && data.length > 0)
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        else
            return null;
    }

}
