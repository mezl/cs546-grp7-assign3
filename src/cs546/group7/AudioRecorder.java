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

// Android media support
import android.media.MediaRecorder ;

// Android content provider support
import android.provider.MediaStore.Audio.Media ;
import android.content.ContentResolver ;
import android.content.ContentValues ;

// Android utilities
import android.net.Uri ;
import android.content.ContentUris ;
import android.util.Log ;

// Android application and OS support
import android.content.Context ;

// Android utilities
import android.util.Log ;

// Java I/O support
import java.io.OutputStream ;
import java.io.File ;

// Java utilities
import java.text.SimpleDateFormat ;
import java.util.Date ;

//------------------------- CLASS DEFINITION ----------------------------

/**
   This class performs the necessary interfacing rituals with Android to
   capture audio from the phone and store it in the
   MediaStore.Audio.Media database.
*/
class AudioRecorder {

// The audio recorder needs a viable Android context to be able to
// retrieve the content resolver, etc.
private Context m_context ;

// This class is mostly a wrapper around the media recorder provided by
// Android.
private MediaRecorder m_recorder ;

// Once the audio tag has been recorded and stored to the
// MediaStore.Audio.Media database, this class's client will need the ID
// of the newly created recording for further access. That ID is obtained
// from the MediaStore URI for the recording.
private Uri m_uri ;

// Although the details of the audio tag are stored in the
// MediaStore.Audio.Media database, its actual contents are stored in a
// file in our application's files area.
private String m_audio_file ;

//-------------------------- INITIALIZATION -----------------------------

/// The constructor expects to be passed a viable Android context. On
/// instantiation, we create a new row in MediaStore.Audio.Media for the
/// recording. If this row cannot be created, the recording will be
/// aborted.
public AudioRecorder(Context C) throws InstantiationException
{
   m_context = C ;

   String group = C.getString(R.string.group_name) ;
   String title = group +
      new SimpleDateFormat(".yyyy-MM-dd.HH:mm:ss").format(new Date()) ;
   m_audio_file = C.getFilesDir().getPath() + File.separator + title ;

   ContentValues cv = new ContentValues(3) ;
   cv.put(Media.DATA, m_audio_file) ;
   cv.put(Media.TITLE, title) ;
   cv.put(Media.MIME_TYPE, "audio/3gpp") ;

   m_uri = C.getContentResolver().insert(Media.INTERNAL_CONTENT_URI, cv) ;
   if (m_uri == null)
      throw new InstantiationException("unable to create new audio file") ;
}

/// This method captures some audio from the phone's mic and sends the
/// output to the temp tag file.
public void start() throws Exception
{
   try
   {
      m_recorder = new MediaRecorder() ;
      m_recorder.setAudioSource(MediaRecorder.AudioSource.MIC) ;
      m_recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) ;
      m_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) ;
      m_recorder.setOutputFile(m_audio_file) ;

      m_recorder.prepare() ;
      m_recorder.start() ;
   }
   catch (Exception e)
   {
      Log.e(null, "MVN: MediaRecorder init error", e) ;
      cleanup_uri() ;
      throw e ;
   }
}

//----------------------------- CLEAN-UP --------------------------------

/// This method stops capturing audio. The temp tag file into which the
/// Android MediaRecorder has recorded is copied to the
/// MediaStore.Audio.Media database.
public void stop() throws Exception
{
   m_recorder.stop() ;
   m_recorder.release() ;
   m_recorder = null ;

   if (! Utils.exists(m_audio_file)) {
      cleanup_uri() ;
      throw new Exception("MediaRecorder didn't record anything") ;
   }
}

// From the MediaStore.Audio.Media database, delete the row that was
// created for the audio tag.
private void cleanup_uri()
{
   m_context.getContentResolver().delete(m_uri, null, null) ;
   m_uri = null ;
}

//--------------------------- AUDIO TAG ID ------------------------------

public long get_id()
{
   return (m_uri == null) ? -1 : ContentUris.parseId(m_uri) ;
}

//-----------------------------------------------------------------------

} // end of class cs546.group7.AudioRecorder
