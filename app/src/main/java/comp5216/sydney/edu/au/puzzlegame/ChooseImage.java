package comp5216.sydney.edu.au.puzzlegame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by libohan on 21/9/17.
 */


public class ChooseImage extends Activity implements View.OnClickListener {

    // IMAGE TYPE
    private static final String IMAGE_TYPE = "image/*";
    // Temp照片路径
    public static String TEMP_IMAGE_PATH;
    // GridView
    private GridView mGvPicList;
    private List<Bitmap> mPicList;
    // default image ID
    private int[] mResPicId;

    public static int level = 2;
    public static int type = 0;
    Button type_level;
    Button gallery;
    Button camera;

    private static final int PICK_IMAGE = 100;
    private static final int TAKE_IMAGE = 200;
    private Uri takeUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_image);

        level = getIntent().getExtras().getInt("level", 2);
        type = getIntent().getExtras().getInt("type", 2);

        int mResId = getIntent().getExtras().getInt("picSelectedID");
        Bitmap mPicSelected;

        type_level = (Button) findViewById(R.id.type_level);
        type_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseImage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        gallery = (Button) findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
//                Intent intent = new Intent(ChooseImage.this, MovingPuzzle.class);
//                intent.putExtra("galleryImage",stringUri);
//                startActivity(intent);

            }
        });

        camera = (Button) findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openCamera();
            }
        });

        mPicList = new ArrayList<Bitmap>();
        // initialize Views
        initViews();
        // 数据适配器
        mGvPicList.setAdapter(new DefaultImageAdapter(ChooseImage.this, mPicList));
        // Item点击监听
        mGvPicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                // 选择默认图片
                if (type == 0) {
                    Intent intent = new Intent(ChooseImage.this, MovingPuzzle.class);
                    intent.putExtra("picSelectedID", mResPicId[position]);
                    intent.putExtra("level", level);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ChooseImage.this, SwippingPuzzle.class);
                    intent.putExtra("picSelectedID", mResPicId[position]);
                    intent.putExtra("level", level);
                    startActivity(intent);
                }
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void openCamera() {

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM",
                System.currentTimeMillis() + ".jpg");
        takeUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, takeUri);
        startActivityForResult(intent, TAKE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == PICK_IMAGE) {

                Uri imageUri = data.getData();

                if (imageUri != null) {

                    if (type == 0) {
                        Intent intent = new Intent(ChooseImage.this, MovingPuzzle.class);
                        intent.setData(imageUri);
                        intent.putExtra("level", level);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ChooseImage.this, SwippingPuzzle.class);
                        intent.setData(imageUri);
                        intent.putExtra("level", level);
                        startActivity(intent);
                    }

                }

            } else if (requestCode == TAKE_IMAGE) {

                if (takeUri != null) {

                    if (type == 0) {
                        Intent intent = new Intent(ChooseImage.this, MovingPuzzle.class);
                        intent.setData(takeUri);
                        intent.putExtra("level", level);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ChooseImage.this, SwippingPuzzle.class);
                        intent.setData(takeUri);
                        intent.putExtra("level", level);
                        startActivity(intent);
                    }

                }

            }
        }
    }

    /**
     * 初始化Views
     */
    private void initViews() {
        mGvPicList = (GridView) findViewById(R.id.image_choosing);
        // 初始化Bitmap数据
        mResPicId = new int[]{
                R.drawable.default1, R.drawable.default2, R.drawable.default3,
                R.drawable.default5, R.drawable.default6, R.drawable.default7,
                R.drawable.default4, R.drawable.default8, R.drawable.default9,
                R.drawable.default10, R.drawable.default11, R.drawable.default12,
                R.drawable.default13, R.drawable.default14,
                R.drawable.default15};
        Bitmap[] bitmaps = new Bitmap[mResPicId.length];
        for (int i = 0; i < bitmaps.length; i++) {
            bitmaps[i] = BitmapFactory.decodeResource(
                    getResources(), mResPicId[i]);
            mPicList.add(bitmaps[i]);
        }
    }

    @Override
    public void onClick(View view) {

    }


}

