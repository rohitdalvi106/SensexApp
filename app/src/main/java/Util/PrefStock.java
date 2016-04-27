package Util;

import android.app.Activity;
import android.content.SharedPreferences;


public class PrefStock {

    SharedPreferences preferencesstock;
    public PrefStock(Activity activity){
        preferencesstock = activity.getPreferences(Activity.MODE_PRIVATE);


    }

    public String getSymbol(){
        return preferencesstock.getString("symbol", "TSLA");
    }

    public void setSymbol(String symbol)
    {
        preferencesstock.edit().putString("symbol", symbol).commit();
    }
}
