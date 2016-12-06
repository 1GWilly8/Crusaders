package hfad.com.crusaders;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by WilliamGay on 11/18/16.
 */
public class BuildMode {
    public static void buildTime(Context context, int keepYN, int wallsYN, int troopsYN){
        if(keepYN == 0){
            Toast.makeText(context, "Time to place your Keep. Do so wisely!",
                    Toast.LENGTH_LONG).show();
        }
        if(wallsYN == 5){

        }
        if(troopsYN == 3){
            Toast.makeText(context, "And most importantly, the troops!",
                    Toast.LENGTH_LONG).show();
        }
    }
}


/*
    final Context context = getApplicationContext();

    final Thread waitForBuild = new Thread(){
        public void run() {
            try {
                if (keepYN == 0) {
                    Toast.makeText(context, "Time to place your Keep. Do so wisely!",
                            Toast.LENGTH_LONG).show();
                    Log.v("**********", "LLLLLLLLL");
                    Thread.sleep(1000);
                } else if(keepYN == 1 && wallsYN == 5){
                    Toast.makeText(context, "Now for the walls",
                            Toast.LENGTH_LONG).show();
                }else if(keepYN == 1 && wallsYN == 0 && troopsYN == 3){
                    Toast.makeText(context, "And most importantly, the troops!",
                            Toast.LENGTH_LONG).show();
                }
            }catch(InterruptedException e){
                Log.v("%%%%%", "e");
            }
        }
    };
waitForBuild.start();*/
