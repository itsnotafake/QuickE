package nippledefensecommittee.quicke;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * class that represents which price options have been selected
     */
    public static class PriceRange{
        static ArrayList<Boolean> priceRange;

        //this block runs only once, when PriceRange is called for the first time
        static{
            priceRange = new ArrayList<>();
            for(int priceLevel = 0; priceLevel < 4; priceLevel++){
                priceRange.set(priceLevel, false);
            }
        }

        static void updatePriceIndication(int position, boolean isGoodPrice){
            try{
                priceRange.set(position, isGoodPrice);
            }catch(IndexOutOfBoundsException e){
                Log.e(TAG, "error: " + e);
            }
        }

        /**
         * Any changes made to priceRange should be done with 'updatePriceIndication'
         * @return a copy of Price Range
         */
        public boolean getPriceIndicationAt(int position){
            return priceRange.get(position);
        }
    }

    /**
     * Class that represents the search radius selected
     */
    public static class AreaRadius{
        private static int areaRadius;

        //I'm assuming here that the default radius will be 5 miles;
        static{
            areaRadius = 5;
        }

        void setRadius(int radius){
            areaRadius = 5;
        }

        public int getRadius(){
            return areaRadius;
        }
    }
}
