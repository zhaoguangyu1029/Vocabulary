package app.company.com.a20_sqlite.utility;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class FileOperator {

    public static final String READ_FILE_OK = "200";
    public static final String OPEN_FILE_FAILED = "0";

    public static String[] getFileContent(String file_path){
        /**
         * 对文件名进行过滤。不符合的文件暂时无法打开
         */

        /**
         * 保留插入对编码的处理，按照文本的编码格式来打开文本，否则会出现乱码
         */
        String[] content = new String[2];
        Charset charset = Charset.forName("utf-8");
        CharsetDecoder decoder = charset.newDecoder();
        RandomAccessFile random_file_read = null;
        //可以添加设置文件编码的功能CharSet实现
        StringBuilder str_builder = new StringBuilder("");
        try {
            random_file_read = new RandomAccessFile(file_path,"r");
        } catch (FileNotFoundException e) {
            Log.d("exception",file_path+":NOT EXISTS");
            content[0] = OPEN_FILE_FAILED;
            content[1] = null;
            return content;
        }
        FileChannel channel_file = random_file_read.getChannel();
        ByteBuffer buffer_byte = ByteBuffer.allocate(2048);
        //CharBuffer一定要设计的够大，和Byte Buffer一样大就可以，因为编码的问题，Char对应几个bytes不一定
        CharBuffer buffer_char = CharBuffer.allocate(2048);
        int bytesRead = 0;
        try {
            bytesRead = channel_file.read(buffer_byte);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (bytesRead != -1) {
            buffer_byte.flip();
            decoder.decode(buffer_byte,buffer_char,false);
            buffer_char.flip();
            while (buffer_char.hasRemaining()){
                str_builder.append(buffer_char.get());
            }
            buffer_byte.clear();
            buffer_char.clear();
            try {
                bytesRead = channel_file.read(buffer_byte);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            channel_file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        content[0] = READ_FILE_OK;
        content[1] = str_builder.toString();
        Log.d("Debug",content[1]);
        return content;
    }
}
