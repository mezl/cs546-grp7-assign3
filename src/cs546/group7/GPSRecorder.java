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
   * This file defines a class for capturing GPS coordinates from    *
   * the gPhone. These coordinates may, optionally be drawn on the   *
   * specified UI widgets (text views).                              *
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

// Android GPS support
import android.location.* ;

// Android UI support
import android.widget.TextView ;

// Android application and OS support
import android.content.Context ;
import android.os.Bundle ;

// Android utilities
import android.util.Log ;

// Java utilities
import java.text.DecimalFormat ;
import java.text.SimpleDateFormat ;
import java.util.Date ;

//------------------------- CLASS DEFINITION ----------------------------

/**
   This class performs the necessary interfacing rituals with Android to
   capture GPS coordinates. As an optional step, if two text widgets are
   specified by some client class, then the captured coordinates are
   painted on those widgets to allow users to see what the current
   coordinates are.
*/
class GPSRecorder {

/// As seems to be the case for almost everything Android, we need a
/// viable context to be able to interface with various parts of the
/// system.
private Context m_context ;

/// Depending on where the user currently is, s/he may or may not have
/// access to GPS data. Thus, we set up a listener when the application
/// starts up to monitor changes/updates to the GPS system.
private LocationListener m_listener ;

/// The GPS listener monitors updates to the GPS coordinates with the
/// following frequency and distance resolution.
public final static int UPDATE_FREQUENCY = 60000 ; // once every minute
public final static int UPDATE_DISTANCE  = 100 ;   // every hundred meters

/// When new GPS coordinates become available, we store them to the
/// following object.
private Location m_location ;

/// This class can draw the current GPS coordinates every time they
/// change to the specified UI elements. Clients must directly set and
/// unset these two public members.
public TextView m_lat ;
public TextView m_lon ;

//-------------------------- INITIALIZATION -----------------------------

/// The constructor expects to be passed a viable Android context. On
/// instantiation, we create a new row in MediaStore.Audio.Media for the
/// recording. If this row cannot be created, the recording will be
/// aborted.
public GPSRecorder(Context C)
{
   m_context = C ;
   setup_listener(C) ;
}

private void setup_listener(Context C)
{
   try
   {
      LocationManager M =
         (LocationManager) C.getSystemService(Context.LOCATION_SERVICE) ;

      Criteria criteria = new Criteria() ;
      criteria.setAccuracy(Criteria.ACCURACY_FINE) ;
      criteria.setAltitudeRequired(false) ;
      criteria.setBearingRequired(false) ;
      criteria.setSpeedRequired(false) ;
      criteria.setCostAllowed(false) ;
      criteria.setPowerRequirement(Criteria.POWER_MEDIUM) ;

      String provider = M.getBestProvider(criteria, true) ;
      if (provider == null) {
         Log.e(null, "MVN: unable to obtain a GPS location provider") ;
         Utils.notify(C, C.getString(R.string.no_gps_provider)) ;
         return ;
      }
      m_listener = new Listener() ;
      M.requestLocationUpdates(provider, UPDATE_FREQUENCY, UPDATE_DISTANCE,
                               m_listener) ;
   }
   catch (Exception e)
   {
      Log.e(null, "MVN: location manager fiasco", e) ;
      shutdown_listener(C) ;
      m_location = null ;
   }
}

//--------------------------- THE LISTENER ------------------------------

// This inner class implements a listener that responds to GPS events
private class Listener implements LocationListener {
   public void onProviderEnabled(String provider) {}
   public void onProviderDisabled(String provider){}
   public void onStatusChanged(String provider, int status, Bundle extras) {}

   public void onLocationChanged(Location L) {
      m_location = L ;
      show_location(m_context) ;
   }
} // end of inner class GPSRecorder.Listener

//----------------------------- CLEAN-UP --------------------------------

public void shutdown_listener(Context C)
{
   if (m_listener == null) // nothing to shutdown
      return ;

   LocationManager M =
      (LocationManager) C.getSystemService(Context.LOCATION_SERVICE) ;
   M.removeUpdates(m_listener) ;
   m_listener = null ;
}

//------------------------------ HELPERS --------------------------------

// Returns true if the last known location is much older than the current
// time.
public boolean stale(Date now)
{
   return (now.getTime() - m_location.getTime()) > 300000 ; // 5 mins
}

public double latitude()
{
   return m_location.getLatitude() ;
}

public double longitude()
{
   return m_location.getLongitude() ;
}

// Show the specified GPS coordinates on the UI
public void show_location(Context C)
{
   if (m_lat == null || m_lon == null)
      return ;

   if (m_location == null)
   {
      String unknown = C.getString(R.string.preview_gps_unknown) ;
      m_lat.setText(unknown) ;
      m_lon.setText(unknown) ;
   }
   else
   {
      DecimalFormat format = new DecimalFormat("###.###") ;
      m_lat.setText(format.format(m_location.getLatitude())) ;
      m_lon.setText(format.format(m_location.getLongitude())) ;
   }
}

//-----------------------------------------------------------------------

} // end of class cs546.group7.AudioRecorder
