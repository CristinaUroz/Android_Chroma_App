package uroz.cristina.chroma_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

public class ChromaActivity extends AppCompatActivity {
    // Declaracio de referencies a elements de la pantalla
    private Button btn_next, btn_prev, btn_hsl, btn_palete;
    private SeekBar barra_chroma;
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
        setContentView(R.layout.chroma_activity);

        // Obtencio de referencies a elements de la pantalla
        btn_next = (Button) findViewById(R.id.next_button_chroma);
        btn_prev = (Button) findViewById(R.id.previous_button_chroma);
        btn_hsl = (Button) findViewById(R.id.hsl_button);
        btn_palete = (Button) findViewById(R.id.palette_button);
        barra_chroma = (SeekBar) findViewById(R.id.tolerance_bar);

        text = (EditText) findViewById(R.id.text2);

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
                Intent intent = new Intent(ChromaActivity.this, ChooseActivity.class);
                // Proba passar dades entre dues activitats
                String nom = text.getText().toString();
                //intent.putExtra(ChooseActivity.KEY_NOM, nom);
                //intent.putExtra(ChooseActivity.KEY_B, primera_vegada);
                ///////////////////////////////////////////
                startActivity(intent);
                finish();
            }
        });

        // Boto next
        // Passar a l'activitat seguent
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChromaActivity.this, EditActivity.class);
                // Proba passar dades entre dues activitats
                String nom = text.getText().toString();
                intent.putExtra(EditActivity.KEY_NOM, nom);
                intent.putExtra(EditActivity.KEY_B, primera_vegada);
                ///////////////////////////////////////////
                startActivity(intent);
                finish();
            }
        });

        // Boto hsl
        //
        btn_hsl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Boto palete
        //
        btn_palete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
