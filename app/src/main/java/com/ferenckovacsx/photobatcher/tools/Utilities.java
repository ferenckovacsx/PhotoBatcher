package com.ferenckovacsx.photobatcher.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ferenckovacsx on 2018-03-05.
 */

public class Utilities {

    public static String getFormattedDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd, HH:mm:ss", Locale.ENGLISH);
        return df.format(c);
    }

    public static String generateName(String type) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyMMdd_", Locale.ENGLISH);

        if (type.equals("IMG")) {
            df = new SimpleDateFormat("yyMMdd_HHmmss", Locale.ENGLISH);
            return "IMG_" + df.format(c) + ".jpg";
        } else return "Batch_" + df.format(c);
    }

    public static void moveFile(String inputFile, String outputPath) throws IOException {

        InputStream in = null;
        OutputStream out = null;

        //create output directory if it doesn't exist
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.getParentFile().mkdirs();
        }


        in = new FileInputStream(inputFile);
        out = new FileOutputStream(outputPath);

        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        in = null;

        // write the output file
        out.flush();
        out.close();
        out = null;

        // delete the original file
        new File(inputFile).delete();
    }

}
