package com.yac.yacapp.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.yac.yacapp.R;
import com.yac.yacapp.activities.AddProductItemsActivity;
import com.yac.yacapp.adapter.UnQualifiedReportsAdapter;
import com.yac.yacapp.domain.CheckReport;
import com.yac.yacapp.domain.Report;

public class ReportsUnqualifiedFragment extends Fragment implements OnClickListener {

	private ListView mListView;
	private CheckReport mCheckReport;
	private List<Report> unQualifiedReports;
	private UnQualifiedReportsAdapter unQualifiedReportsAdapter;
	private Button rightnow_handle_btn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.listview, container, false);
		mListView = (ListView) view.findViewById(R.id.listview);
		rightnow_handle_btn = (Button) view.findViewById(R.id.layout_btn);
		rightnow_handle_btn.setVisibility(View.GONE);
		rightnow_handle_btn.setOnClickListener(this);
		mListView.setDivider(null);
		unQualifiedReports = new ArrayList<Report>();
		unQualifiedReportsAdapter = new UnQualifiedReportsAdapter(getActivity(), unQualifiedReports);
		mListView.setAdapter(unQualifiedReportsAdapter);
		initData();
		return view;
	}

	private void initData() {
		mCheckReport = (CheckReport) getActivity().getIntent().getSerializableExtra("checkReport");
		if (mCheckReport != null) {
			for (Report report : mCheckReport.reports) {
				if (report.status == 0) {
					unQualifiedReports.add(report);
				}
			}
			unQualifiedReportsAdapter.updateData(unQualifiedReports);
			if(unQualifiedReports.size() <= 0){
				rightnow_handle_btn.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.layout_btn) {
			Intent intent = new Intent(getActivity(), AddProductItemsActivity.class);
			intent.putExtra("order_id", mCheckReport.order_id);
			startActivity(intent);
		}
	}

}
