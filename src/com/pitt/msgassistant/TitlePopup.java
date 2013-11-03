package com.pitt.msgassistant;
import java.util.ArrayList;
import java.util.List;

import com.pitt.msgassistant.utils.Utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.text.TextUtils;
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
import android.widget.Toast;

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
                        Log.i(TAG, "Social clicked");
                        sendToSocial();
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
        //Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"));
        //intent.putExtra(Intent.EXTRA_TEXT, msgBody);

        /* General method -- workds fine on emulartor as well as device (Android 4.2.2)
         * also not works in my Nexus 7 (a stupid error -- tablet can not send message)
         *  */
//        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
//        smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
//        smsIntent.setType("vnd.android-dir/mms-sms");
//        smsIntent.setData(Uri.parse("sms:"));
//        smsIntent.putExtra( "sms_body", msgBody);

        try {
            mContext.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            //throw new ActivityNotFoundException(ex.toString());
            Toast.makeText(mContext, "Your device do not support SMS", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMail() {
        Log.i(TAG,"sendMail funciton called");
        String mailBody = editText.getText().toString();
        // it also include the Message App, what it will call depends on client's apps , we may need to customize the return list.
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        
        List<ResolveInfo> resInfo = mContext.getPackageManager().queryIntentActivities(intent, 0);
        
        if (!resInfo.isEmpty()) {
        	List<Intent> targetedIntents = new ArrayList<Intent>();
        	for(ResolveInfo info: resInfo) {
        		Intent targeted = new Intent(Intent.ACTION_SEND);
        		targeted.setType("text/plain");
        		ActivityInfo activityInfo = info.activityInfo;
        		
        		if (activityInfo.packageName.contains("mail") || 
        				TextUtils.equals(activityInfo.packageName, "com.google.android.gm") ) {
        			Log.i(TAG, "email intent contains");
        	        targeted.putExtra(Intent.EXTRA_SUBJECT, "This is an email from MsgAssistant ");
        	        targeted.putExtra(Intent.EXTRA_TEXT, mailBody);  
            		targeted.setPackage(activityInfo.packageName);
            		targetedIntents.add(targeted);
        		}
        	}
			
        	Intent chooserIntent = Intent.createChooser(targetedIntents.remove(0), "Send To Email");
        	if(chooserIntent == null) {
        		Log.i(TAG, "can not find the choose Intent");
        		return;
        	}
        	
        	chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedIntents.toArray(new Parcelable[] {}));
        	
        	try {
                mContext.startActivity(chooserIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mContext, "Can't find share component to share", Toast.LENGTH_SHORT).show();
            }
        	
		}
    }       

    private void sendToSocial() {
        Log.i(TAG,"sendToSocial funciton called");
        String msgBody = editText.getText().toString();
        
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        List<ResolveInfo> activityList = mContext.getPackageManager().queryIntentActivities(sharingIntent, 0);
        
        for(final ResolveInfo info : activityList) {
        	String packageName = info.activityInfo.packageName;
		    Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
		    targetedShareIntent.setType("text/plain");
		    targetedShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "This is a message from MsgAssistant");
		    
		    if (TextUtils.equals(packageName, "com.sina.weibo") ||
		    	TextUtils.equals(packageName, "com.evernote") ||
		    	TextUtils.equals(packageName, "com.linkedin.android") ||
		    	TextUtils.equals(packageName, "com.twitter.android") ||
		    	TextUtils.equals(packageName, "com.google.android.app.plus") ||
		    	TextUtils.equals(packageName, "com.facebook.katana")) {
		    	
    			targetedShareIntent.putExtra(Intent.EXTRA_SUBJECT, "This is from MsgAssistant ");
    			targetedShareIntent.putExtra(Intent.EXTRA_TEXT, msgBody);  
    			targetedShareIntent.setPackage(packageName);
    			targetedShareIntents.add(targetedShareIntent);
		    }
        }
        
        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Send To Social Networks");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
        mContext.startActivity(chooserIntent);
    }

    private void sendToOffice() {
        Log.i(TAG,"sendToOffice funciton called");
        String msgBody = editText.getText().toString();

        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        List<ResolveInfo> activityList = mContext.getPackageManager().queryIntentActivities(sharingIntent, 0);

        for(final ResolveInfo info : activityList) {
            String packageName = info.activityInfo.packageName;
            Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
            targetedShareIntent.setType("text/plain");
            targetedShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "This is a message from MsgAssistant");

            if (TextUtils.equals(packageName, "com.evernote")) {

                targetedShareIntent.putExtra(Intent.EXTRA_SUBJECT, "This is from MsgAssistant ");
                targetedShareIntent.putExtra(Intent.EXTRA_TEXT, msgBody);
                targetedShareIntent.setPackage(packageName);
                targetedShareIntents.add(targetedShareIntent);
            }
        }

        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Send To Office Tools");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
        mContext.startActivity(chooserIntent);
    }


    private void sendMail2() {
        Log.i(TAG,"sendMail2 funciton called");
        String mailBody = editText.getText().toString();

        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        List<ResolveInfo> activityList = mContext.getPackageManager().queryIntentActivities(sharingIntent, 0);

        for(final ResolveInfo info : activityList) {
            String packageName = info.activityInfo.packageName;
            Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
            targetedShareIntent.setType("text/plain");
            targetedShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "This is an email from MsgAssistant");

            if (TextUtils.equals(packageName, "com.android.mms") ||
                    TextUtils.equals(packageName, "com.android.mms") ||
                    TextUtils.equals(packageName, "com.android.mms") ||
                    TextUtils.equals(packageName, "com.yahoo.mail")){
                targetedShareIntent.putExtra(Intent.EXTRA_TEXT, mailBody);
                Log.i(TAG, "email intent contains");
                targetedShareIntent.putExtra(Intent.EXTRA_SUBJECT, "This is from MsgAssistant ");
                targetedShareIntent.putExtra(Intent.EXTRA_TEXT, mailBody);
                targetedShareIntent.setPackage(packageName);
                targetedShareIntents.add(targetedShareIntent);
            }
            targetedShareIntent.setPackage(packageName);
            targetedShareIntents.add(targetedShareIntent);
        }

        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Send To Email");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
        mContext.startActivity(chooserIntent);
    }

    private void sendMail3() {
        Log.i(TAG,"sendMail funciton called");
        String mailBody = editText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "This is a email from MsgAssistant ");
        intent.putExtra(Intent.EXTRA_TEXT, mailBody);  
        mContext.startActivity(intent);
        
        //intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
        //startActivity(Intent.createChooser(intent, "Send Email"));
    }




}
