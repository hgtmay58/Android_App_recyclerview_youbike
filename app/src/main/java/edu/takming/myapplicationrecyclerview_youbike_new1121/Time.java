package edu.takming.myapplicationrecyclerview_youbike_new1121;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

public class Time extends AppCompatActivity implements View.OnClickListener {
    TextView timerText;
    Button stopStartButton;
    private TextView money;
    Button reset;
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;
    private int minutes;
    private int hours;
    private int seconds;
    private int m = 10;
    boolean timerStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer);
        timerText = (TextView) findViewById(R.id.timerText);
        money = (TextView) findViewById(R.id.money);
        stopStartButton = (Button) findViewById(R.id.startStopButton);
        reset = (Button) findViewById(R.id.reset);
        timer = new Timer();
        reset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        resetTapped(reset);
    }

    public void resetTapped(View view) {
        AlertDialog.Builder resetAlert = new AlertDialog.Builder(this);
        resetAlert.setTitle("重置時間");
        resetAlert.setMessage("確定要清除時間嗎?");
        resetAlert.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (timerTask != null) {
                    timerTask.cancel();
                    setButtonUI("START", R.color.colorAccent);
                    time = 0.0;
                    timerStarted = false;
                    timerText.setText(formatTime(0, 0, 0));
                    money.setText("時間價錢");
                }
            }
        });
        resetAlert.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        resetAlert.show();
    }

    public void startStopTapped(View view) {
        if (timerStarted == false) {
            timerStarted = true;
            setButtonUI("STOP", R.color.colorAccent);
            startTimer();
        } else {
            timerStarted = false;
            setButtonUI("START", R.color.colorAccent);
            timerTask.cancel();
        }
    }

    private void setButtonUI(String start, int color) {
        stopStartButton.setText(start);
        stopStartButton.setTextColor(ContextCompat.getColor(this, color));
    }

    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        timerText.setText(getTimerText());
                        /*4小時內，每30分鐘10元
                          4~8 小時內每 30 分鐘 20 元
                          超過 8 小時每 30 分鐘 40 元
                          為方便實測，先改用秒數和分鐘計算
                          */
                        if (minutes < 4) {
                            money.setText("未滿四小時，每半小時10元，" + m + "$");
                            if (seconds % 30 == 0) {
                                m = m + 10;
                                money.setText("未滿四小時，每半小時10元，" + m + "$");
                            }
                        } else if (minutes < 8) {
                            if (seconds % 30 == 0) {
                                m = m + 20;
                                money.setText("超過四小時，每半小時20元，" + m + "$");
                            }
                        } else {
                            if (seconds % 30 == 0) {
                                m = m + 40;
                                money.setText("超過八小時，每半小時40元" + m + "$");
                            }
                        }
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private String getTimerText() {
        int rounded = (int) Math.round(time);
        seconds = ((rounded % 86400) % 3600) % 60;
        minutes = ((rounded % 86400) % 3600) / 60;
        hours = ((rounded % 86400) / 3600);
        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
    }
}