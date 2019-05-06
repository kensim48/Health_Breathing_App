package com.example.kensi.healthbreathingdemo;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
    private Button reset, start;
    private ProgressBar volume0, volume1;
    private Boolean checkStart = false;
    private View rect0, rect1, rect2, rect3;
    private ImageView tick, next, prev, cross;

    private void changeHighlight(final int boxCase){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (boxCase){
                    case 0:
                        tick.setBackground(null);
                        next.setBackground(null);
                        prev.setBackground(null);
                        cross.setBackground(null);
                        break;
                    case 1:
                        tick.setBackground(getResources().getDrawable(R.drawable.image_border));
                        break;
                    case 2:
                        next.setBackground(getResources().getDrawable(R.drawable.image_border));
                        break;
                    case 3:
                        prev.setBackground(getResources().getDrawable(R.drawable.image_border));
                        break;
                    case 4:
                        cross.setBackground(getResources().getDrawable(R.drawable.image_border));
                        break;

                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.start);
        reset = findViewById(R.id.reset);
        volume0 = findViewById(R.id.volume0);
        rect0 = findViewById(R.id.rect0);
        rect1 = findViewById(R.id.rect1);
        rect2 = findViewById(R.id.rect2);
        rect3 = findViewById(R.id.rect3);
        tick = findViewById(R.id.tick);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
        cross = findViewById(R.id.cross);

        final CheckAudio checkAudio = new CheckAudio();



        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkStart) {
                    rect0.setBackgroundColor(Color.parseColor("#00FF00"));
                    rect1.setBackgroundColor(Color.parseColor("#FF0000"));
                    rect2.setBackgroundColor(Color.parseColor("#FF0000"));
                    rect3.setBackgroundColor(Color.parseColor("#FF0000"));
                    checkAudio.execute("");
                    checkStart = true;
                }
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rect0.setBackgroundColor(Color.parseColor("#00FF00"));
                rect1.setBackgroundColor(Color.parseColor("#FF0000"));
                rect2.setBackgroundColor(Color.parseColor("#FF0000"));
                rect3.setBackgroundColor(Color.parseColor("#FF0000"));
                checkAudio.blockStatus = new int[]{1, 0, 0, 0};
                checkAudio.state = 0;
            }
        });
    }

    private class CheckAudio extends AsyncTask<String, Void, String> {
        private SoundMeter soundMeter;
        private int volLevel;
        private double volLevelDoub;
        private int state = 0;
        private boolean hitVol = false, hitCheck = false;
        private View rect0, rect1, rect2, rect3;
        private int highestVol = 0;
        public int[] blockStatus = {1, 0, 0, 0};
        private boolean isBreatheOut = false;


        @Override
        protected String doInBackground(String... params) {
            while (true) {
                if (isCancelled())
                    break;
                volLevelDoub = soundMeter.getAmplitude() / 320;
                volLevel = (int) volLevelDoub;
                Log.d("tag", String.valueOf(volLevel));
                if (isBreatheOut) {
                    volume1.setProgress(volLevel);
                    volume0.setProgress(0);
                } else {
                    volume0.setProgress(volLevel);
                    volume1.setProgress(0);
                }
                try {
                    Thread.sleep(2);
                } catch (Exception e) {

                }
                if (volLevel >= 18) {
                    hitVol = true;
                    highestVol = volLevel > highestVol ? volLevel : highestVol;
                }
                if (hitVol && volLevel < 10) {
                    hitCheck = true;
                    hitVol = false;
                    isBreatheOut = !isBreatheOut;
                    changeHighlight(0);
                }
                if (hitCheck) {
                    if (isBreatheOut) {
                        if (highestVol >= 75) {
                            changeHighlight(1);
                            blockStatus[state] = 3;
                        } else if (highestVol >= 51) {
                            changeHighlight(2);
                            state += 1;
                            if (state > 3)
                                state = 0;
                            for (int i = 0; i <= 3; i++) {
                                if (i == state) {
                                    if (blockStatus[i] != 2) {
                                        blockStatus[i] = 1;
                                    } else {
                                        blockStatus[i] = 3;
                                    }
                                } else {
                                    if (blockStatus[i] == 3) {
                                        blockStatus[i] = 2;
                                    } else if (blockStatus[i] != 2) {
                                        blockStatus[i] = 0;
                                    }
                                }
                            }
                        }
                    } else {
                        if (highestVol >= 76) {
                            changeHighlight(4);
                            blockStatus[state] = 1;
                        } else if (highestVol >= 51) {
                            changeHighlight(3);
                            state -= 1;
                            if (state < 0)
                                state = 3;
                            for (int i = 0; i <= 3; i++) {
                                if (i == state) {
                                    if (blockStatus[i] != 2) {
                                        blockStatus[i] = 1;
                                    } else {
                                        blockStatus[i] = 3;
                                    }
                                } else {
                                    if (blockStatus[i] == 3) {
                                        blockStatus[i] = 2;
                                    } else if (blockStatus[i] != 2) {
                                        blockStatus[i] = 0;
                                    }
                                }
                            }
                        }
                    }
                    if (blockStatus[0] == 0) {
                        rect0.setBackgroundColor(Color.parseColor("#FF0000"));
                    } else if (blockStatus[0] == 1) {
                        rect0.setBackgroundColor(Color.parseColor("#00FF00"));
                    } else if (blockStatus[0] == 2) {
                        rect0.setBackgroundColor(Color.parseColor("#0000FF"));
                    } else if (blockStatus[0] == 3) {
                        rect0.setBackgroundColor(Color.parseColor("#00A0FF"));
                    }
                    if (blockStatus[1] == 0) {
                        rect1.setBackgroundColor(Color.parseColor("#FF0000"));
                    } else if (blockStatus[1] == 1) {
                        rect1.setBackgroundColor(Color.parseColor("#00FF00"));
                    } else if (blockStatus[1] == 2) {
                        rect1.setBackgroundColor(Color.parseColor("#0000FF"));
                    } else if (blockStatus[1] == 3) {
                        rect1.setBackgroundColor(Color.parseColor("#00A0FF"));
                    }
                    if (blockStatus[2] == 0) {
                        rect2.setBackgroundColor(Color.parseColor("#FF0000"));
                    } else if (blockStatus[2] == 1) {
                        rect2.setBackgroundColor(Color.parseColor("#00FF00"));
                    } else if (blockStatus[2] == 2) {
                        rect2.setBackgroundColor(Color.parseColor("#0000FF"));
                    } else if (blockStatus[2] == 3) {
                        rect2.setBackgroundColor(Color.parseColor("#00A0FF"));
                    }
                    if (blockStatus[3] == 0) {
                        rect3.setBackgroundColor(Color.parseColor("#FF0000"));
                    } else if (blockStatus[3] == 1) {
                        rect3.setBackgroundColor(Color.parseColor("#00FF00"));
                    } else if (blockStatus[3] == 2) {
                        rect3.setBackgroundColor(Color.parseColor("#0000FF"));
                    } else if (blockStatus[3] == 3) {
                        rect3.setBackgroundColor(Color.parseColor("#00A0FF"));
                    }
                    hitCheck = false;
                    highestVol = 0;
                }


            }
            return "done";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
            volume0 = findViewById(R.id.volume0);
            volume1 = findViewById(R.id.volume1);
            rect0 = findViewById(R.id.rect0);
            rect1 = findViewById(R.id.rect1);
            rect2 = findViewById(R.id.rect2);
            rect3 = findViewById(R.id.rect3);
            soundMeter = new SoundMeter();
            soundMeter.start();
            Log.d("myTag", "running");
        }

        @Override
        protected void onProgressUpdate(Void... values) {


        }
    }
}