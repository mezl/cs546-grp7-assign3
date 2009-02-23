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

// Android application and OS support
import android.app.Activity ;
import android.os.Bundle ;

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

//-------------------------- INITIALIZATION -----------------------------

/**
   This method is called when the activity is first created. It is akin
   to the "main" function in normal desktop applications.
*/
@Override public void onCreate(Bundle saved_state)
{
   super.onCreate(saved_state) ;
   setContentView(R.layout.main) ;
}

//-----------------------------------------------------------------------

} // end of class AssignmentThree

