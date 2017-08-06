package nippledefensecommittee.quicke.framework;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import design.MealSelectAdapter;
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
        View view = inflater.inflate(R.layout.fragment_mealselect, container, false);
        initializeRecyclerView(view);
        return view;
    }

    @Override
    public String toString(){
        return "FoodSelectFragment";
    }

    private void initializeRecyclerView(View view){
        int mealSwitch = getMealSwitch();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.mealselect_recycler);
        MealSelectAdapter adapter = new MealSelectAdapter(getContext(), mealSwitch);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private int getMealSwitch(){
        int mealSwitch;
        boolean b1 = MainActivity.QuickEUsage.getQuickEUsageAt(0);
        boolean b2 = MainActivity.QuickEUsage.getQuickEUsageAt(1);
        Log.e(TAG, "meal is: " + b1 + ", drink is: " + b2);
        if(b1 && b2){
            Log.e(TAG, "hi3");
            mealSwitch = 2;
        }else if(b1){
            Log.e(TAG, "hi");
            mealSwitch = 0;
        }else if(b2){
            Log.e(TAG, "hi2");
            mealSwitch = 1;
        }else{
            throw new IllegalArgumentException("No Food or Drink selected in QuickEUsage");
        }
        return mealSwitch;
    }
}
