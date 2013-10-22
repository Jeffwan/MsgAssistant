package com.pitt.msgassistant;
import java.util.ArrayList;

import com.pitt.msgassistant.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * @author Jiaxin
 * @version 1.0
 * @since Sep 29, 2013
 */
public class TitlePopup extends PopupWindow {
	protected static final String TAG = "TitlePopup";

    private Context mContext;

	protected final int LIST_PADDING = 10;
	
	private Rect mRect = new Rect();
	
	private final int[] mLocation = new int[2];
	
	private int mScreenWidth,mScreenHeight;

    private static EditText editText;

	private boolean mIsDirty;
	
	private int popupGravity = Gravity.NO_GRAVITY;	
	
	private OnItemOnClickListener mItemOnClickListener;
	
	private ListView mListView;
	
	private ArrayList<ActionItem> mActionItems = new ArrayList<ActionItem>();			
	
	public TitlePopup(Context context){
		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, editText);
	}
	
	public TitlePopup(Context context, int width, int height, EditText editText){
		this.mContext = context;
		this.editText = editText;
		
		setFocusable(true);
		setTouchable(true);	
		setOutsideTouchable(true);
		
		mScreenWidth = Utils.getScreenWidth(mContext);
		mScreenHeight = Utils.getScreenHeight(mContext);
		
		setWidth(width);
		setHeight(height);
		
		setBackgroundDrawable(new BitmapDrawable());
		
		setContentView(LayoutInflater.from(mContext).inflate(R.layout.title_popup, null));
		
		initUI();
	}
		
	private void initUI(){
//		LayoutInflater layout = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );;
//        View view=layout.inflate(R.layout.activity_main, null);
//        editText = (EditText) view.findViewById(R.id.resultmessage);
//        editText.setText("hello world!");
        
		mListView = (ListView) getContentView().findViewById(R.id.title_list);	
		mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                dismiss();

                switch (index) {
                    case 0:
                        Log.i(TAG, "Msg clicked");
                        sendMsg();
                        break;
                    case 1:
                        Log.i(TAG, "Email clicked");
                        sendMail();
                        break;
                    case 2:
                        Log.i(TAG, "Facebook clicked");
                        break;
                    case 3:
                        Log.i(TAG, "Twitter clicked");
                        break;

                    default:
                        break;
                }

                if (mItemOnClickListener != null)
                    mItemOnClickListener.onItemClick(mActionItems.get(index), index);
            }
        });
	}
	
	public void show(View view){
		view.getLocationOnScreen(mLocation);
		
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(), mLocation[1] + view.getHeight());
		
		if(mIsDirty){
			populateActions();
		}
		
		showAtLocation(view, popupGravity, mScreenWidth - LIST_PADDING - (getWidth()/2), mRect.bottom);
	}
	
	private void populateActions(){
		mIsDirty = false;
		mListView.setAdapter(new BaseAdapter() {			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView textView = null;
				
				if(convertView == null){
					textView = new TextView(mContext);
					textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
					textView.setTextSize(14);
					textView.setGravity(Gravity.CENTER);
					textView.setPadding(0, 10, 0, 10);
					textView.setSingleLine(true);
				}else{
					textView = (TextView) convertView;
				}
				
				ActionItem item = mActionItems.get(position);
				
				textView.setText(item.mTitle);
				textView.setCompoundDrawablePadding(10);
                textView.setCompoundDrawablesWithIntrinsicBounds(item.mDrawable, null , null, null);
				
                return textView;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return mActionItems.get(position);
			}
			
			@Override
			public int getCount() {
				return mActionItems.size();
			}
		}) ;
	}
	
	public void addAction(ActionItem action){
		if(action != null){
			mActionItems.add(action);
			mIsDirty = true;
		}
	}
	
	public void cleanAction(){
		if(mActionItems.isEmpty()){
			mActionItems.clear();
			mIsDirty = true;
		}
	}

	public ActionItem getAction(int position){
		if(position < 0 || position > mActionItems.size())
			return null;
		return mActionItems.get(position);
	}			
	
	
	public void setItemOnClickListener(OnItemOnClickListener onItemOnClickListener){
		this.mItemOnClickListener = onItemOnClickListener;
	}
	
	public static interface OnItemOnClickListener{
		public void onItemClick(ActionItem item , int position);
	}

    private void sendMail() {
        Log.i(TAG,"sendMail funciton called");
        String mailBody = editText.getText().toString();
        // it also include the Message App, what it will call depends on client's apps , we may need to customize the return list.
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "This is a email from MsgAssistant ");
        intent.putExtra(Intent.EXTRA_TEXT, mailBody);
        mContext.startActivity(intent);
        //startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private void sendMsg() {

        String msgBody = editText.getText().toString();
        Log.i(TAG, "nothings:? "+editText.getText());
        
        /*General method -- workds fine on emulartor bu not works on device (Android 4.2.2) */
          //Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "smsto:" + "" ) );
          Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.setData(Uri.parse("sms:"));
          intent.putExtra( "sms_body", msgBody);
          
          //intent.setType("vnd.android-dir/mms-sms");
          //intent.putExtra( Intent.EXTRA_TEXT, msgBody);
//        Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"));
//        intent.putExtra(Intent.EXTRA_TEXT, msgBody);
        
        /* General method -- workds fine on emulartor as well as device (Android 4.2.2) 
         * also not works in my Nexus 7
         *  */
//        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
//        smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
//        smsIntent.setType("vnd.android-dir/mms-sms");
//        smsIntent.setData(Uri.parse("sms:")); 
//        smsIntent.putExtra( "sms_body", msgBody);
        
        mContext.startActivity(intent);
    }


}
