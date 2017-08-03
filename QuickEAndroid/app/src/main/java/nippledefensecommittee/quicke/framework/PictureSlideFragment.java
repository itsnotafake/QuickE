package nippledefensecommittee.quicke.framework;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nippledefensecommittee.quicke.R;

/**
 * Created by ETD on 8/2/2017.
 */

public class PictureSlideFragment extends Fragment {

    private static final String TAG = PictureSlideFragment.class.getName();
    public static final String PAGE_NUM = "page";
    private int pageNum;

    public static PictureSlideFragment create(int pageNumber) {
        PictureSlideFragment fragment = new PictureSlideFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_NUM, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PictureSlideFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNum = getArguments().getInt(PAGE_NUM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture_slide, container, false);
    }

    public int getPageNum() {
        return pageNum;
    }
}
