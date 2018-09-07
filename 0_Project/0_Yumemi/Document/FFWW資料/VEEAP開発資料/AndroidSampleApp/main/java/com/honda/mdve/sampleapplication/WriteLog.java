package com.honda.mdve.sampleapplication;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WriteLog {
    private static final String TAG = "Sample_writeLog";
    private static final boolean DEBUG = true;

    private Context mContext;
    FileOutputStream mFileIO = null;

    enum MSGKIND{
        MSGKIND_REQUEST("request"),
        MSGKIND_NOTICE("notice"),
        MSGKIND_MDREQ("MdReq"),
        MSGKIND_MDRES("MdRes"),
        ;
        private String text;
        MSGKIND( final String text){
            this.text = text;
        }
        public String getString(){
            return this.text;
        }
    }

    WriteLog(Context context){
        mContext = context;
        makefile(true);
    }

    public void write_apprecv(MSGKIND msgkind, String json_str) {
        String output_str = new String(getTimestamp("MM-dd HH:mm:ss.SSS") + " " + msgkind.getString() + " " + json_str);
        write(mFileIO, output_str);
    }

    public void write_apprecv(MSGKIND msgkind, String functionname, String json_str) {
        String output_str = new String(getTimestamp("MM-dd HH:mm:ss.SSS") + " " + msgkind.getString() + " " + "[" + functionname + "]" + " " + json_str);
        write(mFileIO, output_str);
    }

    private void write(FileOutputStream fileoutputstream, String outputstring) {
        try {
            fileoutputstream.write(outputstring.getBytes());
            fileoutputstream.write("\n".getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Log can't write");
        }
    }

    public void makefile(boolean make) {
        final String FILENAME_TOP = "MDVETest_recv";
        if (make) {
            // file make
            if (mFileIO == null) {
                String filename = new String(FILENAME_TOP + "_" + getTimestamp("yyyyMMdd_HHmmss") + ".log");
                try {
                    mFileIO = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "LogFile can't open");
                }
                if (DEBUG) Log.d(TAG, "FileOutputStream=" + mFileIO);
            }

        } else {
            // file close
            if (mFileIO != null) {
                try {
                    mFileIO.close();
                } catch (IOException e) {
                    Log.e(TAG, "LogFile can't close");
                }
            }
        }
    }

    private String getTimestamp(String format){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(calendar.getTime());
    }

}
