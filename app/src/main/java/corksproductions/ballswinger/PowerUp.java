package corksproductions.ballswinger;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Thomas on 7/24/2017.
 */

public class PowerUp{
    private int x;
    private int y;
    private int radius = (int) (3.5f*View.unit30);
    private float moveSpeed = View.unit30/2;
    enum PowType {Speed, Ghost, AntiGrav}
    PowType[] pows = {PowType.Speed, PowType.Ghost, PowType.AntiGrav};
    private PowType powType;
    private Rect detCol;
    private boolean used;


    public PowerUp(int xx, int yy, int numOfPow){
        x=xx;
        y=yy;
        powType = pows[numOfPow];
        detCol = new Rect(x-3*radius,y-3*radius,x+3*radius,y+3*radius);
        used=false;
    }
    public void draw(Canvas canvas, Paint paint){
        if (!used) {
            switch (powType) {
                case Speed:
                    paint.setColor(Color.YELLOW);
                    canvas.drawCircle(x, y, radius, paint);
                    paint.setARGB(255, 255, 150, 50);
                    canvas.drawCircle(x, y, radius / 2, paint);
                    break;
                case Ghost:
                    paint.setColor(Color.CYAN);
                    paint.setAlpha(150);
                    canvas.drawCircle(x, y, radius, paint);
                    paint.setARGB(100, 100, 150, 230);
                    canvas.drawCircle(x - radius / 2, y, radius / 2, paint);
                    canvas.drawCircle(x + radius / 2, y, radius / 2, paint);
                    break;
                case AntiGrav:
                    paint.setColor(Color.GREEN);
                    canvas.drawCircle(x,y,radius,paint);
                    paint.setColor(Color.YELLOW);
                    canvas.drawCircle(x,y-radius/2,radius/2,paint);
                    paint.setAlpha(200);
                    canvas.drawCircle(x,y,radius/2,paint);
                    paint.setAlpha(150);
                    canvas.drawCircle(x,y+radius/2,radius/2,paint);
            }
        }
    }

    public Rect getDetCol(){
        return detCol;
    }

    public void collision(Ball ball){
        if (!used) {
            if (Rect.intersects(detCol, ball.getDetCol())) {
                int yy = ball.getY() - y;
                int xx = ball.getX() - x;
                double dis = Math.sqrt(yy * yy + xx * xx);
                if (dis < radius + ball.getRadius()) {
                    used=true;
                    View.playSound(View.Sounds.powerup);
                    switch (powType){
                        case Ghost:
                            ball.ghostEffect();
                            break;
                        case Speed:
                            ball.speedEffect();
                            break;
                        case AntiGrav:
                            ball.antiGravEffect();
                            break;
                    }
                } else {
                    x += moveSpeed * xx / dis;
                    y += moveSpeed * yy / dis;
                }
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
}