package sdlcjt.cn.app.sdlcjtphone.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;

import sdlcjt.cn.app.sdlcjtphone.utils.CalendarTool;

/**
 * 监听接收到的短信
 * Created by slantech on 2019/05/09 15:47
 */
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);

                SMSInfo smsInfo = new SMSInfo();
                smsInfo.setAddress(msg.getOriginatingAddress());

                String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME};
                //设置查询条件
                String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "='" + msg.getOriginatingAddress() + "'";
                Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        cols, selection, null, null);
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    smsInfo.setPerson(cursor.getString(nameFieldColumnIndex));
                }
                cursor.close();
                smsInfo.setTypes("接收");
                smsInfo.setDate(CalendarTool.getStringByFormat(msg.getTimestampMillis(), CalendarTool.dateFormatYMDHMS));
                smsInfo.setBody(msg.getMessageBody());

                // TODO 上传短信数据

                Log.i("SmsReceiver", "address:" + msg.getOriginatingAddress()
                        + "   body:" + msg.getDisplayMessageBody() + "  time:"
                        + msg.getTimestampMillis());
            }
        }
    }
}
