package design;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import nippledefensecommittee.quicke.R;
import nippledefensecommittee.quicke.framework.MainActivity;

/**
 * Created by Devin on 8/4/2017.
 */

public class MealSelectAdapter extends
        RecyclerView.Adapter<MealSelectAdapter.MealSelectAdapterViewHolder> {
    private static final String TAG = MealSelectAdapter.class.getName();
    private static final int VIEW_TYPE_NORMAL = 0;

    /**
     * 0 = Food only
     * 1 = Beverages only
     * 2 = Food and Beverages
     */
    private static int mMealSwitch;
    private Context mContext;

    private static int[] mFoodSelection;
    private static int[] mBeverageSelection;

    public MealSelectAdapter(Context context, int mealSwitch){
        mContext = context;
        mMealSwitch = mealSwitch;

        mFoodSelection = MainActivity.MealSelection.getFoodSelection();
        mBeverageSelection = MainActivity.MealSelection.getBeverageSelection();

        Log.e(TAG, "mealSwitch: " + mealSwitch);
    }

    @Override
    public MealSelectAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        LinearLayout view = (LinearLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.mealselect_listitem, viewGroup, false);
        return new MealSelectAdapterViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final MealSelectAdapterViewHolder viewHolder, final int position){
        viewHolder.setMeal(getMealAt(position));
    }

    private String getMealAt(int position){
        String meal;
        switch(mMealSwitch){
            case 0:
                meal = mContext.getText(mFoodSelection[position]).toString();
                break;
            case 1:
                meal = mContext.getText(mBeverageSelection[position]).toString();
                break;
            case 2:
                if(position < mFoodSelection.length){
                    meal = mContext.getText(mFoodSelection[position]).toString();
                }else{
                    int beveragePosition = position - mFoodSelection.length;
                    meal = mContext.getText(mBeverageSelection[beveragePosition]).toString();
                }
                break;
            default:
                throw new IllegalArgumentException
                        ("Illegal Argument: Invalid mealSwitch value");
        }
        //Log.e(TAG, "position: " + position + ", meal: " + meal);
        return meal;
    }

    @Override
    public int getItemCount(){
        switch(mMealSwitch){
            case 0:
                return MainActivity.MealSelection.getFoodSelection().length;
            case 1:
                return MainActivity.MealSelection.getBeverageSelection().length;
            case 2:
                return (MainActivity.MealSelection.getFoodSelection().length +
                MainActivity.MealSelection.getBeverageSelection().length);
            default:
                throw new IllegalArgumentException("Illegal Argument: Invalid mealSwitch value");
        }
    }

    @Override
    public int getItemViewType(int position){
        return VIEW_TYPE_NORMAL;
    }

    class MealSelectAdapterViewHolder extends RecyclerView.ViewHolder{
        private TextView mMeal_TV;

        MealSelectAdapterViewHolder(View view, int viewType){
            super(view);
            mMeal_TV = (TextView) view.findViewById(R.id.meal_type);
        }

        void setMeal(String meal){
            mMeal_TV.setText(meal);
        }
    }
}
