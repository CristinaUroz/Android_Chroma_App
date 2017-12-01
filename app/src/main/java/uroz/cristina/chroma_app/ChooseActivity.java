package uroz.cristina.chroma_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

//TODO: Borrar buttons
//TODO: posar previus i next a dalt

public class ChooseActivity extends AppCompatActivity {
    // Declaracio de referencies a elements de la pantalla
    private Button btn_add_fore, btn_add_back, btn_del_fore, btn_del_back, btn_next;
    private Button btn1, btn2;
    private EditText text;
    private int codi_imatge=1;

    // Variables globals
    private boolean visibilitatF = true; // Visibilitat del boto + i de la imatge foreground
    private boolean visibilitatB = true; // Visibilitat del boto + i de la imatge background
    private boolean primera_vegada;
    // Proba passar dades entre dues activitats
    public static String KEY_NOM = "KEY_NOM";
    public static String KEY_B = "KEY_B";

    private ImageView fore_ima;
    private ImageView back_ima;
    ///////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_activity);


        // Obtencio de referencies a elements de la pantalla

        fore_ima = (ImageView)findViewById(R.id.ima_fore);
        back_ima = (ImageView)findViewById(R.id.ima_back);
/*        btn1 = (Button) findViewById(R.id.prova1);
        btn2 = (Button) findViewById(R.id.prova2);
        btn_add_fore = (Button) findViewById(R.id.foreground_add);
        btn_add_back = (Button) findViewById(R.id.background_add);
        btn_del_fore = (Button) findViewById(R.id.foreground_delete);
        btn_del_back = (Button) findViewById(R.id.background_delete);*/
        btn_next = (Button) findViewById(R.id.next_button_choose);

       // text = (EditText) findViewById(R.id.text1);

        // Proba passar dades entre dues activitats

        // getIntent().getExtras().isEmpty()
        if (primera_vegada) {
            String nom = getIntent().getExtras().getString(KEY_NOM);
            text.setText(nom);
            primera_vegada = getIntent().getExtras().getBoolean(KEY_B);
        }
        ///////////////////////////////////////////

     /*   btn_add_fore.setVisibility(View.VISIBLE);
        btn_add_back.setVisibility(View.VISIBLE);*/

        // Botons de prova, es reemplaçaran per les imatges que es carregaran
//        btn1.setVisibility(View.INVISIBLE);
  //      btn2.setVisibility(View.INVISIBLE);

        // Boto next
        // Passar a la seguent activitat
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                primera_vegada = true;
                Intent intent = new Intent(ChooseActivity.this, ChromaActivity.class);
                // Proba passar dades entre dues activitats
                String nom = "0"; //text.getText().toString();
                intent.putExtra(ChromaActivity.KEY_NOM, nom);
                intent.putExtra(ChromaActivity.KEY_B, primera_vegada);
                ///////////////////////////////////////////
                startActivity(intent);
             //   finish();
            }
        });

        // Boto delete foreground
        //
        // En lloc d'executarse al fer click al boto, s'ha d'executar quan hi ha una imatge carregada
    /*    btn_del_fore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visibilitatF = !visibilitatF;
                cambiaVisivilitatForeground(visibilitatF);
            }
        });

        // Boto delete background
        //
        // En lloc d'executarse al fer click al boto, s'ha d'executar quan hi ha una imatge carregada
        btn_del_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visibilitatB = !visibilitatB;
                cambiaVisivilitatBackground(visibilitatB);
            }
        });*/

        // Boto add foreground
        //
        fore_ima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codi_imatge=1;

                chooseCameraGallery();
            }
        });

        // Boto add background
        //
        back_ima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                codi_imatge=0;

                chooseCameraGallery();
                //Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(takePicture, 0);//zero can be replaced with any action code


            }
        });


        //////////////////////////////////////////////////////////////////////////////////////
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
        //////////////////////////////////////////////////////////////////////////////////////


    }


    private void chooseCameraGallery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose);
        builder.setMessage(R.string.choose_from);
        builder.setCancelable(false);


        builder.setNegativeButton(R.string.camera, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);//zero can be replaced with any action code
            }
        });

        builder.setPositiveButton(R.string.gallery, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
            }
        });
        builder.create().show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

        ImageView imageview = (ImageView)findViewById(R.id.ima_fore);

        if (codi_imatge==1){
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
            switch(requestCode) {
                case 0:
                    if(resultCode == RESULT_OK){
                        Uri selectedImage = imageReturnedIntent.getData();
                        fore_ima.setImageURI(selectedImage);
                        String aux=fore_ima.toString();
                        Log.d("Cris",aux);
                    }

                    break;
                case 1:
                    if(resultCode == RESULT_OK){
                        Uri selectedImage = imageReturnedIntent.getData();
                        fore_ima.setImageURI(selectedImage);
                        String aux=fore_ima.toString();
                        Log.d("Cris",aux);

                    }
                    break;
            }
        }
        else if (codi_imatge==0){
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
            switch(requestCode) {
                case 0:
                    if(resultCode == RESULT_OK){
                        Uri selectedImage = imageReturnedIntent.getData();
                        back_ima.setImageURI(selectedImage);
                        String aux=back_ima.toString();
                        Log.d("Cris",aux);
                    }

                    break;
                case 1:
                    if(resultCode == RESULT_OK){
                        Uri selectedImage = imageReturnedIntent.getData();
                        back_ima.setImageURI(selectedImage);
                        String aux=back_ima.toString();
                        Log.d("Cris",aux);
                    }
                    break;
            }
        }
    }
}
