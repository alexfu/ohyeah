package com.alextom.ohyeah;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.IOException;



public class MainActivity extends Activity implements View.OnClickListener, MediaPlayer
    .OnCompletionListener {

  private MediaPlayer mediaPlayer;
  private View root;
  private ImageButton button;

  private int counter = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    root = findViewById(R.id.root);
    initTint();
    initMediaPlayer();
    button = (ImageButton) findViewById(R.id.koolaidman);
    button.setOnClickListener(this);
  }

  private void initTint() {
    SystemBarTintManager tintManager = new SystemBarTintManager(this);
    tintManager.setStatusBarTintEnabled(true);
    tintManager.setNavigationBarTintEnabled(true);
    tintManager.setTintColor(getResources().getColor(R.color.kool_aid_red));
  }

  private void initMediaPlayer() {
    mediaPlayer = MediaPlayer.create(this, R.raw.ohyeah);
    mediaPlayer.setOnCompletionListener(this);
  }

  @Override
  public void onClick(View v) {
    if (mediaPlayer.isPlaying()) {
      mediaPlayer.stop();
      try {
        mediaPlayer.prepare();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    root.setBackgroundResource(R.color.kool_aid_red);
    mediaPlayer.start();
    button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.spin));

    if (counter == 3) {
      findViewById(R.id.special).setVisibility(View.VISIBLE);
    }
    counter++;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mediaPlayer.stop();
    mediaPlayer.release();
  }

  @Override
  public void onCompletion(MediaPlayer mp) {
    root.setBackgroundResource(0);
  }
}
