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

// Android content-provider support
import android.provider.MediaStore.Images.Media ;
import android.provider.MediaStore.Images.Thumbnails ;

// Android database support
import android.database.Cursor ;

// Android application and OS support
import android.app.Activity ;
import android.os.Bundle ;

// Android utilities
import android.content.ContentUris ;
import android.util.Log ;

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

/// The photo manager's main screen passes in the ID of the selected
/// thumbnail to display screen using the intent extras mechanism
/// provided by Android. The display screen, in turn, passes this ID in
/// to this tab via the same mechanism.
///
/// To be able to properly store and retrieve this value, the display
/// screen and this tab need to agree on a suitable key/tag to use. The
/// following string is that key.
public static final String EXTRAS_THUMBNAIL_ID = "extras_thumbnail_id" ;

/// Given the thumbnail ID, this class needs to find and display the
/// corresponding full-sized image.
private int m_full_picture_id ;

/// We use the following helper objects to playback and record audio tags
/// for the displayed image.
private AudioRecorder m_audio_recorder ;

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

   Bundle extras = getIntent().getExtras() ;
   m_full_picture_id = full_picture_id(extras.getLong(EXTRAS_THUMBNAIL_ID)) ;

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
   Utils.notify(this, "Playing back audio tag...") ;
}

// Record an audio tag for the displayed image
private void record_audio_tag()
{
   Utils.notify_long(this, getString(R.string.record_audio_msg)) ;

   try
   {
      m_audio_recorder = new AudioRecorder(this) ;
      m_audio_recorder.start() ;
   }
   catch (Exception e)
   {
      Log.e(null, "MVN: unable to create AudioRecorder", e) ;
      Utils.alert(this, getString(R.string.audio_recorder_init_error_msg)) ;
   }
}

// Stop an audio recording currently in progress and connect it to the
// currently displayed image.
private void wind_up_audio()
{
   if (m_audio_recorder == null) // no audio recording in progress
      return ;

   try
   {
      m_audio_recorder.stop() ;
      //m_db.delete_audio(m_full_picture_id) ;
      //m_db.update_audio(m_full_picture_id, m_audio_recorder.get_id()) ;
   }
   catch (Exception e)
   {
      Log.e(null, "MVN: unable to copy temp audio tag file to database, e") ;
      Utils.alert(this, getString(R.string.audio_recording_failed_msg)) ;
   }
   m_audio_recorder = null ;
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
   ImageView img = (ImageView) findViewById(R.id.full_picture) ;
   img.setImageURI(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI,
                                              picture_id)) ;
}

//------------------------------ HELPERS --------------------------------

// Return ID of full-sized image corresponding to specified thumbnail
private int full_picture_id(long thumbnail_id)
{
   try
   {
      String[] columns = new String[] {
         Thumbnails._ID,
         Thumbnails.IMAGE_ID,
      } ;
      String where_clause = Thumbnails._ID + "=" + thumbnail_id ;
      Cursor C = managedQuery(Thumbnails.EXTERNAL_CONTENT_URI, columns,
                              where_clause, null, null) ;
      if (C.getCount() > 0) {
         C.moveToFirst() ;
         return C.getInt(C.getColumnIndex(Thumbnails.IMAGE_ID)) ;
      }
   }
   catch (android.database.sqlite.SQLiteException e)
   {
      Log.e(null, "MVN: unable to retrieve thumbnails", e) ;
   }
   return -1 ;
}

//-----------------------------------------------------------------------

} // end of class cs546.group7.PictureTab
