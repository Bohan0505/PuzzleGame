package comp5216.sydney.edu.au.puzzlegame;

/**
 * Created by libohan on 26/9/17.
 */


import java.util.ArrayList;
import java.util.List;

public class SwippingGameUtil {

    // 游戏信息单元格Bean
    public static List<ItemBean> mItemBeans = new ArrayList<ItemBean>();
    // 空格单元格
    public static ItemBean mBlankItemBean = new ItemBean();

    /**
     * swip two ites
     *
     * @param first  first clicked piece
     * @param second second clicked piece
     */
    public static void swapItems(ItemBean first, ItemBean second) {
        ItemBean tempItemBean = new ItemBean();
        tempItemBean.setBitmapId(first.getBitmapId());
        first.setBitmapId(second.getBitmapId());
        second.setBitmapId(tempItemBean.getBitmapId());
        tempItemBean.setBitmap(first.getBitmap());
        first.setBitmap(second.getBitmap());
        second.setBitmap(tempItemBean.getBitmap());
        SwippingGameUtil.mBlankItemBean = first;
    }

    /**
     * get random pieces
     */
    public static void getPuzzleGenerator() {
        int index = 0;
        // get the random order
        for (int i = 0; i < mItemBeans.size(); i++) {
            index = (int) (Math.random() *
                    SwippingPuzzle.level * SwippingPuzzle.level);
            swapItems(mItemBeans.get(index), SwippingGameUtil.mBlankItemBean);
        }
        List<Integer> data = new ArrayList<Integer>();
        for (int i = 0; i < mItemBeans.size(); i++) {
            data.add(mItemBeans.get(i).getBitmapId());
        }
    }

    // check after each step, if the game is finished or not
    public static boolean isSuccess() {
        for (ItemBean tempBean : SwippingGameUtil.mItemBeans) {
            if (tempBean.getBitmapId() != 0 &&
                    (tempBean.getItemId()) == tempBean.getBitmapId()) {
                continue;
            } else if (tempBean.getBitmapId() == 0 &&
                    tempBean.getItemId() == SwippingPuzzle.level * SwippingPuzzle.level) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

}
