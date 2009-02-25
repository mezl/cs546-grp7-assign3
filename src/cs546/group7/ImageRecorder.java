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

// Android application and OS support
import android.content.Context ;

//------------------------- CLASS DEFINITION ----------------------------

/**
   This class performs the necessary interfacing rituals with Android in
   order to capture a new picture using the gPhone's on-board camera and
   store it to the MediaStore.Images.Media database.
*/
class ImageRecorder extends Recorder {

/// The constructor expects to be passed a viable Android context
public ImageRecorder(Context C)
{
   super(C) ;
}

/// This method captures an image from the phone's camera
@Override public void capture()
{
   // KAI ==> REPLACE THIS AND THE NEXT LINE WITH CAMERA CAPTURE CODE
   Utils.notify(getContext(), "Capturing image from camera...") ;
}

/// This method stores the image captured from the camera to the
/// MediaStore.Images.Media database and returns the ID of the newly
/// added picture.
@Override public long store()
{
   // KAI ==> REPLACE THIS AND THE NEXT TWO LINES WITH IMAGE STORING CODE
   Utils.notify(getContext(), "Storing new picture in database...") ;
   return -1 ; // FIXME!
}

//-----------------------------------------------------------------------

} // end of class cs546.group7.ImageRecorder
