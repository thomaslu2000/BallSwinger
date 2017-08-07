package corksproductions.ballswinger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String prefName = "SharedPrefs";

    Intent intent;

    int[] buttonIds = {R.id.button,R.id.button2,R.id.HelpButton};
    int deviceSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        deviceSize=size.x+size.y;

        setContentView(R.layout.activity_main);

        LinearLayout l = (LinearLayout) findViewById(R.id.MainLayout);
        l.setBackgroundColor(Color.WHITE);

        int textSize = (size.x<size.y)?size.y/30:size.x/30;
        for(int i=0;i<buttonIds.length;i++){
            Button button = (Button) findViewById(buttonIds[i]);
            button.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        }

        ((TextView) findViewById(R.id.textView)).setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize*2.5f);


        initHighScores();

    }
    public void startGame(android.view.View view){
        intent=new Intent(this,GameActivity.class);
        startActivity(intent);
    }
    public void startHighScore(android.view.View view){
        intent=new Intent(this,HighScoreActivity.class);
        startActivity(intent);
    }
    public void startHelp(android.view.View view){
        intent=new Intent(this,HelpActivity.class);
        startActivity(intent);
    }
    public void initHighScores(){
        SharedPreferences.Editor editor = getSharedPreferences(prefName,MODE_PRIVATE).edit();
        SharedPreferences prefs = getSharedPreferences(prefName,MODE_PRIVATE);
        for (int i=1;i<=10;i++){
            editor.putInt("place"+i,prefs.getInt("place"+i,0));
        }
        editor.commit();
    }
}
