package com.leeddev.recorder.AppUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class utils {
    public static String formateMilliSeccond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }
    public static String getTimeAgo(long duration){
        Date now = new Date();
        long longNow = now.getTime();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(longNow-duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(longNow-duration);
        long hours = TimeUnit.MILLISECONDS.toHours(longNow-duration);
        long days = TimeUnit.MILLISECONDS.toDays(longNow-duration);

        if(seconds<60)
        {return  "just now";}

        else if (minutes==1)
        {
            return "a minute ago";
        }

        else if (minutes>1&&minutes<60)
        {
            return minutes+" minutes ago";
        }

        else if (hours==1)
        {
            return "an hour ago";
        }
        else if (hours>1&&hours<24)
        {
            return hours + " hours ago";
        }

        else if (days==1)
        {
            return "a day ago";
        }
        else {
            return days + " days ago";
        }
//conditions ended
    }
}
