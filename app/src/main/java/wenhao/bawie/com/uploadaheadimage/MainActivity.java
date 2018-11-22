package wenhao.bawie.com.uploadaheadimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final int CAMERA_REQUEST_CODE = 123;
    public static final int ALUMB_REQUEST_CODE = 124;
    public static final int CROP_REQUEST_CODE = 125;

    // 拍完照要保存的路径
    private File imgPath;
    private File imgRoot;

    private Uri uri;

    private ImageView chuan_touxiang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chuan_touxiang = (ImageView) findViewById(R.id.chuan_touxiang);

        // 外置存储已挂载，可用
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 获取外置存储的根目录
            File rootSD = Environment.getExternalStorageDirectory();
            imgRoot = new File(rootSD + File.separator + "images");
            if (!imgRoot.exists()) {
                imgRoot.mkdirs();
            }
        }
        //点击弹出popwindow
        chuan_touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopwindow();
            }
        });
    }
    //Popwindow弹框
    private void showPopwindow() {
        View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        //建layout
        View popView = View.inflate(this, R.layout.camera_pop_menu, null);
        //初始化控件
        TextView btnCamera = (TextView) popView.findViewById(R.id.btn_camera_pop_camera);
        TextView btnAlbum = (TextView) popView.findViewById(R.id.btn_camera_pop_album);
        TextView btnCancel = (TextView) popView.findViewById(R.id.btn_camera_pop_cancel);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        final PopupWindow popWindow = new PopupWindow(popView,width,height);

        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);// 设置允许在外点击消失

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_camera_pop_camera:
                        // 调用系统的相机，隐式Intent
                        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // /storage/0/emulate/images/156756789.jpg
                        imgPath = new File(imgRoot, new Date().getTime() + ".jpg");
                        uri = Uri.fromFile(imgPath);
                        intent1.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(intent1, CAMERA_REQUEST_CODE);
                        break;
                    case R.id.btn_camera_pop_album:
                        Intent intent2 = new Intent();
                        intent2.setAction(Intent.ACTION_PICK);
                        // Type是指的跳转的View的类型
                        // image/jpeg, image/png,*代表的是通配符
                        intent2.setType("image/*");
                        startActivityForResult(intent2, ALUMB_REQUEST_CODE);
                        break;
                    case R.id.btn_camera_pop_cancel:

                        break;
                }
                popWindow.dismiss();
            }
        };

        btnCamera.setOnClickListener(listener);
        btnAlbum.setOnClickListener(listener);
        btnCancel.setOnClickListener(listener);

        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 从相机拍照返回的
        if (requestCode == CAMERA_REQUEST_CODE) {

            // 裁剪的隐式Intent
            Intent intent = new Intent("com.android.camera.action.CROP");
            // 数据uri和类型
            intent.setDataAndType(uri, "image/*");
            // 可裁剪
            intent.putExtra("crop", "true");// 可裁剪
            // 宽高比
            intent.putExtra("aspectX", 1); // 裁剪的宽比例
            intent.putExtra("aspectY", 1); // 裁剪的高比例
            // 宽高的具体像素
            intent.putExtra("outputX", 300); // 裁剪的宽度
            intent.putExtra("outputY", 300); // 裁剪的高度

            intent.putExtra("scale", true); // 支持缩放
            // 裁剪后输出的路径

            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(imgPath + ".bak")); // 将裁剪的结果输出到指定的Uri
            // 必须要添加的一句话，否则返回的Intent中不会有数据
            intent.putExtra("return-data", true); // 若为true则表示返回数据
            // 可以省略
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 裁剪成的图片的格式
            // intent.putExtra("noFaceDetection", true); //启用人脸识别
            startActivityForResult(intent, CROP_REQUEST_CODE);
        } else if (requestCode == ALUMB_REQUEST_CODE) {
            // 从相册选择返回的
            if (data==null){
                return;
            }else {
                Uri uri2 = data.getData();
                chuan_touxiang.setImageURI(uri2);
            }
        } else if(requestCode == CROP_REQUEST_CODE) {
            // 裁剪之后的回传
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            chuan_touxiang.setImageBitmap(bmp);
        }
    }
}
