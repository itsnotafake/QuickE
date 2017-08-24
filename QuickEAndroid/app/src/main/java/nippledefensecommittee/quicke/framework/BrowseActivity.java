package nippledefensecommittee.quicke.framework;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import nippledefensecommittee.quicke.R;
import utility.BusinessList;
import utility.DineList;

/**
 * Created by Devin on 8/18/2017.
 */

public class BrowseActivity extends AppCompatActivity implements FragmentChangeListener{
    private static final String TAG = BrowseActivity.class.getName();
    private Context mContext;
    private int mContainerId;
    public TextView mListTotal;

    private static final String FRAGTAG = "fragtag";
    private static final String BROWSETAG = "BrowseFragment";
    private static final String DINELISTTAG = "DineListFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_browse);
        mContainerId = R.id.browse_fragment_container;

        initializeAppBar();
        initializeFragment(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.browse_menu, menu);

        final View list = menu.findItem(R.id.action_list).getActionView();
        mListTotal = (TextView) list.findViewById(R.id.actionbar_listtotal);
        updateListTotal(DineList.size());
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, DineList.print());
                try {
                    if (Integer.valueOf(mListTotal.getText().toString()) > 0) {
                        DineListFragment fragment = new DineListFragment();
                        replaceFragment(fragment, true);
                    }
                }catch(NumberFormatException e){
                    Log.e(TAG, "numberformatexception");
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_list:
                return true;
            default:
                Log.e(TAG, "Unrecognized menu item id");
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        Fragment browseFragment = getSupportFragmentManager()
                .findFragmentByTag(BROWSETAG);
        Fragment dineListFragment = getSupportFragmentManager()
                .findFragmentByTag(DINELISTTAG);
        if(browseFragment != null && browseFragment.isVisible()){
            getSupportFragmentManager().putFragment(outState, FRAGTAG, browseFragment);
        }else if(dineListFragment != null && dineListFragment.isVisible()) {
            getSupportFragmentManager().putFragment(outState, FRAGTAG, dineListFragment);
        }
    }

    /**
     * Appbar initialization removed from onCreate to avoid clutter
     */
    private void initializeAppBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.browse_toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.main_text));
        setSupportActionBar(toolbar);
    }

    /**
     * Loading base fragment into Activity. Removed logic from onCreate
     * to avoid clutter
     * @param savedInstanceState used to determine which fragment to instantiate. If the screen
     * was rotated when displaying SwipeViewFragment, then we want to display SwipeViewFragment
     */
    private void initializeFragment(Bundle savedInstanceState){
        if(savedInstanceState != null) {
            Fragment fragment = getSupportFragmentManager()
                    .getFragment(savedInstanceState, FRAGTAG);
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(fragment)
                    .commit();
        }else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(mContainerId, new BrowseFragment(), BROWSETAG)
                    .commit();
        }
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(mContainerId, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

    /**
     * updates the number being displayed in the actionbar, represents the number
     * of businesses selected and added to the DineList. Called on the main thread, so
     * that we can call this asynchronously
     * @param listTotal new number to be displayed on actionbar
     */
    public void updateListTotal(final int listTotal) {
        if (mListTotal == null){
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listTotal == 0)
                    mListTotal.setVisibility(View.INVISIBLE);
                else {
                    mListTotal.setVisibility(View.VISIBLE);
                    mListTotal.setText(Integer.toString(listTotal));
                }
            }
        });
    }
}
