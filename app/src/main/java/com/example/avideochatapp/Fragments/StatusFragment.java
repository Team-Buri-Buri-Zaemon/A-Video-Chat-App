package com.example.avideochatapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.avideochatapp.Models.Status;
import com.example.avideochatapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {

    private RecyclerView statusList;
    private Button addStatusButton;

    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatusFragment.
     */
    public static StatusFragment newInstance() {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        // Initialize views
        statusList = view.findViewById(R.id.statusList);
        addStatusButton = view.findViewById(R.id.addStatusButton);

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        statusList.setLayoutManager(layoutManager);

        // Set up adapter for RecyclerView
        List<Status> statuses = new ArrayList<>(); // Dummy list of statuses


        return view;
    }
}
