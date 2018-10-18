package comp5216.sydney.edu.au.puzzlegame;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
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


public class SwippingPuzzle extends Activity implements OnClickListener {

    //put the piece back
    public static Bitmap mLastBitmap;
    // N*N
    public static int level = 2;
    // steps
    public static int COUNT_INDEX = 0;
    // time
    public static int TIMER_INDEX = 10*(level-1);
    public static int type=1;
    /**
     * UI更新Handler
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //导数
                    TIMER_INDEX--;
                    mTvTimer.setText("" + TIMER_INDEX);
                    break;
                default:
                    break;
            }
        }
    };
    // selected image
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
    // display steps
    private TextView mTvPuzzleMainCounts;
    // display timer
    private TextView mTvTimer;
    // small pieces
    private List<Bitmap> mBitmapItemLists = new ArrayList<Bitmap>();
    // GridView适配器
    private SwippingPuzzleAdapter mAdapter;
    // show original image
    private boolean mIsShowImg;
    // timer
    private Timer mTimer;
    /**
     * 计时器线程
     */
    private TimerTask mTimerTask;

    private ImageView mFirst;
    private ImageView mSecond;
    ItemBean mFirstItemBean;
    private CountDownTimer countDownTimer;
//    public boolean timerStopped=true;

    Uri myUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipping_puzzle);
        // get the selected image
        Bitmap picSelectedTemp = null;
        // default image
        mResId = getIntent().getExtras().getInt("picSelectedID");
        //gallery image
        mPicPath = getIntent().getExtras().getString("galleryImage");
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
        //prepare and generate the game
        handlerImage(picSelectedTemp);
        initViews();
        generateGame();

        // GridView点击事件
        final Bitmap finalPicSelectedTemp = picSelectedTemp;
        mGvPuzzleMainDetail.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {

                //两次点击同一个item
                    if(mFirst==view){
                    mFirst.setColorFilter(null);
                    mFirst=null;
                    return;
                    }
                    if(mFirst == null){
                        mFirst=(ImageView) view;
                        mFirst.setColorFilter(Color.parseColor("#55FF0000"));
                        mFirstItemBean = SwippingGameUtil.mItemBeans.get(position);
                    }else {
                        mSecond = (ImageView)view;
                        SwippingGameUtil.swapItems(
                            SwippingGameUtil.mItemBeans.get(position),
                            mFirstItemBean);
                        mFirst.setColorFilter(null);
                        mFirst=mSecond=null;
                        // 重新获取图片
                        recreateData();
                        // 通知GridView更改UI
                        mAdapter.notifyDataSetChanged();
                        // count steps
                        COUNT_INDEX++;
                        mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
                        if (SwippingGameUtil.isSuccess()){
                            // 通知GridView更改UI
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(SwippingPuzzle.this, "Success!",
                                Toast.LENGTH_LONG).show();
                            mGvPuzzleMainDetail.setEnabled(false);
                            mTimer.cancel();
                            mTimerTask.cancel();
                            countDownTimer.cancel();
                            AlertDialog.Builder builder =new AlertDialog.Builder(SwippingPuzzle.this);
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
        mBtnSwip = (Button) findViewById(R.id.moving_puzzle);
        mBtnSwip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SwippingPuzzle.this,MovingPuzzle.class);
                intent.putExtra("picSelectedID", mResId);
                intent.putExtra("level", level);
                startActivity(intent);
            }
        });

        mBtnChange = (Button) findViewById(R.id.change_image);
        mBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SwippingPuzzle.this,ChooseImage.class);
//                intent.putExtra("picSelectedID", mResId);
                intent.putExtra("level", level);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });

        mBtnImage = (Button) findViewById(R.id.original_image);
        mBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animShow = AnimationUtils.loadAnimation(
                        SwippingPuzzle.this, R.anim.image_show_anim);
                Animation animHide = AnimationUtils.loadAnimation(
                        SwippingPuzzle.this, R.anim.image_hide_anim);
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

        mBtnRestart = (Button) findViewById(R.id.reset);
        mBtnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirst=null;
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
    }

    private void setTimerStartListener() {
        // will be called at every 1500 milliseconds i.e. every 1.5 second.
        countDownTimer = new CountDownTimer(TIMER_INDEX*1000, TIMER_INDEX*1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                mAdapter.notifyDataSetChanged();
                Toast.makeText(SwippingPuzzle.this, "Time Out!!",
                        Toast.LENGTH_LONG).show();
                mGvPuzzleMainDetail.setEnabled(false);
                mTimer.cancel();
                mTimerTask.cancel();
                countDownTimer.cancel();
                AlertDialog.Builder builder =new AlertDialog.Builder(SwippingPuzzle.this);
                builder.setMessage("Do you want to restart?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                cleanConfig();
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

    // generate the game
    private void generateGame() {
        // get the pieces in the normal order
        new SwippingImageUtil().createInitBitmaps(
                level, mPicSelected, SwippingPuzzle.this);
        // generate the random data
        SwippingGameUtil.getPuzzleGenerator();
        // get the bitmap list of the pieces
        for (ItemBean temp : SwippingGameUtil.mItemBeans) {
            mBitmapItemLists.add(temp.getBitmap());
        }
        // 数据适配器
        mAdapter = new SwippingPuzzleAdapter(this, mBitmapItemLists);
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
        // count every 1000ms (1s) with 0s delay
        mTimer.schedule(mTimerTask, 0, 1000);

        origSuccess();
    }

    //test if it is in order when first start
    private void origSuccess(){
        if(SwippingGameUtil.isSuccess()){
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
        mImageView = new ImageView(SwippingPuzzle.this);
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
        cleanConfig();
        this.finish();
    }

    /**
     * clear configurations
     */
    private void cleanConfig() {

        SwippingGameUtil.mItemBeans.clear();
        mTimer.cancel();
        mTimerTask.cancel();
        COUNT_INDEX = 0;
        TIMER_INDEX = 10*(level-1);
    }

    /**
     * re-get the date
     */
    private void recreateData() {
        mBitmapItemLists.clear();
        for (ItemBean temp : SwippingGameUtil.mItemBeans) {
            mBitmapItemLists.add(temp.getBitmap());
        }
    }

    /**
     * set the image to the target size
     * @param bitmap bitmap
     */
    private void handlerImage(Bitmap bitmap) {

        int screenWidth = ScreenUtil.getScreenSize(this).widthPixels;
        int screenHeigt = ScreenUtil.getScreenSize(this).heightPixels;
        mPicSelected = new SwippingImageUtil().resizeBitmap(
                screenWidth * 0.9f, screenHeigt * 0.7f, bitmap);
    }

    /**
     * initiate Views
     */
    private void initViews() {
        // Button
        mBtnChange = (Button) findViewById(R.id.change_image);
        mBtnImage = (Button) findViewById(R.id.original_image);
        mBtnRestart = (Button) findViewById(R.id.reset);
        // display original image: false
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
        // display Grid
        mGvPuzzleMainDetail.setLayoutParams(gridParams);
        mGvPuzzleMainDetail.setHorizontalSpacing(0);
        mGvPuzzleMainDetail.setVerticalSpacing(0);
        // TV步数
        mTvPuzzleMainCounts = (TextView) findViewById(
                R.id.tv_puzzle_main_counts);
        mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
        // TV计时器
        mTvTimer = (TextView) findViewById(R.id.tv_puzzle_main_time);
        mTvTimer.setText("0s");
        // add the view to display original image
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
