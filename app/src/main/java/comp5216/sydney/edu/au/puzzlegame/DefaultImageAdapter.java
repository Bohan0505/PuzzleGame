package comp5216.sydney.edu.au.puzzlegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by libohan on 25/9/17.
 */

public class DefaultImageAdapter extends BaseAdapter {

    // 映射List
    private List<Bitmap> picList;
    private Context context;

    public DefaultImageAdapter(Context context, List<Bitmap> picList) {
        this.context = context;
        this.picList = picList;
    }

    @Override
    public int getCount() {
        return picList.size();
    }

    @Override
    public Object getItem(int position) {
        return picList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ImageView iv_pic_item = null;
        int density = (int) ScreenUtil.getDeviceDensity(context);
        if (convertView == null) {
            iv_pic_item = new ImageView(context);
            // set image layout
            iv_pic_item.setLayoutParams(new GridView.LayoutParams(
                    120 * density,
                    120 * density));
            // 设置显示比例类型
            iv_pic_item.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            iv_pic_item = (ImageView) convertView;
        }
//        iv_pic_item.setBackgroundColor(color.black);
        iv_pic_item.setImageBitmap(picList.get(position));
        return iv_pic_item;
    }
}

