package com.gettipper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QR_generator extends AppCompatActivity {

    private static final String YOUR_CLIENT_ID = "AZ15E9U8ts9h9boYPKa3dXdVaoK2wTR_f7dZWU--Mf6sKJ81w48XyrjcrwCGXa30xo5q3AKdxZQCSCyw";
    private ImageView qrCodeIV;
    Bitmap bitmap;
    private Toolbar mtoolbar;
    private Button btnSaveImage;
    private String paypalAccount = "https://www.paypal.com/paypalme/";

    //toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    //toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_changeCustomer:
                startActivity((new Intent(this, ScannerActivity.class)));
                return true;
            case R.id.menu_item_updateDetails:
                startActivity((new Intent(this, ProfileActivity.class)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    QRGEncoder qrgEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_activity);


        //appMenu
        mtoolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mtoolbar);

        // initializing all variables.
        qrCodeIV = findViewById(R.id.idIVQrcode);
        btnSaveImage = findViewById(R.id.qr_activity_btn_saveImage);

        if (paypalAccount == null) {

            // if the edittext inputs are empty then execute
            // this method showing a toast message.
            Toast.makeText(QR_generator.this, "Enter some text to generate QR Code", Toast.LENGTH_SHORT).show();
        } else {


            MyFirebaseDB.CallBack_String callBack_string = new MyFirebaseDB.CallBack_String() {
                @Override
                public void dataReady(String value) {

                     paypalAccount = paypalAccount+value;
                    // below line is for getting
                    // the windowmanager service.
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

                    // initializing a variable for default display.
                    Display display = manager.getDefaultDisplay();

                    // creating a variable for point which
                    // is to be displayed in QR Code.
                    Point point = new Point();
                    display.getSize(point);

                    // getting width and
                    // height of a point
                    int width = point.x;
                    int height = point.y;

                    // generating dimension from width and height.
                    int dimen = width < height ? width : height;
                    dimen = dimen * 3 / 4;
                    qrgEncoder = new QRGEncoder(paypalAccount, null, QRGContents.Type.TEXT, dimen);
                    try {
                        // getting our qrcode in the form of bitmap.
                        bitmap = qrgEncoder.encodeAsBitmap();
                        // the bitmap is set inside our image
                        // view using .setimagebitmap method.
                        qrCodeIV.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        // this method is called for
                        // exception handling.
                        Log.e("Tag", e.toString());
                        //  }
                    }

                }
            };

            MyFirebaseDB.getPaypalAccountByUid(FirebaseAuth.getInstance().getCurrentUser().getUid(), callBack_string);
        }

        btnSaveImage.setOnClickListener(view -> saveImage(bitmap));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();

        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
            //     Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            out.flush();
            out.close();
            Toast.makeText(QR_generator.this, "Saved in gallery", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
// Tell the media scanner about the new file so that it is
// immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

}
