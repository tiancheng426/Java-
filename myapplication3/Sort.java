package com.example.myapplication3;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Comparator;

public class Sort {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void sort(Gameview2 topview){
        ArrayList<CardView> list=new ArrayList<>();
        for(int i=0;i<topview.getChildCount();i++){
            CardView view=(CardView)topview.getChildAt(i);
            list.add(view);
        }
        list.sort((o1, o2) -> o1.cardvalue-o2.cardvalue);
        topview.removeAllViews();
        for(int i=0;i<list.size();i++){
            topview.addView(list.get(i));
        }
    }
}
