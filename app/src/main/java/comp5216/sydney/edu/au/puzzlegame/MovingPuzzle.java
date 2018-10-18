package comp5216.sydney.edu.au.puzzlegame;

/**
 * Created by libohan on 26/9/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MovingPuzzle extends Activity implements OnClickListener {

    // the removed image
    public static Bitmap mLastBitmap;
    // N*N
    public static int level = 2;
    // steps
    public static int COUNT_INDEX = 0;
    // time
    public static int TIMER_INDEX = 10*(level-1)*(level-1)*(level*level);
    //current level
    public static int type=0;
    /**
     * UI更新Handler
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    // Timer
                    TIMER_INDEX--;
                    mTvTimer.setText("" + TIMER_INDEX);
                    break;
                default:
                    break;
            }
        }
    };
    private Bitmap mPicSelected;
    // PuzzlePanel
    private GridView mGvPuzzleMainDetail;
    private int mResId;
    private String mPicPath;
    private ImageView mImageView;
    // Button
    private Button mBtnChange;
    private Button mBtnImage;
    private Button mBtnRestart;
    private Button mBtnSwip;
    // show steps
    private TextView mTvPuzzleMainCounts;
    // show timer
    private TextView mTvTimer;
    // small pieces
    private List<Bitmap> mBitmapItemLists = new ArrayList<Bitmap>();
    // GridView adapter
    private MovingPuzzleAdapter mAdapter;
    // if showing the original image
    private boolean mIsShowImg;
    // timer
    private Timer mTimer;
    /**
     * 计时器线程
     */
    private TimerTask mTimerTask;
    private CountDownTimer countDownTimer;
    private Bitmap picSelectedTemp = null;


    Uri myUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moving_puzzle);
        // get the selected image
//        Bitmap picSelectedTemp = null;
        // default / gallery
        mResId = getIntent().getExtras().getInt("picSelectedID");



