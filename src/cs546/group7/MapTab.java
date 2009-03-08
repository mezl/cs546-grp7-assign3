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
   * This file contains the code for the map tab of the display      *
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
   * This file contains the code for the map tab, which shows a map  *
   * of the vicinity of where the picture was taken with a pin on    *
   * the map to indicate the exact spot.                             *
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

// Google maps API
import com.google.android.maps.GeoPoint ;
import com.google.android.maps.MapActivity ;
import com.google.android.maps.MapController ;
import com.google.android.maps.MapView ;
import com.google.android.maps.Overlay ;

// Android GPS support
import android.location.Location ;

// Android graphics support
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

// Android application and OS support
import android.os.Bundle;

// Android utilities
import android.util.Log ;

//---------------------- DISPLAY SCREEN MAP TAB -------------------------

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

   This class implements the tab that shows the map of the vicinity of
   where the picture was taken. A pin is drawn on the exact spot where
   the picture was taken.
*/
public class MapTab extends MapActivity {

//-------------------------- INITIALIZATION -----------------------------

/**
   This method is called when the activity is first created. It is akin
   to the "main" function in normal desktop applications.
*/
@Override public void onCreate(Bundle saved_state)
{
   super.onCreate(saved_state) ;

   long image_id = getIntent().getExtras().getLong(Utils.EXTRAS_PICTURE_ID) ;
   Utils.LatLong gps = Utils.gps_coords(this, image_id) ;
   if (gps == null) // picture displayed on picture tab has no location data
      setContentView(R.layout.map_empty_tab) ;
   else
   {
      setContentView(R.layout.map_tab) ;
      setup_map(gps);
   }
}

void setup_map(Utils.LatLong gps)
{
   try
   {
      MapView M = (MapView) findViewById(R.id.map);
      M.setSatellite(true);

      GeoPoint picture_location =
         new GeoPoint((int) (gps.latitude  * 1000000),
                      (int) (gps.longitude * 1000000));
      M.getOverlays().add(new LocationMarker(picture_location));

      MapController C = M.getController();
      C.setCenter(picture_location);
      C.setZoom(15);
   }
   catch (Exception e)
   {
      Log.e(null, "MVN: map setup bombed", e) ;
   }
}

//-------------------------- LOCATION MARKER ----------------------------

/// This inner class draws a pin on the specified location
private class LocationMarker extends Overlay {

// Location to draw the marker on
private GeoPoint m_location ;

LocationMarker(GeoPoint location)
{
   m_location = location ;
}

/// This method implements the drawing routine that is responsible for
/// putting the location marker's pin on the location this marker is
/// meant to highlight.
@Override
public boolean draw(Canvas canvas, MapView map_view, boolean shadow, long when)
{
   super.draw(canvas, map_view, shadow);

   Point location_screen = new Point() ; // m_location in screen coordinates
   map_view.getProjection().toPixels(m_location, location_screen);

   Paint paint = new Paint();
   paint.setStrokeWidth(1);
   paint.setARGB(255, 255, 255, 255);
   paint.setStyle(Paint.Style.STROKE);

   Bitmap bmp =
      BitmapFactory.decodeResource(getResources(), R.drawable.marker);
   canvas.drawBitmap(bmp, location_screen.x, location_screen.y, paint);
   return true;
}

} // end of inner class MapTab.LocationMarker

//----------------------- MAPS API REQUIREMENTS -------------------------

// All apps that use the Google Maps API are required to implement this
// routine.
@Override protected boolean isRouteDisplayed()
{
   return false ;
}

//-----------------------------------------------------------------------

} // end of class cs546.group7.MapTab
