package app.company.com.a20_sqlite.utility;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import app.company.com.a20_sqlite.data.WordsDB;
import app.company.com.a20_sqlite.db.WordsDBHelper;

public class WordsContentProvider extends ContentProvider {

    private static final int MULTIPLE_WORDS = 1;
    private static final int SINGLE_WORD = 2;
    private static final int DELETE_WORD = 3;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private WordsDBHelper words_db_helper;
    //content://com.coordinates.provider/word/1

    static {
        uriMatcher.addURI(WordsDB.AUTHORITY, WordsDB.Word.PATH_SINGLE, SINGLE_WORD);
        uriMatcher.addURI(WordsDB.AUTHORITY, WordsDB.Word.PATH_MULTIPLE, MULTIPLE_WORDS);
        uriMatcher.addURI(WordsDB.AUTHORITY, WordsDB.Word.PATH_DELETE_WORD,DELETE_WORD);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        SQLiteDatabase db = words_db_helper.getReadableDatabase();
        String whereClause;
        switch (uriMatcher.match(uri)){
            case MULTIPLE_WORDS:
                count = db.delete(WordsDB.Word.TABLE_NAME,selection,selectionArgs);
                break;
            case SINGLE_WORD:
                whereClause = WordsDB.Word._ID+"="+uri.getPathSegments().get(1);
                count = db.delete(WordsDB.Word.TABLE_NAME,whereClause,selectionArgs);
                break;
            case DELETE_WORD:
                //此处设置指定单词的名称进行删除
                whereClause = WordsDB.Word.COLUMN_NAME_WORD+" = "+"\""+uri.getPathSegments().get(1)+"\"";
                db.execSQL("delete from "+ WordsDB.Word.TABLE_NAME+" where "+whereClause+";");
                break;
            default:
                throw new IllegalArgumentException("Unknow uri"+uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MULTIPLE_WORDS:
                return WordsDB.Word.MINE_TYPE_MULTIPLE;
            case SINGLE_WORD:
                return WordsDB.Word.MINE_TYPE_SINGLE;
            default:
                throw new IllegalArgumentException("Unknow Uri:" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = words_db_helper.getWritableDatabase();
        Log.d("Debug","Insert");
        long id = db.insert(WordsDB.Word.TABLE_NAME, null, values);
        if (id > 0) {
            Uri newUri = ContentUris.withAppendedId(WordsDB.Word.CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLException("Failed to insert row into"+uri);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d("Debug","Query");
        SQLiteDatabase db = words_db_helper.getReadableDatabase();
        if (db == null){
            Log.d("Debug","DB is null");
        }
        Log.d("Debug","Query1");
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        Log.d("Debug","Query2");

        qb.setTables(WordsDB.Word.TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case MULTIPLE_WORDS:
                Log.d("Debug","MULTIPLE_WORDS");
                return db != null ? db.query(WordsDB.Word.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder) : null;
            case SINGLE_WORD:
                Log.d("Debug","SINGLE WORD");
                qb.appendWhere(WordsDB.Word._ID + "=" + uri.getPathSegments().get(1));
                return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Unkonwn Uri:" + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        String segment;
        SQLiteDatabase db = words_db_helper.getReadableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)){
            case MULTIPLE_WORDS:
                count = db.update(WordsDB.Word.TABLE_NAME,values,selection,selectionArgs);
                break;
            case SINGLE_WORD:
                segment = uri.getPathSegments().get(1);
                count = db.update(WordsDB.Word.TABLE_NAME, values, WordsDB.Word._ID+"="+segment, selectionArgs);
                break;
            case DELETE_WORD:
                segment = uri.getPathSegments().get(1);
                db.execSQL("Update "+ WordsDB.Word.TABLE_NAME+" set "+ WordsDB.Word.COLUMN_NAME_MEANING+"= "+"\""+values.get(WordsDB.Word.COLUMN_NAME_MEANING) +"\" , "+ WordsDB.Word.COLUMN_NAME_SAMPLE+"= "+"\""+values.get(WordsDB.Word.COLUMN_NAME_SAMPLE)+"\" where word = "+"\""+segment+"\"");
                break;
            default:
                throw new IllegalArgumentException("Unkonwn Uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }
}
