package by.buryser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {
    private EditText userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        userName = (EditText) findViewById(R.id.textField);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName.setText(extras.getString("name"));
        }
    }

    public void okAction(View view) {
        Intent intent = new Intent();
        intent.putExtra("name", userName.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void cancelAction(View view) {
        finish();
    }
}
