package utility;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Devin on 8/23/2017.
 */

public class DineList {

    private static final String TAG = DineList.class.getName();
    private static ArrayList<Business> dineList;

    static{
        dineList = new ArrayList<>();
    }

    public static void add(Business b){
        dineList.add(b);
    }

    public static Business get(int position){
        return dineList.get(position);
    }

    public static void clear(){
        dineList.clear();
    }

    public static int size(){
        return dineList.size();
    }

    public static String print(){
        return dineList.toString();
    }
}
