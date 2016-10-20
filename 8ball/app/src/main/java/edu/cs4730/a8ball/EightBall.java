package edu.cs4730.a8ball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 *  Eight ball.
 */
public class EightBall extends View {

    Bitmap bg;
    Paint mPaint;
    int AnswerNum = 1;
    String Answers[] = { "Signs point to yes",
            "Yes",
            "Reply hazy, try again",
            "Without a doubt",
            "My sources say no",
            "As I see it, yes",
            "You may rely on it",
            "Concentrate and ask again",
            "Outlook not so good",
            "It is decidedly so",
            "Better not tell you now",
            "Very doubtful",
            "Yes - definitely",
            "It is certain",
            "Cannot predict now",
            "Most likely",
            "Ask again later",
            "My reply is no",
            "Outlook good",
            "Don\'t count on it"};

    public EightBall(Context context) {
        super(context);
        init(null, 0);
    }

    public EightBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public EightBall(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        //basically ignoring the attributes and will setup my own.
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.eightball);
        mPaint = new Paint();


    }

    /*
     * Setup the text on the Eight Ball.
     */
    public void changeText() {

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bg,0,0,mPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.i("MSW", ""+getMeasuredWidth());
        Log.i("MSH", ""+getMeasuredHeight());
        int mwidth =0;
        int mheight = 0;
        if (bg != null) {
            mwidth = bg.getWidth();
            mheight = bg.getHeight();
        }
        setMeasuredDimension(mwidth, mheight);

    }

}
