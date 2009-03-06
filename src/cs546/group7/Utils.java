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
   * This file contains an assortment of utility functions used by   *
   * different parts of the photo manager application.               *
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
import android.widget.Toast ;
import android.app.AlertDialog ;

// Android content provider support
import android.provider.MediaStore.Images ;
import android.provider.MediaStore.Audio ;

// Android networking support
import android.net.Uri ;

// Android database support
import android.database.Cursor ;

// Android application and OS support
import android.app.Activity ;
import android.content.Context ;
import android.content.Intent ;

// Android utilities
import android.content.ContentUris ;
import android.util.Log ;

// Java I/O support
import java.io.FileInputStream ;
import java.io.InputStream ;
import java.io.OutputStream ;
import java.io.File ;
import java.io.FilenameFilter ;

// Java utilities
import java.util.regex.* ;

//------------------------- CLASS DEFINITION ----------------------------

/**
   This class provides several handy utility functions.
*/
class Utils {

/// The photo manager's main screen passes the ID of the selected
/// thumbnail to the display screen, which, in turn, passes it on to the
/// picture and map tabs. This parameter is passed between the activities
/// using Android's intent extras mechanism. To be able to properly store
/// and retrieve this value, all the activities that need this parameter
/// must agree on a suitable key/tag to use. The following string is that
/// key.
public static final String EXTRAS_THUMBNAIL_ID = "extras_thumbnail_id" ;

//-------------------------- GPS COORDINATES ----------------------------

/// A simple pair to hold latitude and longitude
public static final class LatLong {
   public double latitude ;
   public double longitude ;

