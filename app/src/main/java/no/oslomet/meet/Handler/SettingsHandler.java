package no.oslomet.meet.Handler;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsHandler
{
    public void setStringSetting(Context context, int id, String value)
    {
        String key = context.getResources().getResourceEntryName(id);
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(key, value);
        spe.commit();
    }

    public void setBooleanSetting(Context context, int id, Boolean value)
    {
        String key = context.getResources().getResourceEntryName(id);
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putBoolean(key, value);
        spe.commit();
    }


    public String getStringSetting(Context context, int id)
    {
        String key = context.getResources().getResourceEntryName(id);
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public Boolean getBooleanSetting(Context context, int id, boolean defaultValue)
    {
        String key = context.getResources().getResourceEntryName(id);
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }
}
