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
   * This file defines an abstract base class for capturing data     *
   * from different sources and storing it to a database. It is      *
   * meant to be used as a common interface for capturing images     *
   * from the camera, audio from the phone's mic and GPS coordinates *
   * from the GPS device. The captured data should then be stored to *
   * an appropriate database (e.g., MediaStore.Images.Media or       *
   * MediaStore.Audio.Media).                                        *
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
   This abstract base class provides a common interface for different
   types of recorders that capture data from some device on the phone and
   store it to an appropriate database.
*/
abstract class Recorder {

/// All recorders store an Android context so they can "reach" and
/// interact with the devices they are recording from (the sources) as
/// well as the target databases they need to store the capture data in
/// (the sinks).
private Context m_context ;

/// An accessor to allow derived classes to retrieve the current context
protected final Context getContext()
{
   return m_context ;
}

/// The constructor expects to be passed a viable Android context.
/// Derived classes should call this constructor prior to performing
/// their own initialization.
protected Recorder(Context C)
{
   m_context = C ;
}

/// This method captures data from the source device. All derived classes
/// must implement this method.
public abstract void capture() ;

/// This method stores the data captured by the capture method to the
/// target database and returns the ID of the newly added data item. All
/// derived classes must implement this method.
public abstract long store() ;

//-----------------------------------------------------------------------

} // end of class cs546.group7.Recorder
