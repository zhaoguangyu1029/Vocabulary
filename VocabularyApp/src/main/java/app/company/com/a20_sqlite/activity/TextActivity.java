package app.company.com.a20_sqlite.activity;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;
import app.company.com.a20_sqlite.R;
import app.company.com.a20_sqlite.dialog.DialogInflater;
import app.company.com.a20_sqlite.utility.FileOperator;
import app.company.com.a20_sqlite.utility.SpannableUtility;
import app.company.com.a20_sqlite.utility.YOUDAOTask;

public class TextActivity extends AppCompatActivity {

    private String filePath = "";
    private SpannableString span_str_content;
    private Handler handler_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handler_text = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                new DialogInflater(TextActivity.this).inflateTranslationDialog((Map<String, String>) msg.obj);
            }
        };
        filePath = getIntent().getStringExtra("filePath");
        String[] file_content = FileOperator.getFileContent(filePath);
        //初始化SpannableString
        span_str_content = new SpannableString(file_content[1]);

        //每取出一个单词的开始和结束的Map，就添加一个样式
        for (Map<String,Integer> m : SpannableUtility.getStartAndEnd(file_content[1])){
            ClickableSpan clickableSpan = getClickableSpan();
            span_str_content.setSpan(clickableSpan,m.get("start"),m.get("end"),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        TextView tv_content = (TextView) findViewById(R.id.tv_read);
        tv_content.setText(span_str_content);
        tv_content.setMovementMethod(LinkMovementMethod.getInstance());
    }
 //监听事件，调用查询youdaoAPI的方法
    private ClickableSpan getClickableSpan(){
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                TextView tv = (TextView) widget;
                final String a = tv.getText().subSequence(tv.getSelectionStart(),tv.getSelectionEnd()).toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = MainActivity.handler_main.obtainMessage();
                        try {
                            msg.obj = YOUDAOTask.analyseJSON(a,YOUDAOTask.doGet(a));
                            handler_text.sendMessage(msg);

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(false);
            }
        };
        return span;
    }
}
