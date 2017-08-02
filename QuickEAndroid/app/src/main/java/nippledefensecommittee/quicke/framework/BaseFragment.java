package nippledefensecommittee.quicke.framework;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import nippledefensecommittee.quicke.R;

/**
 * Created by Devin on 7/26/2017.
 */

public class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        initializeButtons(view);
        //initializeSeekBar(view);
        return view;
    }

    /**
     * Method designed to move code out of onCreateView. Simply assigns to the necessary buttons
     * the correct onButtonClick methods
     *
     * @param view - View for finding the buttons within the view
     */
    private void initializeButtons(View view) {
        final AppCompatImageButton button_eat = (AppCompatImageButton)
                view.findViewById(R.id.main_button_eat);
        final AppCompatImageButton button_drink = (AppCompatImageButton)
                view.findViewById(R.id.main_button_drink);

        final AppCompatButton button_price1 = (AppCompatButton)
                view.findViewById(R.id.main_button_price_1);
        final AppCompatButton button_price2 = (AppCompatButton)
                view.findViewById(R.id.main_button_price_2);
        final AppCompatButton button_price3 = (AppCompatButton)
                view.findViewById(R.id.main_button_price_3);
        final AppCompatButton button_price4 = (AppCompatButton)
                view.findViewById(R.id.main_button_price_4);

        final AppCompatImageButton button_go = (AppCompatImageButton)
                view.findViewById(R.id.main_button_go);

        ColorStateList cswBG = getButtonColorStateListBG(getContext());
        button_eat.setSupportBackgroundTintList(cswBG);
        button_drink.setSupportBackgroundTintList(cswBG);
        button_price1.setSupportBackgroundTintList(cswBG);
        button_price2.setSupportBackgroundTintList(cswBG);
        button_price3.setSupportBackgroundTintList(cswBG);
        button_price4.setSupportBackgroundTintList(cswBG);

        ColorStateList cswText = getButtonColorStateListText(getContext());
        button_price1.setTextColor(cswText);
        button_price2.setTextColor(cswText);
        button_price3.setTextColor(cswText);
        button_price4.setTextColor(cswText);

        button_eat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean activated = button_eat.isActivated();
                button_eat.setActivated(!activated);
                MainActivity.QuickEUsage.updateQuickEUsage(0, button_eat.isActivated());
            }
        });
        button_drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean activated = button_drink.isActivated();
                button_drink.setActivated(!activated);
                MainActivity.QuickEUsage.updateQuickEUsage(1, button_drink.isActivated());
            }
        });

        button_price1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean activated = button_price1.isActivated();
                button_price1.setActivated(!activated);
                MainActivity.PriceRange.updatePriceIndication(0, button_price1.isActivated());
            }
        });
        button_price2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean activated = button_price2.isActivated();
                button_price2.setActivated(!activated);
                MainActivity.PriceRange.updatePriceIndication(1, button_price2.isActivated());
            }
        });
        button_price3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean activated = button_price3.isActivated();
                button_price3.setActivated(!activated);
                MainActivity.PriceRange.updatePriceIndication(2, button_price3.isActivated());
            }
        });
        button_price4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean activated = button_price4.isActivated();
                button_price4.setActivated(!activated);
                MainActivity.PriceRange.updatePriceIndication(3, button_price4.isActivated());
            }
        });

        button_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FoodSelectFragment();
                FragmentChangeListener fcl = (FragmentChangeListener) getActivity();
                fcl.replaceFragment(fragment, true);
            }
        });
    }

    private static ColorStateList getButtonColorStateListBG(Context context) {
        int primaryDark = context.getResources().getColor(R.color.colorPrimaryDark85Opac);
        int accent = context.getResources().getColor(R.color.colorAccent);

        return new ColorStateList(
                new int[][]{{android.R.attr.state_activated}, {-android.R.attr.state_activated}},
                new int[]{accent, primaryDark});
    }

    private static ColorStateList getButtonColorStateListText(Context context) {
        int primaryDark = context.getResources().getColor(R.color.colorPrimaryDark);
        int accent = context.getResources().getColor(R.color.colorAccent);

        return new ColorStateList(
                new int[][]{{android.R.attr.state_activated}, {-android.R.attr.state_activated}},
                new int[]{primaryDark, accent});
    }

    private void initializeSeekBar(View view) {
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.main_seekbar_distance);
    }
}
