package vn.edu.hcmut.komorebi.sleepplease.utilities;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ndtho8205 on 07/01/2017.
 */

public class FilePickerPreference extends Preference {
    private TextView mTitleTextView;
    private TextView mSummaryTextView;

    public FilePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

//        mTitleTextView.setText();
//        mSummaryTextView.setText();
    }
}
