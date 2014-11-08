package cz.czechhackathon.Panchromious.rest;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bohumil.koutsky on 08/11/14.
 */
public class RGBColor {
    public int red;
    public int green;
    public int blue;

    public RGBColor(JSONObject src) {
        try {
            red = src.getInt("red");
            green = src.getInt("green");
            blue = src.getInt("blue");
        }
        catch (JSONException e) {
            Log.e("ERROR", "missing field in json color object");
        }
    }

    public RGBColor(int r, int g, int b) {
        red = r;
        green = g;
        blue = b;
    }

    public int toInt() {
        return 0xff000000 | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff);
    }
}
