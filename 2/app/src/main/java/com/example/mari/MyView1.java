package com.example.mari;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.util.AttributeSet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import android.graphics.Bitmap;

import android.widget.TextView;
import android.graphics.BitmapFactory;


public class MyView1 extends View{

        private Paint paint = new Paint();

        private boolean initial_b;

        public static float x;
        public static float y;

        public static  float cur_x;
        public static float cur_y;

        private Vector<Float> x_v;
        private Vector<Float> y_v;
        private int vector_size;

        public  static float scale;
        public static int expand_ct;

        public static float CENTER_X;
        public static float CENTER_Y;
        private final float DEG_TO_RAD = (float)(3.1415926535 / 180);

        private float x_max;
        private float x_min;
        private float y_max;
        private float y_min;
        private Bitmap flag = BitmapFactory.decodeResource(getResources(), R.drawable.sss);
        private Bitmap mofu = BitmapFactory.decodeResource(getResources(), R.drawable.ggg);

        private int x_offset = 25;
        private int y_offset = 30;
        public MyView1(Context context){
            super(context);
            initial();
        }
        public MyView1(Context context, AttributeSet attrs) {
            super(context, attrs);
            initial();
        }
        public MyView1(Context context, AttributeSet attrs, int defStyle){
            super(context, attrs, defStyle);
            initial();
        }



        @Override
        protected void onDraw(Canvas canvas){
            drawline();
            paint.setTextSize(30);

            if (!MainActivity.tracking){
                drawpoint(CENTER_X,CENTER_Y, canvas);
                canvas.drawBitmap(flag, CENTER_X - x_offset, CENTER_Y - y_offset, paint);
                drawline();
                canvas.drawLine(CENTER_X,CENTER_Y,x_v.elementAt(1) * scale + CENTER_X,y_v.elementAt(1)*scale + CENTER_Y,paint);
                for (int i = 1; i < x_v.size() - 1; ++i) {
                    canvas.drawLine(x_v.elementAt(i)*scale+CENTER_X,y_v.elementAt(i)*scale+CENTER_Y,x_v.elementAt(i + 1)*scale+CENTER_X,y_v.elementAt(i + 1)*scale+CENTER_Y, paint);
                }
                paint.setTextSize(50);
                canvas.drawLine(0,700,scale * 100,700,paint);
                canvas.drawText("100m",0,3,0,650,paint);

                drawpoint(cur_x,cur_y,canvas);
                canvas.drawBitmap(mofu, cur_x - x_offset, cur_y - y_offset, paint);
                recet();
                return;
            }
            if (initial_b){

                canvas.drawText("START",0,5,CENTER_X + 10,CENTER_Y,paint);
                super.onDraw(canvas);
                paint.setColor(Color.argb(255,148,113,73));
                x_v.add(0f);
                y_v.add(0f);
                drawpoint(CENTER_X,CENTER_Y, canvas);
                canvas.drawBitmap(flag, CENTER_X - x_offset, CENTER_Y - y_offset,paint);
                initial_b = false;
                scale = 100;
                return;

            }


                x = (float)((Math.sin(Math.toRadians(MainActivity.angle))) * MainActivity.distance);
                y = (float)((Math.cos(Math.toRadians(MainActivity.angle))) * MainActivity.distance * -1);


                vector_size = x_v.size();
                x += x_v.elementAt(vector_size - 1);
                y += y_v.elementAt(vector_size - 1);
                x_v.add(x);
                y_v.add(y);

                boolean hitoshi = false;

                if (x > x_max){
                   x_max = x;
                   hitoshi = true;
                } else if (x < x_min){
                    x_min = x;
                    hitoshi = true;
                }
                if (y > y_max){
                    y_max = y;
                    hitoshi = true;
                } else if (y < y_min){
                    y_min = y;
                    hitoshi = true;
                }
                if (hitoshi) {
                    expand();
                }

                canvas.drawText("START",0,5,CENTER_X + 10,CENTER_Y,paint);
                drawpoint(CENTER_X,CENTER_Y, canvas);
                canvas.drawBitmap(flag, CENTER_X - x_offset, CENTER_Y - y_offset, paint);
                drawline();

                canvas.drawLine(CENTER_X,CENTER_Y,x_v.elementAt(1) * scale + CENTER_X,y_v.elementAt(1)*scale + CENTER_Y,paint);
                for (int i = 1; i < x_v.size() - 1; ++i) {
                    canvas.drawLine(x_v.elementAt(i)*scale+CENTER_X,y_v.elementAt(i)*scale+CENTER_Y,x_v.elementAt(i + 1)*scale+CENTER_X,y_v.elementAt(i + 1)*scale+CENTER_Y, paint);
                }

                paint.setTextSize(50);
                canvas.drawLine(0,600,scale * 100,600,paint);
                canvas.drawText("100m",0,4,0,575,paint);

                cur_x = (x * scale + CENTER_X);
                cur_y = (y * scale + CENTER_Y);

                drawpoint(cur_x,cur_y,canvas);
                canvas.drawBitmap(mofu, cur_x - x_offset, cur_y - y_offset, paint);

        }
        public void drawline(){
            paint.setStrokeWidth(5);
            //paint.setStyle(Paint.Style.STROKE);
        }
        public void drawpoint(float x, float y, Canvas canvas){
            paint.setStrokeWidth(20);
            //paint.setStyle(Paint.Style.STROKE);
            canvas.drawPoint(x,y,paint);
        }
        public void initial(){

            initial_b = true;
            cur_x = 0;
            cur_y = 0;
            CENTER_X = 250;
            CENTER_Y = 400;

            x_v = new Vector<Float>();
            y_v = new Vector<Float>();

            x_max = 0;
            x_min = 0;
            y_max = 0;
            y_min = 0;
        }
        public void recet(){
            x_v.removeAllElements();
            y_v.removeAllElements();
            initial();
        }
        public void expand(){
            float range_x = x_max - x_min;
            scale = 400 / range_x;

            float range_y = y_max - y_min;
            if (scale > (400 / range_y)){
                scale = 400 / range_y;
            }

            CENTER_X = scale * x_min * -1 + 75;
            CENTER_Y = scale * y_min * -1 + 100;

            /*
        }
            float cur_x_max = (x_v.elementAt(vector_size - 1)) * scale + CENTER_X;
            float cur_x_min = (x_v.elementAt(vector_size - 1)) * scale + CENTER_X;

            //cur_y = (y_v.elementAt(vector_size - 1)) * scale + CENTER_Y;
            float range_x = x_max - x_min;

            scale = 600 / range_x;
            CENTER_X = (range_x - cur_x_min)
            if ((cur_x_max > 500 || cur_x_min < 0 )|| (cur_y > 700 || cur_y < 100)){
                scale *= (9/10.0f);
                cur_x = (x_v.elementAt(vector_size - 1)) * scale + CENTER_X;
                cur_y = (y_v.elementAt(vector_size - 1)) * scale + CENTER_Y;
                */
        }

    }
