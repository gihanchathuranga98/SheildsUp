package com.hdp.careup;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class Tracking extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        Intent intent = new Intent(getContext(), ChildLocationService.class);
//        getContext().startService(intent);

//        AIzaSyCS55_os8ntzl7bcYAaUA8p-xwZiZTqr9A

        getActivity().findViewById(R.id.live_tracking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.profile_container, new LiveTracking(), "TESTING_LOCATION")
                        .commit();
            }
        });

//        getActivity().findViewById(R.id.live_tracking).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getParentFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.profile_container, new TestingLocation(), "TESTING_LOCATION")
//                        .commit();
//            }
//        });

    }
}