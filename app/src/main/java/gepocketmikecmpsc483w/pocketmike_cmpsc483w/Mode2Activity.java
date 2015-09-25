package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Mode2Activity extends AppCompatActivity implements View.OnClickListener{

    Button Mode2BackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_mode2);
        Mode2BackButton = (Button) findViewById(R.id.Mode2BackButton);
        Mode2BackButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mode2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void Mode2BackButtonOnClick() {
        finish();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Mode2BackButton:
                Mode2BackButtonOnClick();
                break;
        }
    }
}
