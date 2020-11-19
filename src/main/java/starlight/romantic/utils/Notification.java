package starlight.romantic.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Aym
 */
public class Notification {
    private static final Logger LOGGER = LoggerFactory.getLogger(Notification.class);
    public static void phone(String key, String title, String content, String url){
        String apiUrl = "https://api.day.app/" + key + "/" + title + "/" + content;
        if(url != null && !"".equals(url)){
            apiUrl += "?url=" + url;
        }
        String result = HttpRequest.get(apiUrl).body();
        LOGGER.info(result);
    }
    public static void weChat(String key, String title, String content){
        String apiUrl = "https://sc.ftqq.com/" + key +".send";
        String result = HttpRequest.post(apiUrl,true, "text", title, "desp",content).body();
        LOGGER.info(result);
    }
}
