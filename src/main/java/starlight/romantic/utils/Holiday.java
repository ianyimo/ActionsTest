package starlight.romantic.utils;

import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author Aym
 */
public class Holiday {
    public static Boolean isFriday() {
        return getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
    }

    public static String getWeek(String date) {
        String[] weeks = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = getCalendar();
        try {
            calendar.setTime(simpleDateFormat.parse(date));
            return weeks[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        } catch (ParseException e) {
            return "";
        }
    }

    public static String getTime(String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(getCalendar().getTime());
    }

    public static String getDateOffset(int offset) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = getCalendar();
        if (offset != 0) {
            calendar.add(Calendar.DATE, offset);
        }
        return simpleDateFormat.format(calendar.getTime());
    }

    public static int getHour() {
        return getCalendar().get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute() {
        return getCalendar().get(Calendar.MINUTE);
    }

    public static Boolean isHoliday(String date) {
        String apiUrl = "http://tool.bitefu.net/jiari/";
        String result = HttpRequest.get(apiUrl, false, "d", date, "back", "json", "info", 1).body();
        JSONObject obj = JSONObject.parseObject(result);
        if (!checkData(obj)) {
            return false;
        }
        return obj.getInteger("type") != 0;
    }

    private static Boolean checkData(JSONObject jsonObject) {
        String key = "status";
        if (jsonObject.get(key) == null) {
            return false;
        }
        return jsonObject.getInteger(key) == 1;
    }

    private static Calendar getCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
    }
}
