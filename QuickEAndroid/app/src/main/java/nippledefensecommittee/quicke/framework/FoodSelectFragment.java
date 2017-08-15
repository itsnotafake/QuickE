package nippledefensecommittee.quicke.framework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import design.MealSelectAdapter;
import nippledefensecommittee.quicke.R;
import sync.YelpSearchIntentService;
import utility.ColorState;

/**
 * Created by Devin on 8/1/2017.
 */

public class FoodSelectFragment extends Fragment {
    private static final String TAG = FoodSelectFragment.class.getName();
    private ActionBar mMainActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Overriding onAttach to get Action Bar. The action bar title is changed in onStart()
     * @param activity
     */
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mMainActionBar = ((MainActivity)activity).getSupportActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mealselect, container, false);
        initializeRecyclerView(view);
        initializeButtons(view);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        try {
            mMainActionBar.setTitle(R.string.mealselection_title);
        }catch(NullPointerException e){
            Log.e(TAG, "NullPointerException, title not changed");
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        MainActivity.MealSelection.clear();
        setCheckboxes(false);
        try {
            AppCompatCheckBox selectAll =
                    (AppCompatCheckBox) getView().findViewById(R.id.mealselect_selectall);
            selectAll.setChecked(false);
        }catch(NullPointerException e){
            Log.e(TAG, "Did not find view 'mealselect_selectall, '" + e);
        }
    }

    @Override
    public String toString(){
        return "FoodSelectFragment";
    }

    private void initializeRecyclerView(View view){
        int mealSwitch = getMealSwitch();
        int columns = 2;

        RecyclerView.LayoutManager layoutManager =
                new CustomGridLayoutManager(getContext(), columns);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.mealselect_recycler);
        MealSelectAdapter adapter = new MealSelectAdapter(getContext(), mealSwitch);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initializeButtons(View view){
        final AppCompatCheckBox selectAll =
                (AppCompatCheckBox) view.findViewById(R.id.mealselect_selectall);

        AppCompatImageButton goButton =
                (AppCompatImageButton) view.findViewById(R.id.mealselect_button_go);

        //First set the checkbox's text color
        ColorStateList cswText = ColorState.getButtonColorStateListText(getContext());
        selectAll.setTextColor(cswText);

        //Allow selectAll checkbox to update text onChecked() vs !onChecked()
        //and difference in checked state affect all other checkboxes
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectAll.isChecked()){
                    setCheckboxes(true);
                }else if(!selectAll.isChecked()){
                    setCheckboxes(false);
                }else{
                    throw new NullPointerException(
                            "FoodSelectFragment.initializeButtons().selectAll problems"
                    );
                }
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(runCheck()){
                    Intent yelpSync = new Intent(getContext(), YelpSearchIntentService.class);
                    yelpSync.putExtra(YelpSearchIntentService.OFFSET_MULTIPLIER, 0);
                    getContext().startService(yelpSync);
                }else{
                    Toast.makeText(getContext(), "You must have a cuisine selected to continue",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void setCheckboxes(boolean setChecked){
        try {
            RecyclerView recycler = (RecyclerView) getView().findViewById(R.id.mealselect_recycler);
            MealSelectAdapter adapter = (MealSelectAdapter) recycler.getAdapter();
            int adapterSize = adapter.getItemCount();
            MealSelectAdapter.MealSelectAdapterViewHolder viewHolder;

            for(int i  = 0; i < adapterSize; i++){
                viewHolder = (MealSelectAdapter.MealSelectAdapterViewHolder)
                        recycler.findViewHolderForAdapterPosition(i);
                viewHolder.setChecked(setChecked);
            }
        }catch(NullPointerException e){
            Log.e(TAG, "FoodSelectFragment.setCheckboxes() cannot find view");
        }
    }

    private int getMealSwitch(){
        int mealSwitch;
        boolean b1 = MainActivity.QuickEUsage.getQuickEUsageAt(0);
        boolean b2 = MainActivity.QuickEUsage.getQuickEUsageAt(1);
        if(b1 && b2){
            mealSwitch = 2;
        }else if(b1){
            mealSwitch = 0;
        }else if(b2){
            mealSwitch = 1;
        }else{
            throw new IllegalArgumentException("No Food or Drink selected in QuickEUsage");
        }
        return mealSwitch;
    }

    private boolean runCheck(){
        //if selectedisEmpty is true we want to return false to indicate the runCheck failed
        return !MainActivity.MealSelection.selectedIsEmpty();
    }

    private class CustomGridLayoutManager extends GridLayoutManager {
        private boolean isScrollEnabled = true;

        public CustomGridLayoutManager(Context context, int columns){
            super(context, columns);
        }

        public void setScrollEnabled(boolean flag){
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically(){
            return isScrollEnabled && super.canScrollVertically();
        }
    }
}
