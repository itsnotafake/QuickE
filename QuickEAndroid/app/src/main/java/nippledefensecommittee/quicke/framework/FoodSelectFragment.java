package nippledefensecommittee.quicke.framework;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nippledefensecommittee.quicke.R;

/**
 * Created by Devin on 8/1/2017.
 */

public class FoodSelectFragment extends Fragment {
    private static final String TAG = FoodSelectFragment.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_foodselect, container, false);
    }
}
