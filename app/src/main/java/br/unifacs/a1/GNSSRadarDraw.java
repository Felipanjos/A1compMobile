package br.unifacs.a1;

import androidx.annotation.Nullable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.location.GnssStatus;
import android.util.AttributeSet;
import android.view.View;

public class GNSSRadarDraw extends View {

    private GnssStatus currentStatus;
    private int height;
    private int width;
    private final Rect textBounds = new Rect();

    public GNSSRadarDraw(Context context, @Nullable AttributeSet attributes) {
        super(context,attributes);
    }

    public void onSatelliteStatusChanged(GnssStatus status) {
        currentStatus = status;
    }

    @Override
    protected void onDraw(Canvas tela) {
        super.onDraw(tela);
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        int radius = width < height ? (int)(width/2*0.9) : (int)(height - 100/(2*0.9));
        int radiusAux = radius;

        Paint pincel = new Paint();
        pincel.setStyle(Paint.Style.FILL);
        pincel.setColor(Color.rgb(5, 14, 56));
        tela.drawCircle(xCircle(0), yCircle(0), radiusAux, pincel);

        pincel.setStyle(Paint.Style.STROKE);
        pincel.setStrokeWidth(2);
        pincel.setColor(Color.GRAY);

        tela.drawCircle(xCircle(0), yCircle(0), radiusAux, pincel);
            radiusAux= (int)(radiusAux*Math.cos(Math.toRadians(45)));
        tela.drawCircle(xCircle(0), yCircle(0), radiusAux, pincel);
            radiusAux=(int)(radiusAux*Math.cos(Math.toRadians(60)));
        tela.drawCircle(xCircle(0), yCircle(0), radiusAux, pincel);

        tela.drawLine(xCircle(0), yCircle(-radius), xCircle(0), yCircle(radius), pincel);
        tela.drawLine(xCircle(-radius), yCircle(0), xCircle(radius), yCircle(0), pincel);

        if (currentStatus != null) {
            for(int element = 0; element < currentStatus.getSatelliteCount(); element++) {
                float intensidade =  currentStatus.getCn0DbHz(element);
                float azimuth = currentStatus.getAzimuthDegrees(element),
                        elevation = currentStatus.getElevationDegrees(element),
                        xc = (float)(radius * Math.cos(Math.toRadians(elevation)) * Math.sin(Math.toRadians(azimuth))),
                        yc = (float)(radius * Math.cos(Math.toRadians(elevation)) * Math.cos(Math.toRadians(azimuth))),
                        x = xCircle(xc),
                        y = yCircle(yc);

                pincel.setTextAlign(Paint.Align.LEFT);
                pincel.setStyle(Paint.Style.FILL);
                pincel.setTextSize(40);
                pincel.setColor(colorPicker(intensidade));

                switch (currentStatus.getConstellationType(element)) {
                    case GnssStatus.CONSTELLATION_GPS:
                        tela.drawRect(xCircle(xc - 15), yCircle(yc + 18), xCircle(xc) + 18, yCircle(yc) + 18, pincel);
                        break;
                    case GnssStatus.CONSTELLATION_GLONASS:
                        drawTriangle(x, y, pincel, tela);
                        break;
                    case GnssStatus.CONSTELLATION_GALILEO:
                        tela.drawCircle(xCircle(xc), yCircle(yc), 18, pincel);
                        break;
                    case GnssStatus.CONSTELLATION_BEIDOU:
                        drawLosango(x, y, pincel, tela);
                        break;
                    case GnssStatus.CONSTELLATION_UNKNOWN:
                        drawPentagon(x, y, pincel, tela);
                        break;
                }
                String satelliteId = " " + currentStatus.getSvid(element) + "";
//                tela.drawText(satelliteId, xCircle(xc) + 10, yCircle(yc) + 10, pincel);
                textoAbaixo(tela, pincel, String.valueOf(satelliteId), xCircle(xc), yCircle(yc));
            }
        }
    }

    private int xCircle(double x) {
        return (int)(x+width/2);
    }

    private int yCircle(double y) {
        return (int)(-y+height/2);
    }

    private void textoAbaixo(Canvas canvas, Paint paint, String texto, float xc, float yc) {
        paint.setColor(Color.WHITE);
        paint.getTextBounds(texto, 0, texto.length(), textBounds);
        canvas.drawText(texto, xc - textBounds.exactCenterX(), (yc + 40) - textBounds.exactCenterY(), paint);
    }

    public void drawTriangle(float x, float y, Paint pincel, Canvas tela) {
        pincel.setStrokeWidth(2);
        pincel.setAntiAlias(true);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(x, y);
        path.lineTo(x, y - 18);
        path.lineTo(x + 18, y + 18);
        path.lineTo(x - 18, y + 18);
        path.lineTo(x, y - 18);

        tela.drawPath(path, pincel);
    }

    public void drawLosango(float x, float y, Paint pincel, Canvas tela) {
        pincel.setStrokeWidth(2);
        pincel.setAntiAlias(true);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(x, y);
        path.lineTo(x, y - 23);
        path.lineTo(x + 23, y);
        path.lineTo(x, y + 23);
        path.lineTo(x -23, y);
        path.lineTo(x, y - 23);

        tela.drawPath(path, pincel);
    }

    public void drawPentagon(float x, float y, Paint pincel, Canvas tela) {
        pincel.setStrokeWidth(2);
        pincel.setAntiAlias(true);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(x, y); // mid
        path.lineTo(x - 13, y - 19);
        path.lineTo(x + 13, y - 19);
        path.lineTo(x + 27, y);
        path.lineTo(x + 13, y + 19);
        path.lineTo(x - 13, y + 19);
        path.lineTo(x - 27, y);
        path.lineTo(x - 13, y - 19);

        tela.drawPath(path, pincel);
    }

    public int colorPicker(float intensidade) {

        if (intensidade <= 10) {
            return Color.RED;
        }
        else if (intensidade > 10 && intensidade <= 20) {
            return Color.rgb(255, 77, 0);
        }
        else if (intensidade > 20 && intensidade <= 30) {
            return Color.YELLOW;
        }
        else if (intensidade > 30 && intensidade <= 50) {
            return Color.GREEN;
        }
        else if (intensidade > 50 && intensidade <= 100) {
            return Color.MAGENTA;
        }
        return 0;
    }
}