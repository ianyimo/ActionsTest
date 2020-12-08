package starlight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import starlight.romantic.Schedule;
import starlight.romantic.utils.Holiday;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Aym
 */
public class Run {
    private static final Logger LOGGER = LoggerFactory.getLogger(Run.class);
    private static final Pattern PATTERN = Pattern.compile("[\t\r\n]");

    public static void main(String[] args) {
        if (args.length == 0) {
            LOGGER.warn("Parameter is empty");
        }
        wob(args[0], args[1], args[2]);
        // punchCard(args[0]);
    }

    private static void wob(String key, String uid, String code) {
        if (Holiday.getHour() > 2 || Holiday.getMinute() < 30) {
            return;
        }
        new Schedule(key).wobSign(uid, code);
    }

    private static void punchCard(String key) {
        int hour = Holiday.getHour();
        if (hour < 8 || hour > 23) {
            return;
        }
        String today = Holiday.getDateOffset(0);
        String tomorrow = Holiday.getDateOffset(1);
        Schedule schedule = new Schedule(key);
        if (hour < 17) {
            schedule.work(today, tomorrow);
            return;
        }
        if (Holiday.getMinute() >= 30) {
            schedule.offWork(today, tomorrow);
        }
    }

    public static String replaceStr(String str) {
        if (str != null) {
            Matcher matcher = PATTERN.matcher(str);
            return matcher.replaceAll(" ");
        }
        return "";
    }
}
