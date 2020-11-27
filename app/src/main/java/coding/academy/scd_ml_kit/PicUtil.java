package coding.academy.scd_ml_kit;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.graphics.BitmapFactory.decodeFile;
import static android.graphics.BitmapFactory.decodeStream;

public class PicUtil {

    public static String getPath(Context context, Uri uri) {
        String path = "";
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int column_index;
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
            cursor.close();
        }
        return path;
    }


    public static File createTempFile(File file) {
        String imageFileName = "SCD_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        //File directory = new File(Environment.getExternalStorageDirectory()
        //      .getPath() + "/com.jessicathornsby.myapplication");
        File directory = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DCIM) + "/SCD");
        if (!directory.exists() || !directory.isDirectory()) {
            directory.mkdirs();
        }
        if (file == null) {
            file = new File(directory, imageFileName);
        }
        return file;
    }


    public static Bitmap resizePhoto(File imageFile, Context context, Uri uri, ImageView view) {
        BitmapFactory.Options newOptions = new BitmapFactory.Options();
        try {
            decodeStream(context.getContentResolver().openInputStream(uri), null, newOptions);
            int photoHeight = newOptions.outHeight;
            int photoWidth = newOptions.outWidth;

            // newOptions.inSampleSize = Math.min(photoWidth / view.getWidth(), photoHeight / view.getHeight());
            newOptions.inSampleSize = Math.max(1, Math.min(photoWidth / view.getWidth(), photoHeight / view.getHeight()));
            newOptions.inJustDecodeBounds = false;
            newOptions.inPurgeable = true;
            return compressPhoto(imageFile, decodeStream(context.getContentResolver().openInputStream(uri), null, newOptions));
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public Bitmap decodeBitmapUri(Context ctx, Uri uri, ImageView imageview) throws FileNotFoundException {
        // int targetW = 600;
        // int targetH = 600;
        // Get the dimensions of the View
        int targetW = imageview.getWidth();
        int targetH = imageview.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));
        //int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        return BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
    }

    public static Bitmap resizePhoto(File imageFile, String path, ImageView view) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        decodeFile(path, options);
        int photoHeight = options.outHeight;
        int photoWidth = options.outWidth;
        options.inSampleSize = Math.min(photoWidth / view.getWidth(), photoHeight / view.getHeight());
        return compressPhoto(imageFile, decodeFile(path, options));
    }

    private static Bitmap compressPhoto(File photoFile, Bitmap bitmap) {
        try {
            FileOutputStream fOutput = new FileOutputStream(photoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOutput);
            fOutput.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return bitmap;
    }


}
