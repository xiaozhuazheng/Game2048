package com.example.game2048;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameLayout extends RelativeLayout {
    private final static String TAG = "GameLayout";
    private int mN = 4; //n行n列
    private int mMargin = 3;//item间隔
    private int mItemSize;//方块边长
    private int mWidth;
    private int mHeight;
    private int mPinding;
    private int mScore = 0;

    private GameItem[] mGameItem;
    private GestureDetector mGestureDetector;
    private CallBackInterface mCallBack;

    private boolean mIsFirst = true;//是否第一次启动
    private boolean mIsMove = false;//是否发生了移动
    private boolean mIsMarge = false;//是否发生了合并

    /*
    * 动作枚举
    */
    private enum ACTION{
        UP,
        RIGHT,
        DOWN,
        LEFT
    }
    private final static float MIX_DISTANCE = 10;//滑动的有效距离

    public GameLayout(Context context) {
        this(context,null);
    }

    public GameLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /*
        * px、dp的相互转换，
        * type1：需要转换的是dp or px
        * type2：具体值
        * type3：DisplayMetrics，屏幕信息类
        */
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mMargin, getResources().getDisplayMetrics());

        //获取边距
        mPinding = Math.min(getPaddingLeft(), getPaddingTop());
        //手势监听
        mGestureDetector = new GestureDetector(new MyGestureDetector());
    }

    //注册回调
    public void setRegister(CallBackInterface callBackInterface){
        this.mCallBack = callBackInterface;
    }

    //重新开始
    public void reStart(){
        //requestLayout();//执行onMeasure、onLayout、onDraw方法
        //invalidate();//只会执行onDraw方法
        for (GameItem item: mGameItem){
            item.setNumber(0);
        }
        mScore = 0;
        if (mCallBack != null){
            mCallBack.setScore(mScore);
        }
        getNewNumber();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG,"onLayout");
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG,"onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        int lenght = Math.min(mWidth,mHeight);
        mItemSize = (lenght - mPinding * 2 - (mN - 1) * mMargin ) / mN;
        if (mIsFirst){
            if (mGameItem == null){
                mGameItem = new GameItem[mN * mN];
            }
            for (int i = 0; i < mGameItem.length; i++){
                GameItem item = new GameItem(getContext());
                mGameItem[i] = item;
                item.setId(i + 1);
                RelativeLayout.LayoutParams lp = new LayoutParams(mItemSize,mItemSize);
                //非最后一列
                if ((i + 1) % mN != 0){
                    lp.rightMargin = mMargin;
                }
                //非第一列
                if (i % mN != 0){
                    lp.addRule(RelativeLayout.RIGHT_OF,mGameItem[i -1].getId());
                }
                //非第一行
                if ((i + 1) > mN){
                    lp.topMargin = mMargin;
                    lp.addRule(RelativeLayout.BELOW,mGameItem[i - mN].getId());
                }
                addView(item,lp);
            }
            getNewNumber();
        }
        mIsFirst = false;
        setMeasuredDimension(lenght, lenght);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG,"onDraw");
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private  class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        //按下
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        //按下后没有松开或者拖动
        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        //轻触后松开
        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        //滑动
        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        //长按
        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        //快速移动(e1 滑动起点,e2 当前手势位置,Vx 每秒x轴移动像素,Vy每秒y轴方向移动像素)
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float Vx, float Vy) {
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
            if (x > MIX_DISTANCE && (Math.abs(Vx) > Math.abs(Vy))){
                doAction(ACTION.RIGHT);
            } else if (x < -MIX_DISTANCE && (Math.abs(Vx) > Math.abs(Vy))){
                doAction(ACTION.LEFT);
            } else if (y > MIX_DISTANCE && (Math.abs(Vx) < Math.abs(Vy))){
                doAction(ACTION.DOWN);
            } else if (y < -MIX_DISTANCE && (Math.abs(Vx) < Math.abs(Vy))){
                doAction(ACTION.UP);
            }
            return true;
        }
    }

    /*
    * 手指移动时，四个方向的所有行都需要移动
    * 1、将每行的值取出并保存到数值
    * 2、根据手势对数组进行移动和合并(判断是否移动、合并)
    * 3、将新的数组放置到每行中
    */
    private void doAction(ACTION action){
        Log.d(TAG,"doAction:" + action);
        for (int i = 0;i < mN; i++){
            List<GameItem> row = new ArrayList<GameItem>();
            //1、将每行的值取出并保存到数值
            for (int j = 0;j < mN; j++){
                int index = getIndexByAction(action,i,j);
                GameItem item = mGameItem[index];
                if (item.getNumber() != 0){
                    //Log.d(TAG,"number:" + item.getNumber());
                    row.add(item);
                }
            }

            //判断是否移动
            for (int j = 0;j < row.size();j++){
                int index = getIndexByAction(action,i,j);
                GameItem item = mGameItem[index];
                if (item.getNumber() != row.get(j).getNumber()){
                    mIsMove = true;
                    break;
                }
            }

            //2、根据手势对数组进行移动和合并
            row = doMerageItem(row);

            //3、将新的数组放置到每行中
            for (int j = 0; j < mN; j++){
                int index = getIndexByAction(action, i, j);
                if (row.size() > j)
                {
                    mGameItem[index].setNumber(row.get(j).getNumber());
                } else
                {
                    mGameItem[index].setNumber(0);
                }
            }
        }
        getNewNumber();
    }
    private List<GameItem> doMerageItem(List<GameItem> row) {
        List<GameItem> backRow = new ArrayList<GameItem>();
        if (row.size() < 2){
            backRow = row;
            return backRow;
        }
        for (int j = 0;j < row.size() - 1;j++){
            GameItem item1 = row.get(j);
            GameItem item2 = row.get(j + 1);
            if (item1.getNumber() == item2.getNumber()){
                mIsMarge = true;
                int value = item1.getNumber() + item2.getNumber();
                item1.setNumber(value);
                item2.setNumber(0);
                //回调显示分数
                mScore += value;
                mCallBack.setScore(mScore);
            }
        }
        for (int j = 0;j < row.size();j++){
            if (row.get(j).getNumber() != 0){
                backRow.add(row.get(j));
            }
        }
        return backRow;
    }

    //根据action获取对应下标,如果为down right则反向储存
    private int getIndexByAction(ACTION action, int i, int j) {
        int index = 0;
        switch (action){
            case UP:
                index = j*mN + i;
                break;
            case DOWN:
                index = (mN-j-1)*mN + i;
                break;
            case LEFT:
                index = i*mN + j;
                break;
            case RIGHT:
                index = i*mN + (mN-j-1);
                break;
        }
        return index;
    }

    //随机生成数字
    private void getNewNumber(){
        if (isGameOver()){
            if (mCallBack != null){
                mCallBack.setGameOver();
                return;
            }
        }
        if (!isFull()){
            if (mIsMarge || mIsMove || mIsFirst){
                int n = mN * mN;
                Random random = new Random();
                int next = random.nextInt(n);
                GameItem item = mGameItem[next];
                while (item.getNumber() != 0){
                    next = random.nextInt(n);
                    item = mGameItem[next];
                }
                item.setNumber(2);
                mIsMarge = mIsMove = false;
            }
        }
    }

    //判断是否还有空格
    private boolean isFull(){
        boolean result = true;
        for (int i = 0;i < mN;i++){
            for (int j = 0;j < mN;j++){
                int index = i*mN + j;
                GameItem item = mGameItem[index];
                if (item.getNumber() == 0){
                    return false;
                }
            }
        }
        return result;
    }

    //判断是否结束游戏(是否还有空格，如果无，是否相同数字)
    private boolean isGameOver(){
        boolean result = true;
        if (!isFull()){
            return false;
        }
        for (int i = 0;i < mN;i++){
            for (int j = 0;j < mN;j++){
                int index = i*mN + j;
                GameItem item = mGameItem[index];
                //上
                if (index - mN > -1){
                    if (item.getNumber() == mGameItem[index - mN].getNumber()){
                        return false;
                    }
                }
                //下
                if (index + mN < mN*mN){
                    if (item.getNumber() == mGameItem[index + mN].getNumber()){
                        return false;
                    }
                }
                //左
                if (index%mN !=0){
                    if (item.getNumber() == mGameItem[index -1].getNumber()){
                        return false;
                    }
                }
                //右
                if ((index + 1)%mN !=0){
                    if (item.getNumber() == mGameItem[index + 1].getNumber()){
                        return false;
                    }
                }
            }
        }

        return result;
    }
}
