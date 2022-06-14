package com.app.shoppingly.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.shoppingly.models.ItemModel;
import com.app.shoppingly.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;


public class AddItemActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int CAMERA_PERMISSION_CODE = 104;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int PICK_IMAGE = 102;
    private Button ButtonUpload ;
    private EditText edtName , edtPrice ,edtDescription;
    private Uri mImageUri = Uri.EMPTY;
    ImageView setImage;
    Context context = AddItemActivity.this;
    private boolean mGranted;
    TextView tvType;
    StorageReference mStorageRef ;
    DatabaseReference mDatabaseRef ;
    private ProgressDialog mProgressDialog;
    private StorageTask mUploadTask;
    String itemName , itemPrice , itemDescription;
    boolean pic = false;
    AlertDialog alertDialog;
    String url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        ButtonUpload = findViewById(R.id.uploadbtn) ;
        edtName = findViewById(R.id.title);
        edtPrice = findViewById(R.id.price);
        edtDescription = findViewById(R.id.description);
        setImage = findViewById(R.id.imageviewset);
        tvType = findViewById(R.id.tvType);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ItemPosts");
        mStorageRef = FirebaseStorage.getInstance().getReference("ItemPosts/");

        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageUri = Uri.EMPTY;

                CharSequence arr[] = new CharSequence[]{
                        "TAKE A PHOTO",
                        "CHOOSE FROM LIBRARY",
                        "CANCEL"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this);
                builder.setTitle("Select");
                builder.setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                                    return;
                                }
                            }

                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, CAMERA_REQUEST_CODE);
                        }
                        if(i==1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!mGranted) {
                                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                                        return;
                                    }
                                }
                            }

                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select an Image"), PICK_IMAGE);
                        }
                        if(i == 2){
                            alertDialog.dismiss();
                        }
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

        ButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemName = edtName.getText().toString().trim();
                itemDescription = edtDescription.getText().toString().trim();
                itemPrice = edtPrice.getText().toString().trim();

                if(TextUtils.isEmpty(itemName)){
                    edtName.setError("Required!");
                    edtName.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(itemPrice)){
                    edtPrice.setError("Required!");
                    edtPrice.requestFocus();
                    return;
                }
                int price = Integer.parseInt(itemPrice);
                if(price<1){
                    edtPrice.setError("Price is invalid");
                    edtPrice.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(itemDescription)){
                    edtDescription.setError("Required!");
                    edtDescription.requestFocus();
                    return;
                }

                if(!pic || url.isEmpty()){
                    Toast.makeText(context, "Please select item picture!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String id = mDatabaseRef.push().getKey();
                ItemModel model = new ItemModel(id,url,itemName, itemPrice,itemDescription,"");

                mDatabaseRef.child(id).setValue(model);
                hideProgressDialog();
                Toast.makeText(getApplicationContext(), "Item Added Successfully" , Toast.LENGTH_LONG).show();

                edtName.setText("");
                edtDescription.setText("");
                edtPrice.setText("");
                Picasso.with(context).load(R.drawable.addimage).placeholder(R.drawable.addimage).into(setImage);

                itemName = "";
                itemPrice="";
                itemDescription="";

                mImageUri = Uri.EMPTY;
                pic = false;
                edtName.requestFocus();
            }
        });

    }

    //getExtension() method code
    private  String getExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver() ;
        MimeTypeMap mime = MimeTypeMap.getSingleton() ;
        return  mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            setImage.setImageBitmap(photo);
            uploadBitmap(photo);
        }
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(setImage);
            uploadFile();
        }
    }

    private void uploadFile() {
        showProgressDialog();
        if(mImageUri !=null){
            final StorageReference fileref = mStorageRef.child(System.currentTimeMillis() + "." + getExtension(mImageUri));
            mUploadTask =   fileref.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            try {
                                url = uri.toString();
                                pic = true;
                                hideProgressDialog();
                            }
                            catch (Exception ex ){
                                Toast.makeText(getApplicationContext()  , "Error : " + ex.toString() , Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    if (mProgressDialog != null) {
                        mProgressDialog.setMessage("Uploading..\n" +(int) progress + " %completed");
                    }
                }
            });
        }
    }

    private void uploadBitmap(Bitmap bitmap) {
        showProgressDialog();
        byte[] data;
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas);
        data = boas.toByteArray();

        final StorageReference fileref = mStorageRef.child(System.currentTimeMillis()+"");
        UploadTask uploadTask = fileref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        url = uri.toString();
                        pic = true;
                        hideProgressDialog();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                hideProgressDialog();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
            }
        });
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Uploading..please wait ");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                mGranted = true;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE);
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}