package comp5216.sydney.edu.au.puzzlegame;

/**
 * Created by libohan on 26/9/17.
 */



import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import java.util.List;


public class MovingPuzzleAdapter extends BaseAdapter {

    // 映射List
    private List<Bitmap> mBitmapItemLists;
    private Context mContext;

    public MovingPuzzleAdapter(Context mContext, List<Bitmap> picList) {
        this.mContext = mContext;
        this.mBitmapItemLists = picList;
    }

    @Override
    public int getCount() {
        return mBitmapItemLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mBitmapItemLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv_pic_item = null;
        if (convertView == null) {
            iv_pic_item = new ImageView(mContext);
            // set the layout
            iv_pic_item.setLayoutParams(new GridView.LayoutParams(
                    mBitmapItemLists.get(position).getWidth(),
                    mBitmapItemLists.get(position).getHeight()));

            iv_pic_item.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            iv_pic_item = (ImageView) convertView;
        }
        iv_pic_item.setImageBitmap(mBitmapItemLists.get(position));
        return iv_pic_item;
    }
}
