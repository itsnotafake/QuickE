package nippledefensecommittee.quicke.framework;

import android.support.v4.app.Fragment;

/**
 * Created by Devin on 8/1/2017.
 */

interface FragmentChangeListener {
    void replaceFragment(Fragment fragment, boolean addToBackStack);
}
