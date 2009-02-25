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
import android.view.Menu ;
import android.view.MenuItem ;

// Android application and OS support
import android.app.Activity ;
import android.os.Bundle ;

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

//-------------------------- INITIALIZATION -----------------------------

/**
   This method is called when the activity is first created. It is akin
   to the "main" function in normal desktop applications.
*/
@Override public void onCreate(Bundle saved_state)
{
   super.onCreate(saved_state) ;
   setContentView(R.layout.picture_tab) ;
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
   Utils.notify(this, "Recording audio tag...") ;
}

//-----------------------------------------------------------------------

} // end of class cs546.group7.PictureTab
