
package app.rentro.pdftoimage;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.FileOutputStream;


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
   		PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(new File(pdfFile), ParcelFileDescriptor.MODE_READ_ONLY));
		Bitmap bitmap;
		PdfRenderer.Page page = renderer.openPage(0);

		int width = reactContext.getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
        int height = reactContext.getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        File output = this.saveImage(bitmap, reactContext.getCacheDir());

        page.close();

        WritableMap map = Arguments.createMap();
        map.putString("outputFile", output.getAbsolutePath());
        promise.resolve(map);

    } catch(Exception e) {
        promise.reject(E_CONVERT_ERROR, e);
    }
  }

  private File saveImage(Bitmap finalBitmap, File cacheDir) {
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
    return file;
  }
}