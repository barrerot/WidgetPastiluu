package com.barrerot.widgetpastiluu;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.barrerot.widgetpastiluu.util.Communicator;
import com.barrerot.widgetpastiluu.util.ToolPillbox;

import org.json.JSONException;
import org.json.JSONObject;

public class HelloWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTime(context, appWidgetManager), 1, 300000);
    }

    private class MyTime extends TimerTask {
        RemoteViews remoteViews;
        AppWidgetManager appWidgetManager;
        ComponentName thisWidget;
        DateFormat format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());

        public MyTime(Context context, AppWidgetManager appWidgetManager) {
            this.appWidgetManager = appWidgetManager;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
            thisWidget = new ComponentName(context, HelloWidget.class);
        }

        @Override
        public void run() {
            String[] lastToma = returnLastToma();
            remoteViews.setTextViewText(R.id.widget_textview1, lastToma[0]);
            remoteViews.setTextViewText(R.id.widget_textview2, lastToma[1]);
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }

        private String[] returnLastToma(){
            final String URL_GETLASTTOMAS = "http://pastillero.buluu.es/rest/get_tomas.php?last_items=10";
            String[] res = new String[2];
            String res1, res2;
            String page = null;

            Log.i("LogsAndroid", "Entre!");

            try {
                page = new Communicator().executeHttpGet(URL_GETLASTTOMAS);
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject json = null;

            try {
                json = new JSONObject(page);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ToolPillbox tool = new ToolPillbox();
            final ArrayList<String> tomasDateArray = tool.getTomasDateJson(json);
            final ArrayList<String> tomasNameArray = tool.getTomasPastillaJson(json);

            String dateTime = tomasDateArray.get(0);
            String[] auxArray = dateTime.split(" - ");

            res1 = auxArray[1];
            res2 = tomasNameArray.get(0) + "\n" + auxArray[0];

            res[0] = res1;
            res[1] = res2;

            return res;
        }

    }
}
