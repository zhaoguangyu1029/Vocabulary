package app.company.com.a20_sqlite.listener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import app.company.com.a20_sqlite.R;
import app.company.com.a20_sqlite.data.Word;
import app.company.com.a20_sqlite.db.DBOperator;
import app.company.com.a20_sqlite.fragment.DetailsFragment;


public class LandListViewOnClickListener  implements AdapterView.OnItemClickListener{

    private Context context;
    private DBOperator dbOperator;

    public LandListViewOnClickListener(Context context,DBOperator dbOperator){
        this.dbOperator = dbOperator;
        this.context = context;
    }

    //LandListView的点击事件，横屏当点击时右侧fragment显示单词数据。
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (context.getResources().getConfiguration().orientation == 2){
            TextView tv_clicked = (TextView) view.findViewById(R.id.tv_fm_words_details_word);
            String str_word = tv_clicked.getText().toString();
            Log.d("Debug",str_word);
            //通过数据库查询到指定单词名的全部信息
            List<Word> data = dbOperator.query(str_word);
            //加载右侧的fragment
            inflateDetailsFragment(data);
        }
    }

    private void inflateDetailsFragment(List data){
        Bundle argument = new Bundle();
        argument.putSerializable("data", (Serializable) data);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(argument);
        ((Activity)context).getFragmentManager().beginTransaction().replace(R.id.fm_land_word_details, fragment).commit();
    }
}
