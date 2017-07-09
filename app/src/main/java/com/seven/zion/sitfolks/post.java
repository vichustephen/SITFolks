package com.seven.zion.sitfolks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class post extends AppCompatActivity {
    private ImageView pimg;
    private EditText title;
    private EditText desc;
    private Button pbutton;
    String decodedimg;
    String iPath;
    private Uri imageUri = null;
    private ProgressDialog mProgressD;
    private StorageReference imgref;
    private DatabaseReference mDatabase;
    private static final int GALLERY_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        pimg = (ImageView)findViewById(R.id.p_img);
        title = (EditText)findViewById(R.id.post_title);
        desc = (EditText)findViewById(R.id.p_desc);
        pbutton = (Button)findViewById(R.id.p_button);
        mProgressD = new ProgressDialog(this);
        imgref = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
       Boolean perm = isStoragePermissionGranted();
        pimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery,GALLERY_CODE);
            }
        });
        pbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPost();
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            //resume tasks needing this permission
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK)
        {
            imageUri = data.getData();
            pimg.setImageURI(imageUri);
            iPath = ImageFilePath.getPath(this,imageUri);

        }
    }
    public  void  startPost() {
        mProgressD.setMessage("Posting...");
        mProgressD.show();
        final String pTitle = title.getText().toString().trim();
        final String pDesc = desc.getText().toString().trim();
        if (!TextUtils.isEmpty(pTitle) && !TextUtils.isEmpty(pDesc))
        {
            if (imageUri!= null)
            {
                StorageReference fpath = imgref.child("Blog_img").child(imageUri.getLastPathSegment());
                decodedimg = decodeFile(iPath);
                Uri uri = Uri.fromFile(new File(decodedimg));
                Log.d("decoded",iPath);
                fpath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri dUri = taskSnapshot.getDownloadUrl();
                        @SuppressWarnings("VisibleForTests")String ss = taskSnapshot.getStorage().toString();
                        Blog model = new Blog(pTitle,pDesc,dUri.toString(),ss);
                        mDatabase.push().setValue(model);
                        mProgressD.dismiss();
                        Intent intent = new Intent(post.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        post.this.finish();
                        Toast.makeText(post.this,"Post Successful !",Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {
                Blog model = new Blog(pTitle, pDesc, "null",null);
                mDatabase.push().setValue(model);
                mProgressD.dismiss();
                Intent intent = new Intent(post.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                post.this.finish();
                Toast.makeText(post.this,"Post Successful !",Toast.LENGTH_LONG).show();
            }
        }
        else {
            mProgressD.dismiss();
            Toast.makeText(this,"Fields Cannot be Empty",Toast.LENGTH_LONG).show();
        }
    }
    private String decodeFile(String path) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, 720, 348, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= 800 && unscaledBitmap.getHeight() <= 800)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, 720, 348, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/myTmpDir");
            Log.d("file ouput","Entered lass");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 70, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}
