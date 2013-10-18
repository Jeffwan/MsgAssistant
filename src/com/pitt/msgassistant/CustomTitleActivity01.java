package com.pitt.msgassistant;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;

public class CustomTitleActivity01 extends Activity {
	//����������ϵİ�ť
	private ImageButton titleBtn;
	
	//���������������ť
	private TitlePopup titlePopup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initView();
		
		initData();
	}
	
	/**
	 * ��ʼ�����
	 */
	private void initView(){
		//ʵ���������ť�����ü���
		titleBtn = (ImageButton) findViewById(R.id.title_btn);
		titleBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				titlePopup.show(v);
			}
		});
				
		//ʵ�����������
		titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}
	
	/**
	 * ��ʼ�����
	 */
	private void initData(){
		//������������������
		titlePopup.addAction(new ActionItem(this, "��������", R.drawable.mm_title_btn_compose_normal));
		titlePopup.addAction(new ActionItem(this, "��Ͳģʽ", R.drawable.mm_title_btn_receiver_normal));
		titlePopup.addAction(new ActionItem(this, "��¼��ҳ", R.drawable.mm_title_btn_keyboard_normal));
		titlePopup.addAction(new ActionItem(this, "ɨһɨ",  R.drawable.mm_title_btn_qrcode_normal));
	}
	
}
