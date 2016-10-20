package edu.cs4730.a8ball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

/**
 *  Eight ball.
 */
public class EightBall extends View {
    int mwidth =0;
    int mheight = 0;
    Bitmap bg;
    Paint mPaint;
    int AnswerNum = 1;
    Random myRandom = new Random();
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
        float scale = getResources().getDisplayMetrics().density; //this gives me the scale value for a mdpi baseline of 1.
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.eightball);
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mPaint.getTextSize()*scale); //scale the font size too
    }

    /*
     * Setup the text on the Eight Ball.
     */
    public void changeText() {
        AnswerNum = myRandom.nextInt(Answers.length);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bg,0,0,mPaint);
        canvas.drawText(Answers[AnswerNum],mwidth/2,mheight/2,mPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.i("MSW", ""+getMeasuredWidth());
        Log.i("MSH", ""+getMeasuredHeight());

        if (bg != null) {
            mwidth = bg.getWidth();
            mheight = bg.getHeight();
        }
        setMeasuredDimension(mwidth, mheight);

    }

}
