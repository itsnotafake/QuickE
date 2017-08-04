package nippledefensecommittee.quicke.framework;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import nippledefensecommittee.quicke.R;

public class RecommendationActivity extends AppCompatActivity {

    private static final String TAG = RecommendationActivity.class.getName();
    private int mContainerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        mContainerId = R.id.fragment_container;

        initializeSlideShowFragment();
    }

    private void initializeSlideShowFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PictureSlideShowFragment fragment = new PictureSlideShowFragment();
        fragmentTransaction.add(mContainerId, fragment);
        fragmentTransaction.commit();
    }
}
