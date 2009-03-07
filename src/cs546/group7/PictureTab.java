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
   * When a thumbnail is selected or when a new image is captured    *
   * with the camera, it will be displayed in a new screen. The user *
   * will be able to play back any associated audio and also display *
   * a map showing the GPS coordinates of where that picture was     *
   * taken.                                                          *
   *                                                                 *
   * Thus, the second screen of the photo manager application        *
   * consists of two tabs: one for showing the picture and the other *
   * for showing the GPS coordinates of where that picture was       *
   * taken.                                                          *
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

// Android graphics support
import android.graphics.BitmapFactory ;
import android.graphics.Bitmap ;

// Android content-provider support
import android.provider.MediaStore.Images.Media ;

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
   application that tags new or old pictures on the gPhone taken with
   audio. Additionally, new pictures acquired through this application
   will be tagged with the GPS coordinates of the current location.

   When a picture's thumbnail is selected on the main screen or when a
   new image is captured with the camera, the application will take users
   to the display screen, which has two tabs: one for showing the
   full-sized version of the selected (or captured) image and the other
   for viewing the GPS coordinates of that picture (if available) on a
   map.

   This class implements the picture tab of the display screen. It shows
   the full-sized image corresponding to the selected (or newly captured)
   image.
*/
public class PictureTab extends Activity {

//-------------------------- INITIALIZATION -----------------------------

/**
   This method is called when the activity is first created. It is akin
   to the "main" function in normal desktop applications. On the picture
   tab of the display screen, we need to retrieve the ID of the image
   that was either selected by the user from the photo manager app's main
   screen or was captured from the camera and display that in the tab's
   full-picture image view.
*/
@Override public void onCreate(Bundle saved_state)
{
   super.onCreate(saved_state) ;
   setContentView(R.layout.picture_tab) ;

   long id = getIntent().getExtras().getLong(Utils.EXTRAS_PICTURE_ID) ;
   display_picture(id) ;
}

//-------------------------- PICTURE DISPLAY ----------------------------

private void display_picture(long id)
{
   try
   {
      InputStream is = getContentResolver().openInputStream(
         ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, id)) ;

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
      Log.e(null, "MVN: unable to read image ID " + id, e) ;
   }
   catch (IOException e)
   {
      Log.e(null, "MVN: unable to close bitmap input stream", e) ;
   }
}

//-----------------------------------------------------------------------

} // end of class cs546.group7.PictureTab
