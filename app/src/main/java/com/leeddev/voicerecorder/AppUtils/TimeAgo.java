package com.leeddev.voicerecorder.AppUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo {

    public String getTimeAgo(long duration){
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
