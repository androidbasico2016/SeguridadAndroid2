package com.example.user.seguridadandroid;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageView imgPersona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgPersona = (ImageView) findViewById(R.id.img);
    }

    public void tomarFoto(View view) throws IOException {



        int permisoWriteExternalCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permisoWriteExternalCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File fileImagen = createImageFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
            startActivityForResult(intent, 123);
        }

    }


    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );


        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("XXXX", "ONcreate" + mCurrentPhotoPath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        galleryAddPic();

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

        imgPersona.setImageBitmap(bitmap);
    }



    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 12:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                  //  Snackbar.make(getCurrentFocus(),"Se reiniciara la app",Snackbar.LENGTH_LONG).show();
                    AlertDialog.Builder builder= new AlertDialog.Builder(this);
                    builder.setMessage("Desea reiniciar la aplicacion para que se tomen los cambios");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //android.os.Process.killProcess(android.os.Process.myPid());

                            PendingIntent pi = PendingIntent.getActivity(
                                    getApplicationContext(),
                                    0,
                                    getIntent(),
                                    PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            am.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pi);

                            // Stop now
                            System.exit(0);
                        }
                    });
                    builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.create().show();



                    //http://stackoverflow.com/questions/32471888/application-not-able-to-access-sd-card-when-write-external-storage-permission-is

                }
                break;

        }
    }

}
