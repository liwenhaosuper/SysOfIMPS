package com.imps.ui.widget;

import com.imps.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopupMenu extends PopupWindow
{
  public static final int DEFAULT_TITLE_HEIGHT = 30;
  public static final int INVALID_VALUE = -1;
  private static final int SPLIT_HEIGHT = 2;
  private Context context;
  private int height;
  private int[] locationInWindow;
  private LinearLayout popupMenu;
  private PopupMenuDomain[] popupMenuDomains;
  private int width;

  public PopupMenu(Activity paramActivity)
  {
    super(paramActivity);
    this.context = paramActivity;
  }

  public void close()
  {
    Animation localAnimation = AnimationUtils.loadAnimation(this.context, R.anim.menu_out);
    localAnimation.setAnimationListener(new Animation.AnimationListener()
    {
      public void onAnimationEnd(Animation paramAnimation)
      {
        PopupMenu.this.dismiss();
      }

      public void onAnimationRepeat(Animation paramAnimation)
      {
      }

      public void onAnimationStart(Animation paramAnimation)
      {
      }
    });
    this.popupMenu.startAnimation(localAnimation);
  }

  public void init()
  {
    this.popupMenu = new LinearLayout(this.context);
    LinearLayout.LayoutParams localLayoutParams1 = new LinearLayout.LayoutParams(this.width, this.height);
    this.popupMenu.setLayoutParams(localLayoutParams1);
    this.popupMenu.setOrientation(1);
    LinearLayout localLinearLayout1 = new LinearLayout(this.context);
    localLinearLayout1.setGravity(53);
    LinearLayout.LayoutParams localLayoutParams2 = new LinearLayout.LayoutParams(-1, -2);
    localLayoutParams2.rightMargin = 5;
    localLinearLayout1.setLayoutParams(localLayoutParams2);
    ImageView localImageView1 = new ImageView(this.context);
    localImageView1.setImageResource(R.drawable.quickaction2_arrow_up);
    localImageView1.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
    localLinearLayout1.addView(localImageView1);
    int i = localImageView1.getHeight();
    LinearLayout localLinearLayout2 = new LinearLayout(this.context);
    localLinearLayout2.setOrientation(1);
    localLinearLayout2.setPadding(3, 0, 3, 0);
    localLinearLayout2.setBackgroundResource(R.drawable.blue_stroke_bg);
    LinearLayout.LayoutParams localLayoutParams3 = new LinearLayout.LayoutParams(this.width, this.height - i);
    localLinearLayout2.setLayoutParams(localLayoutParams3);
    int j = (this.height - i) / this.popupMenuDomains.length - 2;
    int k = 0;
    PopupMenuDomain[] arrayOfPopupMenuDomain = this.popupMenuDomains;
    int m = arrayOfPopupMenuDomain.length;
    for (int n = 0; ; n++)
    {
      if (n >= m)
      {
        this.popupMenu.addView(localLinearLayout1);
        this.popupMenu.addView(localLinearLayout2);
        setContentView(this.popupMenu);
        setWidth(-2);
        setHeight(-2);
        ColorDrawable localColorDrawable = new ColorDrawable(0);
        setBackgroundDrawable(localColorDrawable);
        update();
        return;
      }
      PopupMenuDomain localPopupMenuDomain = arrayOfPopupMenuDomain[n];
      LinearLayout localLinearLayout3 = new LinearLayout(this.context);
      localLinearLayout3.setOrientation(0);
      localLinearLayout3.setGravity(16);
      int i1 = localPopupMenuDomain.getIcon();
      if (i1 != -1)
      {
        ImageView localImageView2 = new ImageView(this.context);
        localImageView2.setImageResource(i1);
        int i2 = (int)(0.7D * j);
        LinearLayout.LayoutParams localLayoutParams4 = new LinearLayout.LayoutParams(i2, i2);
        localLayoutParams4.leftMargin = 5;
        localImageView2.setLayoutParams(localLayoutParams4);
        localImageView2.setClickable(false);
        localImageView2.setFocusable(false);
        localLinearLayout3.addView(localImageView2);
      }
      TextView localMarqueeTextView = new TextView(this.context);
      localMarqueeTextView.setText(localPopupMenuDomain.getTitle());
      localMarqueeTextView.setTextColor(-1);
      localMarqueeTextView.setTextSize(1, 15.0F);
      localMarqueeTextView.setGravity(16);
      LinearLayout.LayoutParams localLayoutParams5 = new LinearLayout.LayoutParams(-1, j);
      localLayoutParams5.leftMargin = 10;
      localLayoutParams5.rightMargin = 10;
      localMarqueeTextView.setLayoutParams(localLayoutParams5);
      localMarqueeTextView.setClickable(false);
      localMarqueeTextView.setFocusable(false);
      localLinearLayout3.setOnClickListener(localPopupMenuDomain.getListener());
      localLinearLayout3.setBackgroundResource(R.drawable.popup_menu_item_status);
      localLinearLayout3.addView(localMarqueeTextView);
      int i3 = 500 + 500 * k;
      TranslateAnimation localTranslateAnimation = new TranslateAnimation(0, this.locationInWindow[0] + this.width, 2, 0.0F, 1, 0.0F, 1, 0.0F);
      localTranslateAnimation.setDuration(i3);
      AnimationSet localAnimationSet = new AnimationSet(false);
      localAnimationSet.addAnimation(localTranslateAnimation);
      localAnimationSet.setDuration(i3);
      localLinearLayout3.startAnimation(localAnimationSet);
      localLinearLayout2.addView(localLinearLayout3);
      int i4 = this.popupMenuDomains.length - 1;
      if (k != i4)
      {
        ImageView localImageView3 = new ImageView(this.context);
        localImageView3.setImageResource(R.drawable.split_horizonal);
        LinearLayout.LayoutParams localLayoutParams6 = new LinearLayout.LayoutParams(-1, -2);
        localImageView3.setLayoutParams(localLayoutParams6);
        localLinearLayout2.addView(localImageView3);
      }
      k++;
    }
  }

  public void showAtLocation(View paramView, int paramInt1, int paramInt2, int paramInt3)
  {
    super.showAtLocation(paramView, paramInt1, paramInt2, paramInt3);
    this.popupMenu.startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.menu_in));
  }

  public static final class Builder
  {
    private PopupMenu popupMenu;

    public Builder(Context paramContext)
    {
      this.popupMenu = new PopupMenu((Activity)paramContext);
    }

    public PopupMenu create()
    {
      this.popupMenu.init();
      return this.popupMenu;
    }

    public int getHeight()
    {
      return this.popupMenu.height;
    }

    public int[] getLocationInWindow()
    {
      return this.popupMenu.locationInWindow;
    }

    public PopupMenu.PopupMenuDomain[] getPopupMenuDomains()
    {
      return this.popupMenu.popupMenuDomains;
    }

    public int getWidth()
    {
      return this.popupMenu.width;
    }

    public void setHeight(int paramInt)
    {
      this.popupMenu.height = paramInt;
    }

    public void setLocationInWindow(int[] paramArrayOfInt)
    {
      this.popupMenu.locationInWindow = paramArrayOfInt;
    }

    public void setPopupMenuDomains(PopupMenu.PopupMenuDomain[] paramArrayOfPopupMenuDomain)
    {
      this.popupMenu.popupMenuDomains = paramArrayOfPopupMenuDomain;
    }

    public void setWidth(int paramInt)
    {
      this.popupMenu.width = paramInt;
    }
  }

  public static class PopupMenuDomain
  {
    private int icon;
    private int index;
    private View.OnClickListener listener;
    private String title;

    public PopupMenuDomain(int paramInt1, int paramInt2, String paramString, View.OnClickListener paramOnClickListener)
    {
      this.index = paramInt1;
      this.icon = paramInt2;
      this.title = paramString;
      this.listener = paramOnClickListener;
    }

    public int getIcon()
    {
      return this.icon;
    }

    public int getIndex()
    {
      return this.index;
    }

    public View.OnClickListener getListener()
    {
      return this.listener;
    }

    public String getTitle()
    {
      return this.title;
    }

    public void setIcon(int paramInt)
    {
      this.icon = paramInt;
    }

    public void setIndex(int paramInt)
    {
      this.index = paramInt;
    }

    public void setListener(View.OnClickListener paramOnClickListener)
    {
      this.listener = paramOnClickListener;
    }

    public void setTitle(String paramString)
    {
      this.title = paramString;
    }
  }
}
