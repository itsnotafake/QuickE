package utility;

import android.content.Context;
import android.content.res.ColorStateList;

import nippledefensecommittee.quicke.R;

/**
 * Created by Devin on 8/7/2017.
 */

public class ColorState {

    public static ColorStateList getButtonColorStateListBG(Context context) {
        int primaryDark = context.getResources().getColor(R.color.colorPrimaryDark85Opac);
        int accent = context.getResources().getColor(R.color.colorAccent);

        return new ColorStateList(
                new int[][]{{android.R.attr.state_activated}, {-android.R.attr.state_activated}},
                new int[]{accent, primaryDark});
    }

    public static ColorStateList getButtonColorStateListText(Context context) {
        int primaryDark = context.getResources().getColor(R.color.colorPrimaryDark);
        int accent = context.getResources().getColor(R.color.colorAccent);

        return new ColorStateList(
                new int[][]{{android.R.attr.state_activated}, {-android.R.attr.state_activated}},
                new int[]{primaryDark, accent});
    }
}
