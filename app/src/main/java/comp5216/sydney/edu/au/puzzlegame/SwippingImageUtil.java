package comp5216.sydney.edu.au.puzzlegame;

/**
 * Created by libohan on 26/9/17.
 */


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.ArrayList;
import java.util.List;


public class SwippingImageUtil {

    public ItemBean itemBean;

    public void createInitBitmaps(int level, Bitmap picSelected,
                                  Context context) {
        Bitmap bitmap = null;
        List<Bitmap> bitmapItems = new ArrayList<Bitmap>();
        // 每个Item的宽高
        int itemWidth = picSelected.getWidth() / level;
        int itemHeight = picSelected.getHeight() / level;
        for (int i = 1; i <= level; i++) {
            for (int j = 1; j <= level; j++) {
                bitmap = Bitmap.createBitmap(
                        picSelected,
                        (j - 1) * itemWidth,
                        (i - 1) * itemHeight,
                        itemWidth,
                        itemHeight);
                bitmapItems.add(bitmap);
                itemBean = new ItemBean(
                        (i - 1) * level + j,
                        (i - 1) * level + j,
                        bitmap);
                SwippingGameUtil.mItemBeans.add(itemBean);
            }
        }

        SwippingPuzzle.mLastBitmap = bitmapItems.get(level*level - 1);
        bitmapItems.remove(level * level - 1);
        SwippingGameUtil.mItemBeans.remove(level * level - 1);
        Bitmap blankBitmap = SwippingPuzzle.mLastBitmap;
        blankBitmap = Bitmap.createBitmap(
                blankBitmap, 0, 0, itemWidth, itemHeight);
        bitmapItems.add(blankBitmap);
        SwippingGameUtil.mItemBeans.add(new ItemBean(level * level, 0, blankBitmap));
        SwippingGameUtil.mBlankItemBean = SwippingGameUtil.mItemBeans.get(level * level - 1);
    }


    public Bitmap resizeBitmap(float newWidth, float newHeight, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(
                newWidth / bitmap.getWidth(),
                newHeight / bitmap.getHeight());
        Bitmap newBitmap = Bitmap.createBitmap(
                bitmap, 0, 0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix, true);
        return newBitmap;
    }
}
