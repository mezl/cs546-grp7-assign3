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
   * This file contains the main code for a photo manager applicati- *
   * on for the Android/gPhone platform. This application displays   *
   * thumbnails of all the currently available pictures and also     *
   * allows users to add new pictures using the camera. New pictures *
   * are automatically tagged with the current GPS coordinates and   *
   * may optionally be tagged with audio.                            *
   *                                                                 *
   * When a thumbnail is selected, it will be displayed in a new     *
   * screen. The user will be able to play back any associated audio *
   * and also display a map showing the GPS coordinates of where     *
   * that picture was taken.                                         *
   *                                                                 *
   *******************************************************************
*/

//----------------------- PACKAGE SPECIFICATION -------------------------

package cs546.group7 ;

//------------------------------ IMPORTS --------------------------------

// Android UI support
import android.widget.SimpleCursorAdapter ;
import android.widget.CursorAdapter ;
import android.widget.GridView ;

// Android content-provider support
import android.provider.MediaStore.Images.Thumbnails ;

// Android database support
import android.database.Cursor ;

// Android application and OS support
import android.app.Activity ;
import android.os.Bundle ;

// Android utilities
import android.content.ContentUris ;
import android.util.Log ;

//------------------------- CLASS DEFINITION ----------------------------

/**
   This class implements the main screen of a photo manager application
   that tags new pictures taken with the camera with the current GPS
   coordinates and optional audio. When a picture's thumbnail is
   selected, the entire image will be displayed and users will be given
   the ability to play back any associated audio and view the GPS
   coordinates of the picture on a map.
*/
public class AssignmentThree extends Activity {

// The main screen of the photo manager application shows thumbnails of
// all the available images in a neat grid.
private GridView m_thumbnails_grid ;

//-------------------------- INITIALIZATION -----------------------------

/**
   This method is called when the activity is first created. It is akin
   to the "main" function in normal desktop applications.
*/
@Override public void onCreate(Bundle saved_state)
{
   super.onCreate(saved_state) ;
   setContentView(R.layout.main) ;

   // Setup the thumbnails grid
   m_thumbnails_grid = (GridView) findViewById(R.id.thumbnails_grid) ;

   // Display the thumbnails of all available images
   display_thumbnails(m_thumbnails_grid) ;
}

//------------------------- IMAGE THUMBNAILS ----------------------------

// Display the available thumbnails in the specified grid view
private void display_thumbnails(GridView G)
{
   String[] from = new String[] {Thumbnails.IMAGE_ID} ;
   int[]    to   = new int[]    {R.id.thumbnail} ;
   G.setAdapter(new SimpleCursorAdapter(this, R.layout.thumbnail,
                                        get_thumbnails(), from, to)) ;
}

// Retrieve the available thumbnails
private Cursor get_thumbnails()
{
   try
   {
      String[] columns = new String[] {
         Thumbnails._ID,
         Thumbnails.DATA,
         Thumbnails.IMAGE_ID,
      } ;
      return managedQuery(Thumbnails.EXTERNAL_CONTENT_URI, columns,
                          null, null, null) ;
   }
   catch (android.database.sqlite.SQLiteException e)
   {
      Log.e(null, "MVN: unable to retrieve thumbnails", e) ;
      return null ;
   }
}

//----------------------- IMAGE CURSOR ADAPTER --------------------------

/**
   This inner class connects the thumbnails obtained from the Android
   MediaStore to the grid view used to display the thumbnails on the main
   screen of the photo manager application.
*/
/*
private class ImageCursorAdapter extends CursorAdapter {
   public ImageCursorAdapter(Context context, Cursor cursor) {
   }
} // end of inner class ImageCursorAdapter
//*/

//-----------------------------------------------------------------------

} // end of class AssignmentThree
