package starlight.romantic;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import starlight.romantic.utils.Holiday;
import starlight.romantic.utils.HttpRequest;
import starlight.romantic.utils.Notification;


/**
 * @author Aym
 */
public class Schedule {
    private static final Logger LOGGER = LoggerFactory.getLogger(Schedule.class);
    private static final String URL_SCHEME = "KYeHRAPP://";
    private String barkKey;

    public Schedule(String key) {
        this.barkKey = key;
    }

    public void wobSign(String oid, String code) {
        String apiUrl = "http://wxapi.zj.chinaunicom.com/wechat-route-rest-1.0/api/daySign/do";
        String refererUrl = "http://wxapi.zj.chinaunicom.com/wechat-route-rest-1.0/view/woCoinStore/index?state=STATE&code=";
        JSONObject params = new JSONObject();
        params.put("oid", oid);
        params.put("cul", "/view/woCoinStore/index");
        params.put("data", new JSONObject());
        HttpRequest httpRequest = HttpRequest.post(apiUrl).referer(refererUrl + code).contentType("application/json", "UTF-8");
        String result = httpRequest.userAgentDefault().send(params.toJSONString()).body();
        LOGGER.info("{} : {}", oid, result);
    }

    public void work(String today, String tomorrow) {
        if (Holiday.isHoliday(today)) {
            if (Holiday.isHoliday(tomorrow)) {
                Notification.phone(this.barkKey, Holiday.getWeek(today), "Today & tomorrow holidays", URL_SCHEME);
                return;
            }
            Notification.phone(this.barkKey, Holiday.getWeek(today), "Today is holiday, but tomorrow...", URL_SCHEME);
            return;
        }
        if (!Holiday.isFriday()) {
            Notification.phone(this.barkKey, "Sign in", "[Click me] Today " + Holiday.getWeek(today), URL_SCHEME);
            return;
        }
        if (!Holiday.isHoliday(tomorrow)) {
            Notification.phone(this.barkKey, "Sign in", "[Click me] Today is friday, but....", URL_SCHEME);
            return;
        }
        Notification.phone(this.barkKey, "Sign in", "[Click me] Today is friday", URL_SCHEME);
    }

    public void offWork(String today, String tomorrow) {
        if (Holiday.isHoliday(today)) {
            return;
        }
        if (Holiday.isFriday() && Holiday.isHoliday(tomorrow)) {
            Notification.phone(this.barkKey, "Sign out", "[Click me] Tomorrow is holiday", URL_SCHEME);
            return;
        }
        Notification.phone(this.barkKey, "Sign out", "[Click me] Tomorrow " + Holiday.getWeek(tomorrow), URL_SCHEME);
    }
}
