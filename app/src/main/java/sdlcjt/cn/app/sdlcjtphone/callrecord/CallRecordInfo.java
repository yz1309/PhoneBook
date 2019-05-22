package sdlcjt.cn.app.sdlcjtphone.callrecord;

/**
 * Created by slantech on 2019/05/09 13:02
 */
public class CallRecordInfo {
    private int id;
    private String name; // 名称
    private String number; // 号码
    private String date; // 日期
    private String type; // 来电:1，拨出:2,未接:3
    private int count; // 通话次数
    private String duration;

    public int getId() {
        return id  ;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number == null ? "" : number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date == null ? "" : date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDuration() {
        return duration == null ? "" : duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
