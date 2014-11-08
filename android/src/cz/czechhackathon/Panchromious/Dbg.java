package cz.czechhackathon.Panchromious;

public class Dbg {
    public static void i(String tag, String string) {
        if (App.isDebuggable()) {
            android.util.Log.i(tag, string);
        }
    }
    public static void e(String tag, String string) {
        if (App.isDebuggable()) {
            android.util.Log.e(tag, string);
        }
    }
    public static void d(String tag, String string) {
        if (App.isDebuggable()) {
            android.util.Log.d(tag, string);
        }
    }
    public static void v(String tag, String string) {
        if (App.isDebuggable()) {
            android.util.Log.v(tag, string);
        }
    }
    public static void w(String tag, String string) {
        if (App.isDebuggable()) {
            android.util.Log.w(tag, string);
        }
    }
}