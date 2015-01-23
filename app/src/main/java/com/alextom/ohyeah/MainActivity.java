package com.alextom.ohyeah;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.IOException;



public class MainActivity extends Activity implements View.OnClickListener, MediaPlayer
    .OnCompletionListener {

  private MediaPlayer mediaPlayer;
  private View root;
  private ImageButton button;
  private AnimatorSet animatorSet;

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
    
    if (counter < 3) {
      if (animatorSet == null) {
        animatorSet = createMainAnimatorSet();
      }
      counter++;
    } else {
      animatorSet = createAltAnimatorSet();
      animatorSet.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          animatorSet.removeAllListeners();
          breakOutKoolAidMan();
        }
      });
    }
    
    animatorSet.start();
  }

  private void breakOutKoolAidMan() {
    startService(new Intent(this, BreakoutService.class));
  }

  private AnimatorSet createMainAnimatorSet() {
    button.setPivotX(button.getWidth() / 2);
    button.setPivotY(button.getHeight() / 2);
    int animationTime = getResources().getInteger(R.integer.animation_speed);
    
    // Base spin animation
    final ValueAnimator spinAnimation = ObjectAnimator.ofFloat(button, "rotation", 0, 720);
    spinAnimation
        .setDuration(animationTime)
        .setInterpolator(new AccelerateDecelerateInterpolator());
    
    // Scale up and down animation
    AnimatorSet scaleAnimatorSet = new AnimatorSet();
    final ValueAnimator scaleYAnimationUp = ObjectAnimator.ofFloat(button, "scaleY", 1, 2);
    final ValueAnimator scaleXAnimationUp = ObjectAnimator.ofFloat(button, "scaleX", 1, 2);
    scaleYAnimationUp
        .setDuration(animationTime / 2)
        .setInterpolator(new AccelerateInterpolator());
    scaleXAnimationUp
        .setDuration(animationTime / 2)
        .setInterpolator(new AccelerateInterpolator());

    final ValueAnimator scaleYAnimationDown = ObjectAnimator.ofFloat(button, "scaleY", 2, 1);
    final ValueAnimator scaleXAnimationDown = ObjectAnimator.ofFloat(button, "scaleX", 2, 1);
    scaleYAnimationDown
        .setDuration(animationTime / 2)
        .setInterpolator(new DecelerateInterpolator());
    scaleXAnimationDown
        .setDuration(animationTime / 2)
        .setInterpolator(new DecelerateInterpolator());

    scaleAnimatorSet.playTogether(scaleXAnimationUp, scaleYAnimationUp);
    scaleAnimatorSet.play(scaleXAnimationDown).after(scaleXAnimationUp);
    scaleAnimatorSet.play(scaleYAnimationDown).after(scaleYAnimationUp);

    // Create the animation set
    AnimatorSet set = new AnimatorSet();
    set.playTogether(spinAnimation, scaleAnimatorSet);
    return set;
  }

  private AnimatorSet createAltAnimatorSet() {
    button.setPivotX(button.getWidth() / 2);
    button.setPivotY(button.getHeight() / 2);
    int animationTime = 500;

    // Base spin animation
    final ValueAnimator spinAnimation = ObjectAnimator.ofFloat(button, "rotation", 0, 720);
    spinAnimation
        .setDuration(animationTime)
        .setInterpolator(new AccelerateDecelerateInterpolator());

    // Scale up and down animation
    AnimatorSet scaleAnimatorSet = new AnimatorSet();
    final ValueAnimator scaleYAnimationUp = ObjectAnimator.ofFloat(button, "scaleY", 1, 2);
    final ValueAnimator scaleXAnimationUp = ObjectAnimator.ofFloat(button, "scaleX", 1, 2);
    scaleYAnimationUp
        .setDuration(animationTime / 2)
        .setInterpolator(new AccelerateInterpolator());
    scaleXAnimationUp
        .setDuration(animationTime / 2)
        .setInterpolator(new AccelerateInterpolator());

    final ValueAnimator scaleYAnimationDown = ObjectAnimator.ofFloat(button, "scaleY", 2, 0);
    final ValueAnimator scaleXAnimationDown = ObjectAnimator.ofFloat(button, "scaleX", 2, 0);
    scaleYAnimationDown
        .setDuration(animationTime / 2)
        .setInterpolator(new DecelerateInterpolator());
    scaleXAnimationDown
        .setDuration(animationTime / 2)
        .setInterpolator(new DecelerateInterpolator());

    scaleAnimatorSet.playTogether(scaleXAnimationUp, scaleYAnimationUp);
    scaleAnimatorSet.play(scaleXAnimationDown).after(scaleXAnimationUp);
    scaleAnimatorSet.play(scaleYAnimationDown).after(scaleYAnimationUp);

    // Create the animation set
    AnimatorSet set = new AnimatorSet();
    set.playTogether(spinAnimation, scaleAnimatorSet);
    return set;
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
