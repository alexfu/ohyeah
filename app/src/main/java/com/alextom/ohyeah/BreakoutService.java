package com.alextom.ohyeah;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.io.IOException;

public class BreakoutService extends Service {
  
  private WindowManager windowManager;
  private int animationTime;
  private int touchSlop;
  private AnimatorSet mainAnimator;
  private MediaPlayer mediaPlayer;
  private ImageView koolAidMan;
  private ImageView breakoutGlass;

  @Override
  public void onCreate() {
    super.onCreate();
    windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    animationTime = getResources().getInteger(R.integer.animation_speed);
    touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();    
    
    koolAidMan = new ImageView(this);
    koolAidMan.setImageResource(R.drawable.free_kool_aid_man);
    breakoutGlass = new ImageView(this);
    breakoutGlass.setImageResource(R.drawable.breakout_glass);
    breakoutGlass.setVisibility(View.GONE);

    final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT);
    
    AnimatorSet introAnimator = createIntroAnimator(koolAidMan);
    introAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationStart(Animator animation) {
        breakoutGlass.postDelayed(new Runnable() {
          @Override
          public void run() {
            mediaPlayer = MediaPlayer.create(BreakoutService.this, R.raw.glass_smash);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
              @Override
              public void onCompletion(MediaPlayer mp) {
                mediaPlayer.setOnCompletionListener(null);
                mediaPlayer.release();
                mediaPlayer = null;
              }
            });
            mediaPlayer.start();
            
            breakoutGlass.setVisibility(View.VISIBLE);
            breakoutGlass.animate()
                .setDuration(2000)
                .alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                  @Override
                  public void onAnimationEnd(Animator animation) {
                    animation.removeAllListeners();
                    windowManager.removeViewImmediate(breakoutGlass);
                    breakoutGlass = null;
                  }
                });
            
            setupTouchListeners(koolAidMan);
            breakoutGlass.removeCallbacks(this);
          }
        }, animationTime/2);
      }
    });

    windowManager.addView(breakoutGlass, params);
    windowManager.addView(koolAidMan, params);
    introAnimator.start();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
  
  private void setupTouchListeners(View view) {
    view.setOnTouchListener(new View.OnTouchListener() {
      private int initialX, initialY;
      private float initialTouchX, initialTouchY;
      private boolean isMoving;
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            isMoving = false;

            WindowManager.LayoutParams params = (WindowManager.LayoutParams) v.getLayoutParams();
            initialX = params.x;
            initialY = params.y;
            initialTouchX = event.getRawX();
            initialTouchY = event.getRawY();
            break;
          case MotionEvent.ACTION_MOVE:
            // Calculate distance moved from original point
            int xDelta = (int) Math.abs(event.getRawX() - initialTouchX);
            int yDelta = (int) Math.abs(event.getRawY() - initialTouchY);

            // Consider this a move if we moved more than the suggested touchSlop
            if (xDelta > touchSlop || yDelta > touchSlop) {
              isMoving = true;
              params = (WindowManager.LayoutParams) v.getLayoutParams();
              params.x = (int) (initialX + event.getRawX() - initialTouchX);
              params.y = (int) (initialY + event.getRawY() - initialTouchY);
              windowManager.updateViewLayout(v, params);
            }
            break;
          case MotionEvent.ACTION_UP:
            if (!isMoving) {
              v.performClick();
            }
            break;
        }

        return true;
      }
    });
    
    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mainAnimator == null) {
          mainAnimator = createMainAnimatorSet(v);
        }
        
        if (mediaPlayer == null) {
          mediaPlayer = MediaPlayer.create(BreakoutService.this, R.raw.ohyeah);
        }
        
        if (mediaPlayer.isPlaying()) {
          mediaPlayer.stop();
          try {
            mediaPlayer.prepare();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

        mediaPlayer.start();
        mainAnimator.start();
      }
    });
  }
  
  private AnimatorSet createIntroAnimator(View view) {
    ValueAnimator spinAnimation = ObjectAnimator.ofFloat(view, "rotation", 0, 720);
    spinAnimation.setDuration(animationTime)
        .setInterpolator(new AccelerateDecelerateInterpolator());

    ValueAnimator scaleXAnimationUp = ObjectAnimator.ofFloat(view, "scaleX", 0, 1);
    ValueAnimator scaleYAnimationUp = ObjectAnimator.ofFloat(view, "scaleY", 0, 1);

    scaleXAnimationUp
        .setDuration(animationTime)
        .setInterpolator(new AccelerateDecelerateInterpolator());
    scaleYAnimationUp
        .setDuration(animationTime)
        .setInterpolator(new AccelerateDecelerateInterpolator());

    AnimatorSet set = new AnimatorSet();
    set.playTogether(spinAnimation, scaleXAnimationUp, scaleYAnimationUp);
    return set;
  }

  private AnimatorSet createMainAnimatorSet(View view) {
    view.setPivotX(view.getWidth() / 2);
    view.setPivotY(view.getHeight() / 2);
    int animationTime = getResources().getInteger(R.integer.animation_speed);

    // Base spin animation
    final ValueAnimator spinAnimation = ObjectAnimator.ofFloat(view, "rotation", 0, 720);
    spinAnimation
        .setDuration(animationTime)
        .setInterpolator(new AccelerateDecelerateInterpolator());

    // Scale up and down animation
    AnimatorSet scaleAnimatorSet = new AnimatorSet();
    final ValueAnimator scaleYAnimationUp = ObjectAnimator.ofFloat(view, "scaleY", 1, 0.5f);
    final ValueAnimator scaleXAnimationUp = ObjectAnimator.ofFloat(view, "scaleX", 1, 0.5f);
    scaleYAnimationUp
        .setDuration(animationTime / 2)
        .setInterpolator(new AccelerateInterpolator());
    scaleXAnimationUp
        .setDuration(animationTime / 2)
        .setInterpolator(new AccelerateInterpolator());

    final ValueAnimator scaleYAnimationDown = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1);
    final ValueAnimator scaleXAnimationDown = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1);
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
  public void onDestroy() {
    super.onDestroy();
    if (mediaPlayer != null) {
      mediaPlayer.stop();
      mediaPlayer.release();
    }
    
    windowManager.removeView(koolAidMan);
    if (breakoutGlass != null) {
      windowManager.removeView(breakoutGlass);
    }
  }
}
