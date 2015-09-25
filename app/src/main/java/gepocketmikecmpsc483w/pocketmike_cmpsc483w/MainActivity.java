package gepocketmikecmpsc483w.pocketmike_cmpsc483w;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button Mode1Button;
    Button Mode2Button;
    Button Mode3Button;
    Button SettingsButton;
    Button CopyrightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Mode1Button = (Button)findViewById(R.id.Mode1Button);
        Mode1Button.setOnClickListener(this);
        Mode2Button = (Button)findViewById(R.id.Mode2Button);
        Mode2Button.setOnClickListener(this);
        Mode3Button = (Button)findViewById(R.id.Mode3Button);
        Mode3Button.setOnClickListener(this);
        SettingsButton = (Button)findViewById(R.id.SettingsButton);
        SettingsButton.setOnClickListener(this);
        CopyrightButton = (Button)findViewById(R.id.CopyrightButton);
        CopyrightButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void Mode1ButtonOnClick()
    {
        startActivity(new Intent("Mode1Activity"));
    }

    private void Mode2ButtonOnClick()
    {
        startActivity(new Intent("Mode2Activity"));
    }

    private void Mode3ButtonOnClick()
    {
        startActivity(new Intent("Mode3Activity"));
    }

    private void SettingsButtonOnClick()
    {
        startActivity(new Intent("SettingsActivity"));
    }

    private void CopyrightButtonOnClick()
    {
        startActivity(new Intent("CopyrightActivity"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Mode1Button:
                Mode1ButtonOnClick();
                break;
            case R.id.Mode2Button:
                Mode2ButtonOnClick();
                break;
            case R.id.Mode3Button:
                Mode3ButtonOnClick();
                break;
            case R.id.SettingsButton:
                SettingsButtonOnClick();
                break;
            case R.id.CopyrightButton:
                CopyrightButtonOnClick();
                break;
        }
    }
}