//        myUri=getIntent().getData();
//        try {
//            picSelectedTemp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), myUri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        if (mResId != 0) {
            picSelectedTemp = BitmapFactory.decodeResource(
                    getResources(), mResId);
        } else {
            Uri imageUri = getIntent().getData();
            if (imageUri != null) {
                try {
                    picSelectedTemp = ImageUtil.getBitmapFormUri(this, imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        level = getIntent().getExtras().getInt("level", 2);
        // prepare for the game and start
        handlerImage(picSelectedTemp);
        initViews();
        generateGame();
        // GridView点击事件
        final Bitmap finalPicSelectedTemp = picSelectedTemp;



        mGvPuzzleMainDetail.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                if (GameUtil.isMoveable(position)) {
                    // swip the piece with the blank
                    GameUtil.swapItems(
                            GameUtil.mItemBeans.get(position),
                            GameUtil.mBlankItemBean);
                    // 重新获取图片
                    recreateData();
                    // 通知GridView更改UI
                    mAdapter.notifyDataSetChanged();
                    // count steps
                    COUNT_INDEX++;
                    mTvPuzzleMainCounts.setText("" + COUNT_INDEX);

                    if (GameUtil.isSuccess()) {
                        // 将最后一张图显示完整
                        recreateData();
                        mBitmapItemLists.remove(level * level - 1);
                        mBitmapItemLists.add(mLastBitmap);
                        // 通知GridView更改UI
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(MovingPuzzle.this, "Success!",
                                Toast.LENGTH_LONG).show();
                        mGvPuzzleMainDetail.setEnabled(false);
                        mTimer.cancel();
                        mTimerTask.cancel();
                        countDownTimer.cancel();
                        AlertDialog.Builder builder =new AlertDialog.Builder(MovingPuzzle.this);
                        builder.setMessage("Do you want to go to next level?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which){
                                        level++;
                                        cleanConfig();
                                        handlerImage(finalPicSelectedTemp);
                                        initViews();
                                        generateGame();
                                        recreateData();
                                        // 通知GridView更改UI
                                        mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
                                        mAdapter.notifyDataSetChanged();
                                        mGvPuzzleMainDetail.setEnabled(true);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which){
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setTitle("Level Up!!");
                        alert.show();
                    }
                }
            }
        });

        // display orginal image
        mBtnImage.setOnClickListener(this);
        // reset the game button
        mBtnRestart.setOnClickListener(this);
        mBtnSwip = (Button) findViewById(R.id.swip_puzzle);
        mBtnSwip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(MovingPuzzle.this,SwippingPuzzle.class);
                    intent.putExtra("picSelectedID", mResId);
                    intent.putExtra("level", level);
                    startActivity(intent);
            }
        });

        mBtnChange = (Button) findViewById(R.id.change_image);
        mBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovingPuzzle.this,ChooseImage.class);
                intent.putExtra("level", level);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });

        mBtnRestart = (Button) findViewById(R.id.reset);
        mBtnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                cleanConfig();
                initViews();
                generateGame();
                recreateData();
                // 通知GridView更改UI
                mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
                mAdapter.notifyDataSetChanged();
                mGvPuzzleMainDetail.setEnabled(true);
            }
        });

        mBtnImage = (Button) findViewById(R.id.original_image);
        mBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animShow = AnimationUtils.loadAnimation(
                        MovingPuzzle.this, R.anim.image_show_anim);
                Animation animHide = AnimationUtils.loadAnimation(
                        MovingPuzzle.this, R.anim.image_hide_anim);
                if (mIsShowImg) {
                    mImageView.startAnimation(animHide);
                    mImageView.setVisibility(View.GONE);
                    mIsShowImg = false;
                } else {
                    mImageView.startAnimation(animShow);
                    mImageView.setVisibility(View.VISIBLE);
                    mIsShowImg = true;
                }
            }
        });

    }

    private void setTimerStartListener() {
        // will be called at every 1500 milliseconds i.e. every 1.5 second.
        countDownTimer = new CountDownTimer(TIMER_INDEX*1000, TIMER_INDEX*1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                mAdapter.notifyDataSetChanged();
                Toast.makeText(MovingPuzzle.this, "Time Out!!",
                        Toast.LENGTH_LONG).show();
                mGvPuzzleMainDetail.setEnabled(false);
                mTimer.cancel();
                mTimerTask.cancel();
                countDownTimer.cancel();
                AlertDialog.Builder builder =new AlertDialog.Builder(MovingPuzzle.this);
                builder.setMessage("Do you want to restart?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                cleanConfig();
//                                countDownTimer.cancel();
//                                handlerImage(finalPicSelectedTemp);
                                initViews();
                                generateGame();
                                recreateData();
                                // 通知GridView更改UI
                                mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
                                mAdapter.notifyDataSetChanged();
                                mGvPuzzleMainDetail.setEnabled(true);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Time Out!!");
                alert.show();
            }
        }.start();
    }


    /**
     * generate the game
     */
    private void generateGame() {
        // get the original pieces in the original order
        new ImageUtil().createInitBitmaps(
                level, mPicSelected, MovingPuzzle.this);
        // generate the random order of pieces
        GameUtil.getPuzzleGenerator();
        // get the all og the bitmap for pieces
        for (ItemBean temp : GameUtil.mItemBeans) {
            mBitmapItemLists.add(temp.getBitmap());
        }
        // 数据适配器
        mAdapter = new MovingPuzzleAdapter(this, mBitmapItemLists);
        mGvPuzzleMainDetail.setAdapter(mAdapter);
        // start timer
        mTimer = new Timer(true);
        // 计时器线程
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        };
        // every 1000ms, that is 1s, with 0s delay
        mTimer.schedule(mTimerTask, 0, 1000);
        //test if it is in order when first start
        origSuccess();
    }

    //test if it is in order when first start
    private void origSuccess(){
        if(GameUtil.isSuccess()){
            recreateData();
            mBitmapItemLists.remove(level * level - 1);
            mBitmapItemLists.add(mLastBitmap);
            // 通知GridView更改UI
            mAdapter.notifyDataSetChanged();
            cleanConfig();
//            handlerImage(finalPicSelectedTemp);
            initViews();
            generateGame();
            recreateData();
            // 通知GridView更改UI
            mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
            mAdapter.notifyDataSetChanged();
            mGvPuzzleMainDetail.setEnabled(true);
        }
    }

    /**
     * add the view to show the original image
     */
    private void addImgView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(
                R.id.rl_puzzle_main_main_layout);
        mImageView = new ImageView(MovingPuzzle.this);
        mImageView.setImageBitmap(mPicSelected);
        int x = (int) (mPicSelected.getWidth() * 0.9F);
        int y = (int) (mPicSelected.getHeight() * 0.9F);
        LayoutParams params = new LayoutParams(x, y);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView.setLayoutParams(params);
        relativeLayout.addView(mImageView);
        mImageView.setVisibility(View.GONE);
    }

    /**
     * 返回时调用
     */
    @Override
    protected void onStop() {
        super.onStop();
        // clear related configuration
        cleanConfig();
        this.finish();
    }

    /**
     * clear all related configuration
     */
    private void cleanConfig() {
        GameUtil.mItemBeans.clear();
        // 停止计时器
        mTimer.cancel();
        mTimerTask.cancel();
        COUNT_INDEX = 0;
        TIMER_INDEX = 10*(level-1)*(level-1)*(level*level);
    }

    /**
     * regenerate the pieces
     */
    private void recreateData() {
        mBitmapItemLists.clear();
        for (ItemBean temp : GameUtil.mItemBeans) {
            mBitmapItemLists.add(temp.getBitmap());
        }
    }

    /**
     * deal with the size of the image
     *
     * @param bitmap bitmap
     */
    private void handlerImage(Bitmap bitmap) {
        // put the image into the fixed size view
        int screenWidth = ScreenUtil.getScreenSize(this).widthPixels;
        int screenHeigt = ScreenUtil.getScreenSize(this).heightPixels;
        mPicSelected = new ImageUtil().resizeBitmap(
                screenWidth * 0.9f, screenHeigt * 0.7f, bitmap);
    }

    /**
     * initialize Views
     */
    private void initViews() {
        // Button
        mBtnChange = (Button) findViewById(R.id.change_image);
        mBtnImage = (Button) findViewById(R.id.original_image);
        mBtnRestart = (Button) findViewById(R.id.reset);
        // not display the original image
        mIsShowImg = false;
        // GridView
        mGvPuzzleMainDetail = (GridView) findViewById(
                R.id.gv_puzzle_main_detail);
        // N*N
        mGvPuzzleMainDetail.setNumColumns(level);
        LayoutParams gridParams = new LayoutParams(
                mPicSelected.getWidth(),
                mPicSelected.getHeight());
        // 水平居中
        gridParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        // 其他格式属性
        gridParams.addRule(
                RelativeLayout.BELOW,
                R.id.ll_puzzle_main_spinner);
        // Grid显示
        mGvPuzzleMainDetail.setLayoutParams(gridParams);
        mGvPuzzleMainDetail.setHorizontalSpacing(0);
        mGvPuzzleMainDetail.setVerticalSpacing(0);
        // steps
        mTvPuzzleMainCounts = (TextView) findViewById(
                R.id.tv_puzzle_main_counts);
        mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
        // timer
        mTvTimer = (TextView) findViewById(R.id.tv_puzzle_main_time);
        mTvTimer.setText("0s");
        // add the view to show the original image
        addImgView();
        setTimerStartListener();
    }

    /**
     * Button点击事件
     */
    @Override
    public void onClick(View v) {

    }
}
