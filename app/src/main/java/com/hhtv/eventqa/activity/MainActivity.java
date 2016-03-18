package com.hhtv.eventqa.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.github.fabtransitionactivity.SheetLayout;
import com.hhtv.eventqa.R;
import com.hhtv.eventqa.fragment.EventDetailFragment;
import com.hhtv.eventqa.fragment.EventHighestVoteFragment;
import com.hhtv.eventqa.fragment.EventQuestionFragment;
import com.hhtv.eventqa.helper.ultis.UserUltis;
import com.hhtv.eventqa.model.event.EventDetail;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SheetLayout.OnFabAnimationEndListener {

    private static final int[] tabIcons = {
            R.drawable.ic_trending_up,
            R.drawable.ic_comment,
            R.drawable.ic_info_outline
    };

    private static final int[] toolbarTitle = {
            R.string.tab_most_voted,
            R.string.tab_question,
            R.string.tab_detail
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

    public void setupTabIcons() {
        for (int i = 0; i < mTabs.getTabCount(); i++) {
            mTabs.getTabAt(i).setIcon(tabIcons[i]);

        }
    }


    public EventDetail mModel;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mModel = (EventDetail) getIntent().getSerializableExtra("curEvent");
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.eventdetail_toolbar);
        setSupportActionBar(toolbar);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimary);*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupViewPager(mPager);
        mTabs.setupWithViewPager(mPager);
        setupTabIcons();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            @DebugLog
            public void onClick(View v) {
                mSheetLayout.expandFab();
                /*if (UserUltis.getUserId(MainActivity.this) != -1) {

                } else {
                    Toast.makeText(MainActivity.this, "Login to post new question !", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
        mSheetLayout.setFab(mFab);
        mSheetLayout.setFabAnimationEndListener(this);

        getSupportActionBar().setTitle(getResources().getString(toolbarTitle[0]));
        setupNavigationView();


    }

    private void setupNavigationView() {
        if (UserUltis.getUserId(this) == -1) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer_notsigned);

            navigationView.getHeaderView(0).findViewById(R.id.nav_header_ava).setVisibility(View.GONE);
            navigationView.getHeaderView(0).findViewById(R.id.nav_header_name).setVisibility(View.GONE);
            navigationView.getHeaderView(0).findViewById(R.id.nav_header_email).setVisibility(View.GONE);
            navigationView.getHeaderView(0).findViewById(R.id.nav_header_circle).setVisibility(View.GONE);
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);
            navigationView.getHeaderView(0).findViewById(R.id.nav_header_ava).setVisibility(View.VISIBLE);
            navigationView.getHeaderView(0).findViewById(R.id.nav_header_name).setVisibility(View.VISIBLE);
            navigationView.getHeaderView(0).findViewById(R.id.nav_header_email).setVisibility(View.VISIBLE);
            navigationView.getHeaderView(0).findViewById(R.id.nav_header_circle).setVisibility(View.VISIBLE);
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_ava)).setText(UserUltis.getUserName(this)
                    .substring(0, 1).toUpperCase());
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_name)).setText(UserUltis.getUserName(this));
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_email)).setText(UserUltis.getUserEmail(this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelTimer();
    }

    Timer mTimer;
    final int TIMER_DELAY = 10 * 1000;

    public void initTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("MYTAG", "timer excecute at: " + DateTime.now().toString("hh:mm:ss"));
                reloadContent();
            }
        }, TIMER_DELAY, TIMER_DELAY);
    }

    public void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(EventHighestVoteFragment.newInstance(mModel.getId(), UserUltis.getUserId(
                MainActivity.this
        )), "ONE");
        adapter.addFragment(EventQuestionFragment.newInstance(mModel.getId(), UserUltis.getUserId(
                MainActivity.this
        )), "TWO");
        adapter.addFragment(EventDetailFragment.newInstance(mModel), "THREE");
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(getResources().getString(toolbarTitle[position]));
                switch (position) {
                    case 0:
                        hideFab(false);
                        break;
                    case 1:
                        hideFab(false);
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

    public boolean isMFabShown() {
        return mFab.isShown();
    }

    public void hideFab(boolean hide) {
        if (hide && mFab.isShown()) {
            mFab.hide();
            return;
        }
        if (!hide && !mFab.isShown()) {
            mFab.show();
        }

    }

    public final int POSTQUESTIONREQCODE = 99;
    public final int SIGNINREQCODE = 100;

    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(MainActivity.this, PostQuestionActivity.class);
        intent.putExtra("eventid", mModel.getId());
        startActivityForResult(intent, POSTQUESTIONREQCODE);
    }

    public void reloadContent(){
        EventHighestVoteFragment hf = (EventHighestVoteFragment) ((ViewPagerAdapter) mPager.getAdapter())
                .getItem(0);
        hf.processLoadQuestion(mModel.getId(),
                UserUltis.getUserId(this), false);
        EventQuestionFragment f = (EventQuestionFragment) ((ViewPagerAdapter) mPager.getAdapter())
                .getItem(1);
        f.processUpdateQuestion();
        EventDetailFragment ed = (EventDetailFragment) ((ViewPagerAdapter)mPager.getAdapter())
                .getItem(2);
        ed.updateEventDetail();
    }

    @Override
    @DebugLog
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case POSTQUESTIONREQCODE:
                mSheetLayout.contractFab();
                boolean isPost = data.getBooleanExtra("post",false);
                if (isPost){
                    EventQuestionFragment f = (EventQuestionFragment) ((ViewPagerAdapter) mPager.getAdapter())
                            .getItem(1);
                    f.instantInsert(data.getStringExtra("body"));
                    /*EventQuestionFragment f2 = (EventQuestionFragment) ((ViewPagerAdapter) mPager.getAdapter())
                            .getItem(0);
                    f2.instantInsert(data.getStringExtra("body"));*/
                    reloadContent();
                }
                break;
            case SIGNINREQCODE:
                setupNavigationView();

                break;
            default:
                break;
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FragmentManager f = getSupportFragmentManager();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (f.getBackStackEntryCount() != 0) {
            f.popBackStack();
        } else {
            //super.onBackPressed();
            new MaterialDialog
                    .Builder(MainActivity.this)
                    .title(R.string.exit)
                    .content(R.string.do_you_want_to_exit)
                    .negativeText(R.string.dismiss)
                    .theme(Theme.LIGHT)
                    .positiveText(R.string.exit)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.dismiss();
                            MainActivity.this.finish();
                            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                        }
                    }).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_signout) {
            new MaterialDialog.Builder(MainActivity.this)
                    .title(R.string.confirm_signout)
                    .content(R.string.would_you_like_to_sign_out)
                    .negativeText(R.string.dismiss)
                    .positiveText(R.string.signout)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.dismiss();
                            UserUltis.logout(MainActivity.this);
                            setupNavigationView();
                        }
                    }).show();
        } else if (id == R.id.nav_signin) {
            Intent i = new Intent(this, UserSigninActivity.class);
            startActivityForResult(i, SIGNINREQCODE);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
