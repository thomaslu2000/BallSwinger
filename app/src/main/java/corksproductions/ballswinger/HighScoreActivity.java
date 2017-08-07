package corksproductions.ballswinger;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.widget.Button;
import android.widget.TextView;

public class HighScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        SharedPreferences prefs = getSharedPreferences(MainActivity.prefName,MODE_PRIVATE);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float textSize=size.y/20;

        int[] ids = {R.id.FirstPlaceScore,R.id.SecondPlaceScore,R.id.ThirdPlaceScore,R.id.FourthPlaceScore,R.id.FifthPlaceScore,
                R.id.SixthPlaceScore,R.id.SeventhPlaceScore,R.id.EighthPlaceScore,R.id.NinthPlaceScore,R.id.TenthPlaceScore};
        for (int i=0;i<ids.length;i++) {
            TextView textView = (TextView) findViewById(ids[i]);
            textView.setText(GameActivity.wordPlace(i+1)+" Place: "+prefs.getInt("place"+(i+1),0));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        }
        TextView textView = (TextView) findViewById(R.id.HighScores);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize*1.2f);
        Button mainMenu = (Button) findViewById(R.id.button3);
        mainMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void end(android.view.View view){
        finish();
    }
}
