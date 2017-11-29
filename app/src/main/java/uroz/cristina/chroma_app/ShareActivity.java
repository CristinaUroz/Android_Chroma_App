package uroz.cristina.chroma_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ShareActivity extends AppCompatActivity {
    // Declaracio de referencies a elements de la pantalla
    private Button btn_share, btn_save, btn_restart, btn_finish, btn_prev;

    // Variables locals

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

        // Boto prev
        // Passar a l'activitat anterior
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShareActivity.this, EditActivity.class);
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
