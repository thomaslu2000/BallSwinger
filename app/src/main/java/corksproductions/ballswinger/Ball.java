package corksproductions.ballswinger;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Thomas on 7/24/2017.
 */

public class Ball{
    private int x;
    private int y;
    private float vx=0;
    private float vy=0;
    private int radius = (int) (2*View.unit30);
    private float cirSpdLim = View.unit30*2.2f;
    private double grav = View.unit30/15;
    private int color = Color.BLUE;
    float angle=0;
    float angleMod=0;
    double rad;
    private float speedMult = 1;
    private float gravMult = 1;
    int xx;
    int yy;
    private int ghostCounter=0;
    private int speedCounter=0;
    private int antiGravCounter=0;
    enum MoveType {regular, circle, breakCircle, newCircle};
    MoveType moveType = MoveType.regular;
    double vel = 0;

    public int getRadius() {
        return radius;
    }

    public Ball(int xx, int yy){
        x=xx;
        y=yy;
    }
    public float getVx(){ return vx;}
    public float getVy(){return vy;}
    public int getX(){return x;}
    public int getY(){return y;}



    private int ropeLength= (int) (5*View.unit30/6);

    public void draw(Canvas canvas, Paint paint){
        if(moveType!=MoveType.regular) {
            paint.setStrokeWidth(ropeLength);
            paint.setColor(Color.RED);
            paint.setColor(Color.BLACK);
            canvas.drawLine(x, y, xx, yy, paint);
        }

        if (speedCounter>0){
            paint.setARGB(100,255,45,45);
            canvas.drawCircle(x,y,5*radius/4,paint);
        }

        if (antiGravCounter>0){
            paint.setARGB(150,250,255,120);
            canvas.drawCircle(x,y,5*radius/4,paint);
        }

        paint.setColor(color);
        if (ghosted()) paint.setAlpha(100);

        canvas.drawCircle(x,y,radius,paint);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void newSpin(int newxx, int newyy) {
        moveType=MoveType.newCircle;
        xx=newxx;
        yy=newyy;
        rad=Math.sqrt(((xx-x)*(xx-x))+((yy-y)*(yy-y)));
    }

    public Rect getDetCol(){
        return new Rect(x-radius,y-radius,x+radius,y+radius);
    }

    public void setMoveType(MoveType type){
        moveType = type;
    }

    public void cameraMove(int xShift, int yShift){
        x+=xShift;
        y+=yShift;
        xx+=xShift;
        yy+=yShift;
    }

    public void thirdStep() {
        if(moveType==MoveType.regular) {
            x += vx * speedMult/3;
            y += vy * speedMult/3;
        } else{
            angle+=speedMult*0.4f*vel/rad;
            x= (int) (xx+rad*Math.cos(angle));
            y = (int) (yy+rad*Math.sin(angle));
        }
    }

    public boolean ghosted(){return ghostCounter>0;}
    public void ghostEffect(){
        ghostCounter+=150;
    }
    public void speedEffect(){
        if (speedCounter<=0) {
            boost(2);
            speedMult *= 2;
            if (gravMult == 1) gravMult = 0.5f;
        }
        speedCounter+=100;
    }
    public void antiGravEffect(){
        antiGravCounter+=200;
        gravMult=0;
    }

    public void modSpeedMult(float mult){
        speedMult*=mult;
    }
    public void modGravMult(float mult){
        gravMult*=mult;
    }

    public void bounce(double angle){
        double a = 2*(vx*Math.cos(angle)+vy*Math.sin(angle));
        vx= (float) (a*Math.cos(angle)-vx);
        vy= (float) (a*Math.sin(angle)-vy);
    }
    public void revVel(){vel=-vel;}

    public void boost(float mult){
        if (moveType==MoveType.regular){
            vx*=mult;
            vy*=mult;
        } else vel*= mult;
    }
    public void inField(Atrep atrep){
        if(moveType==MoveType.regular){
            float force = atrep.getForce();
            int gx = atrep.getX();
            int gy = atrep.getY();
            double dis = Math.sqrt((gx-x)*(gx-x)+(gy-y)*(gy-y));
            vy+=gravMult*force*(gy-y)/dis;
            vx+=gravMult*force*(gx-x)/dis;
        }
    }
    public void raiseGhostCounter(int i) {
        ghostCounter+=i;
    }
    public void hitEdges(int top, int bottom){
        if(y>bottom-radius){
            View.playSound(View.Sounds.hit);
            if(moveType==MoveType.regular) {if (vy>0) vy=-vy;}
            else vel= -vel;
        }
        if(y<top+radius){
            View.playSound(View.Sounds.hit);
            if(moveType==MoveType.regular) { if (vy<0) vy=-vy;}
            else vel= -vel;
        }
    }
    public void update(){
        if (ghosted()) ghostCounter--;
        if (speedCounter>0){
            if (speedCounter==1){
                speedMult*=0.5;
                if (gravMult==0.5) gravMult=1;
            }
            speedCounter--;
        }
        if (antiGravCounter>0){
            if (antiGravCounter==1) gravMult=1;
            boost(1.1f);
            antiGravCounter--;
        }
        switch(moveType){
            case regular:
                vy+=grav*gravMult;
                x+=vx*speedMult;
                y+=vy*speedMult;
                double speed=Math.sqrt(vx*vx+vy*vy);
                if (speed>cirSpdLim){
                    vx*=cirSpdLim/speed;
                    vy*=cirSpdLim/speed;
                }
                break;
            case newCircle:
                rad = Math.sqrt(((xx-x)*(xx-x))+((yy-y)*(yy-y)));
                angle = (float) (Math.atan2(y-yy,x-xx));
                vel=Math.sqrt(vx*vx+vy*vy)*((rotateAngle(Math.atan2(vy,vx),-angle))<=0 ? -1 : 1);
                View.playSound(View.Sounds.swoosh);
                moveType=MoveType.circle;
            case circle:
                vel*=1.1;
                vel+=Math.cos(angle)*grav*gravMult;
                if(Math.abs(vel)>cirSpdLim) vel = Math.signum(vel)*cirSpdLim;
                angleMod= (float) (1.2*vel/rad);
                angle += angleMod*speedMult;
                x= (int) (xx+rad*Math.cos(angle));
                y = (int) (yy+rad*Math.sin(angle));
                break;
            case breakCircle:
                vx= (float) (-vel*Math.sin(angle));
                vy= (float) (vel*Math.cos(angle));
                moveType=MoveType.regular;
                break;
        }
    }

    public static double rotateAngle(double angle, double angShift){
        angle+=angShift;
        if (angle>Math.PI) angle-=2*Math.PI;
        if (angle<-Math.PI) angle+=2*Math.PI;
        return angle;
    }
}
