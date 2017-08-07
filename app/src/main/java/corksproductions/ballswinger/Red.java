package corksproductions.ballswinger;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Thomas on 7/24/2017.
 */

public class Red {
    private int x;
    private float vx=View.unit30/2;
    private int MAX_Y;
    private float speedMult = 1;
    private float speedLimit = 1.5f*View.unit30;
    private int scoreBound;
    private float speedGain = View.unit30*0.0025f;
    private boolean sped=false;
    public Red(int xLoc, int maxy, int deviceSize){
        x=xLoc;
        MAX_Y=maxy;
        scoreBound=25*deviceSize;
    }
    public void draw(Canvas canvas, Paint paint){
        if (x>0){
            paint.setColor(Color.RED);
            paint.setAlpha(100);
            canvas.drawRect(0,0,x,MAX_Y,paint);
        }
    }
    public void update(){
        if (!sped&&View.score>scoreBound){
            sped=true;
            speedLimit*=1.3f;
        }
        x+=vx*speedMult;
        if(vx<speedLimit) {
            if(x<0) vx+=speedGain;
        }
        if (x>0&&vx>speedLimit/10) vx*=0.995;
    }
    public void setSpeedMult(float mult){
        speedMult=mult;
    }
    public void cameraMove(int xShift, int yShift) {
        x+=xShift;
    }

    public int getX() {
        return x;
    }
}
