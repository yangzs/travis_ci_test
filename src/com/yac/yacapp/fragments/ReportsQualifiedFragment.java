package com.yac.yacapp.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yac.yacapp.R;
import com.yac.yacapp.adapter.QualifiedReportsAdapter;
import com.yac.yacapp.domain.CheckReport;
import com.yac.yacapp.domain.Report;

public class ReportsQualifiedFragment extends Fragment {
	private ListView mListView;
	private CheckReport mCheckReport;
	private List<Report> qualifiedReports;
	private QualifiedReportsAdapter qualifiedReportsAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.listview, container, false);
		mListView = (ListView) view.findViewById(R.id.listview);
		qualifiedReports = new ArrayList<Report>();
		qualifiedReportsAdapter = new QualifiedReportsAdapter(getActivity(), qualifiedReports);
		mListView.setAdapter(qualifiedReportsAdapter);
		initData();
		return view;
	}

	private void initData() {
		mCheckReport = (CheckReport) getActivity().getIntent().getSerializableExtra("checkReport");
		if(mCheckReport != null){
			for(Report report : mCheckReport.reports){
				if(report.status == 1){
					qualifiedReports.add(report);
				}
			}
			qualifiedReportsAdapter.updateData(qualifiedReports);
		}
	}
}
