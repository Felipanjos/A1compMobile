package br.unifacs.a1;

import androidx.annotation.Nullable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.GnssStatus;
import android.util.AttributeSet;
import android.view.View;

public class GNSSRadarDraw extends View {

    private GnssStatus currentStatus;
    private int radius, height, width;

    public GNSSRadarDraw(Context context, @Nullable AttributeSet attributes) {super(context,attributes);}
    public void onSatelliteStatusChanged(GnssStatus status) {
        currentStatus = status;
    }
    @Override
    protected void onDraw(Canvas tela) {
        super.onDraw(tela);
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        if (width<height)
            radius =(int)(width/2*0.9);
        else
            radius =(int)(height/2*0.9);

        radius = width < height ? (int)(width/2*0.9) : (int)(height/2*0.9);

        Paint pincel=new Paint();
        pincel.setStyle(Paint.Style.STROKE);
        pincel.setStrokeWidth(5);
        pincel.setColor(Color.GREEN);

        int radiusAux = radius;
        tela.drawCircle(xCircle(0), yCircle(0), radiusAux, pincel);
            radiusAux=(int)(radiusAux*Math.cos(Math.toRadians(45)));
        tela.drawCircle(xCircle(0), yCircle(0), radiusAux, pincel);
            radiusAux=(int)(radiusAux*Math.cos(Math.toRadians(60)));
        tela.drawCircle(xCircle(0), yCircle(0), radiusAux, pincel);

        tela.drawLine(xCircle(0), yCircle(-radius), xCircle(0), yCircle(radius), pincel);
        tela.drawLine(xCircle(-radius), yCircle(0), xCircle(radius), yCircle(0), pincel);

        pincel.setColor(Color.WHITE);
        pincel.setStyle(Paint.Style.FILL);

        if (currentStatus != null) {
            for(int element = 0; element<currentStatus.getSatelliteCount(); element++) {
                float azimuth = currentStatus.getAzimuthDegrees(element),
                        elevation = currentStatus.getElevationDegrees(element),
                        x = (float)(radius * Math.cos(Math.toRadians(elevation)) * Math.sin(Math.toRadians(azimuth))),
                        y = (float)(radius * Math.cos(Math.toRadians(elevation)) * Math.cos(Math.toRadians(azimuth)));
                tela.drawCircle(xCircle(x), yCircle(y), 10, pincel);
                    pincel.setTextAlign(Paint.Align.LEFT);
                    pincel.setTextSize(30);
                String satelliteId = currentStatus.getSvid(element) + "";
                tela.drawText(satelliteId, xCircle(x) + 10, yCircle(y) + 10, pincel);
            }
        }
    }
    private int xCircle(double x) { return (int)(x+width/2); }
    private int yCircle(double y) { return (int)(-y+height/2); }
}