package ir.hphamid.instagram.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import ir.hphamid.instagram.HttpAddresses;
import ir.hphamid.instagram.HttpHelper;
import ir.hphamid.instagram.R;
import ir.hphamid.instagram.reqAndres.FailedSuccessResponse;
import ir.hphamid.instagram.reqAndres.FileUploadResponse;
import ir.hphamid.instagram.reqAndres.ShareImageRequest;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShareImageActivity extends Activity {
    private final static int IMAGE_SELECT = 1;
    private final static int IMAGE_CAPTURE = 2;

    private final static String uriKey = "uri";


    public final static int WriteExternalPermissionRequest = 1;

    private ImageView imageView;
    private EditText description;
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
        description = (EditText) findViewById(R.id.share_description);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                Glide.with(this).load(imageUri).asBitmap().into(imageView);
            }
        } else if(savedInstanceState != null && savedInstanceState.getParcelable(uriKey) != null) {
            imageUri = savedInstanceState.getParcelable(uriKey);
        }else{
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
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    uploadImage();
                } catch (IOException e) {
                    Toast.makeText(ShareImageActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(uriKey, imageUri);
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
            imageUri = data.getData();
            Glide.with(this).load(data.getData()).asBitmap().into(imageView);
        } else if (requestCode == IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
//            imageView.setImageBitmap(imageBitmap);
            Glide.with(this).load(imageUri).asBitmap().into(imageView);
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

    private void uploadImage() throws IOException {
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 90, stream);
        File temp = File.createTempFile("temp", ".jpg", getCacheDir());
        FileOutputStream outputStream = new FileOutputStream(temp);
        stream.writeTo(outputStream);
        outputStream.close();
//        byte[] data = stream.toByteArray();
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("fileItems[0].path", "/uploadedFiles/")
                .addFormDataPart("fileItems[0].replacing", "true")
                .addFormDataPart("fileItems[0].fileToUpload", UUID.randomUUID().toString().replace("-", "") + ".jpg",
                        RequestBody.create(MediaType.parse("image/jpg"), temp)).build();

        Request request = new Request.Builder()
                .url(HttpAddresses.UploadAddress)
                .addHeader("X-Backtory-Storage-Id", HttpAddresses.StorageId)
                .addHeader("Authorization", HttpHelper.getInstance().getLoginHeader(this))
                .post(requestBody)
                .build();
        HttpHelper.getInstance().getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ShareImageActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShareImageActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resBody = response.body().string();
                if(response.code() == HttpURLConnection.HTTP_OK || response.code() == HttpURLConnection.HTTP_CREATED){
                    final FileUploadResponse res = new Gson().fromJson(resBody, FileUploadResponse.class);
//
                    sendUploadedFileToServer(res.getSavedFilesUrls().get(0), dialog);
                }else{
                    ShareImageActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ShareImageActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

    }

    private void sendUploadedFileToServer(String address, final Dialog dialog){
        ShareImageRequest requestObject = new ShareImageRequest();
        requestObject.setImageUri(address);
        requestObject.setDescription(description.getText().toString());
        RequestBody requestBody = RequestBody.create(HttpHelper.JSON, new Gson().toJson(requestObject));
        Request request = new Request.Builder()
                .url(HttpAddresses.NewImage)
                .addHeader("Authorization", HttpHelper.getInstance().getLoginHeader(ShareImageActivity.this))
                .post(requestBody)
                .build();
        HttpHelper.getInstance().getClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                ShareImageActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShareImageActivity.this, "Failed to Share image", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FailedSuccessResponse res = new Gson().fromJson(response.body().string(), FailedSuccessResponse.class);
                if(res.isSuccess()){
                    ShareImageActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ShareImageActivity.this, "Image Shared Successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                        }
                    });
                }else{
                    ShareImageActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ShareImageActivity.this, "Failed to Share image", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }

}
