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
   * This file contains the code for the display screen of a photo   *
   * manager application that presents users the thumbnails of all   *
   * the currently available pictures and allows them to add new     *
   * pictures using the camera. New pictures are automatically       *
   * tagged with the current GPS coordinates and may optionally be   *
   * tagged with audio.                                              *
   *                                                                 *
   * When a thumbnail is selected, it will be displayed in a new     *
   * screen. The user will be able to play back any associated audio *
   * and also display a map showing the GPS coordinates of where     *
   * that picture was taken. Thus, the second screen of the photo    *
   * manager application consists of two tabs: one for showing the   *
   * picture and the other for showing the GPS coordinates of where  *
   * that picture was taken.                                         *
   *                                                                 *
   * This file contains the code for the top-level tab that houses   *
   * the two "sub-tabs" of the application's second screen.          *
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
import android.widget.TabHost ;

import android.content.Intent ;

// Android application and OS support
import android.app.TabActivity ;
import android.os.Bundle ;

//------------------ APPLICATION'S SECONDARY SCREEN ---------------------

/**
   This class is part of the display screen of a photo manager
   application that tags new pictures taken with the camera with the
   current GPS coordinates and optional audio. When a picture's thumbnail
   is selected, the entire image will be displayed and users will be
   given the ability to play back any associated audio and view the GPS
   coordinates of the picture on a map.

   This class implements the top-level tab of the display screen that
   contains the two subtabs, i.e., the picture and map tabs.
*/
public class DisplayScreen extends TabActivity {

/// The photo manager's main screen passes in the ID of the selected
/// thumbnail to this screen using the intent extras mechanism provided
/// by Android. To be able to properly store and retrieve this value,
/// both screens need to agree on a suitable key/tag to use. The
/// following string is that key.
public static final String EXTRAS_THUMBNAIL_ID = "extras_thumbnail_id" ;

//-------------------------- INITIALIZATION -----------------------------

/**
   This method is called when the activity is first created. On the photo
   manager application's second screen, we have two tabs: one for
   displaying the full-sized version of the thumbnailed image selected on
   the first (i.e., main) screen and another for showing the map of where
   that picture was acquired.

   For the top-level UI element that houses these tabs (i.e., this
   class), we simply create and add the subtabs to this one.
*/
@Override protected void onCreate(Bundle saved_state)
{
   super.onCreate(saved_state) ;

   // Retrieve the ID of the selected thumbnail from the extras bundle
   Bundle extras = getIntent().getExtras() ;
   long id = extras.getLong(EXTRAS_THUMBNAIL_ID) ;

   // Setup the picture and map tabs
   final TabHost H = getTabHost() ;
   setup_picture_tab(H, id) ;
   setup_map_tab(H, id) ;
}

private void setup_picture_tab(final TabHost H, long thumbnail_id)
{
   Intent I = new Intent(this, PictureTab.class) ;
   I.putExtra(PictureTab.EXTRAS_THUMBNAIL_ID, thumbnail_id) ;

   TabHost.TabSpec tab = H.newTabSpec("picture_tab") ;
   tab.setIndicator(getString(R.string.picture_tab_label),
                    getResources().getDrawable(R.drawable.picture_tab_icon)) ;
   tab.setContent(I) ;
   H.addTab(tab) ;
}

private void setup_map_tab(final TabHost H, long thumbnail_id)
{
   Intent I = new Intent(this, MapTab.class) ;
   //I.putExtra(MapTab.EXTRAS_THUMBNAIL_ID, thumbnail_id) ;

   TabHost.TabSpec tab = H.newTabSpec("map_tab") ;
   tab.setIndicator(getString(R.string.map_tab_label),
                    getResources().getDrawable(R.drawable.map_tab_icon)) ;
   tab.setContent(I) ;
   H.addTab(tab) ;
}

//-----------------------------------------------------------------------

} // end of class cs546.group7.DisplayScreen
