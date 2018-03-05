package com.ferenckovacsx.android.photobatcher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ferenckovacsx on 2018-03-05.
 */

public class Utilities {

    static String getFormattedDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd, HH:mm:ss", Locale.ENGLISH);
        return df.format(c);
    }

    static String generateBatchName(String type) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.ENGLISH);

        if (type.equals("IMG")) {
            df = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
            return "IMG_" + df.format(c);
        } else return "Batch_" + df.format(c);
    }

}
