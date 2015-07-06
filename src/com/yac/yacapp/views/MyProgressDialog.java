package com.yac.yacapp.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.yac.yacapp.R;

public class MyProgressDialog extends Dialog {
	private Context context = null;
	private static MyProgressDialog myProgressDialog = null;

	public MyProgressDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;

	}

	protected MyProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public MyProgressDialog(Context context) {
		super(context);
		this.context = context;

	}

	public static MyProgressDialog createDialog(Context context) {
		myProgressDialog = new MyProgressDialog(context, R.style.MyProgressDialog);
		myProgressDialog.setContentView(R.layout.myprogress_dialog);
		myProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		myProgressDialog.setCancelable(true);

		return myProgressDialog;
	}

	public MyProgressDialog setTitile(String strTitle) {
		return myProgressDialog;
	}

	public MyProgressDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) myProgressDialog.findViewById(R.id.id_tv_loadingmsg);

		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}

		return myProgressDialog;
	}

}
