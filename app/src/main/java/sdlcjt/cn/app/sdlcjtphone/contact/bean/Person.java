package sdlcjt.cn.app.sdlcjtphone.contact.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sdlcjt.cn.app.sdlcjtphone.contact.utils.PinYinUtils;

/**
 * 联系人列表信息
 */
public class Person implements Serializable {
    public static final String Serial_Name = "person";
    //姓名
    private int raw_contact_id;//raw_contact表的_id
    private String mimetypeforname;//data表的mimetype_id
    private String mimetypeforphone;//data表的mimetype_id
    private String name;
    private byte[] pics;
    private List<String> phonenum;
    //拼音首字母
    private String headerWord;

    public Person(String name, List<String> phonenum) {
        this.name = name;
        this.phonenum = phonenum;
    }

    public Person(int raw_contact_id, String mimetypeforname, String mimetypeforphone, String name, List<String> phonenum, byte[] pics) {
        this.raw_contact_id = raw_contact_id;
        this.mimetypeforname = mimetypeforname;
        this.mimetypeforphone = mimetypeforphone;
        this.name = name;
        this.phonenum = phonenum;
        this.pics = pics;
        if (!TextUtils.isEmpty(name)){
            if (PinYinUtils.checkStrFirstIsLetter(name)){
                this.headerWord = name.substring(0,1).toUpperCase();
            }
            else if (PinYinUtils.checkStrFirstIsHanZi(name)){
                String pinyin = PinYinUtils.getPinyin(name);
                if (!TextUtils.isEmpty(pinyin) && pinyin.length() > 1) {
                    this.headerWord = pinyin.substring(0, 1);
                }
                else {
                    this.headerWord = "#";
                }
            }
            else {
                this.headerWord = "#";
            }
        }else {
            this.headerWord = "#";
        }
    }

    public byte[] getPics() {
        return pics;
    }

    public void setPics(byte[] pics) {
        this.pics = pics;
    }

    public int getRaw_contact_id() {
        return raw_contact_id;
    }

    public void setRaw_contact_id(int raw_contact_id) {
        this.raw_contact_id = raw_contact_id;
    }

    public String getMimetypeforname() {
        return mimetypeforname == null ? "" : mimetypeforname;
    }

    public void setMimetypeforname(String mimetypeforname) {
        this.mimetypeforname = mimetypeforname;
    }

    public String getMimetypeforphone() {
        return mimetypeforphone == null ? "" : mimetypeforphone;
    }

    public void setMimetypeforphone(String mimetypeforphone) {
        this.mimetypeforphone = mimetypeforphone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeaderWord() {
        return headerWord;
    }

    public List<String> getPhonenum() {
        return phonenum == null ? new ArrayList<>() : phonenum;
    }

    public void setPhonenum(List<String> phonenum) {
        this.phonenum = phonenum;
    }
}
