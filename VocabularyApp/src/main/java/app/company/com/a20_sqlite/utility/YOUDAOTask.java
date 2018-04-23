package app.company.com.a20_sqlite.utility;

import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import app.company.com.a20_sqlite.data.WordsDB;


public class YOUDAOTask{
    private static final String YOUDAO_KEYFROM = "haobaoshui";
    private static final String YOUDAO_KEY = "1650542691";
    private static final String YOUDAO_API= "http://fanyi.youdao.com/openapi.do?keyfrom="+YOUDAO_KEYFROM+"&key="+YOUDAO_KEY+"&type=data&doctype=json&version=1.1&q=";

    private static URL url_target;
    private static URLConnection connection;

    /**
     * 返回JSON数据
     * @param str_find
     * @return
     * @throws IOException
     */
    public static String doGet(String str_find) throws IOException {
        String reply = "";
        url_target = new URL(YOUDAO_API+ URLEncoder.encode(str_find,"utf-8"));
        HttpURLConnection connection = (HttpURLConnection) url_target.openConnection();
        connection.setConnectTimeout(3000);
        connection.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
        String line;
        while ((line = reader.readLine()) != null){
            reply += line;
        }
        //reply表示最后的json数据
        return reply;
    }

    public static Map analyseJSON(String word,String data_json) throws JSONException {
        HashMap<String,String> result = new HashMap<>();
        JSONObject json_data = new JSONObject(data_json);
        String error_code = json_data.getString("errorCode");
        if (error_code.equals("0")){
            JSONArray translation = json_data.has("translation")?json_data.getJSONArray("translation"):null;
            JSONObject basic = json_data.has("basic")?json_data.getJSONObject("basic"):null;
            JSONArray web = json_data.has("web")?json_data.getJSONArray("web"):null;
            String phonetic = null;
            String uk_phonetic = null;
            String us_phonetic = null;
            JSONArray explains = null;
            if (basic != null){
                phonetic = basic.has("phonetic")?basic.getString("phonetic"):null;
                uk_phonetic = basic.has("uk_phonetic")?basic.getString("uk_phonetic"):null;
                us_phonetic = basic.has("us_phonetic")?basic.getString("us_phonetic"):null;
                explains = basic.has("explains")?basic.getJSONArray("explains"):null;
            }
            String translationStr = "";
            if (translation != null){
                translationStr = "\n翻译：\n";
                for (int i = 0; i < translation.length();i ++){
                    translationStr += "\t"+translation.getString(i)+"\n";
                }
            }
            String webStr = "\n网络词义：\n";
            if (web != null){
                for (int i = 0;i < web.length();i++){
                    if (web.getJSONObject(i).has("value")){
                        webStr += web.getJSONObject(i).getString("value");
                    }
                    if (web.getJSONObject(i).has("key")){
                        webStr += web.getJSONObject(i).getString("key")+"\n";
                    }
                }
            }
            String phoneticStr=(phonetic!=null? "\n发音："+phonetic:"")
                    +(uk_phonetic!=null? "\n英式发音："+uk_phonetic:"")
                    +(us_phonetic!=null? "\n美式发音："+us_phonetic:"");
            String explainStr="";
            if(explains!=null){
                explainStr="\n\n释义：\n";
                for(int i=0;i<explains.length();i++){
                    explainStr+="\t"+explains.getString(i)+"\n";
                }
            }
            result.put("status","0");
            result.put(WordsDB.Word.COLUMN_NAME_WORD,word.trim());
            result.put(WordsDB.Word.COLUMN_NAME_MEANING,explainStr.trim());
            result.put(WordsDB.Word.COLUMN_NAME_SAMPLE,webStr.trim());
            return result;
        }else {
            result.put("status",error_code);
            return result;
        }
    }
}
