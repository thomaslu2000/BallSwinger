package corksproductions.ballswinger;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Thomas on 7/24/2017.
 */

public class Bump{
    private int x;
    private int y;
    private int radius;
    private Rect detCol;

    public Bump(int x, int y, int radius){
        this.x=x;
        this.y=y;
        this.radius=radius;
        detCol = new Rect(x-radius,y-radius,x+radius,y+radius);
    }

    public void draw(Canvas canvas, Paint paint){
        paint.setColor(Color.MAGENTA);
        canvas.drawCircle(x,y,radius,paint);
    }

    public void collision(Ball ball){
        if (Rect.intersects(detCol,ball.getDetCol())){
            int xx = ball.getX()-x;
            int yy = ball.getY()-y;
            float dis = (float) Math.sqrt(xx*xx+yy*yy);
            if (dis < radius+ball.getRadius()) {
                View.playSound(View.Sounds.hit);
                if (ball.getMoveType() == Ball.MoveType.regular) {
                    ball.bounce(Ball.rotateAngle(Math.atan2(yy, xx), Math.PI / 2));
                } else {
                    ball.revVel();
                }
                do {
                    ball.thirdStep();
                    xx = ball.getX() - x;
                    yy = ball.getY() - y;
                    dis = (float) Math.sqrt(xx * xx + yy * yy);
                } while(dis<radius+ball.getRadius());
            }
        }
    }

    public void cameraMove(int xShift, int yShift) {
        x+=xShift;
        y+=yShift;
        detCol.left+=xShift;
        detCol.right+=xShift;
        detCol.top+=yShift;
        detCol.bottom+=yShift;
    }

    public Rect getDetCol() {
        return detCol;
    }
}