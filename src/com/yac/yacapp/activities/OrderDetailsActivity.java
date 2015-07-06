package com.yac.yacapp.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.yac.yacapp.R;
import com.yac.yacapp.domain.Car;
import com.yac.yacapp.domain.Order4Create;
import com.yac.yacapp.domain.Product;
import com.yac.yacapp.domain.Product4Create;
import com.yac.yacapp.domain.ProductPart;
import com.yac.yacapp.icounts.App;
import com.yac.yacapp.icounts.BaseActivity;
import com.yac.yacapp.icounts.ICounts;
import com.yac.yacapp.utils.Utils;
import com.yac.yacapp.views.MyProgressDialog;
import com.yac.yacapp.views.MyToast;

public class OrderDetailsActivity extends BaseActivity implements ICounts, OnClickListener {

	private static final int GET_PRODUCTS_CODE = 0;
	private LinearLayout mBack_ll;
	private TextView mBack_tv, mActionbar_title_tv;
	private ImageView mClose_img;
	private ImageView mCar_brand_img;
	private TextView mCar_licence_tv, mCar_brand_tv;

	private ImageView mBeizhu_img;
	private LinearLayout mProduct_ll;
	private ListView mProduct_listview;
	private EditText mBeizhu_tv;

	private RelativeLayout mBottom_rl;
	private TextView mTotal_price_tv;
	private Button mSettle_accounts_btn;

	private List<Product> mRequiredProducts;
	private List<ProductPart> mProductParts;

