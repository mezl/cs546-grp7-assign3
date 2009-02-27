package cs546.group7 ;

import java.io.OutputStream;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class ImageCaptureCallback implements PictureCallback {

	private OutputStream filoutputStream;

	public ImageCaptureCallback(OutputStream filoutputStream) {
		this.filoutputStream = filoutputStream;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		try {
			Log.v(getClass().getSimpleName(), "onPictureTaken=" + data
					+ " length = " + data.length);
			filoutputStream.write(data);
			filoutputStream.flush();
			filoutputStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		

		
		
	}
}