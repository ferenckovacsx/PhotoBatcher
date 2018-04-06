package com.ferenckovacsx.photobatcher.tools;

import android.media.ExifInterface;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pixy.meta.Metadata;
import pixy.meta.iptc.IPTCApplicationTag;
import pixy.meta.iptc.IPTCDataSet;

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

    public static void moveFile(String inputFile, String outputPath, String batchName) throws IOException {

        //create output directory if it doesn't exist
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.getParentFile().mkdirs();
        }

        FileInputStream fin = new FileInputStream(inputFile);
        FileOutputStream fout = new FileOutputStream(outputPath);

        List<IPTCDataSet> iptcs = new ArrayList<>();
        iptcs.add(new IPTCDataSet(IPTCApplicationTag.CAPTION_ABSTRACT, batchName));
        Metadata.insertIPTC(fin, fout, iptcs, true);

        fin.close();
        fout.close();

        // delete the original file
        new File(inputFile).delete();
    }
}
