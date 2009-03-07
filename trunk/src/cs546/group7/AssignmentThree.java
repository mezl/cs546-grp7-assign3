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
   * This file contains the main code for a photo manager applicati- *
   * on for the Android/gPhone platform. This application displays   *
   * thumbnails of all the currently available pictures and also     *
   * allows users to add new pictures using the camera. New pictures *
   * are automatically tagged with the current GPS coordinates and   *
   * may optionally be tagged with audio.                            *
   *                                                                 *
   * When a thumbnail is selected, it will be displayed in a new     *
   * screen. The user will be able to play back any associated audio *
   * and also display a map showing the GPS coordinates of where     *
   * that picture was taken.                                         *
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
import android.widget.SimpleCursorAdapter ;
import android.widget.ImageView ;
import android.widget.GridView ;
import android.widget.AdapterView ;

import android.view.Menu ;
import android.view.MenuItem ;
import android.view.View ;

// Android content-provider support
import android.provider.MediaStore.Audio ;
import android.provider.MediaStore.Images ;
import android.content.ContentResolver ;

// Android database support
import android.database.Cursor ;

// Android application and OS support
import android.content.Context ;
import android.app.Activity ;
import android.content.Intent ;
import android.os.Bundle ;

// Android utilities
import android.content.ContentUris ;
import android.net.Uri ;
import android.util.Log ;

// Java I/O support
import java.io.File ;

//--------------------- APPLICATION'S MAIN SCREEN -----------------------

