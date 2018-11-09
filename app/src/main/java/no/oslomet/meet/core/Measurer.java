package no.oslomet.meet.core;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class Measurer
{
    private Context context;
    public Measurer(Context context)
    {
        this.context = context;
    }

    public int get_DisplayHeight(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static float convertPixelsToDp(float px){

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();

        float dp = px / (metrics.densityDpi / 160f);

        return Math.round(dp);

    }

    public int convertDpToPx(int dp){
        return Math.round(dp*(context.getResources().getDisplayMetrics().xdpi/DisplayMetrics.DENSITY_DEFAULT));
    }



    public int convertPxToDp(int px){
        return Math.round(px/(Resources.getSystem().getDisplayMetrics().xdpi/DisplayMetrics.DENSITY_DEFAULT));
    }

}
