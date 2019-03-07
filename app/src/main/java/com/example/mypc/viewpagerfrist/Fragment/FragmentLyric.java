package com.example.mypc.viewpagerfrist.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mypc.viewpagerfrist.Model.Music;
import com.example.mypc.viewpagerfrist.R;

import java.util.ArrayList;

public class FragmentLyric extends Fragment {

    View view;
    public TextView textView;
    String s="";

    public static FragmentLyric newInstance(String danh){
        FragmentLyric fragment=new FragmentLyric();
        Bundle bundle=new Bundle();
        bundle.putString("danh",danh);
        fragment.setArguments(bundle);
        return fragment;
    }

    public FragmentLyric(){
        Log.d("YYY","xong rou'");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getArguments()!=null){
            s=getArguments().getString("danh");
        }

        view=inflater.inflate(R.layout.fragment_lyric,container,false);
        textView=view.findViewById(R.id.txtDanh);
        textView.setText(s);
        Log.d("TTT","FragmentLyric");
        return view;
    }

    public void setTextView(String a){
        textView.setText(a);
    }
}
