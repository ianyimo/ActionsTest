package starlight.romantic.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author Aym
 */
public class NetEaseMusic {
    final static String API_URL = "https://tenapi.cn/comment/";
    final static String API_URLS = "https://api.4gml.com/NeteaseMusic";

    public static String comment() {
        String result = HttpRequest.get(API_URL).userAgentDefault().body();
        JSONObject obj = JSONObject.parseObject(result);
        if (obj.getInteger("code") != 200) {
            return "";
        }
        if (obj.get("data") == null || obj.getJSONObject("data").isEmpty() || obj.getJSONObject("data").get("content") == null) {
            return "";
        }
        return obj.getJSONObject("data").getString("content");
    }
    public static String random(){
        try{
            String result = HttpRequest.get(API_URLS).userAgentDefault().body();
            JSONObject obj = JSONObject.parseObject(result);
            return obj.getString("content");
        }catch (Exception e){
            return "";
        }
    }
}
