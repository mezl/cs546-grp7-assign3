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
   * This file defines a class for capturing audio from the gPhone's *
   * mic. The newly captured audio data is stored in the             *
   * MediaStore.Audio.Media database and its ID returned to client   *
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
   order to capture the current location using the gPhone's on-board GPS
   device. This data is then stored in the ??? database.
*/
class AudioRecorder extends Recorder {

/// The constructor expects to be passed a viable Android context
public AudioRecorder(Context C)
{
   super(C) ;
}

/// This method captures some audio from the phone's mic
@Override public void capture()
{
   // JI HYUN ==> REPLACE THIS AND THE NEXT LINE WITH AUDIO CAPTURE CODE
   Utils.notify(getContext(), "Capturing audio from mic...") ;
}

/// This method stores the audio captured from the mic to the
/// MediaStore.Audio.Media database and returns the ID of the newly added
/// audio file.
@Override public long store()
{
   // JI HYUN ==> REPLACE THIS AND NEXT TWO LINES WITH AUDIO STORING CODE
   Utils.notify(getContext(), "Storing audio in database...") ;
   return -1 ; // FIXME!
}

//-----------------------------------------------------------------------

} // end of class cs546.group7.AudioRecorder
