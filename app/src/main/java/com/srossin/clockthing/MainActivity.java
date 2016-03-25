package com.srossin.clockthing;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private TextView[] mButtons = new TextView[4];
    private int mFocused=-1;

    private Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mFocused != -1){
                mTimes[mFocused] += (SystemClock.uptimeMillis() - mSystime);
            }
            mSystime = SystemClock.uptimeMillis();
            updateUI();

            mHandler.postDelayed(this, 1000);
        }
    };

    private String[] mNames = new String[4];

    public static String NAME1 = "name1";
    public static String NAME2 = "name2";
    public static String NAME3 = "name3";
    public static String NAME4 = "name4";
    public static String EXTRA_NAME = "blarg";

    public static String ARG = "arg";
    private String TIMES = "Times";
    private String SYS_TIME = "Sys_Time";
    private String FOCUS = "Focus";

    private long[] mTimes = new long[4];
    long mSystime;

    SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSystime = SystemClock.uptimeMillis();

        mButtons[0] = (TextView) findViewById(R.id.button1);
        mButtons[1] = (TextView) findViewById(R.id.button2);
        mButtons[2] = (TextView) findViewById(R.id.button3);
        mButtons[3]= (TextView) findViewById(R.id.button4);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        updateNames(null, -1);

        for (int  i=0; i < 4; i++) {
            final int j = i;
            mButtons[j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFocused != -1) {
                        mButtons[mFocused].setSelected(false);
                        mTimes[mFocused] += (SystemClock.uptimeMillis() -mSystime);
                        Log.d("tag",Long.toString(mTimes[mFocused]));
                    }
                    if (mFocused ==j) {
                        mFocused = -1;
                        mButtons[j].setSelected(false);
                    }
                    else {
                        mFocused = j;
                        mButtons[j].setSelected(true);
                    }
                    mSystime = SystemClock.uptimeMillis();
                    mHandler.removeCallbacks(mRunnable);
                    mHandler.postDelayed(mRunnable, 10);
                }
            });

            mButtons[j].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return launchNameDialog(j);
                }
            });
        }

        mHandler.postDelayed(mRunnable, 1000);
        updateUI();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLongArray(TIMES, mTimes);
        outState.putLong(SYS_TIME, mSystime);
        outState.putInt(FOCUS, mFocused);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mTimes = savedInstanceState.getLongArray(TIMES);
        mFocused = savedInstanceState.getInt(FOCUS);

        if (mFocused != -1){
            mTimes[mFocused] += (SystemClock.uptimeMillis() - savedInstanceState.getLong(SYS_TIME));
        }

        updateUI();
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void updateUI(){
        for (int i= 0; i<4; i++){
            Long seconds = mTimes[i]/1000;
            Long hours = seconds/3600;
            Long minutes = (seconds%3600)/60;
            seconds = seconds%60;
            mButtons[i].setText(String.format(mNames[i] + "\n%02d:%02d:%02d", hours, minutes, seconds));
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    private boolean launchNameDialog(int num){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle args= new Bundle();
        args.putInt(ARG, num);
        args.putString(EXTRA_NAME, mNames[num]);
        ChangeAliasDialogFragment newFragment = new ChangeAliasDialogFragment();
        newFragment.setArguments(args);

        newFragment.show(ft, "change_name");
        return true;
    }

    public void updateNames(String name, int num){
        mNames[0] = mSharedPref.getString(NAME1, "Timer 1");
        mNames[1] = mSharedPref.getString(NAME2, "Timer 2");
        mNames[2] = mSharedPref.getString(NAME3, "Timer 3");
        mNames[3] = mSharedPref.getString(NAME4, "Timer 4");
        if (name != null) mNames[num] = name;
    }
}
