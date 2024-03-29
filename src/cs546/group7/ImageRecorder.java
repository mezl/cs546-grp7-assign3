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

// Android camera support
import android.hardware.Camera ;

// Android UI support
import android.widget.Button ;
import android.widget.TextView ;

import android.view.SurfaceView ;
import android.view.SurfaceHolder ;
import android.view.View ;
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
import android.content.pm.ActivityInfo ;
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

// Once an image has been captured, we store it in the images database
// and keep track of its URI so that we can tag it with the current GPS
// coordinates prior to exiting the activity.
private String m_new_uri ;

// In order to tag each image with the current GPS coordinates, we need
// the following beasties:
private GPSRecorder m_gps ;

//-------------------------- INITIALIZATION -----------------------------

/**
   This method is called when the activity is first created. It is akin
   to the "main" function in normal desktop applications.
*/
@Override protected void onCreate(Bundle saved_state)
{
   super.onCreate(saved_state) ;

   requestWindowFeature(Window.FEATURE_NO_TITLE) ;
   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) ;
   setContentView(R.layout.preview_screen) ;

   setup_camera_preview() ;
   setup_location_listener() ;
   setup_accept_reject_buttons() ;
}

private void setup_camera_preview()
{
   SurfaceView   V = (SurfaceView) findViewById(R.id.surface_view) ;
   SurfaceHolder H = V.getHolder() ;
   H.addCallback(new PreviewHolderCallback()) ;
   H.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS) ;
}

private void setup_location_listener()
{
   m_gps = GPSRecorder.instance(this) ;
   m_gps.use_ui((TextView) findViewById(R.id.preview_latitude),
                (TextView) findViewById(R.id.preview_longitude)) ;
   m_gps.show_location() ;
}

private void setup_accept_reject_buttons()
{
   final Context context = this ;

   Button B = (Button) findViewById(R.id.preview_accept_btn) ;
   B.setOnClickListener(new View.OnClickListener() {
      public void onClick(View V) {
         if (m_new_uri == null) // picture not yet taken
            Utils.notify(context, getString(R.string.take_picture_msg)) ;
         else // picture taken and okayed by user ==> go to display screen
         {
            long id = Utils.get_thumbnail_id((Activity) context, m_new_uri) ;
            Utils.display_picture(context, id) ;
            finish() ;
         }
      }}) ;

   B = (Button) findViewById(R.id.preview_reject_btn) ;
   B.setOnClickListener(new View.OnClickListener() {
      public void onClick(View V) {
         if (m_new_uri == null) // picture not yet taken
            Utils.notify(context, getString(R.string.take_picture_msg)) ;
         else // picture taken but rejected by user ==> delete and start over
         {
            reject_picture() ;
            m_camera.stopPreview() ;
            m_camera.startPreview() ;
         }
      }}) ;
}

//----------------------------- CLEAN-UP --------------------------------

/// When the activity is potentially on the verge of being dismissed, we
/// check if the location listener has produced anything useful and
/// update the newly captured image's latitude and longitude data fields.
@Override protected void onPause()
{
   if (m_new_uri != null) // picture taken and successfully stored in database
   {
      if (m_gps.stale(new Date()))
         //update(m_new_uri, 34.021124, -118.287553) ;
         ; // don't show incorrect coordinates
      else
         update(m_new_uri, m_gps.latitude(), m_gps.longitude()) ;
   }
   m_gps.use_ui(null, null) ;
   super.onPause() ;
}

// Adds GPS location data to the image captured by the camera
private void update(String uri, double latitude, double longitude)
{
   ContentValues cv = new ContentValues(2) ;
   cv.put(Media.LATITUDE, latitude) ;
   cv.put(Media.LONGITUDE, longitude) ;
   getContentResolver().update(Uri.parse(uri), cv, null, null) ;
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
      return true ;
   }
   if (key_code == KeyEvent.KEYCODE_BACK)
   {
      reject_picture() ;
      finish() ;
      return true ;
   }
   return super.onKeyDown(key_code, event) ;
}

//-------------------------- CAMERA PREVIEW -----------------------------

/**
   This inner class implements a surface holder callback that initiates
   and shuts down the camera preview.
*/
private class PreviewHolderCallback implements SurfaceHolder.Callback {
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
} // end of inner class ImageRecorder.PreviewSurfaceHolder

// Delete the picture snapped by the user from the database (presumably,
// this is because the user wants to try again).
private void reject_picture()
{
   if (m_new_uri == null) // no picture taken yet
      return ;
   Utils.delete_picture(this, m_new_uri) ;
   m_new_uri = null ;
}

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
   Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length) ;
   m_new_uri = Media.insertImage(getContentResolver(), image,
                                 title(today), description(today)) ;
   if (m_new_uri == null) {
      Utils.alert(m_context, m_context.getString(R.string.capture_failed_msg));
      return ;
   }
   update(m_new_uri, today.getTime()) ;
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

// Time-stamps the image captured by the camera
private void update(String uri, long date_taken)
{
   ContentValues cv = new ContentValues(1) ;
   cv.put(Media.DATE_TAKEN, date_taken) ;
   getContentResolver().update(Uri.parse(uri), cv, null, null) ;
}

} // end of inner class ImageRecorder.ImageCaptureCallback

//-----------------------------------------------------------------------

} // end of class cs546.group7.ImageRecorder
