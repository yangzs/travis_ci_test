package com.yac.yacapp.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yac.yacapp.R;
import com.yac.yacapp.fragments.HomeFragment;
import com.yac.yacapp.fragments.HomeFragment.MyViewpagerPositionListener;
import com.yac.yacapp.fragments.MyPageFragment;
import com.yac.yacapp.icounts.BaseFragmentActivity;
import com.yac.yacapp.icounts.ICounts;

@SuppressWarnings("unused")
public class MainActivity extends BaseFragmentActivity implements ICounts, OnClickListener, MyViewpagerPositionListener {

	private FrameLayout mFrame_content;
	private LinearLayout mLlMenu;
	private LinearLayout ll_nav_home, ll_plus_add, ll_nav_my;
	private ImageView mImage_nav_home, mImage_plus_add, mImage_nav_mypage;
	private TextView tv_nav_home, tv_select_service, tv_mine;

	private HomeFragment mHomeFragment;
	private MyPageFragment myPageFragment;
	private View mParentView;
	private PopupWindow mPopupWindow;
	private LinearLayout mPopupLL;
	private ImageView mToggleImg;

	private FragmentTransaction mTransaction;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mParentView = getLayoutInflater().inflate(R.layout.activity_main, null);
		setContentView(mParentView);
		initView();
		initFragments();
		initPopup();
	}

	private void initPopup() {
		mPopupWindow = new PopupWindow(this);
		View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
		mPopupLL = (LinearLayout) view.findViewById(R.id.ll_popup);
		mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
		mPopupWindow.setHeight(LayoutParams.MATCH_PARENT);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.popup_bg)));
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setContentView(view);
		mPopupWindow.setAnimationStyle(R.style.mypopup_anim);

		view.findViewById(R.id.ll_keeper_pick).setOnClickListener(this);
		view.findViewById(R.id.ll_door_service).setOnClickListener(this);
		view.findViewById(R.id.ll_validate_car).setOnClickListener(this);
		view.findViewById(R.id.ll_keeper_test).setOnClickListener(this);
		view.findViewById(R.id.ll_verify_loss).setOnClickListener(this);
		view.findViewById(R.id.ll_emergency_rescue).setOnClickListener(this);
		mToggleImg = (ImageView) view.findViewById(R.id.toggle_popup_img);
		mToggleImg.setOnClickListener(this);
		mPopupLL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissServicePopWin();
			}
		});
	}

	private void initView() {
		mFrame_content = (FrameLayout) findViewById(R.id.frame_content);
		mLlMenu = (LinearLayout) findViewById(R.id.llMenu);
		ll_nav_home = (LinearLayout) findViewById(R.id.ll_nav_home);
		ll_plus_add = (LinearLayout) findViewById(R.id.ll_plus_add);
		ll_nav_my = (LinearLayout) findViewById(R.id.ll_nav_my);
		mImage_nav_home = (ImageView) findViewById(R.id.image_nav_home);
		mImage_plus_add = (ImageView) findViewById(R.id.image_plus_add);
		mImage_nav_mypage = (ImageView) findViewById(R.id.image_nav_my);
		tv_nav_home = (TextView) findViewById(R.id.tv_nav_home);
		tv_select_service = (TextView) findViewById(R.id.tv_select_service);
		tv_mine = (TextView) findViewById(R.id.tv_mine);

		ll_nav_home.setOnClickListener(this);
		ll_plus_add.setOnClickListener(this);
		ll_nav_my.setOnClickListener(this);
	}

	private void initFragments() {
		mTransaction = getSupportFragmentManager().beginTransaction();
		mHomeFragment = new HomeFragment();
		mHomeFragment.setListener(this);
		myPageFragment = new MyPageFragment();
		mTransaction.add(R.id.frame_content, mHomeFragment);
//		mTransaction.add(R.id.frame_content, myPageFragment);
//		mTransaction.hide(myPageFragment).show(mHomeFragment);
		mTransaction.commit();
		mImage_nav_home.setSelected(true);
		mImage_nav_mypage.setSelected(false);
		tv_nav_home.setTextColor(getResources().getColor(R.color.yac_green));
		tv_mine.setTextColor(getResources().getColor(R.color.black));
	}

	private Fragment mContent;

	public void switchContent(Fragment from, Fragment to) {
		if (mContent != to) {
			mContent = to;
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			if (!to.isAdded()) { // 先判断是否被add过
				transaction.hide(from).add(R.id.frame_content, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
			} else {
				transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
			}
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, SixServiceActivity.class);
		switch (v.getId()) {
		case R.id.ll_nav_home:
			switchContent(myPageFragment, mHomeFragment);
			mImage_nav_home.setSelected(true);
			mImage_nav_mypage.setSelected(false);
			tv_nav_home.setTextColor(getResources().getColor(R.color.yac_green));
			tv_mine.setTextColor(getResources().getColor(R.color.black));
			break;
		case R.id.ll_plus_add:
			showServicePopupWin();
			break;
		case R.id.ll_nav_my:
			switchContent(mHomeFragment, myPageFragment);
			mImage_nav_mypage.setSelected(true);
			mImage_nav_home.setSelected(false);
			tv_mine.setTextColor(getResources().getColor(R.color.yac_green));
			tv_nav_home.setTextColor(getResources().getColor(R.color.black));
			break;
		case R.id.ll_keeper_pick:
			intent.addFlags(KEEPER_PICK_SERVICE);
			startActivity(intent);
			dismissServicePopWin();
			break;
		case R.id.ll_door_service:
			intent.addFlags(DOOR_SERVICE);
			startActivity(intent);
			dismissServicePopWin();
			break;
		case R.id.ll_validate_car:
			intent.addFlags(VALIDATE_CAR_SERVICE);
			startActivity(intent);
			dismissServicePopWin();
			break;
		case R.id.ll_keeper_test:
			intent.addFlags(KEEPER_TEST_SERVICE);
			startActivity(intent);
			dismissServicePopWin();
			break;
		case R.id.ll_verify_loss:
			intent.addFlags(VERIFY_LOSS_SERVICE);
			startActivity(intent);
			dismissServicePopWin();
			break;
		case R.id.ll_emergency_rescue:
			intent.addFlags(EMERGENCY_RESCUE_SERVICE);
			startActivity(intent);
			dismissServicePopWin();
			break;
		case R.id.toggle_popup_img:
			dismissServicePopWin();
			break;
		default:
			break;
		}
	}

	private void showServicePopupWin() {
		AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
		interpolator.getInterpolation(1f);
		Animation animationTranslate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.activity_translate_in);
		animationTranslate.setInterpolator(interpolator);
		mPopupLL.startAnimation(animationTranslate);
		Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.img_rotate);
		animation.setFillAfter(true);
		mToggleImg.startAnimation(animation);
		mPopupWindow.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);
	}

	private void dismissServicePopWin() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mToggleImg.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.img_rotate_out));
			Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.activity_translate_out);
			animation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					mPopupWindow.dismiss();
				}
			});
			mPopupLL.startAnimation(animation);
		}
	}

	@Override
	public void pageHasChanged(int position) {
		if (position == 0) {
			// TODO 加上动画
			mLlMenu.setVisibility(View.INVISIBLE);
		} else {
			mLlMenu.setVisibility(View.VISIBLE);
		}
	}
}
