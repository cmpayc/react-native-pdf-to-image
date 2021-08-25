
package app.rentro.pdftoimage;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class RNPdfToImageModule extends ReactContextBaseJavaModule {

    private static final String E_CONVERT_ERROR = "E_CONVERT_ERROR";

    private final ReactApplicationContext reactContext;

    public RNPdfToImageModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNPdfToImage";
    }

    @ReactMethod
    public void convert(String pdfFile, Promise promise) {
        try {
            File file;
            boolean isContentFile = false;
            if (pdfFile.startsWith("content://")) {
                file = getContentFilePath(pdfFile);
                isContentFile = true;
            } else if (pdfFile.startsWith("file://")) {
                file = new File(pdfFile);
            } else {
                file = new File("file://" + pdfFile);
            }
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
            Bitmap bitmap;
            PdfRenderer.Page page = renderer.openPage(0);

            int width = reactContext.getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
            int height = reactContext.getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            float ratio = Math.min((float) 800 / bitmap.getWidth(), (float) 800 / bitmap.getHeight());
            int w = Math.round((float) ratio * bitmap.getWidth());
            int h = Math.round((float) ratio * bitmap.getHeight());
            Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, w, h, false);
            bitmap.recycle();
            WritableMap map = this.saveImage(newBitmap, reactContext.getCacheDir());
            page.close();
            if (isContentFile) {
                file.delete();
            }

            promise.resolve(map);
        } catch(Exception e) {
            promise.reject(E_CONVERT_ERROR, e);
        }
    }

    private WritableMap saveImage(Bitmap finalBitmap, File cacheDir) {
        File file = new File(cacheDir.getAbsolutePath() + File.separator + System.currentTimeMillis() + "_pdf.jpg");
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Long fileLength = file.length();
        WritableMap map = Arguments.createMap();
        map.putString("file", file.getAbsolutePath());
        map.putInt("width", finalBitmap.getWidth());
        map.putInt("height", finalBitmap.getHeight());
        map.putInt("size", fileLength.intValue());
        return map;
    }

    private File getContentFilePath(String filePath) {
        Uri returnUri = Uri.parse(filePath);
        Cursor returnCursor = reactContext.getContentResolver().query(returnUri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        // int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        File file = new File(reactContext.getCacheDir(), name);
        try {
            InputStream inputStream = reactContext.getContentResolver().openInputStream(returnUri);
            OutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            returnCursor.close();
            return null;
        } finally {
            returnCursor.close();
        }
        return file;
    }
}