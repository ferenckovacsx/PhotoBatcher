package com.ferenckovacsx.android.photobatcher;

/**
 * Created by ferenckovacsx on 2018-03-05.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Allow custom list rows to be checked.
 *
 * @author msama (michele.sama@gmail.com) on 08/09/13.
 * @author psaeedi on 08/09/13.
 */
public class CheckableLayout extends LinearLayout implements Checkable {

    private boolean checked;

    public CheckableLayout(Context context) {
        super(context);
    }

    public CheckableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
        Log.i("Checkable", "Toggle");
    }
}