package com.avatlantik.smartmilking.logs;


import android.app.Activity;

public class ReportingActivity extends Activity
{
    private static Activity sForegroundInstance;

    public static Activity getForegroundInstance()
    {
        return sForegroundInstance;
    }

    protected void onPause()
    {
        super.onPause();
        sForegroundInstance = null;
    }

    protected void onResume()
    {
        super.onResume();
        sForegroundInstance = this;
    }
}
