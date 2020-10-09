package coding.academy.scd_ml_kit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    Button mCamera , mGallary ;


    private static final int requestPermissionID = 101;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_PHOTO_GALLERY = 2;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    public static final int WRITE_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageId);
        mCamera= findViewById(R.id.camera);
        mGallary= findViewById(R.id.Gallery);
        //find textview
        textView = findViewById(R.id.textId);

        /*check app level permission is granted for Camera
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //grant the permission
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }

         */

        checkPermission(REQUEST_TAKE_PHOTO , false);



        mGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(REQUEST_PHOTO_GALLERY , true);
            }
        });


        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkPermission(REQUEST_TAKE_PHOTO , true);
                      }
        });
    }



    //Check whether the user has granted the WRITE_STORAGE permission//

    public void checkPermission(int requestCode , boolean open) {
        switch (requestCode) {

            case REQUEST_TAKE_PHOTO :
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED){
                        //permission not enabled, request it
                        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},requestCode );
                    }
                   else if (open) {
                        //permission already granted
                        openCamera();
                    }
                }
                else {
                    if (open) {
                        //permission already granted
                        openCamera();
                    }
                }

                break;


            case REQUEST_PHOTO_GALLERY :
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_GRANTED){
                        selectPicture();
                    }
                    else {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                    }
                }
                break;


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.e("MainActivity", "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                // openCamera();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }





    }


    public void openCamera() {
        photoFile = PicUtil.createTempFile(photoFile);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            try {

            } catch (Exception ex) {
                // Error occurred while creating the File
                Log.e("Main" , ex.toString()) ;

            }

            if (photoFile != null) {

              /*  imageUri = Uri.fromFile(image);

                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);


*/
                Uri photoURI = FileProvider.getUriForFile(this,
                        "coding.academy.scd_ml_kit.fileprovider",
                        photoFile);

              //  Uri photoURI =  Uri.fromFile(photoFile);
                Log.e("Main" , photoURI.getPath()) ;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION) ;
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }


        }

        //open the camera => create an Intent object

      //  intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI , "image/*" );
     //   startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    public File photoFile;

    private void selectPicture() {
        photoFile = PicUtil.createTempFile(photoFile);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI , "image/*" );
        startActivityForResult(intent, REQUEST_PHOTO_GALLERY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

           if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK ) {
              /*
                Bundle bundle = data.getExtras();
                //from bundle, extract the image
                Bitmap bitmap = (Bitmap) bundle.get("data");

                   imageView.setImageBitmap(myBitmap);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    FbVisionTextRecognizer(myBitmap);

               */

               // Uri imageUri = (Uri) data.getData();
             //  Uri imageUri = Uri.fromFile(photoFile);

               Uri imageUri = FileProvider.getUriForFile(this,
                       "coding.academy.scd_ml_kit.fileprovider",
                       photoFile);

               Log.e("imageUri =" , imageUri.getPath()) ;

               Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
               mediaScanIntent.setData(imageUri);
               this.sendBroadcast(mediaScanIntent);


               Bitmap myBitmap = PicUtil.resizePhoto(photoFile, this, imageUri, imageView);

                if(myBitmap !=null) {
                    imageView.setImageBitmap(myBitmap);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    FbVisionTextRecognizer(myBitmap);
                }

            }
            else if (requestCode == REQUEST_PHOTO_GALLERY && resultCode == RESULT_OK)
            {
                Uri imageUri = (Uri) data.getData();
                Log.e("ocr", "launchMediaScanIntent = " + imageUri.getPath() );
                 /*
                String path = PicUtil.getPath(this, imageUri);
                Bitmap  myBitmap ;
                if (path == null) {
                    myBitmap = PicUtil.resizePhoto(photo, this, imageUri, imageView);
                } else {
                    myBitmap = PicUtil.resizePhoto(photo, path, imageView);
                }
                if (myBitmap != null) {
                    textView.setText(null);
                    imageView.setImageBitmap(myBitmap);
                }
 */
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(imageUri);
                this.sendBroadcast(mediaScanIntent);

                String path = PicUtil.getPath(this, imageUri);

                Bitmap myBitmap ;
                if (path == null) {
                    myBitmap = PicUtil.resizePhoto(photoFile, this, imageUri, imageView);
                } else {
                    myBitmap = PicUtil.resizePhoto(photoFile, path, imageView);
                }
                if (myBitmap != null) {
                   // textView.setText(null);
                    //imageView.setImageBitmap(myBitmap);
                    imageView.setImageBitmap(myBitmap);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    FbVisionTextRecognizer(myBitmap) ;
                }

               // Bitmap bitmap2 = decodeBitmapUri( this, imageUri , imageView );





            }





        } catch (Exception e){
            Log.e("Main" , e.toString()) ;
        }




    }





    FirebaseVision firebaseVision ;
    FirebaseVisionImage firebaseVisionImage ;
    FirebaseVisionTextRecognizer firebaseVisionTextRecognizer ;
    private String FbVisionTextRecognizer(Bitmap bitmap){
        //process the image
        //1. create a FirebaseVisionImage object from a Bitmap object
         firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        //2. Get an instance of FirebaseVision
         firebaseVision = FirebaseVision.getInstance();
        //3. Create an instance of FirebaseVisionTextRecognizer
        firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
        //4. Create a task to process the image
        Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);
        //5. if task is success

        task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
               // String s = firebaseVisionText.getText();
                processExtractedText(firebaseVisionText) ;
              //  textView.setText(s);
            }
        });
        //6. if task is failure
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        return textView.getText().toString() ;
    }


    private void processExtractedText(FirebaseVisionText firebaseVisionText) {
        textView.setText(null);
        if (firebaseVisionText.getTextBlocks().size() == 0) {
            textView.setText("No_text");
            return;
        }
        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
            textView.append(block.getText());
        }
    }












}