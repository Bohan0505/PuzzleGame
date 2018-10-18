package comp5216.sydney.edu.au.puzzlegame;

/**
 * Created by libohan on 26/9/17.
 */

 import java.util.ArrayList;
 import java.util.List;

/**
 *swip pieces and generate game
 */
public class GameUtil {

    // List to store all of the small pieces
    public static List<ItemBean> mItemBeans = new ArrayList<ItemBean>();
    // store the blank image
    public static ItemBean mBlankItemBean = new ItemBean();

    /**
     * if the item is movable
     *
     * @param position position
     * @return
     */
    public static boolean isMoveable(int position) {
        int level = MovingPuzzle.level;
        // get the blank image
        int blankId = GameUtil.mBlankItemBean.getItemId() - 1;
        if (blankId-position==level || position-blankId==level){
            return true;
        }
        // same line, distance: 1
        if ((blankId / level == position / level) &&
                Math.abs(blankId - position) == 1) {
            return true;
        }
        return false;
    }

    /**
     * swip the blank with item
     *
     * @param from  the piece clicked
     * @param blank blank piece
     */
    public static void swapItems(ItemBean from, ItemBean blank) {
        ItemBean tempItemBean = new ItemBean();
        // 交换BitmapId
        tempItemBean.setBitmapId(from.getBitmapId());
        from.setBitmapId(blank.getBitmapId());
        blank.setBitmapId(tempItemBean.getBitmapId());
        // 交换Bitmap
        tempItemBean.setBitmap(from.getBitmap());
        from.setBitmap(blank.getBitmap());
        blank.setBitmap(tempItemBean.getBitmap());
        // 设置新的Blank
        GameUtil.mBlankItemBean = from;
    }

    /**
     * Random the Item
     */
    public static void getPuzzleGenerator() {
        int index = 0;
        // random the order
        for (int i = 0; i < mItemBeans.size(); i++) {
            index = (int) (Math.random() *
                    MovingPuzzle.level * MovingPuzzle.level);
            swapItems(mItemBeans.get(index), GameUtil.mBlankItemBean);
        }
        List<Integer> data = new ArrayList<Integer>();
        for (int i = 0; i < mItemBeans.size(); i++) {
            data.add(mItemBeans.get(i).getBitmapId());
        }
        // decide if this can be solved
        if (canSolve(data)) {
            return;
        } else {
            getPuzzleGenerator();
        }
    }

    /**
     *
     * @return success
     */
    public static boolean isSuccess() {
        for (ItemBean tempBean : GameUtil.mItemBeans) {
            if (tempBean.getBitmapId() != 0 &&
                    (tempBean.getItemId()) == tempBean.getBitmapId()) {
                continue;
            } else if (tempBean.getBitmapId() == 0 &&
                    tempBean.getItemId() == MovingPuzzle.level * MovingPuzzle.level) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * test if the generated random pieces can be solved
     * @param data the list of pieces
     * @return can e solved or not
     */
    public static boolean canSolve(List<Integer> data) {
        // 获get blank Id
        int blankId = GameUtil.mBlankItemBean.getItemId();
        // algorithm
        if (data.size() % 2 == 1) {
            return getInversions(data) % 2 == 0;
        } else {
            // 从底往上数,空格位于奇数行
            if (((blankId - 1) / MovingPuzzle.level) % 2 == 1) {
                return getInversions(data) % 2 == 0;
            } else {
                // 从底往上数,空位位于偶数行
                return getInversions(data) % 2 == 1;
            }
        }
    }

    /**
     * 计算倒置和算法
     *
     * @param data 拼图数组数据
     * @return 该序列的倒置和
     */
    public static int getInversions(List<Integer> data) {
        int inversions = 0;
        int inversionCount = 0;
        for (int i = 0; i < data.size(); i++) {
            for (int j = i + 1; j < data.size(); j++) {
                int index = data.get(i);
                if (data.get(j) != 0 && data.get(j) < index) {
                    inversionCount++;
                }
            }
            inversions += inversionCount;
            inversionCount = 0;
        }
        return inversions;
    }
}
