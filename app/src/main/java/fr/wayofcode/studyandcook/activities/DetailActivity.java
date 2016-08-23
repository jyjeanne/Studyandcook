package fr.wayofcode.studyandcook.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.toolbox.ImageLoader;
import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.samples.apps.iosched.ui.SlidingTabLayout;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.nineoldandroids.view.ViewHelper;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

import fr.wayofcode.studyandcook.R;
import fr.wayofcode.studyandcook.databases.DbHelperFavorites;
import fr.wayofcode.studyandcook.databases.DbHelperRecipes;
import fr.wayofcode.studyandcook.fragments.PagerFragment;
import fr.wayofcode.studyandcook.utils.AppSingleton;
import fr.wayofcode.studyandcook.utils.ImageLoaderFromDrawable;
import fr.wayofcode.studyandcook.utils.Utils;

public class DetailActivity extends BaseActivity implements
        ObservableScrollViewCallbacks, View.OnClickListener {

    // Create view objects
    private ImageView mImgImageRecipe;
    private View mOverlayView;
    private View mTitleView;
    //private AdView mAdView;
    private TextView mTxtRecipeName, mTxtRecipeInfo;
    private TouchInterceptionFrameLayout mInterceptionLayout;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    private FloatingActionButton mFabFavorite;
    private FrameLayout mPagerWrapper,mHeader;
    private CircleProgressBar mPrgLoading;

    // Create dimen variables
    private int mSlop;
    private int mFlexibleSpaceHeight;
    private int mTabHeight;
    private boolean mScrolled;

    // Create data variables
    public static String[] sPagerTitles;
    private String mSelectedId;
    public static String sRecipeId, sCategoryId, sCategoryName,sRecipePrice, sRecipeName,
            sCookTime, sServings, sSummary, sIngredients, sSteps, sRecipeImage;
    private String mCookTimeLabel, mMoneySymbol,mMinutesLabel, mServingsLabel, mPersonsLabel, mPersonLabel;

    // Create instance of database helper
    private DbHelperRecipes mDBhelperRecipes;
    private DbHelperFavorites mDBhelperFavorites;

    private Resources res;
    private boolean mIsAdmobVisible;

    private ImageLoaderFromDrawable mImageLoaderFromDrawable;
    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        // Get string and dimension resources from strings.xml and dimens.xml
        res = this.getResources();
        sPagerTitles = res.getStringArray(R.array.pagers_detail);
        mCookTimeLabel = res.getString(R.string.cook_time);
        mMoneySymbol= res.getString(R.string.price_dollar_symbol);
        mMinutesLabel = res.getString(R.string.minutes);
        mServingsLabel = res.getString(R.string.servings);
        mPersonsLabel = res.getString(R.string.persons);
        mPersonLabel = res.getString(R.string.person);
        mFlexibleSpaceHeight = res.getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mTabHeight = res.getDimensionPixelSize(R.dimen.tab_height);
        int mImageWidth = res.getDimensionPixelSize(R.dimen.flexible_space_image_height);
        int mImageHeight = res.getDimensionPixelSize(R.dimen.flexible_space_image_height);

        mImageLoaderFromDrawable = new ImageLoaderFromDrawable(this, mImageWidth, mImageHeight);
        mImageLoader = AppSingleton.getInstance(this).getImageLoader();
        // Stored selected ID that passed from previous activity
        Intent i = getIntent();
        mSelectedId = i.getStringExtra(Utils.ARG_KEY);

        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());

        // Connect view objects with view ids in xml
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mImgImageRecipe = (ImageView) findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);
        //mAdView = (AdView) findViewById(R.id.adView);
        mTitleView = findViewById(R.id.title);
        mTxtRecipeName = (TextView) findViewById(R.id.txtRecipeName);
        mTxtRecipeInfo = (TextView) findViewById(R.id.txtRecipeInfo);
        mFabFavorite = (FloatingActionButton) findViewById(R.id.fabFavorite);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mPagerWrapper = (FrameLayout) findViewById(R.id.pager_wrapper);
        mHeader = (FrameLayout) findViewById(R.id.header);
        mPrgLoading = (CircleProgressBar) findViewById(R.id.prgLoading);
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mInterceptionLayout = (TouchInterceptionFrameLayout) findViewById(R.id.container);

        // Set elevation value of header view
        ViewCompat.setElevation(mHeader, getResources().getDimension(R.dimen.toolbar_elevation));

        // Set pager below flexible space
        mPagerWrapper.setPadding(0, mFlexibleSpaceHeight, 0, 0);

        //mIsAdmobVisible = Utils.admobVisibility(mAdView, Utils.IS_ADMOB_VISIBLE);

        // Set toolbar as actionbar and up navigation button and set toolbar title to null
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(null);

        // Set progress circle loading color
        mPrgLoading.setColorSchemeResources(R.color.accent_color);

        // Set click listener to fab button
        mFabFavorite.setOnClickListener(this);


        // Load ad in background using asynctask class
       // new SyncShowAd(mAdView).execute();

        // Set tab layout configuration
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent_color));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mPager);
        ((FrameLayout.LayoutParams) slidingTabLayout.getLayoutParams()).topMargin =
                mFlexibleSpaceHeight - mTabHeight;

        ViewConfiguration vc = ViewConfiguration.get(this);
        mSlop = vc.getScaledTouchSlop();
        mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);
        ScrollUtils.addOnGlobalLayoutListener(mInterceptionLayout, new Runnable() {
            @Override
            public void run() {
                updateFlexibleSpace();
            }
        });

        // Create database helper objects
        mDBhelperRecipes = new DbHelperRecipes(this);
        mDBhelperFavorites = new DbHelperFavorites(this);

        // Create database of recipes and favorites
        try {
            mDBhelperRecipes.createDataBase();
            mDBhelperFavorites.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        // Open recipes and favorites database
        mDBhelperRecipes.openDataBase();
        mDBhelperFavorites.openDataBase();

        // If recipe is available in favorites database set fab icon with favorite icon,
        // else with favorite outline icon
        if(mDBhelperFavorites.isDataAvailable(mSelectedId)) {
            mFabFavorite.setIconDrawable(new IconicsDrawable(getApplicationContext())
                    .icon(GoogleMaterial.Icon.gmd_favorite)
                    .color(Color.WHITE));
        }else{
            mFabFavorite.setIconDrawable(new IconicsDrawable(getApplicationContext())
                    .icon(GoogleMaterial.Icon.gmd_favorite_outline)
                    .color(Color.WHITE));
        }

        // Get data from recipes database in asynctask class
        new SyncGetData().execute();

        // Handle menu item in toolbar
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case android.R.id.home:
                        finish();
                        return true;
                    case R.id.menuShare:
                        createShareIntent();
                        return true;
                    default:
                        return true;
                }
            }
        });

    }

    // Asynctask class to load admob in background
    public class SyncShowAd extends AsyncTask<Void, Void, Void> {

        AdView ad;
        AdRequest adRequest, interstitialAdRequest;
        InterstitialAd interstitialAd;
        int interstitialTrigger;

        public SyncShowAd(AdView ad){
            this.ad = ad;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Check ad visibility. If visible, create adRequest
            if(mIsAdmobVisible) {
                // Create an ad request
                if (Utils.IS_ADMOB_IN_DEBUG) {
                    adRequest = new AdRequest.Builder().
                            addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                } else {
                    adRequest = new AdRequest.Builder().build();
                }

                // When interstitialTrigger equals ARG_TRIGGER_VALUE, display interstitial ad
                interstitialAd = new InterstitialAd(DetailActivity.this);
                interstitialAd.setAdUnitId(DetailActivity.this.getResources().getString(R.string.interstitial_ad_unit_id));
                interstitialTrigger = Utils.loadPreferences(Utils.ARG_TRIGGER, DetailActivity.this);
                if(interstitialTrigger == Utils.ARG_TRIGGER_VALUE) {
                    if(Utils.IS_ADMOB_IN_DEBUG) {
                        interstitialAdRequest = new AdRequest.Builder()
                                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                .build();
                    }else {
                        interstitialAdRequest = new AdRequest.Builder().build();
                    }
                    Utils.savePreferences(Utils.ARG_TRIGGER, 0, DetailActivity.this);
                }else{
                    Utils.savePreferences(Utils.ARG_TRIGGER, (interstitialTrigger+1),
                            DetailActivity.this);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Check ad visibility. If visible, display ad banner and interstitial
            if(mIsAdmobVisible) {
                // Start loading the ad
                ad.loadAd(adRequest);

                if (interstitialTrigger == Utils.ARG_TRIGGER_VALUE) {
                    // Start loading the ad
                    interstitialAd.loadAd(interstitialAdRequest);

                    // Set the AdListener
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            if (interstitialAd.isLoaded()) {
                                interstitialAd.show();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode) {

                        }

                        @Override
                        public void onAdClosed() {

                        }

                    });
                }
            }

        }
    }

    // Method to add actionbar item menu layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    // Method to handle fab button click
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fabFavorite:
                if(mDBhelperFavorites.isDataAvailable(mSelectedId)) {
                    // If recipe has already available in favorites database.
                    // display dialog to confirm remove it
                    new MaterialDialog.Builder(DetailActivity.this)
                            .title(R.string.confirm)
                            .content(R.string.confirm_message)
                            .positiveText(R.string.remove)
                            .negativeText(R.string.cancel)
                            .positiveColorRes(R.color.primary_color)
                            .negativeColorRes(R.color.primary_color)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    boolean result = mDBhelperFavorites.deleteRecipeFromFavorites(mSelectedId);
                                    if (result) {
                                        new SnackBar.Builder(DetailActivity.this)
                                                .withMessage(getString(R.string.success_remove))
                                                .show();
                                        mFabFavorite.setImageResource(R.mipmap.ic_favorite_outline_white_36dp);
                                        dialog.dismiss();
                                    }
                                }
                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }else{
                    // If recipes is not available in favorites database. add to favorites database
                    boolean result = mDBhelperFavorites.addRecipeToFavorites(sRecipeId, sCategoryId,
                            sRecipeName,sRecipePrice, sCookTime, sServings, sSummary,
                            sIngredients, sSteps, sRecipeImage);

                    if(result){
                        new SnackBar.Builder(DetailActivity.this)
                                .withMessage(getString(R.string.success_add_data))
                                .show();
                        mFabFavorite.setImageResource(R.mipmap.ic_favorite_white_36dp);
                    }
                }
                break;

        }
    }

    // Asynctask class to handle load data from database in background
    public class SyncGetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // When data still retrieve from database display loading view
            // and hide other view
            mPrgLoading.setVisibility(View.VISIBLE);
            mPagerWrapper.setVisibility(View.GONE);
            mHeader.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Get data from database
            getDataFromDatabase(mSelectedId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(sRecipeImage.toLowerCase().contains("http")){
                mImageLoader.get(sRecipeImage,
                        com.android.volley.toolbox.ImageLoader.getImageListener(mImgImageRecipe,
                                R.mipmap.empty_photo, R.mipmap.empty_photo));

            } else {
                // When finished retrieve data from database, display data to the views
                int image = getResources().getIdentifier(sRecipeImage, "drawable", getPackageName());
                mImageLoaderFromDrawable.loadBitmap(image, mImgImageRecipe);
            }


            String personLabel = sServings.equals("1") ? mPersonLabel: mPersonsLabel;
            mTxtRecipeName.setText(sRecipeName);
            mTxtRecipeInfo.setText(mCookTimeLabel + " " + sCookTime + " " + mMinutesLabel + ", " +
                    mServingsLabel + " " + sServings + " " + personLabel+ ", " +mMoneySymbol+sRecipePrice);

            // Hide loading view and display other views
            mPrgLoading.setVisibility(View.GONE);
            mPagerWrapper.setVisibility(View.VISIBLE);
            mHeader.setVisibility(View.VISIBLE);

        }
    }


    // Method to share content to other apps
    Intent createShareIntent() {
        String intro = res.getString(R.string.intro_message);
        String extra = res.getString(R.string.extra_message);
        String gPlayURL = res.getString(R.string.google_play_url);
        String appName = res.getString(R.string.app_name);
        String here = res.getString(R.string.here);
        String message = intro+" "+sRecipeName+extra+" "+appName+" "+here+" "+gPlayURL;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(i, getResources().getString(R.string.share_to)));
        return i;
    }

    // Method to retrieve data from database
    public void getDataFromDatabase(String id){
        ArrayList<Object> row = mDBhelperRecipes.getRecipeDetail(id);

        sRecipeId = row.get(0).toString();
        sRecipeName = row.get(1).toString();
        sRecipePrice = Utils.formatRecipePrice(row.get(2).toString());
        sCategoryId = row.get(3).toString();
        sCategoryName = row.get(4).toString();
        sCookTime = row.get(5).toString();
        sServings = row.get(6).toString();
        sSummary = row.get(7).toString();
        sIngredients = row.get(8).toString();
        sSteps = row.get(9).toString();
        sRecipeImage = row.get(10).toString();

    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {

        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {

        super.onResume();
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // Method to handle physical back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Call transition when physical back button pressed
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    // Method to get parent activity
    @Override
    public Intent getSupportParentActivityIntent() {
        Intent parentIntent= getIntent();
        // Get the parent class name
        String className = parentIntent.getStringExtra(Utils.ARG_PARENT_ACTIVITY);

        Intent newIntent=null;
        try {
            // Open parent activity.
            newIntent = new Intent(this, Class.forName(getApplicationContext().getPackageName() +
                    "." + className));

            overridePendingTransition(R.anim.open_main, R.anim.close_next);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newIntent;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private TouchInterceptionFrameLayout.TouchInterceptionListener mInterceptionListener =
            new TouchInterceptionFrameLayout.TouchInterceptionListener() {
                @Override
                public boolean shouldInterceptTouchEvent
                        (MotionEvent ev, boolean moving, float diffX, float diffY) {
                    if (!mScrolled && mSlop < Math.abs(diffX) && Math.abs(diffY) < Math.abs(diffX)) {
                        // Horizontal scroll is maybe handled by ViewPager
                        return false;
                    }

                    Scrollable scrollable = getCurrentScrollable();
                    if (scrollable == null) {
                        mScrolled = false;
                        return false;
                    }

                    // If interceptionLayout can move, it should intercept.
                    // And once it begins to move, horizontal scroll shouldn't work any longer.
                    int flexibleSpace = mFlexibleSpaceHeight - mTabHeight;
                    int translationY = (int) ViewHelper.getTranslationY(mInterceptionLayout);
                    boolean scrollingUp = 0 < diffY;
                    boolean scrollingDown = diffY < 0;
                    if (scrollingUp) {
                        if (translationY < 0) {
                            mScrolled = true;
                            return true;
                        }
                    } else if (scrollingDown) {
                        if (-flexibleSpace < translationY) {
                            mScrolled = true;
                            return true;
                        }
                    }
                    mScrolled = false;
                    return false;
                }

                @Override
                public void onDownMotionEvent(MotionEvent ev) {
                }

                @Override
                public void onMoveMotionEvent(MotionEvent ev, float diffX, float diffY) {
                    int flexibleSpace = mFlexibleSpaceHeight - mTabHeight;
                    float translationY = ScrollUtils.getFloat(
                            ViewHelper.getTranslationY(mInterceptionLayout) + diffY, -flexibleSpace, 0);
                    updateFlexibleSpace(translationY);
                    if (translationY < 0) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)
                                mInterceptionLayout.getLayoutParams();
                        lp.height = (int) (-translationY + getScreenHeight());
                        mInterceptionLayout.requestLayout();
                    }
                }

                @Override
                public void onUpOrCancelMotionEvent(MotionEvent ev) {
                    mScrolled = false;
                }
            };

    private Scrollable getCurrentScrollable() {
        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return null;
        }
        View view = fragment.getView();
        if (view == null) {
            return null;
        }
        return (Scrollable) view.findViewById(R.id.scroll);
    }

    private void updateFlexibleSpace() {
        updateFlexibleSpace(ViewHelper.getTranslationY(mInterceptionLayout));
    }

    private void updateFlexibleSpace(float translationY) {
        ViewHelper.setTranslationY(mInterceptionLayout, translationY);
        int minOverlayTransitionY = getActionBarSize() - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mImgImageRecipe,
                ScrollUtils.getFloat(-translationY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        float flexibleRange = mFlexibleSpaceHeight - getActionBarSize();

        ViewHelper.setAlpha(mOverlayView,
                ScrollUtils.getFloat(-translationY / flexibleRange, 0, 1));


        ViewHelper.setPivotY(mTitleView, 0);
    }


    private Fragment getCurrentFragment() {
        return mPagerAdapter.getItemAt(mPager.getCurrentItem());
    }


    /**
     * This adapter provides two types of fragments as an example.
     * {@linkplain #createItem(int)} should be modified if you use this example for your app.
     */
    private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        protected Fragment createItem(int position) {
            Fragment f;
            final int pattern = position % 5;
            switch (pattern) {
                case 0:
                    f = PagerFragment.newInstance(sSummary);
                    break;
                case 1:
                    f = PagerFragment.newInstance(sIngredients);
                    break;
                case 2:
                    f = PagerFragment.newInstance(sSteps);
                    break;
                default:
                    f = PagerFragment.newInstance(sSummary);
                    break;
            }
            return f;
        }

        @Override
        public int getCount() {
            return sPagerTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return sPagerTitles[position];
        }
    }
}
