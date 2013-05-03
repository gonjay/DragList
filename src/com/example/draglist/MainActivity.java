package com.example.draglist;



import java.util.ArrayList;
import java.util.Collection;

import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private LayoutParams windowParams = null;
	private WindowManager windowManager = null;
	private View dragItemView,dragView;
	private MyAdapter adapter;
	private TextView item,countTextView;
	private Vibrator vibrator;
	private int oldX,oldY,count;
	private boolean isChoiceMode,isAll;
	private boolean[] isDel;
	private Animation showAnim,hideAnim;
	private ImageView delImageView,countImageView;
	private LinearLayout menu;
	private ListView listView;

	private void init(){
		isChoiceMode = false;
		isAll = false;
		count = 0;
		listView.setAdapter(adapter);
		isDel = new boolean[adapter.getCount()];
		for (int i = 0; i < isDel.length; i++) {
			isDel[i] = false;
		}
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
        //震动器
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        listView = (ListView)findViewById(R.id.listView1);
        adapter = new MyAdapter(this);
        init();
        
        menu = (LinearLayout)findViewById(R.id.menu);
		menu.setVisibility(View.GONE);
		delImageView = (ImageView)menu.findViewById(R.id.delimg);
		countImageView = (ImageView)menu.findViewById(R.id.countimg);
		countTextView = (TextView)menu.findViewById(R.id.count);
		//菜单进入动画
		showAnim = new TranslateAnimation( Animation.RELATIVE_TO_SELF,0.0f
				, Animation.RELATIVE_TO_SELF, 0.0f
				, Animation.RELATIVE_TO_SELF, 1.0f
				, Animation.RELATIVE_TO_SELF, 0.0f);
		showAnim.setDuration(300);
		//菜单退出动画
		hideAnim = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0.0f
				, Animation.RELATIVE_TO_SELF, 0.0f
				, Animation.RELATIVE_TO_SELF, 0.0f
				, Animation.RELATIVE_TO_SELF, 1.0f);
		hideAnim.setDuration(200);
		
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				//获取当前所长按的View
				dragItemView = arg1;
				//设置隐藏
				dragItemView.setVisibility(View.INVISIBLE);
				
				dragView = getLayoutInflater().inflate(R.layout.listdetail, null);
				dragView.setBackgroundResource(R.drawable.list_yellow_single);
				item = (TextView)dragView.findViewById(R.id.item);
				item.setText(adapter.mData.get(arg2));
				startDrag();
				vibrator.vibrate(20);
				//防止多次长按时menu多次出现
				if (!isChoiceMode) {
					menu.setVisibility(View.VISIBLE);
					menu.startAnimation(showAnim);
				}
				isChoiceMode = true;				
				if (!isDel[arg2]) {
					count++;
					countTextView.setText(String.valueOf(count));
				}
				//标记已选择
				adapter.isSelected.put(arg2, true);
				isDel[arg2] = true;
				return false;
			}
		});
        listView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (dragView != null) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						stopDrag();
						
						dragItemView.setVisibility(View.VISIBLE);
						dragItemView.setBackgroundResource(R.drawable.list_yellow_single);
						//进入删除区域进行删除，没有对其他设备进行适配，可以自行调整
						if ((int)event.getX()>0&&(int)event.getX()<320&&(int)event.getY()>760) {
							delItem();
						}
						break;
					case MotionEvent.ACTION_MOVE:
						drag((int)event.getX() - oldX,(int)event.getY() - oldY);
						if ((int)event.getX()>0&&(int)event.getX()<320&&(int)event.getY()>760) {
							delImageView.setBackgroundResource(R.color.rred);
							dragView.setBackgroundResource(R.drawable.list_red_single);
						}
						else {
							delImageView.setBackgroundResource(R.drawable.menu);
							dragView.setBackgroundResource(R.drawable.list_yellow_single);
						}
						break;
					}
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					oldX = (int)event.getX();
					oldY = (int)event.getY();
				}
				return false;
			}
        });
        listView.setOnItemClickListener(new OnItemClickListener() {

        	@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
        			long arg3) {
        		if (isDel[arg2]) {
        			arg1.setBackgroundResource(R.drawable.select);
        			isDel[arg2] = false;
        			adapter.isSelected.put(arg2, false);
        			count--;
        			countTextView.setText(String.valueOf(count));
        		}
        		else if (isChoiceMode) {
        			arg1.setBackgroundResource(R.drawable.list_yellow_single);
        			isDel[arg2] = true;
        			adapter.isSelected.put(arg2, true);
        			count++;
        			countTextView.setText(String.valueOf(count));
        		}
        		for (int i = 0; i < isDel.length; i++) {
        			if (isDel[i]) {
        				isChoiceMode = true;
        				return;
        			}
        			else {
        				isChoiceMode = false;
        			}
        		}
        		if (!isChoiceMode&&menu.getVisibility() == View.VISIBLE) {
        			menu.startAnimation(hideAnim);
        			menu.setVisibility(View.GONE);
        		}

        	}
        });
        countImageView.setOnClickListener(new View.OnClickListener() {

        	@Override
        	public void onClick(View v) {
        		if (isAll) {
        			for (int i = 0; i < isDel.length; i++) {
        				isDel[i] = false;
        				adapter.isSelected.put(i, false);
        				if (listView.getChildAt(i) != null) {
        					listView.getChildAt(i).setBackgroundResource(R.drawable.select);
        				}
        				countTextView.setText("0");
        				count = 0;
        				isAll = false;
        				isChoiceMode = false;
        				menu.startAnimation(hideAnim);
        				menu.setVisibility(View.GONE);
        			}
        		}
        		else {
        			for (int i = 0; i < isDel.length; i++) {
        				isDel[i] = true;
        				adapter.isSelected.put(i, true);
        				if (listView.getChildAt(i) != null) {
        					listView.getChildAt(i).setBackgroundResource(R.drawable.list_yellow_single);
        				}
        				countTextView.setText("ALL");
        				count = isDel.length;
        				isAll = true;
        			}
        		}

        	}
        });
        delImageView.setOnClickListener(new View.OnClickListener() {

        	@Override
        	public void onClick(View v) {				
        		delItem();
        	}
        });
	}

	private void delItem(){
		Collection<String> delWhich = new ArrayList<String>();
		for (int i = 0; i < isDel.length; i++) {
			if (isDel[i]) {
				delWhich.add(adapter.mData.get(i));
				adapter.isSelected.put(i, false);
			}
		}
		adapter.delItem(delWhich);
		init();
		menu.startAnimation(hideAnim);
		menu.setVisibility(View.GONE);
	}
	private void startDrag() {
		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		windowParams.x = dragItemView.getLeft() + 10;
		windowParams.y = dragItemView.getTop() + 100;
		windowParams.alpha = 0.8f;
		windowParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|0x00000010;
		windowManager = (WindowManager)MainActivity.this.getSystemService(
				Context.WINDOW_SERVICE);
		windowManager.addView(dragView, windowParams);
	}
	private void drag(int x, int y){
		windowParams.x = x + dragItemView.getLeft() + 10;
		windowParams.y = y + dragItemView.getTop() + 100;
		windowManager.updateViewLayout(dragView, windowParams);
	}
	private void  stopDrag() {
		if(dragView != null){
			windowManager.removeView(dragView);
			dragView = null;
		}
	}

}
