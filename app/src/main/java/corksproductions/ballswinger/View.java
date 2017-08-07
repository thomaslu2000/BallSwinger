package corksproductions.ballswinger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static corksproductions.ballswinger.MainActivity.prefName;

/**
 * Created by Thomas on 7/24/2017.
 */
public class View extends SurfaceView implements Runnable {
    volatile boolean playing;
    private boolean inProgress=true;

    private Thread gThread = null;
    private Context mContext;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Random rand = new Random();

    public int MAX_X;
    public int MAX_Y;

    private ArrayList<Atrep> atreps = new ArrayList<Atrep>();
    private ArrayList<Bump> bumps = new ArrayList<Bump>();
    private ArrayList<PowerUp> powerUps = new ArrayList<PowerUp>();

    private int gridInterval;
    private int rightBound;
    private int bottomBound;
    private int topBound;
    private int xChange;
    private int bumpNum = 10;
    private int atrepNum = 1;
    private int powerUpNum=2;

    Ball bally;
    Red red;

    public static int score=0;
    private int finalScore;
    private Rect exit;

    private Rect slowButton;
    private int[] slowCoord=new int[2];
    private boolean slowed=false;
    private int slowCounter = 500;
    private int slowRad;

    private int pauseX;
    private int pauseY;
    private int pauseRadius;
    private Rect pauseRect;

    public static float unit30;
    private Rect screenRect;
    private String endText;

    public enum Sounds {hit,powerup,win,lose,swoosh}

    public View(Context context, int screenX, int screenY
    ){
        super(context);
        mContext=context;
        surfaceHolder = getHolder();
        paint = new Paint();
        MAX_X = screenX;
        MAX_Y = screenY;

        unit30= 25*(screenX+screenY)/4000;
        screenRect=new Rect(0,0,MAX_X,MAX_Y);

        slowCoord[0]= (int) (11*unit30); slowCoord[1]= (int) (MAX_Y-15*unit30);
        slowRad = (int) (7*unit30);
        slowButton= new Rect(slowCoord[0]-slowRad,slowCoord[1]-slowRad,slowCoord[0]+slowRad,slowCoord[1]+slowRad);

        pauseX= slowRad;
        pauseY= slowRad;
        pauseRadius= (int) (3*unit30);
        pauseRect= new Rect(pauseX-pauseRadius,pauseY-pauseRadius,pauseX+pauseRadius,pauseY+pauseRadius);

        exit=new Rect((int) (MAX_X/2-11*unit30),(int) (MAX_Y/2-5*unit30),(int) (MAX_X/2+11*unit30),(int) (MAX_Y/2+5*unit30)); //MAX_X/2-MAX_X/4,MAX_Y/2-MAX_X/8,MAX_X/2+MAX_X/4,MAX_Y/2+MAX_X/8

        gridInterval= (int) (5*unit30);
        rightBound= 4*MAX_X;
        bottomBound= 2*MAX_Y;
        topBound= 0;
        score=0;

        bally = new Ball(MAX_X/2,MAX_Y/2);
        xChange=-bally.getX();
        red = new Red(MAX_X/8,MAX_Y,MAX_X+MAX_Y);


        createObjects(MAX_X,3*MAX_X);
        createObjects(3*MAX_X,5*MAX_X);
        paint.setTextSize(4*unit30);


    }


    @Override
    public void run() {
        while(playing){
            music();
            cameraShift();
            update();
            collisions();
            genNextSection();
            drawing();
            nap();
        }
    }

    private void cameraShift(){
        float propX = (bally.getX()-MAX_X/2.0f)/(MAX_X/2);
        float propY = (bally.getY()-MAX_Y/2.0f)/(MAX_Y/2);
        double spd = (bally.moveType == Ball.MoveType.regular ? Math.sqrt(bally.getVx()*bally.getVx()+bally.getVy()*bally.getVy()) : Math.abs(bally.vel)/2);
        cameraMove((int) (-4*spd * propX), 0);
        cameraMove(0, (int) (-4*spd * propY) );
    }

    private void update() {
        bally.hitEdges(topBound,bottomBound);
        bally.update();
        red.update();
        score=xChange+bally.getX();
        if (slowed){
            slowCounter-=2;
            if (slowCounter<0) toggleSlow();
        } else{
            if (slowCounter<500) slowCounter++;
        }

    }

