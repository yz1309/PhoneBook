package sdlcjt.cn.app.sdlcjtphone.contact;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sdlcjt.cn.app.sdlcjtphone.R;
import sdlcjt.cn.app.sdlcjtphone.contact.adapter.ContactPhoneAddAdapter;
import sdlcjt.cn.app.sdlcjtphone.contact.bean.Person;
import sdlcjt.cn.app.sdlcjtphone.contact.bean.PersonResultEvent;
import sdlcjt.cn.app.sdlcjtphone.entity.StatusResultEvent;
import sdlcjt.cn.app.sdlcjtphone.utils.AndroidsUtils;
import sdlcjt.cn.app.sdlcjtphone.utils.PhotoFromPhotoAlbum;
import sdlcjt.cn.app.sdlcjtphone.utils.ULogger;

/**
 * 联系人编辑
 */
public class ContactEditActivity extends AppCompatActivity {

    private Person person;
    private ContactPhoneAddAdapter mAdapter;
    private Bitmap bitmap = null;
    private byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);
        ButterKnife.bind(this);
        person = (Person) getIntent().getSerializableExtra(Person.Serial_Name);

        initListView();
        initData();

    }

    private void initListView() {
        mAdapter = new ContactPhoneAddAdapter(R.layout.contact_phone_add_item);
        rvContactDetailPhone.setLayoutManager(new LinearLayoutManager(this));
        rvContactDetailPhone.setAdapter(mAdapter);
        mAdapter.addData("");
    }

    private void initData() {
        if (person == null)
            return;
        if (person.getPics() != null) {
            Bitmap bt = AndroidsUtils.byte2Bitmap(person.getPics());
            if (bt != null) {
                bitmap = bt;
                ivHeader.setImageBitmap(bt);
            }
        }
        etContactDetailName.setText(person.getName());
        mAdapter.setNewData(person.getPhonenum());
    }

    @OnClick({R.id.iv_header, R.id.ll_back, R.id.ll_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_header:
                goPhotoAlbum();
                break;
            case R.id.ll_back:
                finish();
                break;
            case R.id.ll_save:
                if (person != null && !TextUtils.isEmpty(person.getName()))
                    edit();
                else
                    save();
                break;
        }
    }

    //激活相册操作
    private void goPhotoAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String photoPath;
        if (requestCode == 2 && resultCode == RESULT_OK) {
            photoPath = PhotoFromPhotoAlbum.getRealPathFromUri(this, data.getData());
            Bitmap loacalBitmap = AndroidsUtils.getLoacalBitmap(photoPath);
            if (loacalBitmap != null) {
                bitmap = loacalBitmap;
                ivHeader.setImageBitmap(loacalBitmap);
                bytes = AndroidsUtils.bitmap2Bytes(imageZoom(bitmap));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void save() {
        String name = etContactDetailName.getText().toString();
        if (!checkInput())
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Uri uriRawContact = ContactsContract.RawContacts.CONTENT_URI;
                    ContentResolver resolver = getContentResolver();
                    ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                    // 向raw_contact表添加一条记录
                    //此处.withValue("account_name", null)一定要加，不然会抛NullPointerException
                    ContentProviderOperation operation1 = ContentProviderOperation
                            .newInsert(uriRawContact).withValue("account_name", null).build();
                    operations.add(operation1);

                    // 向data添加数据
                    Uri uriData = ContactsContract.Data.CONTENT_URI;


                    if (bitmap != null) {
                        //添加头像
                        ContentProviderOperation operationHeaderPic = ContentProviderOperation
                                .newInsert(uriData).withValueBackReference("raw_contact_id", 0)
                                //withValueBackReference的第二个参数表示引用operations[0]的操作的返回id作为此值
                                .withValue("mimetype", "vnd.android.cursor.item/photo")
                                .withValue("data15", bytes)
                                .build();
                        operations.add(operationHeaderPic);
                    }
                    //添加姓名
                    ContentProviderOperation operationName = ContentProviderOperation
                            .newInsert(uriData).withValueBackReference("raw_contact_id", 0)
                            //withValueBackReference的第二个参数表示引用operations[0]的操作的返回id作为此值
                            .withValue("mimetype", "vnd.android.cursor.item/name")
                            .withValue("data1", name)
                            .withValue("data2", "1").build();
                    operations.add(operationName);
                    for (int i = 0; i < getPhoneNumList().size(); i++) {
                        //添加手机数据
                        ContentProviderOperation operationPhone = ContentProviderOperation
                                .newInsert(uriData).withValueBackReference("raw_contact_id", 0)
                                .withValue("mimetype", "vnd.android.cursor.item/phone_v2")
                                .withValue("data1", getPhoneNumList().get(i))
                                .withValue("data2", i == 0 ? "2" : "7").build();

                        // data2 2代表主号码,7其他号码
                        operations.add(operationPhone);
                    }

                    resolver.applyBatch(ContactsContract.AUTHORITY, operations);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new StatusResultEvent(1));
                            Toast.makeText(ContactEditActivity.this, "新增成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ContactEditActivity.this, "新增失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private Boolean checkInput() {
        Boolean rtn = true;
        String name = etContactDetailName.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return false;
        }

        Boolean isInputPhone = false;
        List<String> phoneNum = new ArrayList<>();
        if (mAdapter != null && mAdapter.getData() != null && mAdapter.getData().size() > 0) {
            for (int i = 0; i < mAdapter.getData().size(); i++) {
                if (!TextUtils.isEmpty(mAdapter.getData().get(i))) {
                    isInputPhone = true;
                    phoneNum.add(mAdapter.getData().get(i));
                }
            }
        }
        if (!isInputPhone) {
            Toast.makeText(this, "请输入电话", Toast.LENGTH_SHORT).show();
            return false;
        }
        return rtn;
    }

    private List<String> getPhoneNumList() {
        List<String> phoneNum = new ArrayList<>();
        if (mAdapter != null && mAdapter.getData() != null && mAdapter.getData().size() > 0) {
            for (int i = 0; i < mAdapter.getData().size(); i++) {
                if (!TextUtils.isEmpty(mAdapter.getData().get(i))) {
                    phoneNum.add(mAdapter.getData().get(i));
                }
            }
        }
        return phoneNum;
    }

    private void edit() {
        String name = etContactDetailName.getText().toString();
        List<String> phoneNumList = getPhoneNumList();
        if (!checkInput())
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO 删除掉该联系人下的所有电话号码
                    ArrayList<ContentProviderOperation> opsDelPhone = new ArrayList<>();
                    opsDelPhone.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                            .withSelection("raw_contact_id = ? and mimetype = ? ", new String[]{String.valueOf(person.getRaw_contact_id()), "vnd.android.cursor.item/phone_v2"})
                            .build());

                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, opsDelPhone);

                    ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                    if (bitmap != null) {
                        ArrayList<ContentProviderOperation> opsDelPic = new ArrayList<>();
                        opsDelPic.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                                .withSelection("raw_contact_id = ? and mimetype = ? ", new String[]{String.valueOf(person.getRaw_contact_id()), "vnd.android.cursor.item/photo"})
                                .build());
                        getContentResolver().applyBatch(ContactsContract.AUTHORITY, opsDelPic);

                        /*ContentValues valuesHeaderPic = new ContentValues();
                        // 更新联系人头像
                        valuesHeaderPic.put("data15", bytes);
                        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                .withValues(valuesHeaderPic)
                                .withSelection("raw_contact_id = ? and mimetype = ? ", new String[]{String.valueOf(person.getRaw_contact_id()), "vnd.android.cursor.item/photo"})
                                .build());*/
                        ContentProviderOperation operationHeaderPic = ContentProviderOperation
                                .newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValue("raw_contact_id", person.getRaw_contact_id())
                                .withValue("mimetype", "vnd.android.cursor.item/photo")
                                .withValue("data15", bytes)
                                .build();
                        ops.add(operationHeaderPic);

                        ULogger.e("update photo,raw_contact_id=" + person.getRaw_contact_id() + ",mimetype=vnd.android.cursor.item/photo");
                    }

                    ContentValues valuesOne = new ContentValues();
                    // 更新联系人姓名
                    valuesOne.put("data1", name);
                    ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withValues(valuesOne)
                            .withSelection("raw_contact_id = ? and mimetype = ? ", new String[]{String.valueOf(person.getRaw_contact_id()), person.getMimetypeforname()})
                            .build());

                    /*String[] arrsTwo = new String[]{String.valueOf(person.getRaw_contact_id()), person.getMimetypeforphone()};
                    ContentValues valuesTwo = new ContentValues();
                    // 更新联系人电话
                    valuesTwo.put("data1", phone);
                    ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withValues(valuesTwo)
                            .withSelection("raw_contact_id = ? and mimetype = ? ", arrsTwo)
                            .build());*/

                    for (int i = 0; i < phoneNumList.size(); i++) {
                        //添加手机数据
                        ContentProviderOperation operationPhone = ContentProviderOperation
                                .newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValue("raw_contact_id", person.getRaw_contact_id())
                                .withValue("mimetype", "vnd.android.cursor.item/phone_v2")
                                .withValue("data1", phoneNumList.get(i))
                                .withValue("data2", i == 0 ? "2" : "7").build();

                        // data2 2代表主号码,7其他号码
                        ops.add(operationPhone);
                    }


                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (bitmap != null) {
                                person.setPics(AndroidsUtils.bitmap2Bytes(bitmap));
                            } else {
                                person.setPics(new byte[0]);
                            }
                            person.setName(name);
                            person.setPhonenum(phoneNumList);
                            EventBus.getDefault().post(new StatusResultEvent(1));
                            EventBus.getDefault().post(new PersonResultEvent(person));
                            Toast.makeText(ContactEditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ContactEditActivity.this, "修改失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private Bitmap imageZoom(Bitmap bitMap) {
        //图片允许最大空间 单位：KB
        double maxSize = 150.00;
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        double mid = b.length / 1024;
        //判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            //开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i),
                    bitMap.getHeight() / Math.sqrt(i));
        }

        return bitMap;
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.ll_save)
    LinearLayout llSave;
    @BindView(R.id.iv_header)
    RoundedImageView ivHeader;
    @BindView(R.id.et_contact_detail_name)
    EditText etContactDetailName;
    @BindView(R.id.rv_contact_detail_phone)
    RecyclerView rvContactDetailPhone;
}
