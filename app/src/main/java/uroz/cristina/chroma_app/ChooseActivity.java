package uroz.cristina.chroma_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseActivity extends AppCompatActivity {
    // Declaracio de referencies a elements de la pantalla
    private Button btn_add_fore, btn_add_back, btn_del_fore, btn_del_back, btn_next;
    private Button btn1, btn2;

    // Variables locals
    private boolean visibilitatF = true; // Visibilitat del boto + i de la imatge foreground
    private boolean visibilitatB = true; // Visibilitat del boto + i de la imatge background

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_activity);

        // Obtencio de referencies a elements de la pantalla
        btn1 = (Button) findViewById(R.id.prova1);
        btn2 = (Button) findViewById(R.id.prova2);
        btn_add_fore = (Button) findViewById(R.id.foreground_add);
        btn_add_back = (Button) findViewById(R.id.background_add);
        btn_del_fore = (Button) findViewById(R.id.foreground_delete);
        btn_del_back = (Button) findViewById(R.id.background_delete);
        btn_next = (Button) findViewById(R.id.next_button_choose);

        btn_add_fore.setVisibility(View.VISIBLE);
        btn_add_back.setVisibility(View.VISIBLE);

        // Botons de prova, es reemplaçaran per les imatges que es carregaran
        btn1.setVisibility(View.INVISIBLE);
        btn2.setVisibility(View.INVISIBLE);


        // En lloc d'executarse al fer click al boto, s'ha d'executar quan hi ha una imatge carregada
        btn_del_fore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visibilitatF = !visibilitatF;
                cambiaVisivilitatForeground(visibilitatF);
            }
        });

        // En lloc d'executarse al fer click al boto, s'ha d'executar quan hi ha una imatge carregada
        btn_del_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visibilitatB = !visibilitatB;
                cambiaVisivilitatBackground(visibilitatB);
            }
        });


        // FOREGROUND
        // Agafar imatge de la galeria i mostrarla
        /*

         */


        // Agafar una imatge amb la camera i mostrarla
        /*

         */

        // Mostrar quadre de dialeg per triar si volem agafar la imatge le la galeria o de la camera
        /*

         */

        // BACKGROUND
        /*

         */
    }

    private void cambiaVisivilitatForeground(boolean b) {

        if (b) {
            btn1.setVisibility(View.INVISIBLE);
            btn_add_fore.setVisibility(View.VISIBLE);
        } else {
            btn1.setVisibility(View.VISIBLE);
            btn_add_fore.setVisibility(View.INVISIBLE);
        }
    }

    private void cambiaVisivilitatBackground(boolean b) {

        if (b) {
            btn2.setVisibility(View.INVISIBLE);
            btn_add_back.setVisibility(View.VISIBLE);
        } else {
            btn2.setVisibility(View.VISIBLE);
            btn_add_back.setVisibility(View.INVISIBLE);
        }
    }
}