/**
   This class implements the main screen of a photo manager application
   that tags new pictures taken with the camera with the current GPS
   coordinates and optional audio. When a picture's thumbnail is
   selected, the entire image will be displayed and users will be given
   the ability to play back any associated audio and view the GPS
   coordinates of the picture on a map.
*/
public class AssignmentThree extends Activity {

// The main screen of the photo manager application shows thumbnails of
// all the available images in a neat grid.
private GridView m_thumbnails_grid ;

// The photo manager application maintains audio tags for the available
// images by mapping picture IDs to audio IDs using a custom database.
private AudioTagsDB m_db ;

//-------------------------- INITIALIZATION -----------------------------

/**
   This method is called when the activity is first created. It is akin
   to the "main" function in normal desktop applications.
*/
@Override public void onCreate(Bundle saved_state)
{
   super.onCreate(saved_state) ;
   setContentView(R.layout.main) ;

   m_db = new AudioTagsDB(this) ;
   m_db.open() ;

   final Context context = this ;

   // Setup the thumbnails grid
   m_thumbnails_grid = (GridView) findViewById(R.id.thumbnails_grid) ;
   m_thumbnails_grid.setOnItemClickListener(
      new AdapterView.OnItemClickListener() {
         public void onItemClick(AdapterView A, View V, int pos, long id) {
            Utils.display_picture(context, id) ;
         }}) ;

   // Display the thumbnails of all available images
   display_thumbnails(m_thumbnails_grid) ;
}

/**
   This method is called the first time the application's menu is
   displayed. The code to setup the menu is pretty much boilerplate,
   i.e., inflate the appropriate menu from an XML resource file.
*/
@Override public boolean onCreateOptionsMenu(Menu menu)
{
   getMenuInflater().inflate(R.menu.main_menu, menu) ;
   return true ;
}

//------------------------- LIFE-CYCLE EVENTS ---------------------------

/// Called when the activity ends. In our app, we should close the
/// connection to the database.
@Override protected void onPause()
{
   m_db.close() ;
   m_db = null ;
   super.onPause() ;
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
   selected. In the case of the photo manager application, this
   corresponds to the main menu associated with the main screen. The
   handler is pretty much boilerplate; it simply despatches handling for
   each menu item to an appropriate method.
*/
@Override public boolean onOptionsItemSelected(MenuItem item)
{
   switch (item.getItemId())
   {
      case R.id.add_picture:
         capture_image() ;
         return true ;

      case R.id.remove_picture:
         remove_image(m_thumbnails_grid.getSelectedItemId()) ;
         return true ;

      case R.id.remove_all:
         remove_all() ;
         return true ;
   }
   return super.onOptionsItemSelected(item) ;
}

// Use the on-board camera to get a new image and store it in the
// database along with the current GPS coordinates. This is handled by a
// separate activity.
private void capture_image()
{
   startActivity(new Intent(this, ImageRecorder.class)) ;
}

// Remove the image corresponding to the selected thumbnail
private void remove_image(long thumbnail_id)
{
   if (thumbnail_id == m_thumbnails_grid.INVALID_ROW_ID) {
      Utils.notify(this, getString(R.string.select_thumbnail_msg)) ;
      return ;
   }

   long image_id  = Utils.full_picture_id(this, thumbnail_id) ;
   m_db.delete_audio(m_db.get_audio_id(image_id)) ;
   if (Utils.taken_by_me(this, image_id, getString(R.string.group_name)))
      Utils.delete_picture(this, image_id) ;
   else
      Utils.notify(this, getString(R.string.not_taken_by_me)) ;
   display_thumbnails(m_thumbnails_grid) ;
}

// Remove all the images acquired by this application
private void remove_all()
{
   int line = 0 ;
   try
   {
      String group_name = getString(R.string.group_name) ;
      String audio_file_name_pattern =
         getFilesDir().getPath() + File.separator + group_name + ".*\\.3gp" ;
      Utils.unlink_all(audio_file_name_pattern) ; ++line ;
      m_db.clear() ; ++line ;

      ContentResolver R = getContentResolver() ;
      String where_clause = Audio.Media.TITLE + " LIKE '%" + group_name + "%'" ;
      R.delete(Audio.Media.INTERNAL_CONTENT_URI, where_clause, null) ; ++line;

      // DEVNOTE: For some reason, the following means of deleting all
      // images from the media store doesn't work. It works fine for
      // audio, but not for images. As a workaround we delete all the
      // pictures one-by-one.
      /*
      where_clause = Images.Media.DESCRIPTION + " LIKE '%" + group_name + "%'" ;
      R.delete(Images.Media.EXTERNAL_CONTENT_URI, where_clause, null) ;++line;
      //*/
      Utils.delete_all_pictures(this, group_name) ;

      display_thumbnails(m_thumbnails_grid) ;++line;
   }
   catch (Exception e)
   {
      Utils.alert(this, "line ID = " + line) ;
   }
}

//------------------------- IMAGE THUMBNAILS ----------------------------

// Display the available thumbnails in the specified grid view
private void display_thumbnails(GridView G)
{
   String[] from = new String[] {Images.Thumbnails._ID} ;
   int[]    to   = new int[]    {R.id.thumbnail} ;
   G.setAdapter(new ThumbnailsAdapter(this, R.layout.thumbnail,
                                      get_thumbnails(), from, to)) ;
}

// Retrieve the available thumbnail IDs
private Cursor get_thumbnails()
{
   try
   {
      String[] columns = new String[] {
         Images.Thumbnails._ID,
      } ;
      String where_clause =
         Images.Thumbnails.KIND + "=" + Images.Thumbnails.MINI_KIND ;
      return managedQuery(Images.Thumbnails.EXTERNAL_CONTENT_URI, columns,
                          where_clause, null, null) ;
   }
   catch (android.database.sqlite.SQLiteException e)
   {
      Log.e(null, "MVN: unable to retrieve thumbnails", e) ;
      return null ;
   }
}

//------------------------ THUMBNAILS ADAPTER ---------------------------

/**
   This inner class connects the thumbnails obtained from the Android
   MediaStore to the grid view used to display the thumbnails on the main
   screen of the photo manager application.
*/
private class ThumbnailsAdapter extends SimpleCursorAdapter {

/// Our custom adapter's constructor takes the same arguments as a
/// standard SimpleCursorAdapter. The only difference between this
/// adapter and the SimpleCursorAdapter is that it uses a custom view
/// binder to bind cursor columns to corresponding UI elements.
public ThumbnailsAdapter(Context context, int layout, Cursor cursor,
                         String[] from, int[] to)
{
   super(context, layout, cursor, from, to) ;
   setViewBinder(new ViewBinder()) ;
}

/// This inner class is the binder used to bind thumbnail IDs to an image
/// view.
private class ViewBinder implements SimpleCursorAdapter.ViewBinder {

/// This method is called by SimpleCursorAdapter for "transferring" the
/// contents of each underlying data item (as pointed to by its cursor)
/// to an appropriate UI element.
///
/// In our case, the cursor holds the IDs of all the available thumbnails
/// on the phone. We want to draw these thumbnails on image views that
/// are arranged in a grid. To do this, we create the appropriate URI for
/// the thumbnail using its ID and instruct the target image view to
/// obtain its contents through that URI.
public boolean setViewValue(View V, Cursor C, int column)
{
   int i = C.getColumnIndex(Images.Thumbnails._ID) ;
   if (column == i) {
      ImageView img = (ImageView) V ;
      img.setImageURI(ContentUris.withAppendedId(
         Images.Thumbnails.EXTERNAL_CONTENT_URI, C.getInt(i))) ;
      return true ;
   }
   return false ;
}

} // end of inner class AssignmentThree.ThumbnailsAdapter.ViewBinder
} // end of inner class AssignmentThree.ThumbnailsAdapter

//-----------------------------------------------------------------------

} // end of class cs546.group7.AssignmentThree
