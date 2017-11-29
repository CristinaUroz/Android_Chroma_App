package uroz.cristina.chroma_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ShareActivity extends AppCompatActivity {
    // Declaracio de referencies a elements de la pantalla
    private Button btn_share, btn_save, btn_restart, btn_finish, btn_prev;
    private EditText text;

    // Variables globals
    private boolean primera_vegada;
    // Proba passar dades entre dues activitats
    public static String KEY_NOM = "KEY_NOM";
    public static String KEY_B = "KEY_B";
    ///////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);

        // Obtencio de referencies a elements de la pantalla
        btn_prev = (Button) findViewById(R.id.previous_button_share);
        btn_share = (Button) findViewById(R.id.share_button);
        btn_restart = (Button) findViewById(R.id.restart_button);
        btn_finish = (Button) findViewById(R.id.finish_button);
        btn_save = (Button) findViewById(R.id.save_button);

        text = (EditText) findViewById(R.id.text4);

        // Proba passar dades entre dues activitats
        String nom = getIntent().getExtras().getString(KEY_NOM);
        text.setText(nom);
        primera_vegada = getIntent().getExtras().getBoolean(KEY_B);
        ///////////////////////////////////////////

        // Boto prev
        // Passar a l'activitat anterior
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShareActivity.this, EditActivity.class);
                // Proba passar dades entre dues activitats
                String nom = text.getText().toString();
                intent.putExtra(EditActivity.KEY_NOM, nom);
                intent.putExtra(EditActivity.KEY_B, primera_vegada);
                ///////////////////////////////////////////
                startActivity(intent);
                finish();
            }
        });

        // Boto share
        //
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Boto save
        //
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        // Boto restart
        //
        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        // Boto finish
        //
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
