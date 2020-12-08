package starlight.romantic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import starlight.romantic.utils.Holiday;
import starlight.romantic.utils.HttpRequest;
import starlight.romantic.utils.Notification;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


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
        LOGGER.info("{} >>> {}", oid, result);
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

    public void tieBaSign(String bDuSs) {
        String cookie = "BDUSS=" + bDuSs;
        String tbs = getTbs(cookie);
        if (tbs == null || "".equals(tbs)) {
            return;
        }
        LOGGER.info("tbs {}", tbs);
        List<String> waitList = getFollow(cookie);
        int waitTotal = waitList.size();
        List<String> success = new ArrayList<String>();
        String apiUrl = "http://c.tieba.baidu.com/c/c/forum/sign";
        int retry = 3;
        try {
            while (success.size() < waitTotal && retry > 0) {
                LOGGER.info("{} >>> begin {}", 3 - retry + 1, waitTotal - success.size());
                Iterator<String> iterator = waitList.iterator();
                while (iterator.hasNext()) {
                    String tieBaName = iterator.next();
                    String signature = toMd5("kw=" + tieBaName + "tbs=" + tbs + "tiebaclient!!!");
                    String result = HttpRequest.post(apiUrl, false, "kw", tieBaName, "tbs", tbs, "sign", signature).contentType("application/x-www-form-urlencoded", "UTF-8").header("Cookie", cookie).header("Host", "tieba.baidu.com").userAgentDefault().body();
                    System.out.println("result = " + result);
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject.getInteger("error_code") == 0) {
                        iterator.remove();
                        success.add(tieBaName);
                        LOGGER.info("{} sign success", tieBaName);
                    } else {
                        LOGGER.warn("{} error", tieBaName);
                    }
                }
                LOGGER.info("size " + waitList.size());
                if (success.size() != waitTotal) {
                    Thread.sleep(1000 * 60 * 5);
                    tbs = getTbs(cookie);
                }
                retry--;
            }
        } catch (Exception e) {
            LOGGER.error("partial exception >>> " + e);
        }
        LOGGER.info("waitTotal >>> {} > sign success {}", waitTotal, success.size());
    }

    private static String getTbs(String cookie) {
        String apiUrl = "http://tieba.baidu.com/dc/common/tbs";
        String validKey = "is_login";
        try {
            String result = HttpRequest.get(apiUrl).contentType("application/x-www-form-urlencoded", "UTF-8").userAgentDefault().header("Cookie", cookie).body();
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject.getInteger(validKey) != 1) {
                LOGGER.error("get tbs failed >>> {}", result);
                return "";
            }
            return jsonObject.getString("tbs");
        } catch (Exception e) {
            LOGGER.error("get tbs exception >>> " + e);
            return "";
        }
    }

    private List<String> getFollow(String cookie) {
        String apiUrl = "https://tieba.baidu.com/mo/q/newmoindex";
        try {
            String result = HttpRequest.get(apiUrl).contentType("application/x-www-form-urlencoded", "UTF-8").header("cookie", cookie).userAgentDefault().body();
            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("like_forum");
            List<String> res = new ArrayList<String>();
            for (Object object : jsonArray) {
                // != 1
                if (((JSONObject) object).getInteger("is_sign") == 0) {
                    res.add(((JSONObject) object).getString("forum_name"));
                }
            }
            return res;
        } catch (Exception e) {
            LOGGER.error("get follow exception >>> " + e);
            return Collections.emptyList();
        }
    }

    public static String toMd5(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(string.getBytes("UTF-8"));
            return new BigInteger(1, messageDigest.digest()).toString(16);
        } catch (Exception e) {
            LOGGER.error("md5 exception >>> " + e);
            return "";
        }
    }
}
