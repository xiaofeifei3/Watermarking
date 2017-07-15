package com.chenqihong.watermarking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.chenqihong.watermarking.watermarking.ImageUtils;
import com.chenqihong.watermarking.watermarking.Watermarking;

import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;

import static me.nereo.multi_image_selector.MultiImageSelector.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static int REQUEST_PICK_WATER_IMAGE = 200;
    public static int REQUEST_PICK_HOST_IMAGE = 201;
    private Button mWaterImageButton;
    private Button mHostImageButton;
    private Button mExtractButton;
    private ImageView mImageDisplayView;
    private String mWatermarkingPath;
    private Bitmap mCreatedBitmap;
    private Handler mCreateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 1){
                Bitmap bitmap = msg.getData().getParcelable("image");
                mImageDisplayView.setImageBitmap(bitmap);
                mCreatedBitmap = bitmap;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        mWaterImageButton = (Button)findViewById(R.id.editText);
        mHostImageButton = (Button)findViewById(R.id.pickPhoto);
        mExtractButton = (Button)findViewById(R.id.pickInfo);
        mImageDisplayView = (ImageView)findViewById(R.id.showImage);
        mWaterImageButton.setOnClickListener(this);
        mHostImageButton.setOnClickListener(this);
        mExtractButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mWaterImageButton){
            create().single().start(this, REQUEST_PICK_WATER_IMAGE);
        }else if(v == mHostImageButton){
            create().single().start(this, REQUEST_PICK_HOST_IMAGE);
        }else if(v == mExtractButton){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = mCreateHandler.obtainMessage();
                    message.what = 1;
                    Bitmap image = Watermarking.extract(mCreatedBitmap, ImageUtils.getImage(mWatermarkingPath));
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("image", image);
                    message.setData(bundle);
                    message.sendToTarget();
                }
            }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PICK_WATER_IMAGE && resultCode == RESULT_OK){
            List<String> path = data.getStringArrayListExtra(EXTRA_RESULT);
            mWatermarkingPath = path.get(0);
            Toast.makeText(MainActivity.this, "已选择水印图片：" + mWatermarkingPath, Toast.LENGTH_SHORT).show();
        }else if(requestCode == REQUEST_PICK_HOST_IMAGE && resultCode == RESULT_OK){
            List<String> path = data.getStringArrayListExtra(EXTRA_RESULT);
            final String originalPath = path.get(0);
            if(null == mWatermarkingPath || null == originalPath){
                Toast.makeText(MainActivity.this, "没有选择水印图片或宿主图片", Toast.LENGTH_LONG).show();
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Message message = mCreateHandler.obtainMessage();
                        message.what = 1;
                        mCreatedBitmap = Watermarking.embed(originalPath, mWatermarkingPath);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("image", mCreatedBitmap);
                        message.setData(bundle);
                        message.sendToTarget();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
