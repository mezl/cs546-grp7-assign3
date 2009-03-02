/*
 *******************************************************************
 *                                                                 *
 *    CSCI-546, Spring 2009 Assignment III Solution by Group #7    *
 *                                                                 *
 *    Group Members: Chang, Chin-Kai      chinkaic@usc.edu         *
 *                   Moon, Ji Hyun        jihyunmo@usc.edu         *
 *                   Patlolla, Avinash    patlolla@usc.edu         *
 *                   Viswanathan, Manu    mviswana@usc.edu         *
 *                                                                 *
 *******************************************************************
 *                                                                 *
 * This file defines a class for capturing pictures from the       *
 * phone's camera. The newly captured image is stored in the       *
 * MediaStore.Images.Media database and its ID returned to client  *
 * object.                                                         *
 *                                                                 *
 *******************************************************************
 */

/*
   AssignmentThree -- a photo manager application for the Google Phone

   Copyright (C) 2009 Chin-Kai Chang
                      Ji Hyun Moon
                      Avinash Patlolla
                      Manu Viswanathan

   This file is part of AssignmentThree.

   AssignmentThree is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   AssignmentThree is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with AssignmentThree; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
   USA.
 */

/*
 REVISION HISTORY

 $HeadURL$
 $Id$
 */

//----------------------- PACKAGE SPECIFICATION -------------------------

package cs546.group7 ;

//------------------------------ IMPORTS --------------------------------

// Android GPS support
import android.location.* ;

// Android camera support
import android.hardware.Camera ;

// Android UI support
import android.view.SurfaceView ;
import android.view.SurfaceHolder ;
import android.view.Window ;

import android.view.KeyEvent ;

// Android graphics support
import android.graphics.BitmapFactory ;
import android.graphics.Bitmap ;

// Android content provider support
import android.provider.MediaStore.Images.Media ;
import android.content.ContentValues ;

// Android networking support
import android.net.Uri ;

// Android application and OS support
import android.app.Activity ;
import android.content.Context ;
import android.os.Bundle ;

// Android utilities
import android.util.Log ;

// Java utilities
import java.text.SimpleDateFormat ;
import java.util.Date ;

//------------------------- CLASS DEFINITION ----------------------------

/**
   This class performs the necessary interfacing rituals with Android in
   order to capture a new picture using the gPhone's on-board camera and
   store it to the MediaStore.Images.Media database.
*/
public class ImageRecorder extends Activity {

// Since this class's raison d'etre is to capture images from the camera,
// it shouldn't be surprising that we need this thing:
private Camera m_camera ;

//-------------------------- INITIALIZATION -----------------------------

/**
   This method is called when the activity is first created. It is akin
   to the "main" function in normal desktop applications.
*/
@Override public void onCreate(Bundle saved_state)
{
   super.onCreate(saved_state) ;
   requestWindowFeature(Window.FEATURE_NO_TITLE) ;
   setContentView(new CameraPreview(this)) ;
}

LocationManager m_location_manager ;
Location m_location ;

LocationListener m_location_listener = new LocationListener() {
   public void onProviderDisabled(String provider) {
      m_location = null ;
   }

   public void onLocationChanged(Location L) {
      m_location = L ;
      m_location_manager.removeUpdates(m_location_listener) ;
   } ;

   public void onProviderEnabled(String provider) {}
   public void onStatusChanged(String provider, int status, Bundle extras) {}
} ;

@Override public void onStart()
{
   try
   {
      m_location_manager = (LocationManager)
         getSystemService(Context.LOCATION_SERVICE) ;

      Criteria C = new Criteria() ;
      C.setAccuracy(Criteria.ACCURACY_FINE) ;
      C.setAltitudeRequired(false) ;
      C.setBearingRequired(false) ;
      C.setSpeedRequired(false) ;
      C.setCostAllowed(false) ;
      C.setPowerRequirement(Criteria.POWER_MEDIUM) ;

      String location_provider = m_location_manager.getBestProvider(C, true) ;
      if (location_provider != null) {
         Location last_known_location =
            m_location_manager.getLastKnownLocation(location_provider) ;
         if (last_known_location == null) // bogus GPS
            m_location_manager.requestLocationUpdates(location_provider,
                                                      10000, 100,
                                                      m_location_listener) ;
         else { // got good last known location
            m_location = last_known_location ;
            m_location_manager.removeUpdates(m_location_listener) ;
         }
      }
   }
   catch (Exception e)
   {
      Log.e(null, "MVN: location manager fiasco", e) ;
   }
}

//----------------------------- CLEAN-UP --------------------------------

@Override public void onStop()
{
   super.onStop() ;
   if (m_location_manager != null)
      m_location_manager.removeUpdates(m_location_listener) ;
}

//-------------------------- KEYBOARD EVENTS ----------------------------

/**
   This method is called when the activity receives a keyboard event. In
   the case of this activity, i.e., camera preview-and-capture, we
   respond to presses of the center trackball/mouse button by taking the
   picture of whatever the camera is currently looking at (users should
   be able to tell what they're going to get through the preview).

   In case users press the back button, we return to the photo manager's
   main screen without taking a picture using the camera.
*/
@Override public boolean onKeyDown(int key_code, KeyEvent event)
{
   if (key_code == KeyEvent.KEYCODE_DPAD_CENTER)
   {
      m_camera.takePicture(null, null, new ImageCaptureCallback(this)) ;
      //finish() ;
      return true ;
   }
   if (key_code == KeyEvent.KEYCODE_BACK)
   {
      finish() ;
      return true ;
   }
   return super.onKeyDown(key_code, event) ;
}

//-------------------------- CAMERA PREVIEW -----------------------------

/**
   This inner class implements the camera preview. It is pretty much
   lifted verbatim from the Android CameraPreview sample.
*/
private class CameraPreview extends SurfaceView
                            implements SurfaceHolder.Callback {
   private SurfaceHolder m_holder ;

   public CameraPreview(Context C) {
      super(C) ;
      m_holder = getHolder() ;
      m_holder.addCallback(this) ;
      m_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS) ;
   }

