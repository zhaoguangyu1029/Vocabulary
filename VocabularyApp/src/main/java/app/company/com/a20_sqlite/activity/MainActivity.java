package app.company.com.a20_sqlite.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.company.com.a20_sqlite.dialog.DialogInflater;
import app.company.com.a20_sqlite.listener.FindWordsFromYOUDAOListener;
import app.company.com.a20_sqlite.listener.LandListViewOnClickListener;
import app.company.com.a20_sqlite.R;
import app.company.com.a20_sqlite.db.DBOperator;
import app.company.com.a20_sqlite.data.WordsDB;
import app.company.com.a20_sqlite.db.WordsDBHelper;
import app.company.com.a20_sqlite.fragment.WordsListFragment;
import app.company.com.a20_sqlite.utility.FilePathUtil;

public class MainActivity extends AppCompatActivity implements WordsListFragment.OnFragmentInteractionListener,View.OnClickListener{

    public static Handler handler_main;
    public static DBOperator dbOperator;

    private ListView lv_fm_words_list;
    private DialogInflater dialogInflater;
    private Button btn_search;
    private EditText et_search;
    private Button btn_find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbOperator = DBOperator.getDBOperator(this,new WordsDBHelper(this));
        lv_fm_words_list = (ListView) findViewById(R.id.lv_fm_words_list);
        dialogInflater = new DialogInflater(this);
        registerForContextMenu(lv_fm_words_list);
        lv_fm_words_list.setOnItemClickListener(new LandListViewOnClickListener(this,dbOperator));
        //ListView设置数据
        setWordsListView(dbOperator.getAll());
        handler_main = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                HashMap result = (HashMap) msg.obj;
                handleResult(result);
            }
        };
        btn_search = (Button) findViewById(R.id.btn_search);
        et_search = (EditText) findViewById(R.id.et_search);
        btn_search.setOnClickListener(this);
        btn_find = (Button) findViewById(R.id.btn_find);
        btn_find.setOnClickListener(new FindWordsFromYOUDAOListener(this,et_search));
    }

    /**
     * 给ListView设置要显示的数据
     * @param items items表示要显示的数据
     */
    public void setWordsListView(List<Map<String,String>> items){
        SimpleAdapter adapter;
        //定义竖屏时的显示方式
        if (getResources().getConfiguration().orientation == 1){
            adapter = new SimpleAdapter(this,items,R.layout.port_list_view_item,new String[]{
                    WordsDB.Word.COLUMN_NAME_WORD,
                    WordsDB.Word.COLUMN_NAME_MEANING},new int[]{R.id.tv_fm_words_details_word,R.id.tv_fm_words_details_meaning});
        }
        else{
            //定义横屏时的显示方式
            adapter = new SimpleAdapter(this,items,R.layout.land_list_view_item,new String[]{WordsDB.Word.COLUMN_NAME_WORD},new int[]{R.id.tv_fm_words_details_word});
        }
        lv_fm_words_list.setAdapter(adapter);
    }

    private void handleResult(Map<String,String> result){
        switch (result.get("status")){
            case "0":
                dialogInflater.inflateTranslationDialog(result);
                break;
            case "20":
                Toast.makeText(this,"要翻译的文本过长",Toast.LENGTH_LONG).show();
                break;
            case "30":
                Toast.makeText(this,"无法进行有效的翻译",Toast.LENGTH_LONG).show();
                break;
            case "40":
                Toast.makeText(this,"不支持的语言类型",Toast.LENGTH_LONG).show();
                break;
            case "50":
                Toast.makeText(this,"无效的key",Toast.LENGTH_LONG).show();
                break;
            case "60":
                Toast.makeText(this,"无词典结果，仅在获取词典结果生效",Toast.LENGTH_LONG).show();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbOperator.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_add:
                dialogInflater.inflateAddDialog();
                break;
            case R.id.menu_delete:
                dialogInflater.inflateDeleteDialog();
                break;
            case R.id.menu_refresh:
                //刷新界面
                setWordsListView(dbOperator.getAll());
                break;
            case R.id.menu_load_artical:
                //载入文本，对于文本的单词进行识别并添加点击事件，进行翻译。
                //得到选择的文件路径

                //打开文件管理器
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //文件管理器打开，选择文件后返回MainActivity，然后处理
                startActivityForResult(intent,1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //得到文件路径后打开TextActivity
            Uri uri = data.getData();
            String file_path = FilePathUtil.getPathByUri4kitkat(this,uri);
            Intent i = new Intent(MainActivity.this,TextActivity.class);
            i.putExtra("filePath",file_path);
            startActivity(i);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.context_menu_edit:
                dialogInflater.inflateEditDialog(item);
                if (getResources().getConfiguration().orientation == 1)
                    setWordsListView(dbOperator.getAll());
                break;
            case R.id.context_menu_del:
                deleteSelectedWord(item);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteSelectedWord(MenuItem item){
        String str_delete_word;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        View view = info.targetView;
        str_delete_word = ((TextView)view.findViewById(R.id.tv_fm_words_details_word)).getText().toString();
        dbOperator.deleteWords(str_delete_word);
        setWordsListView(dbOperator.getAll());
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu,menu);
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * 点击搜索，能够进行模糊查找
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_search:
                String str_search = et_search.getText().toString();
                if (!str_search.equals(""))
                    setWordsListView(dbOperator.likeQuery(str_search));
                break;
        }
    }
}
