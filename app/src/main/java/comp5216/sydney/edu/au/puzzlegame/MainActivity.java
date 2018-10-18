package comp5216.sydney.edu.au.puzzlegame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button instruction;
    Button choose_image;
    private RadioGroup difficultyGroup;
    private RadioGroup typeGroup;
    private RadioButton difficultyButton;
    private RadioButton typeButton;
    int level=2;
    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        difficultyGroup = (RadioGroup) findViewById(R.id.radioGroup);
        typeGroup = (RadioGroup) findViewById(R.id.radioGroup1);

        addListenerOnButton();
    }

    public void addListenerOnButton() {

        difficultyGroup = (RadioGroup) findViewById(R.id.radioGroup);
        choose_image= (Button) findViewById(R.id.choose_image);

        choose_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int selected_diffi = difficultyGroup.getCheckedRadioButtonId();
                difficultyButton = (RadioButton) findViewById(selected_diffi);
                String text = difficultyButton.getText().toString();

//                Toast.makeText(MainActivity.this, difficultyButton.getText(), Toast.LENGTH_SHORT).show();

                switch (text){
                    case "2X2":
                        level = 2;
//                        if(level==2){
//                            Toast.makeText(MainActivity.this, difficultyButton.getText(), Toast.LENGTH_SHORT).show();
//                        }
                        break;
                    case "3X3":
                        level = 3;
//                        if(level==3){
//                            Toast.makeText(MainActivity.this, difficultyButton.getText(), Toast.LENGTH_SHORT).show();
//                        }
                        break;
                    case "4X4":
                        level = 4;
//                        if(level==4){
//                            Toast.makeText(MainActivity.this, difficultyButton.getText(), Toast.LENGTH_SHORT).show();
//                        }
                        break;
                    case "5X5":
                        level = 5;
//                        if(level==5){
//                            Toast.makeText(MainActivity.this, difficultyButton.getText(), Toast.LENGTH_SHORT).show();
//                        }
                        break;
                }

                int selected_type = typeGroup.getCheckedRadioButtonId();
                typeButton = (RadioButton)findViewById(selected_type);
                String type_text = typeButton.getText().toString();

                switch (type_text){
                    case "Moving Puzzle":
                        type=0;
//                        if(type==0){
//                            Toast.makeText(MainActivity.this, typeButton.getText(), Toast.LENGTH_SHORT).show();
//                        }
                        break;
                    case "Swipping Puzzle":
                        type=1;
//                        if(type==1){
//                            Toast.makeText(MainActivity.this, typeButton.getText(), Toast.LENGTH_SHORT).show();
//                        }
                        break;

                }

                Intent intent = new Intent(MainActivity.this, ChooseImage.class);
                intent.putExtra("level", level);
                intent.putExtra("type", type);
                startActivity(intent);
            }

        });

    }


        //在menu bar加 instruction
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.introduction) {
            startActivityForResult(new Intent(this, Instruction.class), 1024);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
