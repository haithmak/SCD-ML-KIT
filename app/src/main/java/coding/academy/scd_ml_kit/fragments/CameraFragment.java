package coding.academy.scd_ml_kit.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;

import coding.academy.scd_ml_kit.Analyse;
import coding.academy.scd_ml_kit.PicUtil;
import coding.academy.scd_ml_kit.R;

import static android.app.Activity.RESULT_OK;


public class CameraFragment extends Fragment {

    private static final int requestPermissionID = 101;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_PHOTO_GALLERY = 2;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    public static final int WRITE_STORAGE = 100;

    private Analyse _analyse;

    ImageView imageView;
    TextView textView;
    TextView textSuggestion;
    Button mCamera, mGallary;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.imageId);
        mCamera = view.findViewById(R.id.camera);
        mGallary = view.findViewById(R.id.Gallery);
        //find textview
        textView = view.findViewById(R.id.textId);
        textSuggestion = view.findViewById(R.id.textSuggestion);
        _analyse = new Analyse(requireContext().getApplicationContext());

        /*check app level permission is granted for Camera
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //grant the permission
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }
         */

        checkPermission(REQUEST_TAKE_PHOTO, false);


        mGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(REQUEST_PHOTO_GALLERY, true);
            }
        });


        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission(REQUEST_TAKE_PHOTO, true);
            }
        });

    }

    //Check whether the user has granted the WRITE_STORAGE permission//

    public void checkPermission(int requestCode, boolean open) {
        switch (requestCode) {

            case REQUEST_TAKE_PHOTO:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (requireContext().getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            requireContext().getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED) {
                        //permission not enabled, request it
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                    } else if (open) {
                        //permission already granted
                        openCamera();
                    }
                } else {
                    if (open) {
                        //permission already granted
                        openCamera();
                    }
                }

                break;


            case REQUEST_PHOTO_GALLERY:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (requireContext().getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                        selectPicture();
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
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
                if (ActivityCompat.checkSelfPermission(requireContext().getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
        if (takePictureIntent.resolveActivity(requireContext().getApplicationContext().getPackageManager()) != null) {

            try {

            } catch (Exception ex) {
                // Error occurred while creating the File
                Log.e("Main", ex.toString());

            }

            if (photoFile != null) {

              /*  imageUri = Uri.fromFile(image);

                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);


*/
                Uri photoURI = FileProvider.getUriForFile(requireContext().getApplicationContext(),
                        "coding.academy.scd_ml_kit.fileprovider",
                        photoFile);

                //  Uri photoURI =  Uri.fromFile(photoFile);
                Log.e("Main", photoURI.getPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
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
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_PHOTO_GALLERY);
    }


    //https://www.androidauthority.com/ml-kit-extracting-text-from-images-google-machine-learning-sdk-911740/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
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

                Uri imageUri = FileProvider.getUriForFile(requireContext().getApplicationContext(),
                        "coding.academy.scd_ml_kit.fileprovider",
                        photoFile);

                Log.e("imageUri =", imageUri.getPath());

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(imageUri);
                requireContext().getApplicationContext().sendBroadcast(mediaScanIntent);


                Bitmap myBitmap = PicUtil.resizePhoto(photoFile, requireContext().getApplicationContext(), imageUri, imageView);

                if (myBitmap != null) {
                    imageView.setImageBitmap(myBitmap);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    FbVisionTextRecognizer(myBitmap);
                }

            } else if (requestCode == REQUEST_PHOTO_GALLERY && resultCode == RESULT_OK) {
                Uri imageUri = (Uri) data.getData();
                Log.e("ocr", "launchMediaScanIntent = " + imageUri.getPath());

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
                requireContext().getApplicationContext().sendBroadcast(mediaScanIntent);

                String path = PicUtil.getPath(requireContext().getApplicationContext(), imageUri);

                Bitmap myBitmap;
                if (path == null) {
                    myBitmap = PicUtil.resizePhoto(photoFile, requireContext().getApplicationContext(), imageUri, imageView);
                } else {
                    myBitmap = PicUtil.resizePhoto(photoFile, path, imageView);
                }
                if (myBitmap != null) {
                    // textView.setText(null);
                    //imageView.setImageBitmap(myBitmap);
                    imageView.setImageBitmap(myBitmap);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    FbVisionTextRecognizer(myBitmap);
                }

                // Bitmap bitmap2 = decodeBitmapUri( this, imageUri , imageView );


            }


        } catch (Exception e) {
            Log.e("Main", e.toString());
        }


    }


    FirebaseVision firebaseVision;
    FirebaseVisionImage firebaseVisionImage;
    FirebaseVisionTextRecognizer firebaseVisionTextRecognizer;

    private String FbVisionTextRecognizer(Bitmap bitmap) {
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
                processExtractedText(firebaseVisionText);
                //  textView.setText(s);
            }
        });
        //6. if task is failure
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(requireContext().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        return textView.getText().toString();
    }


    private void processExtractedText(FirebaseVisionText firebaseVisionText) {
        textView.setText(null);
        StringBuilder textToAnalyse = new StringBuilder();

        if (firebaseVisionText.getTextBlocks().size() == 0) {
            textView.setText("No_text");
            return;
        }

        StringBuilder finalText = new StringBuilder();

        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
            //  textView.append(block.getText() );
            String blockText = block.getText();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();

            for (FirebaseVisionText.Line line : block.getLines()) {
                String lineText = line.getText();


                finalText.append(block.getText());

                Point[] lineCornerPoints = line.getCornerPoints();

                Rect lineFrame = line.getBoundingBox();

                for (FirebaseVisionText.Element element : line.getElements()) {
                    String elementText = element.getText();

                    textView.append(element.getText() + "\n");

                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();

                }

                textToAnalyse.append(line.getText()).append("\n");

            }

        }


        _analyse.analyseNormalText(textToAnalyse.toString(), textView, textSuggestion);

    }

}