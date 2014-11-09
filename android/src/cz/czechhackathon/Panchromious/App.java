package cz.czechhackathon.Panchromious;

/**
 * Created by bohumil.koutsky on 08/11/14.
 */


import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.goebl.david.Webb;

import java.util.Locale;

public class App extends Application {

    public static Context context;
    protected static boolean debuggable;
    protected static String langCode;

    //public static final String API = "http://private-862ac-panchromatious.apiary-mock.com/api";
    public static final String API = "http://panchromious.herokuapp.com/api";

    public static final String BT_NAME = "Panchrom.io";

    public void onCreate(){
        App.context = getApplicationContext();
        App.debuggable = (App.context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        App.langCode = Locale.getDefault().getLanguage();


    }

    public static Context getAppContext() {
        return context;
    }

    public static boolean isDebuggable() {
        return App.debuggable;
    }

    public static boolean isPortrait() {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        return rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180;
    }

    public static void toast(String text, int length) {
        Toast.makeText(context, text, length).show();
    }

    public static void notImplemented() {
        toast("Not Yet Implemented", Toast.LENGTH_SHORT);
    }
}

