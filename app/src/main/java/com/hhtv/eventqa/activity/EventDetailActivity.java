package com.hhtv.eventqa.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.fabtransitionactivity.SheetLayout;
import com.hhtv.eventqa.R;
import com.hhtv.eventqa.fragment.EventDetailFragment;
import com.hhtv.eventqa.fragment.EventQuestionFragment;
import com.hhtv.eventqa.fragment.EventUserFragment;
import com.hhtv.eventqa.helper.UserUltis;
import com.hhtv.eventqa.model.event.EventDetail;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;

/**
 * Created by nienb on 1/3/16.
 */
public class EventDetailActivity extends AppCompatActivity implements SheetLayout.OnFabAnimationEndListener{
    private static final int[] tabIcons = {
            R.drawable.ic_comment,
            R.drawable.ic_info_outline,
            R.drawable.ic_action_user
    };

    private static final int[] toolbarTitle = {
            R.string.tab_question,
            R.string.tab_detail,
            R.string.tab_user
    };

    @Bind(R.id.eventdetail_toolbar)
    Toolbar toolbar;
    @Bind(R.id.eventdetail_container)
    ViewPager mPager;
    @Bind(R.id.eventdetail_tabs)
    TabLayout mTabs;
    @Bind(R.id.eventdetail_fab)
    FloatingActionButton mFab;
    @Bind(R.id.bottom_sheet)
    SheetLayout mSheetLayout;


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void setupTabIcons(){
        for (int i = 0; i < mTabs.getTabCount(); i++){
            mTabs.getTabAt(i).setIcon(tabIcons[i]);

        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(EventDetailActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Confirm leave");
        builder.setMessage("Do you want to leave this event ?");
        builder.setPositiveButton("Stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Leave", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Intent i = new Intent(EventDetailActivity.this, HomeActivity.class);
                startActivity(i);
                EventDetailActivity.this.finish();
            }
        });
        builder.show();
    }

    public void setPage(int position){
        mPager.setCurrentItem(position, true);
    }

    public EventDetail mModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mModel = (EventDetail)getIntent().getSerializableExtra("curEvent");
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimary);

        setSupportActionBar(toolbar);

        setupViewPager(mPager);
        mTabs.setupWithViewPager(mPager);
        setupTabIcons();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            @DebugLog
            public void onClick(View v) {
                if (UserUltis.getUserId(EventDetailActivity.this) != -1) {
                    mSheetLayout.expandFab();
                } else {
                    Toast.makeText(EventDetailActivity.this, "Login to post new question !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mSheetLayout.setFab(mFab);
        mSheetLayout.setFabAnimationEndListener(this);

        getSupportActionBar().setTitle(getResources().getString(toolbarTitle[0]));

    }



    private void setupViewPager(ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(EventQuestionFragment.newInstance(mModel.getId(), UserUltis.getUserId(
                EventDetailActivity.this
        )), "ONE");
        adapter.addFragment(EventDetailFragment.newInstance(mModel), "TWO");
        adapter.addFragment(EventUserFragment.newInstance(), "THREE");
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*SpannableString s = new SpannableString(getResources().getString(toolbarTitle[position]));
                s.setSpan(new TypefaceSpan(MainActivity.this, "titlefont.ttf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
                getSupportActionBar().setTitle(getResources().getString(toolbarTitle[position]));
                switch (position) {
                    case 0:
                        hideFab(false);
                        /*((EventQuestionFragment) adapter.getItem(0)).processLoadQuestion(mModel.getId(), UserUltis.getUserId(
                                EventDetailActivity.this
                        ));*/
                        break;
                    case 1:
                        hideFab(true);
                        break;
                    case 2:
                        hideFab(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public boolean isMFabShown(){
        return mFab.isShown();
    }
    public void reLoadQuestionTab(){
        EventQuestionFragment f = (EventQuestionFragment) ((ViewPagerAdapter)mPager.getAdapter()).getItem(0);
        f.processLoadQuestion(mModel.getId(), UserUltis.getUserId(this));
    }
    public void hideFab(boolean hide){
        if (hide)
            mFab.hide();
        else
            mFab.show();
    }

    public final int POSTQUESTIONREQCODE = 99;
    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(EventDetailActivity.this, PostQuestionActivity.class);
        intent.putExtra("eventid",mModel.getId());
        startActivityForResult(intent, POSTQUESTIONREQCODE);
    }
    @Override
    @DebugLog
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == POSTQUESTIONREQCODE){
            mSheetLayout.contractFab();
            if (data.getBooleanExtra("post",false)){
                try {
                    EventQuestionFragment f = (EventQuestionFragment) ((ViewPagerAdapter)mPager.getAdapter())
                            .getItem(0);
                    f.processUpdateQuestion();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{

            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

}