	private int fromCode;
	private AsyncHttpClient mClient;
	private MyProgressDialog myProgressDialog;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_PRODUCTS_CODE:
				String content = (String) msg.obj;
				parserProductsJson(content);
				break;
			case PARSERJSON_SUCCESS:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				invilateContentView();
				break;
			case PARSERJSON_FAILURE:
			case NET_FAILURE:
				if (myProgressDialog != null && myProgressDialog.isShowing()) {
					myProgressDialog.dismiss();
				}
				String message = (String) msg.obj;
				handlePARSERJSON_NET_FAILURE(message);
				break;
			default:
				break;
			}
		};
	};

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_details);
		fromCode = getIntent().getFlags();
		initView();
		initData();
		getNetData();
	}

	private void initView() {
		findViewById(R.id.head_rl).setOnClickListener(mSoftInputOnClickListener);
		mBack_ll = (LinearLayout) findViewById(R.id.back_ll);
		mBack_tv = (TextView) findViewById(R.id.back_tv);
		mActionbar_title_tv = (TextView) findViewById(R.id.actionbar_title_tv);
		mClose_img = (ImageView) findViewById(R.id.close_img);
		mCar_brand_img = (ImageView) findViewById(R.id.car_brand_img);
		mCar_licence_tv = (TextView) findViewById(R.id.car_licence_tv);
		mCar_brand_tv = (TextView) findViewById(R.id.car_brand_tv);
		mBeizhu_img = (ImageView) findViewById(R.id.beizhu_img);
		mProduct_ll = (LinearLayout) findViewById(R.id.product_ll);
		mProduct_listview = (ListView) findViewById(R.id.product_listview);
		mBeizhu_tv = (EditText) findViewById(R.id.beizhu_tv);
		mBottom_rl = (RelativeLayout) findViewById(R.id.bottom_rl);
		mTotal_price_tv = (TextView) findViewById(R.id.total_price_tv);
		mSettle_accounts_btn = (Button) findViewById(R.id.settle_accounts_btn);

		mBack_ll.setVisibility(View.VISIBLE);
		mBack_ll.setOnClickListener(this);
		mActionbar_title_tv.setText("服务信息");
		mClose_img.setOnClickListener(this);
		Car car = App.mCurrentCar;
		if (car != null) {
			mCar_licence_tv.setText(car.licence.toString());
			StringBuffer buffer = new StringBuffer();
			buffer.append(car.brand).append(" ").append(car.category).append(" ").append(car.model);
			mCar_brand_tv.setText(buffer.toString());
			mImageLoader.displayImage(car.brand_img_url.thumbnail_url, mCar_brand_img);
		}
		mSettle_accounts_btn.setOnClickListener(this);
	}

	private void getNetData() {
		mClient = new AsyncHttpClient();
		myProgressDialog = MyProgressDialog.createDialog(this);
		myProgressDialog.setMessage(getString(R.string.progress_msg));
		myProgressDialog.show();
		Map<String, String> map = new HashMap<String, String>();
		map.put("product_package_type", String.valueOf(fromCode));
		map.put("car_model_type", String.valueOf(App.mCurrentCar.model_type));
		Utils.get(this, mClient, GET_PRODUCTS_SUBURL, map, mHandler, GET_PRODUCTS_CODE, true);
	}

	protected void invilateContentView() {
		MyListviewAdapter adapter = new MyListviewAdapter(this, mProductParts);
		mProduct_listview.setAdapter(adapter);

		mBottom_rl.setVisibility(View.VISIBLE);
		Double totalPrice = getTotalPrice();
		mTotal_price_tv.setText("￥"+String.valueOf(totalPrice));
	}

	private double getTotalPrice() {
		double totalPrice = 0;
		if (mRequiredProducts != null && mRequiredProducts.size() > 0) {
			for (Product product : mRequiredProducts) {
				totalPrice = totalPrice + product.price * product.unit_count + product.labour_price;
			}
		}
		if(fromCode == KEEPER_PICK_SERVICE && mProductParts != null && mProductParts.size() >0){
			for(ProductPart productPart: mProductParts){
				for(Product product : productPart.products){
					if(product.isChecked){
						totalPrice = totalPrice + product.price * product.unit_count + product.labour_price;
					}
				}
			}
		}
		return totalPrice;
	}
	
	protected void updateTotalPrice() {
		Double totalPrice = getTotalPrice();
		mTotal_price_tv.setText("￥"+String.valueOf(totalPrice));
//		mTotal_price_tv.setText(getString(R.string.price_text, totalPrice));
	}

	private void initData() {
		switch (fromCode) {
		case KEEPER_PICK_SERVICE:
			mBack_tv.setText("接车服务");
			mBeizhu_img.setVisibility(View.GONE);
			mProduct_ll.setVisibility(View.VISIBLE);
			mBeizhu_tv.setVisibility(View.VISIBLE);
			break;
		case DOOR_SERVICE:
			mBack_tv.setText("汽车美容");
			mBeizhu_img.setImageResource(R.drawable.beizhu_meirong);
			break;
		case VALIDATE_CAR_SERVICE:
			mBack_tv.setText("保险续险");
			mBeizhu_img.setImageResource(R.drawable.beizhu_baoxian);
			break;
		case KEEPER_TEST_SERVICE:
			mBack_tv.setText("管家检测");
			mBeizhu_img.setImageResource(R.drawable.beizhu_jiance);
			break;
		case VERIFY_LOSS_SERVICE:
			mBack_tv.setText("代办定损");
			mBeizhu_img.setImageResource(R.drawable.beizhu_dingsun);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_ll:
			finish();
			break;
		case R.id.settle_accounts_btn:
			settleAccounts();
			break;
		case R.id.close_img:
			closeActivities();
			break;
		default:
			break;
		}
	};

	private void settleAccounts() {
		Order4Create order4Create = new Order4Create();
		List<Product4Create> product4Creates = new ArrayList<Product4Create>();
		if (mRequiredProducts != null && mRequiredProducts.size() > 0) {
			for (Product product : mRequiredProducts) {
				Product4Create product4Create = new Product4Create();
				product4Create.product_type = product.product_type;
				product4Create.unit_count = product.unit_count;
				product4Create.selection_mode = 1;//1,用户自主下单;2,待确认;3,同意;4,拒绝
				product4Creates.add(product4Create);
			}
		}
		if(mProductParts != null && mProductParts.size() >0){
			for(ProductPart productPart: mProductParts){
				for(Product product : productPart.products){
					if(product.isChecked){
						Product4Create product4Create = new Product4Create();
						product4Create.part_type = productPart.part_type;
						product4Create.product_type = product.product_type;
						product4Create.unit_count = product.unit_count;
						product4Create.selection_mode = 1;
						product4Creates.add(product4Create);
					}
				}
			}
		}
		order4Create.products = product4Creates;
		String comment = mBeizhu_tv.getText().toString().trim();
		if(!TextUtils.isEmpty(comment))
			order4Create.product_comment = comment; 
		Intent intent = new Intent(this, PlaceOrderActivity.class);
		intent.putExtra("order4create", order4Create);
		startActivityForResult(intent, 1);//当下单界面下单成功时，此界面关闭
	}

	protected void parserProductsJson(String content) {
		try {
			JSONObject object = new JSONObject(content);
			JSONArray pro_arr = object.optJSONArray("required_products");
			if (pro_arr != null) {
				mRequiredProducts = new ArrayList<Product>();
				for (int k = 0; k < pro_arr.length(); k++) {
					mRequiredProducts.add(parserProduct(pro_arr.getString(k)));
				}
			}
			JSONArray part_arr = object.optJSONArray("optional_products");
			if (part_arr != null) {
				mProductParts = new ArrayList<ProductPart>();
				JSONObject part_obj;
				for (int i = 0; i < part_arr.length(); i++) {
					part_obj = part_arr.optJSONObject(i);
					ProductPart part = new ProductPart();
					part.part_type = part_obj.optInt(ProductPart.KEY_PART_TYPE);
					part.part_name = part_obj.optString(ProductPart.KEY_PART_NAME);
					JSONArray product_arr = part_obj.optJSONArray(ProductPart.KEY_PRODUCTS);
					if (product_arr != null) {
						List<Product> products = new ArrayList<Product>();
						for (int j = 0; j < product_arr.length(); j++) {
							Product product = parserProduct(product_arr.getString(j));
							if(j == 0){
								product.isChecked = true;
							}
							products.add(product);
						}
						part.products = products;
					}
					mProductParts.add(part);
				}
			}
			mHandler.sendEmptyMessage(PARSERJSON_SUCCESS);
		} catch (JSONException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(PARSERJSON_FAILURE);
		}
	}

	private Product parserProduct(String content) {
		Product product = new Product();
		try {
			JSONObject object = new JSONObject(content);
			product.recommended = object.optBoolean(Product.KEY_RECOMMENDED);
			product.price = object.optDouble(Product.KEY_PRICE);
			product.unit = object.optString(Product.KEY_UNIT);
			product.product_type = object.optInt(Product.KEY_PRODUCT_TYPE);
			product.product_name = object.optString(Product.KEY_PRODUCT_NAME);
			product.labour_price = object.optDouble(Product.KEY_LABOUR_PRICE);
			product.unit_count = object.optInt(Product.KEY_UNIT_COUNT);
			product.user_defined = object.optBoolean(Product.KEY_USER_DEFINED);
			JSONArray str_arr = object.optJSONArray(Product.KEY_PRODUCT_CATEGORIES);
			if (str_arr != null) {
				product.product_categories = new ArrayList<String>();
				for (int i = 0; i < str_arr.length(); i++) {
					product.product_categories.add(str_arr.optString(i));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return product;
	}

	private class MyListviewAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<ProductPart> mProductParts;
		private Context mContext;

		public MyListviewAdapter(Context context, List<ProductPart> list) {
			this.mContext = context;
			mInflater = LayoutInflater.from(context);
			this.mProductParts = list;
		}

		@Override
		public int getCount() {
			return mProductParts.size();
		}

		@Override
		public ProductPart getItem(int position) {
			return mProductParts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_productpart, null);
				holder.product_part_tv = (TextView) convertView.findViewById(R.id.product_part_tv);
				holder.products_rgp = (LinearLayout) convertView.findViewById(R.id.products_rgp);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final ProductPart productPart = getItem(position);
			holder.product_part_tv.setText(productPart.part_name);
			// TODO product view
			holder.products_rgp.removeAllViews();
			for(int i=0; i<productPart.products.size(); i++){
				final Product product = productPart.products.get(i);
				TextView textView = new TextView(mContext);
				//textView.setPadding(30, 20, 30, 20);
				textView.setGravity(Gravity.CENTER);
				textView.setMinWidth(mContext.getResources().getDimensionPixelSize(R.dimen.product_text_width));
				textView.setMinHeight(mContext.getResources().getDimensionPixelSize(R.dimen.product_text_height));
				textView.setText(product.product_name);
				if(product.isChecked){
					textView.setTextColor(mContext.getResources().getColor(R.color.white));
					textView.setBackgroundResource(R.drawable.bg_toggleradio_buttom_checked);
				}else{
					textView.setTextColor(mContext.getResources().getColor(R.color.black));
					textView.setBackgroundResource(R.drawable.bg_white_toggleradio_buttom_normal);
				}
				textView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						boolean flag = product.isChecked;
						for(int j=0; j<productPart.products.size(); j++){
							productPart.products.get(j).isChecked = false;
						}
						if(!flag){
							product.isChecked = true;
						}
						notifyDataSetChanged();
						updateTotalPrice();
					}
				});
				holder.products_rgp.addView(textView);
				View view = new View(mContext);
				view.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
				view.setMinimumWidth(10);
				holder.products_rgp.addView(view);
			}
			return convertView;
		}

		class ViewHolder {
			TextView product_part_tv;
			LinearLayout products_rgp;
		}
	}

}
