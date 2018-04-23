package app.company.com.a20_sqlite.listener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import app.company.com.a20_sqlite.activity.MainActivity;
import app.company.com.a20_sqlite.utility.YOUDAOTask;

public class FindWordsFromYOUDAOListener implements View.OnClickListener {

    private static final String YOUDAO_KEYFROM = "haobaoshui";
    private static final String YOUDAO_KEY = "1650542691";
    private static final String YOUDAO_API= "http://fanyi.youdao.com/openapi.do?keyfrom="+YOUDAO_KEYFROM+"&key="+YOUDAO_KEY+"&type=data&doctype=json&version=1.1&q=";

    private Context context;
    private EditText et_input;
    private Handler handler_main;
    private URL url_target;
    private URLConnection connection;

    /**
     *
     * @param context 上下文信息
     * @param target 要查询的单词名
     */
    public FindWordsFromYOUDAOListener(Context context,EditText target){
        this.context = context;
        this.et_input = target;
        handler_main = MainActivity.handler_main;
    }

    @Override
    public void onClick(View v) {
        new Thread(new Task()).start();
    }

    private class Task implements Runnable{
        @Override
        public void run() {
            try {
                Message msg = handler_main.obtainMessage();
                msg.obj = YOUDAOTask.analyseJSON(et_input.getText().toString(),YOUDAOTask.doGet(et_input.getText().toString()));
                handler_main.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
