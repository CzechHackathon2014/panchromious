package cz.czechhackathon.Panchromious.rest;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bohumil.koutsky on 08/11/14.
 */
public class ColorRGBGet {
    public String name;
    public RGBColor color;
    public double distance;
    public double doubt;

    public ColorRGBGet(JSONObject json) {
        try {
            color = new RGBColor(json.getJSONObject("color"));
            distance = json.getDouble("distance");
            doubt = json.getDouble("doubt");
            name = json.getString("name");
        } catch (JSONException e) {
            Log.e("ERROR", "missing fields in json ColorRGBGet object");
        }
    }

}
