package com.hdp.careup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListOfChildren extends Fragment {

    ArrayList<String[]> childrenDetails;

    public ListOfChildren(){

    }

    public ListOfChildren(ArrayList<String[]> childrenDetails) {
        this();
        this.childrenDetails = childrenDetails;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_of_children, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = getActivity().findViewById(R.id.list_of_children_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ListOfChildrenAdapter adapter = new ListOfChildrenAdapter(childrenDetails);
        recyclerView.setAdapter(adapter);
    }
}
