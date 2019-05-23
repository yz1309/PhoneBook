package sdlcjt.cn.app.sdlcjtphone.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Created by slantech on 2019/05/23 11:29
 */
public class ContactsUtils {
    public static void getContacts(Activity activity) {
        StringBuffer sb = new StringBuffer("\ngetContacts");
        ContentResolver resolver = activity.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, new String[]{"_id", "name_raw_contact_id"}, null, null, null);
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(0);
            int name_raw_contact_id = cursor.getInt(1);
            sb.append("\n_id=" + _id + ",name_raw_contact_id=" + name_raw_contact_id);
        }

        ULogger.e(sb.toString());
    }

    public static void getRawContacts(Activity activity) {
        StringBuffer sb = new StringBuffer("\ngetRawContacts");
        ContentResolver resolver = activity.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.RawContacts.CONTENT_URI, new String[]{"_id", "contact_id", "display_name"}, null, null, null);
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(0);
            int contact_id = cursor.getInt(1);
            String display_name = cursor.getString(2);
            sb.append("\n_id=" + _id + ",contact_id=" + contact_id + ",display_name=" + display_name);
        }
        ULogger.e(sb.toString());
    }

    public static void getData(Activity activity) {
        StringBuffer sb = new StringBuffer("\ngetData");
        ContentResolver resolver = activity.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI, new String[]{"_id", "raw_contact_id", "data1",
                "mimetype"}, null, null, null);
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(0);
            int raw_contact_id = cursor.getInt(1);
            String data1 = cursor.getString(2);
            String mimetypeTemp = cursor.getString(cursor.getColumnIndex("mimetype"));
            sb.append("\n_id=" + _id + ",raw_contact_id=" + raw_contact_id + ",data1=" + data1 + ",mimetypeTemp=" + mimetypeTemp);
        }
        ULogger.e(sb.toString());
    }
}
