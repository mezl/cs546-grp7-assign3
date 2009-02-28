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

// Android services
import android.media.MediaScannerConnection ;

// Android networking support
import android.net.Uri ;

// Android application and OS support
import android.content.Context ;
import android.content.Intent ;

//------------------------- CLASS DEFINITION ----------------------------

/**
   This class provides several handy utility functions.
*/
class Utils {

//------------------------- UI NOTIFICATIONS ----------------------------

/// A short notification message that doesn't steal focus or require any
/// specific interaction on the user's part to dismiss. It simply appears
/// briefly and fades away.
public final static void notify(Context C, String msg)
{
   Toast.makeText(C, msg, Toast.LENGTH_SHORT).show() ;
}

/// Show an error box
public final static void alert(Context C, String msg)
{
   AlertDialog.Builder alert = new AlertDialog.Builder(C) ;
   alert.setMessage(msg) ;
   alert.setPositiveButton(R.string.alert_okay_label, null) ;
   alert.show() ;
}

//----------------------- ANDROID MEDIA HELPERS -------------------------

/// This function adds a thumbnail for a newly created image.
///
/// DEVNOTE: Unfortunately, this does not work! For some reason, we get
/// an IllegalStateException due to the connection to the media scanner
/// service never being made.
public final static void add_thumbnail(Context C, Uri uri)
{
   MediaScannerConnection media_scanner = new MediaScannerConnection(C, null) ;
   media_scanner.connect() ;
   media_scanner.scanFile(uri.getEncodedPath(), null) ;
   media_scanner.disconnect() ;
}

/// This function attempts to work around the previous one by rescanning
/// the entire SD card, which ought to result in creating thumbnails for
/// any new images in the MediaStore.Images.Media database that might
/// have been added recently.
///
/// DEVNOTE: This does not work either. Android sucks ass.
public final static void rescan_sdcard(Context C, Uri uri)
{
   Intent I = new Intent(Intent.ACTION_MEDIA_MOUNTED) ;
   I.setData(Uri.parse("file:///sdcard")) ;
   I.putExtra("read-only", false) ;
   C.sendBroadcast(I) ;
}

//-----------------------------------------------------------------------

} // end of class cs546.group7.Utils
