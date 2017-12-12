package uroz.cristina.chroma_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

public class EditActivity extends AppCompatActivity {
    // Declaracio de referencies a elements de la pantalla
    private Button btn_prev, btn_next, btn_fore, btn_back, btn_contrast, btn_brillo, btn_temp, btn_rot, btn_satu, btn_opac;
    private SeekBar barra_edit;
    private ImageView ima_mixed;

    // Variables globals

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

        // Boto prev
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditActivity.this, ChromaActivity.class);

                startActivity(intent);
                finish();
            }
        });

        // Boto next
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditActivity.this, ShareActivity.class);

                startActivity(intent);
                finish();
            }
        });

        // Boto foreground
        btn_fore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Boto background
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Boto contrast
        btn_contrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Boto brillo
        btn_brillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Boto temperatura
        btn_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Boto rotacio
        btn_rot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Boto saturacio
        btn_satu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Boto opacitat
        btn_opac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
