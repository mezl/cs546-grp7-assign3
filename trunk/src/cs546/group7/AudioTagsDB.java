/*
   *******************************************************************
   *                                                                 *
   *    CSCI-546, Spring 2009 Assignment II Solution by Group #7     *
   *                                                                 *
   *    Group Members: Chang, Chin-Kai      chinkaic@usc.edu         *
   *                   Moon, Ji Hyun        jihyunmo@usc.edu         *
   *                   Patlolla, Avinash    patlolla@usc.edu         *
   *                   Viswanathan, Manu    mviswana@usc.edu         *
   *                                                                 *
   *******************************************************************
   *                                                                 *
   * This file defines a class to encapsulate the audio tags         *
   * database. This database maps image IDs to corresponding audio   *
   * tag IDs for the photo manager application developed for the 3rd *
   * assignment of CSCI-546.                                         *
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

// Android content-provider support
import android.provider.MediaStore.Audio ;

// Android content support
import android.content.ContentUris ;
import android.content.ContentValues ;
import android.content.Context ;

// Android database support
import android.database.Cursor ;
import android.database.SQLException ;
import android.database.sqlite.SQLiteDatabase ;
import android.database.sqlite.SQLiteOpenHelper ;

// Android application support
import android.app.Activity ;

// Android utilities
import android.util.Log ;

//------------------------- CLASS DEFINITION ----------------------------

/**
   This class encapsulates access to the photo manager application's
   audio tags database that maps image IDs to the IDs of their
   corresponding audio tags.
*/
public class AudioTagsDB {

/// The column names in the various tables making up the database
public static final String _ID      = "_id" ;
public static final String IMAGE_ID = "image_id" ;
public static final String AUDIO_ID = "audio_id" ;

// Database name, etc.
private static final String DB_NAME    = "cs546_grp7_audio_tags.db" ;
private static final String DB_TAG     = "cs546_grp7_audio_tags" ;
private static final int    DB_VERSION = 2 ;

// This application's database stores the IDs of available images and
// their corresponding audio tags.
public static final String AUDIO_TAGS_TABLE = "audio_tags" ;

// The SQL statements used to create the above table
private static final String CREATE_AUDIO_TAGS_TABLE =
    "CREATE TABLE " + AUDIO_TAGS_TABLE + "("
   + _ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
   + IMAGE_ID  + " LONG NOT NULL, "
   + AUDIO_ID  + " LONG NOT NULL) ; " ;

// This inner class encapsulates opening the SQLite database
private static class DBHelper extends SQLiteOpenHelper {
   DBHelper(Context C) {super(C, DB_NAME, null, DB_VERSION) ;}

   @Override public void onCreate(SQLiteDatabase db) {
      db.execSQL(CREATE_AUDIO_TAGS_TABLE) ;
   }

   @Override public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
      Log.w(DB_TAG, "Upgrading database from version " + oldVer +
            " to " + newVer + ", which will destroy all old data") ;
      clear(db) ;
      onCreate(db) ;
   }

   public void clear(SQLiteDatabase db) {
      db.execSQL("DROP TABLE IF EXISTS " + AUDIO_TAGS_TABLE) ;
   }
}

// This class interfaces with the SQLite database that comes with Android
private DBHelper m_db_helper ;
private SQLiteDatabase m_db ;

// Android's global context about the application environment
private final Context m_context ;

//---------------------- DATABASE INITIALIZATION ------------------------

// Constructor
public AudioTagsDB(Context context)
{
   m_context = context ;
}

/**
   Open the search results database. If it cannot be opened, try to
   create a new instance of the database. If it cannot be created, throw
   an exception to signal the failure.

   @return this (self reference, allowing this to be chained in an
           initialization call)
   @throws SQLException if the database could be neither opened or created
*/
public AudioTagsDB open() throws SQLException
{
   m_db_helper = new DBHelper(m_context) ;
   m_db = m_db_helper.getWritableDatabase() ;
   return this ;
}

//------------------------ DATABASE OPERATIONS --------------------------

/// Return the ID of the audio tag corresponding to the specified image
public long get_audio_id(long image_id)
{
   Cursor c = m_db.query(true, AUDIO_TAGS_TABLE, new String[] {AUDIO_ID},
                         IMAGE_ID + "=" + image_id,
                         null, null, null, null, null) ;
   ((Activity) m_context).startManagingCursor(c) ;
   if (c == null || c.getCount() <= 0) // specified image not in database
      return -1 ;

   c.moveToFirst() ;
   return c.getLong(c.getColumnIndex(AUDIO_ID)) ;
}

/// Updates the audio tag ID corresponding to the specfied image
public void update(long image_id, long new_audio_id)
{
   String[] columns    = new String[] {_ID, IMAGE_ID, AUDIO_ID} ;
   String where_clause = IMAGE_ID + " = " + image_id ;

   Cursor c = m_db.query(true, AUDIO_TAGS_TABLE, columns, where_clause,
                         null, null, null, null, null) ;
   ((Activity) m_context).startManagingCursor(c) ;
   if (c == null || c.getCount() == 0) // insert new entry
   {
      ContentValues v = new ContentValues(2) ;
      v.put(IMAGE_ID, image_id) ;
      v.put(AUDIO_ID, new_audio_id) ;
      m_db.insert(AUDIO_TAGS_TABLE, null, v) ;
   }
   else // update existing entry
   {
      c.moveToFirst() ;

      delete_audio_from_system(c.getLong(c.getColumnIndex(AUDIO_ID))) ;

      ContentValues v = new ContentValues(2) ;
      v.put(IMAGE_ID, image_id) ;
      v.put(AUDIO_ID, new_audio_id) ;
      m_db.update(AUDIO_TAGS_TABLE, v,
                  _ID + "=" + c.getLong(c.getColumnIndex(_ID)), null) ;
   }
}

/// Delete the specified audio tag from MediaStore.Audio.Media. But
/// before doing that, get the name of the audio file and delete it.
private void delete_audio_from_system(long audio_id)
{
   Utils.unlink(Utils.get_audio_file_name((Activity) m_context, audio_id)) ;
   m_context.getContentResolver().delete(ContentUris.withAppendedId(
      Audio.Media.INTERNAL_CONTENT_URI, audio_id), null, null) ;
}

/// Removes the specified audio ID and its corresponding image ID from
/// the image-audio mappings database maintained by our photo manager
/// application.
public void delete_audio(long audio_id)
{
   if (audio_id <= 0) // invalid ID
      return ;
   delete_audio_from_system(audio_id) ;
   m_db.delete(AUDIO_TAGS_TABLE, AUDIO_ID + "=" + audio_id, null) ;
}

//------------------------- DATABASE CLEAN-UP ---------------------------

public void clear()
{
   m_db_helper.clear(m_db) ;
   m_db_helper.onCreate(m_db) ;
}

public void close()
{
   m_db_helper.close() ;
}

//-----------------------------------------------------------------------

} // end of class NotesDbAdapter
