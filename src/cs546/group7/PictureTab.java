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
   * This file contains the code for the picture tab of the display  *
   * screen of a photo manager application that presents users the   *
   * thumbnails of all the currently available pictures and allows   *
   * them to add new pictures using the camera. New pictures are     *
   * automatically tagged with the current GPS coordinates and may   *
   * optionally be tagged with audio.                                *
   *                                                                 *
   * When a thumbnail is selected, it will be displayed in a new     *
   * screen. The user will be able to play back any associated audio *
   * and also display a map showing the GPS coordinates of where     *
   * that picture was taken. Thus, the second screen of the photo    *
   * manager application consists of two tabs: one for showing the   *
   * picture and the other for showing the GPS coordinates of where  *
   * that picture was taken.                                         *
   *                                                                 *
   * This file contains the code for the picture tab.                *
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

// Android UI support
import android.widget.ImageView ;

import android.view.Menu ;
import android.view.MenuItem ;

import android.view.KeyEvent ;

// Android media support
import android.media.MediaPlayer ;

// Android graphics support
import android.graphics.BitmapFactory ;
import android.graphics.Bitmap ;

// Android content-provider support
import android.provider.MediaStore.Images ;
import android.provider.MediaStore.Audio ;

// Android database support
import android.database.Cursor ;

// Android application and OS support
import android.app.Activity ;
import android.os.Bundle ;

// Android utilities
import android.content.ContentUris ;
import android.util.Log ;

// Java I/O support
import java.io.InputStream ;
import java.io.FileNotFoundException ;
import java.io.IOException ;

//-------------------- DISPLAY SCREEN PICTURE TAB -----------------------

