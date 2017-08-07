package corksproductions.ballswinger;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

import java.io.IOException;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private View view;

    static ArrayList<MediaPlayer> mediaArray = new ArrayList<>();
    static MediaPlayer music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        music=MediaPlayer.create(this, R.raw.song3);

        music.setVolume(0.6f,0.6f);

        mediaArray.add(MediaPlayer.create(this, R.raw.hit));
        mediaArray.add(MediaPlayer.create(this,R.raw.powerup));
        mediaArray.add(MediaPlayer.create(this,R.raw.win));
        mediaArray.add(MediaPlayer.create(this,R.raw.lose));
        mediaArray.add(MediaPlayer.create(this,R.raw.swoosh));
        for(MediaPlayer a : mediaArray){
            a.setVolume(0.2f,0.2f);
        }
        mediaArray.get(4).setVolume(0.5f,0.5f);

        view = new View (this, size.x,size.y);
        setContentView(view);
    }
    static String[] placeStr= {"First","Second","Third","Fourth","Fifth","Sixth","Seventh","Eighth","Ninth","Tenth"};
    public static String wordPlace(int a) {
        try{
            return placeStr[a-1];
        } catch (IndexOutOfBoundsException e){
            return "";
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        view.pause();
        if (music.isPlaying()) {
            music.stop();
            try {
                music.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (music.isPlaying()) {
            music.stop();
            try {
                music.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

