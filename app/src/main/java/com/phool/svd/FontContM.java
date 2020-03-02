package com.phool.svd;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Paxees on 10/12/2018.
 */

public class FontContM {
    Context context;
    public FontContM(Context context, EditText editText)
    {
        Typeface amaranth_regular = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        editText.setTypeface(amaranth_regular);
    }
    public FontContM(Context context, Button button)
    {
        Typeface amaranth_regular = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        button.setTypeface(amaranth_regular);
    }
    public FontContM(Context context, TextView textView)
    {
        Typeface amaranth_regular = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        textView.setTypeface(amaranth_regular);
    }
}
