package ir.hphamid.instagram.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ir.hphamid.instagram.R;

public class ShareImageActivity extends Activity {
    private final static int IMAGE_SELECT = 1;
    private final static int IMAGE_CAPTURE = 2;


    public final static int WriteExternalPermissionRequest = 1;

    private ImageView imageView;
    private Button shareButton;
    private Button cancelButton;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_image);
        imageView = (ImageView) findViewById(R.id.share_image_image_view);
        shareButton = (Button) findViewById(R.id.share_image_share_button);
        cancelButton = (Button) findViewById(R.id.share_image_cancel_button);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                Glide.with(this).load(imageUri).into(imageView);
            }
        } else {
            showChooseImageSelector();
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseImageSelector();
            }
        });
    }

    protected void showChooseImageSelector() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_image).setItems(R.array.sources,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_SELECT);
                        } else {
                            if(ContextCompat.checkSelfPermission(ShareImageActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(ShareImageActivity.this, "permission not granted", Toast.LENGTH_SHORT).show();
                                if(ActivityCompat.shouldShowRequestPermissionRationale
                                        (ShareImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                    Toast.makeText(ShareImageActivity.this, "We need to get permission!", Toast.LENGTH_SHORT).show();
                                }
                                ActivityCompat.requestPermissions(ShareImageActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WriteExternalPermissionRequest);
                            }else{
                                Toast.makeText(ShareImageActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
                                sendCameraIntent();
                            }
                        }
                    }
                });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photo = null;
            try {
                photo = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(photo != null){
                Uri file  = FileProvider.getUriForFile(this, "ir.hphamid.instagram.fileProvider", photo);
                imageUri = Uri.fromFile(photo);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE);
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == WriteExternalPermissionRequest){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sendCameraIntent();
            }else{
                Toast.makeText(ShareImageActivity.this, "permission denied :((", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                return;
            }
            Glide.with(this).load(data.getData()).into(imageView);
        } else if (requestCode == IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
//            imageView.setImageBitmap(imageBitmap);
            Glide.with(this).load(imageUri).into(imageView);
        } else {
            showChooseImageSelector();
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

}
