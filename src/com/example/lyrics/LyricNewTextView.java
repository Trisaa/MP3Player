package com.example.lyrics;

import java.util.ArrayList;
import java.util.List;

import com.example.common.fileoperation.Lyric;
import com.example.common.fileoperation.Lyric.LrcSentence;
import com.example.playactivity.LrcFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver.OnDrawListener;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.TextView;

public class LyricNewTextView extends TextView implements Runnable{
	 private Paint mPaint;  
	    private float mX;  
	    private Lyric mLyric;  
	    private Paint mPathPaint;  
	    public String test = "test";  
	    public int index=0;  
	    private  String flag="test";
	    private String flag_1="text";
	    public float mTouchHistoryY;  
	    private int mY;  
	    private long currentTime; // 当前行歌词持续的时间，用该时间来sleep  
	    private float middleY;// y轴中间  
	    private static final int DY = 50; // 每一行的间隔  
	    private List<LrcSentence> list = new ArrayList<LrcSentence>();
	    private float currentScrollY;
	    private boolean isStop = false;
	    private boolean start=true;
	    
	    
	    
	    
//	    public void openOnDraw(boolean onDrawfalse){
//	    	
//	    	this.onDrawfalse=onDrawfalse;
//	    }
	    
//	    public void setcurrentScrollY(boolean isTouching){
//	    	this.isTouching=isTouching;
//	    	
//	    }
	public void setTime(long currentTime){
	    	this.currentTime=currentTime;
	}
	    
	      
	public void setlyric(Lyric lyric){
		mLyric=lyric;
	    	
	}
	    
	public void setList(List<LrcSentence> list){
		this.list=list;  
	}

	public LyricNewTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public LyricNewTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public LyricNewTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public boolean invalidate_1() {
		// TODO Auto-generated method stub
		super.invalidate();
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawColor(0xEFeffff);  
		int index=0;
        Paint p = mPaint;  
        Paint p2 = mPathPaint; 
        p.setTextSize(30);
        p2.setTextSize(33);
        p.setTextAlign(Paint.Align.CENTER);
        p2.setTextAlign(Paint.Align.CENTER); 
        float tempY = middleY;
        if(mLyric==null){
        	return;
        }
   	if(mLyric.getindex(currentTime)==null){
  		return;
  	}
      index=list.indexOf(mLyric.getindex(currentTime));
    
       canvas.drawText(list.get(index).getSentence(),mX, middleY, p2); 
       test=list.get(index).getSentence();
       flag=list.get(index).getSentence();
        // 画出本句之前的句子  
        for (int i = index-1; i>=0; i--) {  
            // 向上推移  
        	if(flag!=list.get(i).getSentence()){
            tempY = tempY - DY;  
//            if (tempY < 0) {  
//                break;  
//           }
            canvas.drawText(list.get(i).getSentence(), mX, tempY, p); 
        }
        }  
       tempY = middleY;  
        for (int i = index+1; i <mLyric.getlist().size(); i++) {
            // 往下推移  
            if(test!=list.get(i).getSentence()){
             tempY = tempY + DY; 
             canvas.drawText(list.get(i).getSentence(), mX, tempY,p);  
             test=list.get(i).getSentence();
            }
        }
	}
	  protected void onSizeChanged(int w, int h, int ow, int oh) {  
	        super.onSizeChanged(w, h, ow, oh);  
	        mX = w * 0.5f;  // remember the center of the screen  
	        mY = h; 
	        middleY = h * 0.5f;  
	      //  middleY=h;
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(!isStop){
		currentScrollY +=0.1;
		scrollTo(0,(int) currentScrollY);
		postDelayed(this, 10);
		}
		if(isStop)
		{
			currentScrollY=5;
			postDelayed(this, 10);
		}
		
	}
	public void startScroll() {
		isStop = false;
		this.removeCallbacks(this);
		post(this);
	}
	public void stopScroll() {
		isStop = true;
	}
	 private void init() {  
	        setFocusable(true); 
	        // 非高亮部分  
	        //list=mLyric.getlist();
	        mPaint = new Paint();  
	        mPaint.setAntiAlias(true);  
	        mPaint.setTextSize(22);  
	        mPaint.setColor(Color.BLACK);  
	        mPaint.setTypeface(Typeface.SERIF);  
	        // 高亮部分 当前歌词  
	        mPathPaint = new Paint();  
	        mPathPaint.setAntiAlias(true);  
	        mPathPaint.setColor(Color.BLUE);  
	        mPathPaint.setTextSize(22);  
	        mPathPaint.setTypeface(Typeface.SANS_SERIF);  
	    } 
}
