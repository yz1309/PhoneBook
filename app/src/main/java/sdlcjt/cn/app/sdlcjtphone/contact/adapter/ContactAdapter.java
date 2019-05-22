package sdlcjt.cn.app.sdlcjtphone.contact.adapter;
/*
 * 描述:       ListView列表适配器
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import sdlcjt.cn.app.sdlcjtphone.R;
import sdlcjt.cn.app.sdlcjtphone.contact.bean.Person;
import sdlcjt.cn.app.sdlcjtphone.utils.AndroidsUtils;

public class ContactAdapter extends BaseAdapter {
    private Context mContext;
    private List<Person> list;
    private LayoutInflater inflater;

    public ContactAdapter(Context context, List<Person> list) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.contact_list_item, null);
            holder.tv_word = (TextView) convertView.findViewById(R.id.tv_word);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.ivHeader = (RoundedImageView) convertView.findViewById(R.id.iv_header);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Person person = list.get(position);
        String word = person.getHeaderWord();
        holder.tv_word.setText(word);
        holder.tv_name.setText(Html.fromHtml(person.getName()));

        try {
            Bitmap bitmap = AndroidsUtils.byte2Bitmap(person.getPics());
            if (person.getPics() == null || bitmap == null) {
                holder.ivHeader.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_contact_default));
            } else {
                holder.ivHeader.setImageBitmap(bitmap);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

        //将相同字母开头的合并在一起
        if (position == 0) {
            //第一个是一定显示的
            holder.tv_word.setVisibility(View.VISIBLE);
        } else {
            //后一个与前一个对比,判断首字母是否相同，相同则隐藏
            String headerWord = list.get(position - 1).getHeaderWord();
            if (word.equals(headerWord)) {
                holder.tv_word.setVisibility(View.GONE);
            } else {
                holder.tv_word.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView tv_word;
        private TextView tv_name;
        private RoundedImageView ivHeader;
    }
}
