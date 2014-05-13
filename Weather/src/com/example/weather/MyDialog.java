package com.example.weather;

import com.example.absdemo.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

public class MyDialog extends Dialog {
	private Button btn1;
	private Button btn2;
		
	private TextView title;
	private TextView content;
    
    private String titleStr;
    private String contentStr;
    
    public MyDialog(Context context) {
        super(context);
    }
    public MyDialog(Context context, int theme){
        super(context, R.style.MyDialog);
    }
    public MyDialog(Context context, String title, String content){
        super(context, R.style.MyDialog);
        this.titleStr = title;
        this.contentStr = content;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog);
        
        btn1 = (Button) findViewById(R.id.dialog_button_ok);
        btn2 = (Button) findViewById(R.id.dialog_button_cancel);
        title = (TextView) findViewById(R.id.dialog_title);
        content = (TextView) findViewById(R.id.dialog_content);
        
        title.setText(titleStr);
        content.setText(contentStr);
        btn2.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					dismiss();
				}
				return false;
			}
		});
    }
    public Button getButton1(){
    	return btn1;
    }
}
 