/**
   This class is part of the display screen of a photo manager
   application that tags new pictures taken with the camera with the
   current GPS coordinates and optional audio. When a picture's thumbnail
   is selected, the entire image will be displayed and users will be
   given the ability to play back any associated audio and view the GPS
   coordinates of the picture on a map.

   This class implements the picture tab of the display screen. It shows
   the full-sized image corresponding to the thumbnail that was selected
   on the previous (i.e., main) screen of the application. A pair of
   buttons below the image allow users to either play or record an audio
   tag for the displayed image.
*/
public class PictureTab extends Activity {

// Given the thumbnail ID, this class needs to find and display the
// corresponding full-sized image.
private int m_full_picture_id ;

// The photo manager application maintains audio tags for the available
// images by mapping picture IDs to audio IDs using a custom database.
private AudioTagsDB m_db ;

// We use the following helper objects to playback and record audio tags
// for the displayed image.
private AudioRecorder m_audio_recorder ;
private MediaPlayer   m_audio_player ;

//-------------------------- INITIALIZATION -----------------------------

/**
   This method is called when the activity is first created. It is akin
   to the "main" function in normal desktop applications. On the picture
   tab of the display screen, we need to retrieve the ID of the thumbnail
   selected by the user on the photo manager app's main screen and
   display that in the tab's full-picture image view.
*/
@Override public void onCreate(Bundle saved_state)
{
   super.onCreate(saved_state) ;
   setContentView(R.layout.picture_tab) ;

   m_db = new AudioTagsDB(this) ;
   m_db.open() ;

   m_full_picture_id = Utils.full_picture_id(this,
      getIntent().getExtras().getLong(Utils.EXTRAS_THUMBNAIL_ID)) ;

   display_picture(m_full_picture_id) ;
}

/**
   This method is called the first time the activity's menu is displayed.
   The code to setup the menu is pretty much boilerplate, i.e., inflate
   the appropriate menu from an XML resource file.
*/
@Override public boolean onCreateOptionsMenu(Menu menu)
{
   getMenuInflater().inflate(R.menu.picture_tab_menu, menu) ;
   return true ;
}

//------------------------- LIFE-CYCLE EVENTS ---------------------------

/// Called when the activity ends. In our app, we should close the
/// connection to the database.
@Override protected void onPause()
{
   super.onPause() ;
   m_db.close() ;
   m_db = null ;
}

/// Called when the activity is resumed
@Override protected void onResume()
{
   super.onResume() ;
   if (m_db == null) {
      m_db = new AudioTagsDB(this) ;
      m_db.open() ;
   }
}

//--------------------------- MENU COMMANDS -----------------------------

/**
   This method is called when a menu item from the activity's menu is
   selected. The handler is pretty much boilerplate; it simply despatches
   handling for each command on the picture tab's menu to an appropriate
   method.
*/
@Override public boolean onOptionsItemSelected(MenuItem item)
{
   switch (item.getItemId())
   {
      case R.id.play_audio_tag:
         play_audio_tag() ;
         return true ;

      case R.id.record_audio_tag:
         record_audio_tag() ;
         return true ;
   }
   return super.onOptionsItemSelected(item) ;
}

// Play back the audio tag (if any) associated with the displayed image
private void play_audio_tag()
{
   try
   {
      long audio_id = m_db.get_audio_id(m_full_picture_id) ;
      if (audio_id == -1) {
         Utils.alert(this, getString(R.string.no_audio_tag_msg)) ;
         return ;
      }

      m_audio_player = new MediaPlayer() ;
      if (m_audio_player == null) {
         Utils.alert(this, getString(R.string.audio_player_init_error_msg)) ;
         return ;
      }
      m_audio_player.setDataSource(Utils.get_audio_file_name(this, audio_id)) ;
      m_audio_player.prepare() ;
      m_audio_player.start() ;

      Utils.notify_long(this, getString(R.string.play_audio_msg)) ;
   }
   catch (IOException e)
   {
      Log.e(null, "MVN: unable to play back audio tag", e) ;
      m_audio_player = null ;
      Utils.alert(this, getString(R.string.audio_player_init_error_msg)) ;
   }
}

// Record an audio tag for the displayed image
private void record_audio_tag()
{
   try
   {
      m_audio_recorder = new AudioRecorder(this) ;
      m_audio_recorder.start() ;
      Utils.notify_long(this, getString(R.string.record_audio_msg)) ;
   }
   catch (Exception e)
   {
      Log.e(null, "MVN: unable to create AudioRecorder", e) ;
      m_audio_recorder = null ;
      Utils.alert(this, getString(R.string.audio_recorder_init_error_msg)) ;
   }
}

// Graceful shutdown (hopefully) of audio playback or recording
private void wind_up_audio()
{
   if (m_audio_recorder != null)
      wind_up_audio_recording() ;
   else if (m_audio_player != null)
      wind_up_audio_playback() ;
}

// Stop an audio recording currently in progress and connect it to the
// currently displayed image.
private void wind_up_audio_recording()
{
   try
   {
      m_audio_recorder.stop() ;

      long recording_id = m_audio_recorder.get_id() ;
      if (recording_id != -1)
         m_db.update(m_full_picture_id, recording_id) ;
   }
   catch (Exception e)
   {
      Log.e(null, "MVN: unable to copy temp audio tag file to database", e) ;
      Utils.alert(this, getString(R.string.audio_recording_failed_msg)) ;
   }
   m_audio_recorder = null ;
}

// Stop audio tag playback for the currently displayed image
private void wind_up_audio_playback()
{
   try
   {
      m_audio_player.stop() ;
      m_audio_player.release() ;
   }
   catch (Exception e)
   {
      Log.e(null, "MVN: something went wrong winding up audio playback", e) ;
   }
   m_audio_player = null ;
}

//-------------------------- KEYBOARD EVENTS ----------------------------

/**
   This method is called when the activity receives a keyboard event. In
   the case of this activity, i.e., full-size image display, we respond
   to presses of the center trackball/mouse button by stopping the audio
   tag recording if it is in progress and then storing the ID of this
   newly created audio tag in the application's database linking images
   with their corresponding audio tags.

   In case users press the back button, we return to the photo manager's
   main screen. But if an audio tag is being recorded, then we end the
   process gracefully, perform the database update and then return to the
   main screen.
*/
@Override public boolean onKeyDown(int key_code, KeyEvent event)
{
   if (key_code == KeyEvent.KEYCODE_DPAD_CENTER)
   {
      wind_up_audio() ;
      return true ;
   }
   if (key_code == KeyEvent.KEYCODE_BACK)
   {
      wind_up_audio() ;
      finish() ;
      return true ;
   }
   return super.onKeyDown(key_code, event) ;
}

//-------------------------- PICTURE DISPLAY ----------------------------

private void display_picture(long picture_id)
{
   try
   {
      InputStream is = getContentResolver().
         openInputStream(ContentUris.withAppendedId(
            Images.Media.EXTERNAL_CONTENT_URI, picture_id)) ;

      BitmapFactory.Options options = new BitmapFactory.Options() ;
      options.inSampleSize = 4 ;
      Bitmap bmp = BitmapFactory.decodeStream(is, null, options) ;
      is.close() ;

      if (bmp != null) {
         ImageView img = (ImageView) findViewById(R.id.full_picture) ;
         img.setImageBitmap(bmp) ;
      }
   }
   catch (FileNotFoundException e)
   {
      Log.e(null, "MVN: unable to read image ID " + picture_id, e) ;
   }
   catch (IOException e)
   {
      Log.e(null, "MVN: unable to close bitmap input stream", e) ;
   }
}

//-----------------------------------------------------------------------

} // end of class cs546.group7.PictureTab
