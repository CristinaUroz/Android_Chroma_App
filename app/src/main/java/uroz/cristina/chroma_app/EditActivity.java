package uroz.cristina.chroma_app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {
    // Declaracio de referencies a elements de la pantalla
    private Button btn_prev, btn_next, btn_fore, btn_back, btn_contrast, btn_brillo, btn_temp, btn_rot, btn_satu, btn_opac;
    private SeekBar barra_edit;
    private ImageView ima_mixed;


    // Variables globals
    private Uri fore_uri;
    private Uri back_uri;
    public static String KEY_FORE_URI3 = "KEY_FORE_URI3";
    public static String KEY_BACK_URI3 = "KEY_BACK_URI3";
    private int valor_barra;
    private int EDIT_IMAGE_CODE = 0;
    private int EDIT_VARIABLE_CODE = 0;

    // On es guarden els valors de la seekbar per cada efecte i imatge (fore i back)
    private int[][] valors_editables = new int[2][6];
    // Al acabar les probes, es poden borrar
    private String[] edit_image = {"Foreground", "Background"};
    private String[] edit_variable = {"Contrast", "Bright", "Warmth", "Rotation", "Saturation", "Opacity"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        // Obtencio de referencies a elements de la pantalla
        btn_prev = (Button) findViewById(R.id.previous_button_edit);
        btn_next = (Button) findViewById(R.id.next_button_edit);
        btn_fore = (Button) findViewById(R.id.foreground_button_edit);
        btn_back = (Button) findViewById(R.id.background_button_edit);
        btn_contrast = (Button) findViewById(R.id.contrast_button);
        btn_brillo = (Button) findViewById(R.id.brightness_button);
        btn_temp = (Button) findViewById(R.id.temperature_button);
        btn_rot = (Button) findViewById(R.id.rotation_button);
        btn_satu = (Button) findViewById(R.id.saturation_button);
        btn_opac = (Button) findViewById(R.id.opacity_button);
        barra_edit = (SeekBar) findViewById(R.id.selection_bar);
        ima_mixed = (ImageView) findViewById(R.id.ima_mixed);

        // Configuracio de la barra
        barra_edit.setMax(100);
        barra_edit.setProgress(50);

        //// Dades activitat anterior
        fore_uri = Uri.parse(getIntent().getExtras().getString(KEY_FORE_URI3));
        back_uri = Uri.parse(getIntent().getExtras().getString(KEY_BACK_URI3));

        ima_mixed.setImageURI(fore_uri);

        // Inicialitzacio del vector dels valors
        for (int i = 0; i < 2; i++) {
            //valors_editables[i] = new int[6];
            for (int j = 0; j < 6; j++) {
                valors_editables[i][j] = 50;
            }
        }

        // Accions que s'executaran quan es mogui la barra
        barra_edit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE] = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                edit_image();
            }
        });

        // Boto prev
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditActivity.this, ChromaActivity.class);
                String back = back_uri.toString();
                String fore = fore_uri.toString();
                intent.putExtra(ChromaActivity.KEY_FORE_URI2, fore);
                intent.putExtra(ChromaActivity.KEY_BACK_URI2, back);
                startActivity(intent);
                finish();
            }
        });

        // Boto next
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditActivity.this, ShareActivity.class);
                try {
                    String back = back_uri.toString();
                    String fore = fore_uri.toString();
                    intent.putExtra(ShareActivity.KEY_BACK_URI4, back);
                    intent.putExtra(ShareActivity.KEY_FORE_URI4, fore);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    //String msg = e.toString();
                    String msg = getString(R.string.missing_data);
                    Toast.makeText(EditActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
        });

        // Boto foreground
        btn_fore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_IMAGE_CODE = 0;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
            }
        });

        // Boto background
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_IMAGE_CODE = 1;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
            }
        });

        // Boto contrast
        btn_contrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_VARIABLE_CODE = 0;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
            }
        });

        // Boto brillo
        btn_brillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_VARIABLE_CODE = 1;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
            }
        });

        // Boto temperatura
        btn_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_VARIABLE_CODE = 2;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
            }
        });

        // Boto rotacio
        btn_rot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_VARIABLE_CODE = 3;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
            }
        });

        // Boto saturacio
        btn_satu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_VARIABLE_CODE = 4;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
            }
        });

        // Boto opacitat
        btn_opac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_VARIABLE_CODE = 5;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
            }
        });
    }

    private void edit_image() {
        String msg = String.format(edit_image[EDIT_IMAGE_CODE] + " " + edit_variable[EDIT_VARIABLE_CODE] + " " + valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
        // Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Toast t = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        t.show();
    }
}
