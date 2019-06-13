package com.example.ymz.qr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.custom.activity.CaptureActivity;
import com.google.custom.common.BitmapUtils;
import com.my.dialogdemo.MyDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mHeadTitle;
    /**
     * 输入内容，生成二维码
     */
    private EditText mEt;
    /**
     * 生成二维码
     */
    private Button mBtn1;
    /**
     * 扫一扫（支持识别相册二维码）
     */
    private Button mBtn2;
    private Button mBtn3;
    private ImageView mImage;
    /**
     * 扫描的结果
     */
    private TextView mTvResult;
    private ImageView mImageCallback;

    private final static int REQ_CODE = 1028;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mContext = this;
        //Android8.0动态权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AndPermission.with(this)
                    .requestCode(100)
                    .permission(Permission.CAMERA,
                            Permission.STORAGE)
                    .start();
        }
    }

    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showToast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        mHeadTitle = (TextView) findViewById(R.id.head_title);
        mEt = (EditText) findViewById(R.id.et);
        mBtn2 = (Button) findViewById(R.id.btn1);
        mBtn2.setOnClickListener(this);
        mBtn1 = (Button) findViewById(R.id.btn2);
        mBtn1.setOnClickListener(this);
        mBtn3 = (Button) findViewById(R.id.btn3);
        mBtn3.setOnClickListener(this);
        mImage = (ImageView) findViewById(R.id.image);
        mTvResult = (TextView) findViewById(R.id.tv_result);
        mImageCallback = (ImageView) findViewById(R.id.image_callback);
        mHeadTitle.setText("二维码");


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn1:
                initFlickingA();
                break;
            case R.id.btn2:
                Intent intent = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(intent, REQ_CODE);
                break;
            case R.id.btn3:
               startActivity(new Intent(mContext, CustomActivity.class));
                break;
        }
    }


    private void initFlickingA() {
        String content = mEt.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            showToast("请输入内容");
            return;
        }

        mImage.setVisibility(View.VISIBLE);
        //隐藏扫码结果view
        mImageCallback.setVisibility(View.GONE);
        mTvResult.setVisibility(View.GONE);
        Bitmap bitmap = null;

        try {
            bitmap = BitmapUtils.create2DCode(content);//根据内容生成二维码
            mImage.setImageBitmap(bitmap);
        } catch (Exception e) {//WriterException
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            mImage.setVisibility(View.GONE);
            mTvResult.setVisibility(View.VISIBLE);
            mImageCallback.setVisibility(View.VISIBLE);
            String result = "";
            Bitmap bitmap = null;
            try{
                result = data.getStringExtra(CaptureActivity.SCAN_QRCODE_RESULT);
                bitmap = data.getParcelableExtra(CaptureActivity.SCAN_QRCODE_BITMAP);
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            if (result != ""){
                mTvResult.setText("扫码结果："+result);
                showToast("扫码结果：" + result);
            }else{
                mTvResult.setText("未扫描的二维码，请重试");
                showToast("未扫描的二维码");
            }

            if(bitmap != null){
                mImageCallback.setImageBitmap(bitmap);//现实扫码图片
            }
        }

    }

    private void showToast(String msg) {
        Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();

    }


}
