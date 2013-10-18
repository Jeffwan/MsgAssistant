package com.pitt.msgassistant;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * @author Jiaxin
 *
 */
public class MainActivity extends Activity {
	
	private ImageButton titleBtn;
	private TitlePopup titlePopup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);		
		
		initView();		
		
		initData();
	}


	private void initView(){
		titleBtn = (ImageButton) findViewById(R.id.title_btn);
		titleBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				titlePopup.show(v);
			}
		});
				
		titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}	

	private void initData(){
		titlePopup.addAction(new ActionItem(this, "Message", R.drawable.mm_title_btn_compose_normal));
		titlePopup.addAction(new ActionItem(this, "Email", R.drawable.mm_title_btn_receiver_normal));
		titlePopup.addAction(new ActionItem(this, "Facebook", R.drawable.mm_title_btn_keyboard_normal));
		titlePopup.addAction(new ActionItem(this, "Twitter",  R.drawable.mm_title_btn_qrcode_normal));
	}	
}
