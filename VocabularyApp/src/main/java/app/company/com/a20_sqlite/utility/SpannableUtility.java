package app.company.com.a20_sqlite.utility;

import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpannableUtility {

    private static final String REGEX_WORD = "[a-z|A-Z]+";
    private static final String START = "start";
    private static final String END = "end";

    public static List<Map<String,Integer>> getStartAndEnd(String s_content){
        List<Map<String,Integer>> result = new LinkedList<>();
        Matcher matcher = Pattern.compile(REGEX_WORD).matcher(s_content);
        Map<String,Integer> single;
        //matcher正则表达式进行查找符合条件的
        while (matcher.find()){
            single = new HashMap<>();
            single.put(START,matcher.start());
            single.put(END,matcher.end());
            result.add(single);
        }
        return result;
    }
}
