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
   * When a thumbnail is selected, it will be displayed in a new     *
   * screen. The user will be able to play back any associated audio *
   * and also display a map showing the GPS coordinates of where     *
   * that picture was taken. Thus, the second screen of the photo    *
   * manager application consists of two tabs: one for showing the   *
   * picture and the other for showing the GPS coordinates of where  *
   * that picture was taken.                                         *
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
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

//---------------------- DISPLAY SCREEN MAP TAB -------------------------

/**
   This class is part of the display screen of a photo manager
   application that tags new pictures taken with the camera with the
   current GPS coordinates and optional audio. When a picture's thumbnail
   is selected, the entire image will be displayed and users will be
   given the ability to play back any associated audio and view the GPS
   coordinates of the picture on a map.

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
	
	MapView mv = null;
	MapController mc = null;
	GeoPoint gp = null;
	Location location = null;
	
@Override public void onCreate(Bundle saved_state)
{
   super.onCreate(saved_state) ;

   int image_id = Utils.full_picture_id(this,
      getIntent().getExtras().getLong(Utils.EXTRAS_THUMBNAIL_ID)) ;

   Utils.LatLong gps = Utils.gps_coords(this, image_id) ;
   if (gps == null)
      setContentView(R.layout.map_empty_tab) ;
   else {
      setContentView(R.layout.map_tab) ;
      GoogleMapDisplay(gps);
      }
}
void GoogleMapDisplay(Utils.LatLong gps) {
	//if(gps.latitude != 180 && gps.longitude != 180)
	//{
		mv = (MapView) findViewById(R.id.map);
		gp = new GeoPoint((int) (gps.latitude * 1000000), (int) (gps.longitude * 1000000));
		try {
			mv.setTraffic(false);
			mv.setSatellite(true);//false);
			mv.setStreetView(false);//true);
			mc = mv.getController();
			mc.setCenter(gp);
			mc.setZoom(15);
	
			// Add a location mark
			LocOverlay myLocationOverlay = new LocOverlay();
			List<Overlay> list = mv.getOverlays();
			list.add(myLocationOverlay);
		}
		catch (RuntimeException e) {
			//
		}
	//}
//	else {
		// Put an alert message here
//	}
}

/*protected boolean isRouteDisplayed() {
	// TODO Auto-generated method stub
	return false;
}*/

//void CalcLatAndLon(Location location) {
	
//}
/* Class overload draw method plots a marker on the map */
protected class LocOverlay extends com.google.android.maps.Overlay {
	
	@Override
	public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
		Paint paint = new Paint();
		
		super.draw(canvas, mv, shadow);
		// Converts lat/lon-Point to coordinates on the screen.
		Point screenCoordsObj = new Point();
		mv.getProjection().toPixels(gp, screenCoordsObj);
		
		paint.setStrokeWidth(1);
		paint.setARGB(255, 255, 255, 255);
		paint.setStyle(Paint.Style.STROKE);
		
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
		
		canvas.drawBitmap(bmp, screenCoordsObj.x, screenCoordsObj.y, paint);
		return true;
	}
}
//}

//----------------------- MAPS API REQUIREMENTS -------------------------

// All apps that use the Google Maps API are required to implement this
// routine.
@Override protected boolean isRouteDisplayed()
{
   return false ;
}

//-----------------------------------------------------------------------

} // end of class cs546.group7.MapTab