   public LatLong(double latitude, double longitude) {
      this.latitude  = latitude ;
      this.longitude = longitude ;
   }
}

//-------------------------- IMAGE UTILITIES ----------------------------

/// Return ID of full-sized image corresponding to specified thumbnail
public final static int full_picture_id(Activity A, long thumbnail_id)
{
   try
   {
      String[] columns = new String[] {
         Images.Thumbnails._ID,
         Images.Thumbnails.IMAGE_ID,
      } ;
      String where_clause = Images.Thumbnails._ID + "=" + thumbnail_id ;
      Cursor C = A.managedQuery(Images.Thumbnails.EXTERNAL_CONTENT_URI,
                                columns, where_clause, null, null) ;
      if (C.getCount() > 0) {
         C.moveToFirst() ;
         return C.getInt(C.getColumnIndex(Images.Thumbnails.IMAGE_ID)) ;
      }
   }
   catch (android.database.sqlite.SQLiteException e)
   {
      Log.e(null, "MVN: unable to retrieve thumbnail ID " + thumbnail_id, e) ;
   }
   return -1 ;
}

/// Return the thumbnail ID of the specified picture given its string URI
public final static long get_thumbnail_id(Activity A, String uri)
{
   return get_thumbnail_id(A, ContentUris.parseId(Uri.parse(uri))) ;
}

/// Return the thumbnail ID of the specified picture given its ID
public final static long get_thumbnail_id(Activity A, long image_id)
{
   try
   {
      String[] columns = new String[] {
         Images.Thumbnails._ID,
      } ;
      String where_clause = Images.Thumbnails.IMAGE_ID + "=" + image_id ;
      Cursor C = A.managedQuery(Images.Thumbnails.EXTERNAL_CONTENT_URI,
                                columns, where_clause, null, null) ;
      if (C.getCount() > 0) {
         C.moveToFirst() ;
         return C.getInt(C.getColumnIndex(Images.Thumbnails._ID)) ;
      }
   }
   catch (Exception e)
   {
      Log.e(null, "MVN: unable to get thumbnail for image " + image_id, e) ;
   }
   return -1 ;
}

/// Return the GPS coordinates corresponding to the specified image
public final static LatLong gps_coords(Activity A, int image_id)
{
   try
   {
      String[] columns = new String[] {
         Images.Media._ID,
         Images.Media.LATITUDE, Images.Media.LONGITUDE,
      } ;
      String where_clause = Images.Media._ID + "=" + image_id ;
      Cursor C = A.managedQuery(Images.Media.EXTERNAL_CONTENT_URI,
                                columns, where_clause, null, null) ;
      if (C.getCount() > 0) {
         C.moveToFirst() ;
         double lat = C.getDouble(C.getColumnIndex(Images.Media.LATITUDE)) ;
         double lon = C.getDouble(C.getColumnIndex(Images.Media.LONGITUDE)) ;
         Log.e(null, "image ID " + image_id + ": lat = " + lat
                                            + ", lon = " + lon) ;
         if (lat != 0 && lon != 0)
            return new LatLong(lat, lon) ;
      }
   }
   catch (android.database.sqlite.SQLiteException e)
   {
      Log.e(null, "MVN: unable to retrieve image ID " + image_id, e) ;
   }
   return null ;
}

/// Delete a picture and its thumbnails given its URI in string form
public final static void delete_picture(Activity A, String uri)
{
   A.getContentResolver().delete(Uri.parse(uri), null, null) ;
}

/// Delete a picture and its thumbnails given its ID
public final static void delete_picture(Activity A, long id)
{
}

/// Start the activity that displays the selected picture and allows
/// users to play back any associated audio message and place the picture
/// on a map. The DisplayScreen activity expects to know the thumbnail ID
/// of the picture to be displayed.
public final static void display_picture(Context C, long thumbnail_id)
{
   Intent I = new Intent(C, DisplayScreen.class) ;
   I.putExtra(EXTRAS_THUMBNAIL_ID, thumbnail_id) ;
   C.startActivity(I) ;
}

//-------------------------- AUDIO UTILITIES ----------------------------

/// Retrieve the name of the audio file corresponding to the specified ID
public final static String get_audio_file_name(Activity A, long audio_id)
{
   try
   {
      String[] columns = new String[] {
         Audio.Media._ID,
         Audio.Media.DATA,
      } ;
      String where_clause = Audio.Media._ID + "=" + audio_id ;
      Cursor C = A.managedQuery(Audio.Media.INTERNAL_CONTENT_URI, columns,
                                where_clause, null, null) ;
      if (C.getCount() > 0) {
         C.moveToFirst() ;
         String audio_file = C.getString(C.getColumnIndex(Audio.Media.DATA)) ;
         C.close() ;
         return audio_file ;
      }
   }
   catch (android.database.sqlite.SQLiteException e)
   {
      Log.e(null, "MVN: unable to retrieve audio ID " + audio_id, e) ;
   }
   return null ;
}

//------------------------- UI NOTIFICATIONS ----------------------------

/// A short notification message that doesn't steal focus or require any
/// specific interaction on the user's part to dismiss. It simply appears
/// briefly and fades away.
public final static void notify(Context C, String msg)
{
   Toast.makeText(C, msg, Toast.LENGTH_SHORT).show() ;
}

/// Long notification message
public final static void notify_long(Context C, String msg)
{
   Toast.makeText(C, msg, Toast.LENGTH_LONG).show() ;
}

/// Show an error box
public final static void alert(Context C, String msg)
{
   AlertDialog.Builder alert = new AlertDialog.Builder(C) ;
   alert.setMessage(msg) ;
   alert.setPositiveButton(R.string.alert_okay_label, null) ;
   alert.show() ;
}

//----------------------- FILE SYSTEM FUNCTIONS -------------------------

/// This function returns true if the specified file exists, is readable
/// and actually has some data in it; false otherwise.
public final static boolean exists(String file_name)
{
   File f = new File(file_name) ;
   return f.exists() && f.canRead() && f.length() > 0 ;
}

/// This function removes the specified file
public final static void unlink(String file_name)
{
   new File(file_name).delete() ;
}

/// Remove all files matching specified pattern
public final static void unlink_all(final String glob)
{
   final int last_sep = glob.lastIndexOf(File.separatorChar) ;

   File dir = new File(glob.substring(0, last_sep)) ;
   String[] list = dir.list(new FilenameFilter() {
         private Pattern regex = Pattern.compile(glob.substring(last_sep + 1));
         public  boolean accept(File dir, String file_name) {
            return regex.matcher(new File(file_name).getName()).matches() ;
         }
      }) ;

   for (int i = 0; i < list.length; ++i)
      unlink(list[i]) ;
}

/// Copy the named file byte-by-byte to the supplied output stream
public final static
void copy(String file_name, OutputStream out) throws Exception
{
   InputStream in = new FileInputStream(file_name) ;
   byte[] buf = new byte[512] ;
   int len ;
   while ((len = in.read(buf)) > 0)
      out.write(buf, 0, len) ;
   in.close() ;
}

//-----------------------------------------------------------------------

} // end of class cs546.group7.Utils
