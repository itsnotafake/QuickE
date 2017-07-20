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

    //static class that represents which price options have been selected
    public static class PriceRange{

        static ArrayList<Boolean> priceRange;

        static{
            priceRange = new ArrayList<>();
            for(int i = 0; i < 4; i++){
                priceRange.set(i, false);
            }
        }

        public static void updatePriceIndication(int position, boolean isGoodPrice){
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
}
