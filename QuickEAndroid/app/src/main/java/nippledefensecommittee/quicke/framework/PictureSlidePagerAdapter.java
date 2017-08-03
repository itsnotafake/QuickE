package nippledefensecommittee.quicke.framework;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import nippledefensecommittee.quicke.R;

/**
 * Created by ETD on 8/1/2017.
 */

public class PictureSlidePagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Integer[] images; //TODO: Figure out how to populate images array with yelp photo results
    private int NUM_SLIDES = 5;

    public PictureSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public PictureSlidePagerAdapter(FragmentManager fm, Integer[] img) {
        super(fm);
        images = img;
    }

    @Override
    public Fragment getItem(int position) {
        return PictureSlideFragment.create(position);
    }

    @Override
    public int getCount() {
        return NUM_SLIDES;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.fragment_picture_slide, null);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.sliderButton);
        imageButton.setImageResource(images[position]);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