    private void drawing() {
        if(surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            drawLines();
            drawBounds();

            for (Atrep a : atreps) if(Rect.intersects(a.getDetCol(),screenRect))a.draw(canvas,paint);
            for (Bump a : bumps) if(Rect.intersects(a.getDetCol(),screenRect))a.draw(canvas,paint);
            for (PowerUp a : powerUps) if(Rect.intersects(a.getDetCol(),screenRect))a.draw(canvas,paint);

            bally.draw(canvas,paint);

            if(slowed) canvas.drawARGB(50,160,250,255);

            red.draw(canvas,paint);

            drawSlowButton();
            drawPause();
            if (red.getX()>-2*MAX_X)drawWarning(red.getX()>=0?255:(254*red.getX()/(2*MAX_X)));

            if (!inProgress){
                paint.setColor(Color.LTGRAY);
                canvas.drawRect(exit,paint);
                paint.setColor(Color.BLACK);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Main Menu",MAX_X/2,MAX_Y/2+MAX_Y/64,paint);
                canvas.drawText(endText,MAX_X/2,exit.top-MAX_Y/8,paint);
                canvas.drawText("Score: "+finalScore,MAX_X/2,exit.bottom+MAX_Y/8,paint);
            }else{
                paint.setColor(Color.BLACK);
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("Distance: "+score,MAX_X,7*MAX_Y/8,paint);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void collisions(){
        if (!bally.ghosted()) {
            for (Atrep rep : atreps)
                if (rep.inbounds(bally)) {bally.inField(rep);}
            for (Bump a : bumps) a.collision(bally);
        } else{
            for (Bump a : bumps) if (Rect.intersects(bally.getDetCol(),a.getDetCol())) bally.raiseGhostCounter(1);
        }
        for (PowerUp a : powerUps) a.collision(bally);
        if (inProgress&&bally.getX()<red.getX())lost();
    }

    private void genNextSection(){
        if (bally.getX()>rightBound){
            createObjects(rightBound+MAX_X,rightBound+3*MAX_X);
            rightBound+=2*MAX_X;
            for (int i=0;i<bumpNum;i++) bumps.remove(0);
            for (int i=0;i<atrepNum;i++) atreps.remove(0);
            for (int i=0;i<powerUpNum;i++) powerUps.remove(0);
        }
    }

    private void nap() {
        try {
            gThread.sleep(14);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void music(){
        if (!GameActivity.music.isPlaying()) GameActivity.music.start();
    }
    public void pause() {
        playing = false;
        try {
            gThread.join();
        } catch (InterruptedException e) {
        }
    }


    public void resume() {
        playing = true;
        gThread = new Thread(this);
        gThread.start();
    }

    public void lost(){
        inProgress=false;
        finalScore=score;
        int a = updateHighScores(finalScore);
        if (a==0){
            endText= "Game Over";
            playSound(Sounds.lose);
        }
        else{
            endText = "New High Score!"+"\r\n"+GameActivity.wordPlace(a)+" Place!";
            playSound(Sounds.win);
        }
    }



    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        if (inProgress) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (slowButton.contains(x, y)) {
                        toggleSlow();
                    } else if (pauseRect.contains(x, y)) {
                        if (playing) {
                            pause();
                            canvas = surfaceHolder.lockCanvas();
                            canvas.drawARGB(100, 200, 200, 200);
                            paint.setColor(Color.BLACK);
                            paint.setTextAlign(Paint.Align.CENTER);
                            canvas.drawText("PAUSED", MAX_X / 2, MAX_Y / 2, paint);
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        } else {
                            resume();
                        }
                    } else {
                        bally.newSpin(x, y);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (bally.getMoveType()!= Ball.MoveType.regular) {
                        bally.setMoveType(Ball.MoveType.breakCircle);
                    }
                    break;
            }
        }
        else{
            if (event.getAction()==MotionEvent.ACTION_DOWN){
                if (exit.contains(x,y)) {
                    if (GameActivity.music.isPlaying()) GameActivity.music.stop();
                    leave();
                }
            }
        }
        return true;
    }

    public void cameraMove(int xShift, int yShift){ //pans to same direction
        for (Atrep a : atreps) a.cameraMove(xShift,yShift);
        for (Bump a : bumps) a.cameraMove(xShift,yShift);
        for (PowerUp a : powerUps) a.cameraMove(xShift,yShift);
        bally.cameraMove(xShift,yShift);
        red.cameraMove(xShift,yShift);
        rightBound+=xShift;
        bottomBound+=yShift;
        topBound+=yShift;
        xLineShift= (xLineShift+xShift)%gridInterval;
        yLineShift= (yLineShift+yShift)%gridInterval;
        xChange-=xShift;
    }
    private int xLineShift=0;
    private int yLineShift=0;

    public void drawLines(){
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        for(int i = xLineShift;i<MAX_X;i+=gridInterval) canvas.drawLine(i,0,i,MAX_Y,paint);
        for(int i = yLineShift;i<MAX_Y;i+=gridInterval) canvas.drawLine(0,i,MAX_X,i,paint);
    }
    public void drawBounds(){
        paint.setColor(Color.DKGRAY);
        canvas.drawRect(0,bottomBound,MAX_X,MAX_Y,paint);
        canvas.drawRect(0,0,MAX_X,topBound,paint);
    }
    private void drawSlowButton() {
        paint.setColor(Color.CYAN);
        canvas.drawCircle(slowCoord[0],slowCoord[1],slowRad,paint);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Slow",slowCoord[0],slowCoord[1]+slowRad/6,paint);
    }
    private void drawPause(){
        paint.setColor(Color.LTGRAY);
        canvas.drawCircle(pauseX,pauseY,pauseRadius,paint);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(pauseRadius/3);
        canvas.drawLine(pauseX-pauseRadius/3,pauseY-pauseRadius/3,pauseX-pauseRadius/3,pauseY+pauseRadius/3,paint);
        canvas.drawLine(pauseX+pauseRadius/3,pauseY-pauseRadius/3,pauseX+pauseRadius/3,pauseY+pauseRadius/3,paint);
    }
    private void drawWarning(int alpha){
        paint.setColor(Color.RED);
        paint.setAlpha(alpha);
        canvas.drawRect(pauseX,pauseRect.bottom+MAX_Y/16,pauseX+MAX_Y/16,pauseRect.bottom+MAX_Y/4,paint);
        canvas.drawCircle(pauseX+MAX_Y/32,pauseRect.bottom+5*MAX_Y/16,MAX_Y/24,paint);
    }

    public void createObjects(int startX, int endX){
        int xDif = endX-startX;
        int yDif = bottomBound-topBound;
        for(int i=0;i<bumpNum;i++){ // Bumps
            bumps.add(new Bump(startX+rand.nextInt(xDif),topBound+rand.nextInt(yDif), (int) (3*unit30+rand.nextInt((int) (5*unit30)))));
        }
        for (int i=0;i<atrepNum;i++){ // Atreps
            atreps.add(new Atrep(startX+rand.nextInt(xDif),topBound+rand.nextInt(yDif), (rand.nextBoolean()?1:-1)*(unit30/5+((int) unit30/6>0?rand.nextInt((int) (unit30/6)):rand.nextFloat())),12*unit30+rand.nextInt((int) (10*unit30))));
        }
        for (int i=0;i<powerUpNum;i++){ // PowerUps
            powerUps.add(new PowerUp(startX+rand.nextInt(xDif),topBound+rand.nextInt(yDif),rand.nextInt(3)));
        }
    }

    public void toggleSlow(){
        float mult = (slowed? 2 : 0.5f);
        bally.modSpeedMult(mult);
        bally.modGravMult(mult);
        red.setSpeedMult(mult);
        for(Atrep a : atreps) a.setSpeedMult(mult);
        slowed=!slowed;
    }
    public void leave(){
        ((Activity) mContext).finish();
    }

    public int updateHighScores(int a){
        SharedPreferences.Editor editor = mContext.getSharedPreferences(prefName,MODE_PRIVATE).edit();
        SharedPreferences prefs = mContext.getSharedPreferences(prefName,MODE_PRIVATE);
        ArrayList<Integer> scores = new ArrayList<>();
        int added=0;
        for(int i = 1; i<=10;i++){
            scores.add(prefs.getInt("place"+i,0));
            if (added==0&&a>scores.get(i-1)){
                added=i;
                scores.add(i-1,a);
            }
        }
        for (int i=1; i<=10;i++){
            editor.putInt("place"+i,scores.get(i-1));
        }
        editor.commit();
        return added;
    }

    public static void playSound(Sounds sound){
        int a;
        switch (sound){
            case hit:a=0;break;
            case powerup: a=1; break;
            case win: a=2; break;
            case lose: a=3; break;
            case swoosh: a=4; break;
            default: a=0;
        }
        if (GameActivity.mediaArray.get(a).isPlaying()) {
            GameActivity.mediaArray.get(a).stop();
            try {
                GameActivity.mediaArray.get(a).prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        GameActivity.mediaArray.get(a).start();
    }
}