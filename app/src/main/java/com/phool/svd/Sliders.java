package com.phool.svd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phool.svd.MainContent.Launch;


/**
 * Created by Paxees on 9/6/2018.
 */

public class Sliders extends AppCompatActivity {
    //    private SessionManager session;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip;
    PreferenceManager preferenceManager;
    TextView sliderTV1, sliderTV2, slider2TV1, slider2TV2, slider3TV1, slider3TV2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slider);
        preferenceManager = new PreferenceManager(this);
        /*============================*/
        if (!preferenceManager.FirstLaunch()) {
            launchMain();
            finish();
        }
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

        btnSkip = (Button) findViewById(R.id.btn_skip);
//        btnNext = (Button) findViewById(R.id.btn_next);
//        new FontContM(Sliders.this,btnNext);
        new FontContM(Sliders.this,btnSkip);
        //slides subServiceSpinnerLayout array
        layouts = new int[]{
                R.layout.viewpager_slide4,
                R.layout.viewpager_slide1,
                R.layout.viewpager_slide2,};

        // adding bottom dots
        addBottomDots(0);

        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    private void launchMain() {
        preferenceManager.setFirstTimeLaunch(false);
        startActivity(new Intent(this, Launch.class));
        finish();
    }
    public void btnSkipClick(View v) {
       launchMain();
       finish();
    }

//    public void btnNextClick(View v) {
//        // checking for last page
//        // if last page home screen will be launched
//        int current = getItem(+1);
//        if (current < layouts.length) {
//            // move to next screen
//            viewPager.setCurrentItem(current);
//        } else {
//            launchHomeScreen();
//        }
//    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
//                btnNext.setText(getString(R.string.str_start));
//                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
//                btnNext.setText(getString(R.string.str_next));
//                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    //addBottomsDots Mthd
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.dot_inactive));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.dot_active));
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    //Rough SignScreen
    private void launchHomeScreen() {

        startActivity(new Intent(this, Launch.class));
        finish();

    }


    public class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;


        public ViewPagerAdapter() {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            /*Typeface contm = Typeface.createFromAsset(getAssets(), "fonts/contm.ttf");
            if (position == 0) {
                sliderTV1 = view.findViewById(R.id.slider1_tv1);
                sliderTV2 = view.findViewById(R.id.slider1_tv2);
                sliderTV1.setTypeface(contm);
                sliderTV2.setTypeface(contm);
            } else if (position == 1) {
                slider2TV1 = view.findViewById(R.id.slider2_tv1);
                slider2TV2 = view.findViewById(R.id.slider2_tv2);
                slider2TV1.setTypeface(contm);
                slider2TV2.setTypeface(contm);
            } else if (position == 2) {
                slider3TV1 = view.findViewById(R.id.slider3_tv1);
                slider3TV2 = view.findViewById(R.id.slider3_tv2);
                slider3TV1.setTypeface(contm);
                slider3TV2.setTypeface(contm);
            }*/
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

/*=====================================*/
    }
}
