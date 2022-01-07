package com.writercorporation.maudit;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.writercorporation.database.DatabaseManager;
import com.writercorporation.design.CircularProgressBar;
import com.writercorporation.utils.ConnectionDetector;

import net.sqlcipher.database.SQLiteDatabase;

public class SplashScreenActivity extends Activity {
    private ConnectionDetector cd;
    CircularProgressBar circularProgressBar;
    TextView versionNameTV;
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
        SQLiteDatabase.loadLibs(this);
        DatabaseManager.init(this);




    }
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (RootUtil.isDeviceRooted()){
            return;
        }
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String version = info.versionName;
            versionNameTV = (TextView) findViewById(R.id.versionText);
            versionNameTV.setText("Version: "+version);
        }catch(PackageManager.NameNotFoundException nnfe){
           // nnfe.printStackTrace();
        }
        CountDown _tik;
        circularProgressBar = (CircularProgressBar) findViewById(R.id.circularProgress);
        _tik=new CountDown(5000,5000,this,LoginActivity.class);// It delay the screen for 2 second and after that switch to YourNextActivity
        _tik.start();
        StartAnimations();
    }
    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout l=(RelativeLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.logo);


        iv.clearAnimation();
        iv.startAnimation(anim);
    }


     class CountDown extends CountDownTimer {
        private Activity _act;
        private Class _cls;
        public CountDown(long millisInFuture, long countDownInterval,Activity act,Class cls) {
            super(millisInFuture, countDownInterval);
            _act=act;
            _cls=cls;
            cd = new ConnectionDetector(SplashScreenActivity.this);
        }
        @Override
        public void onFinish() {
           // if(cd.isConnectingToInternet()) {
                _act.startActivity(new Intent(_act, _cls));
                _act.finish();
            /*}else {
                Toast.makeText(SplashScreenActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                circularProgressBar.setVisibility(View.VISIBLE);
                circularProgressBar.setProgressColor(getResources().getColor(R.color.primary_wsg));
                circularProgressBar.setProgressWidth(5);

               final Handler handler = new Handler();

                final Runnable r = new Runnable() {
                    int i=0;
                    public void run() {
                        circularProgressBar.setProgress(i);

                        i++;
                        if(i<=100){
                            handler.postDelayed(this, 200);
                        }
                    }
                };

                handler.postDelayed(r, 200);
            }
*/        }
        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

}