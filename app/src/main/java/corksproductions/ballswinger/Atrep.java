package corksproductions.ballswinger;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Thomas on 7/24/2017.
 */

public class Atrep{
    float force;
    int x;
    int y;
    float radius;
    Rect detCol;
    private int animPhase = 0;
    private float speedMult = 1;
    private float animNum = View.unit30/2;

    public Atrep(int x, int y, float force, float radius){ //positive force attracts
        this.x=x;
        this.y=y;
        this.force = force;
        if(force>0) this.force/=2;
        this.radius = radius;
        updateDetCol();
    }

    public void updateDetCol(){
        detCol = new Rect((int) (x-radius),(int) (y-radius),(int) (x+radius),(int) (y+radius));
    }
    public void cameraMove(int xShift, int yShift) {
        x+=xShift;
        y+=yShift;
        updateDetCol();
    }

    public void draw(Canvas canvas, Paint paint){
        paint.setARGB(45,255,123,0);
        canvas.drawCircle(x,y,radius,paint);
        paint.setColor(Color.RED);
        paint.setAlpha(30);
        if (force>0) {
            canvas.drawCircle(x,y,radius-animPhase,paint);
        } else{
            canvas.drawCircle(x,y,animPhase+animNum,paint);
        }
        animPhase= (int) ((animPhase+4*speedMult)%(radius-animNum));
    }
    public int getX(){return x;}
    public int getY(){return y;}

    public Rect getDetCol(){return detCol;}

    public void setSpeedMult(float mult){
        speedMult=mult;
    }
    public boolean inbounds(Ball ball){
        return Rect.intersects(getDetCol(),ball.getDetCol()) && Math.sqrt((x-ball.getX())*(x-ball.getX())+(y-ball.getY())*(y-ball.getY()))<radius+ball.getRadius();
    }

    public float getForce() {
        return force;
    }
}
