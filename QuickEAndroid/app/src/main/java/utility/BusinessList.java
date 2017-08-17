package utility;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Devin on 8/17/2017.
 */

public class BusinessList {

    private static ArrayList<Business> businessList;

    static{
        businessList = new ArrayList<>();
    }

    public static void addBusiness(Business b){
        businessList.add(b);
    }

    public static Business getBusiness(int position){
        return businessList.get(position);
    }

    public static void removeBusiness(int position){
        businessList.remove(position);
    }

    public static void clear(){
        businessList.clear();
    }

    public static int size(){
        return businessList.size();
    }

    public static void randomize(){
        Collections.shuffle(businessList);
    }

    public static void addBulk(String bulkList){

    }
}