   public void surfaceCreated(SurfaceHolder H) {
      m_camera = Camera.open() ;
      m_camera.setPreviewDisplay(H) ;
   }

   public void surfaceChanged(SurfaceHolder H, int format, int w, int h) {
      Camera.Parameters params = m_camera.getParameters() ;
      params.setPreviewSize(w, h) ;
      m_camera.setParameters(params) ;
      m_camera.startPreview() ;
   }

   public void surfaceDestroyed(SurfaceHolder H) {
      m_camera.stopPreview() ;
      m_camera.release() ;
      m_camera = null ;
   }
} // end of inner class ImageRecorder.CameraPreview

//---------------------- IMAGE CAPTURE CALLBACK -------------------------

/**
   This inner class is used to create bitmaps out of the picture data
   Android obtains from the camera and store these bitmaps along with
   their thumbnails to the MediaStore.Images database.
*/
private class ImageCaptureCallback implements Camera.PictureCallback {

// The callback will need a viable Android context for retrieving
// resources, querying the MediaStore databases, etc.
private Context m_context ;

ImageCaptureCallback(Context C)
{
   m_context = C ;
}

/// The Camera device will invoke this function when a picture has been
/// taken.
public void onPictureTaken(byte[] data, Camera camera)
{
   Date today = new Date() ;

   // Stuff the raw camera data into a bitmap image object and store it
   // to the MediaStore.Images database.
   Bitmap image   = BitmapFactory.decodeByteArray(data, 0, data.length) ;
   String new_uri = Media.insertImage(getContentResolver(), image,
                                      title(today), description(today)) ;
   if (new_uri == null) {
      Utils.alert(m_context, m_context.getString(R.string.capture_failed_msg));
      return ;
   }
   update(new_uri, today.getTime()) ;

   // Retrieve current location and update captured image's record to
   // include these attributes.
   ///*
   //Location gps_coords = get_gps_coordinates() ;
   Location gps_coords = m_location ;
   if (gps_coords == null || stale(gps_coords, today))
      Log.e(null, "MVN: unable to obtain GPS location fix for new image") ;
   else
      update(new_uri, gps_coords.getLatitude(), gps_coords.getLongitude()) ;
   //*/
}

// Returns a suitable title for the newly captured image
private String title(Date d)
{
   return m_context.getString(R.string.group_name)
        + new SimpleDateFormat(".yyyy-MM-dd.HH:mm:ss").format(d) ;
}

// Returns a suitable descriptive comment for the newly captured image
private String description(Date d)
{
   return m_context.getString(R.string.capture_msg)
        + new SimpleDateFormat(" EEEE, MMMM dd, yyyy 'at' hh:mmaa").format(d) ;
}

// Retrieves the current or last known location
private Location get_gps_coordinates()
{
   try
   {
      LocationManager M = (LocationManager)
         m_context.getSystemService(Context.LOCATION_SERVICE) ;

      Criteria C = new Criteria() ;
      C.setAccuracy(Criteria.ACCURACY_COARSE) ;
      C.setAltitudeRequired(false) ;
      C.setBearingRequired(false) ;
      C.setSpeedRequired(false) ;
      C.setCostAllowed(false) ;
      C.setPowerRequirement(Criteria.POWER_MEDIUM) ;

      return M.getLastKnownLocation(M.getBestProvider(C, false)) ;
   }
   catch (Exception e)
   {
      return null ;
   }
}

// Returns true if the last known location is much older than the current
// time.
private boolean stale(Location last_known_location, Date now)
{
   return (now.getTime() - last_known_location.getTime()) > 300000 ; // 5 mins
}

// Time-stamps the image captured by the camera
private void update(String uri, long date_taken)
{
   ContentValues cv = new ContentValues(1) ;
   cv.put(Media.DATE_TAKEN, date_taken) ;
   getContentResolver().update(Uri.parse(uri), cv, null, null) ;
}

// Adds GPS location data to the image captured by the camera
private void update(String uri, double latitude, double longitude)
{
   ContentValues cv = new ContentValues(2) ;
   cv.put(Media.LATITUDE, latitude) ;
   cv.put(Media.LONGITUDE, longitude) ;
   getContentResolver().update(Uri.parse(uri), cv, null, null) ;
}

} // end of class inner ImageRecorder.ImageCaptureCallback

//-----------------------------------------------------------------------

} // end of class cs546.group7.ImageRecorder
