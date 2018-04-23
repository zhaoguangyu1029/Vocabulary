package app.company.com.a20_sqlite.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import app.company.com.a20_sqlite.R;
import app.company.com.a20_sqlite.activity.MainActivity;
import app.company.com.a20_sqlite.data.Word;
import app.company.com.a20_sqlite.data.WordsDB;
import app.company.com.a20_sqlite.fragment.DetailsFragment;


public class DialogInflater {

    private Context context;

    public DialogInflater(Context context){
        this.context = context;
    }

    public void inflateDeleteDialog(){
        final View view_delete = ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_delete,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("全部删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.dbOperator.deleteAll();
                Toast.makeText(context,"全部删除",Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText et_delete = (EditText) view_delete.findViewById(R.id.et_del_word);
                String str_word = et_delete.getText().toString();
                MainActivity.dbOperator.deleteWords(str_word);
                Toast.makeText(context,"删除:"+str_word,Toast.LENGTH_LONG).show();
            }
        }).setView(view_delete).show();

    }

    public void inflateEditDialog(MenuItem item){
        View itemView;
        final String str_word;
        Word word;
        String str_meaning;
        String str_sample;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        itemView = info.targetView;
        str_word = ((TextView)itemView.findViewById(R.id.tv_fm_words_details_word)).getText().toString();
        word = MainActivity.dbOperator.query(str_word).get(0);
        str_meaning = word.getMeaning();
        str_sample = word.getSample();
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_add,null);
        final EditText et_word = (EditText) view.findViewById(R.id.et_word);
        final EditText et_meaning = (EditText) view.findViewById(R.id.et_meaning);
        final EditText et_sample = (EditText) view.findViewById(R.id.et_sample);
        et_word.setText(str_word);
        et_meaning.setText(str_meaning);
        et_sample.setText(str_sample);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("修改单词数据").setView(view).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String str_new_word = "";
                String str_new_meaning = "";
                String str_new_sample = "";
                str_new_word = et_word.getText().toString();
                str_new_meaning = et_meaning.getText().toString();
                str_new_sample = et_sample.getText().toString();
                MainActivity.dbOperator.updateWords(str_word,str_new_word,str_new_meaning,str_new_sample);
                if (context.getResources().getConfiguration().orientation == 2){
                    inflateDetailsFragment(MainActivity.dbOperator.query(str_new_word));
                }
            }
        }).show();
    }

    private void inflateDetailsFragment(List data){
        Bundle argument = new Bundle();
        argument.putSerializable("data", (Serializable) data);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(argument);
        ((Activity)context).getFragmentManager().beginTransaction().replace(R.id.fm_land_word_details, fragment).commit();
    }

    public void inflateAddDialog(){
        View add_view = ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_add,null);
        final EditText et_name = (EditText) add_view.findViewById(R.id.et_word);
        final EditText et_meaning = (EditText) add_view.findViewById(R.id.et_meaning);
        final EditText et_sample = (EditText) add_view.findViewById(R.id.et_sample);
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(context);
        alert_builder.setView(add_view).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("增加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String word = et_name.getText().toString();
                String meaning = et_meaning.getText().toString();
                String sample = et_sample.getText().toString();
                MainActivity.dbOperator.insertWords(word,meaning,sample);
                Toast.makeText(context,"插入成功",Toast.LENGTH_LONG).show();
            }
        }).show();

    }

    public void inflateTranslationDialog(final Map<String,String> content){
        //将查询的数据信息拼接在一个String中
        StringBuilder builder = new StringBuilder();
        builder.append(content.get(WordsDB.Word.COLUMN_NAME_WORD));
        builder.append(content.get(WordsDB.Word.COLUMN_NAME_MEANING));
        builder.append(content.get(WordsDB.Word.COLUMN_NAME_SAMPLE));
        View translation_view = ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_translation,null);
        final TextView tv_translation = (TextView) translation_view.findViewById(R.id.tv_dialog_translation);
        tv_translation.setText(builder.toString());
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(context);
        alert_builder.setView(translation_view).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("增加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //将指定的单词插入到数据库
                MainActivity.dbOperator.insertWords(content.get(WordsDB.Word.COLUMN_NAME_WORD),content.get(WordsDB.Word.COLUMN_NAME_MEANING),content.get(WordsDB.Word.COLUMN_NAME_SAMPLE));
            }
        }).show();

    }

}
