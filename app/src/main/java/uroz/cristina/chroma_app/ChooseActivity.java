package uroz.cristina.chroma_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

//TODO: posar previus i next a dalt

public class ChooseActivity extends AppCompatActivity {
    // Declaracio de referencies a elements de la pantalla
    private Button btn_next;
    private int codi_imatge = 1;

    // Variables globals
    private ImageView fore_ima;
    private ImageView back_ima;
    private Uri fore_uri;
    private Uri back_uri;
    private String fileName;
    private File dir;
    public static String fore_uri1 = "fore_uri2";
    public static String back_uri1 = "back_uri2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_activity);

        // Creació del directori
        dir = new File (Environment.getExternalStorageDirectory(), "/ChromAppPhotos/");
        if (!dir.exists()){
            dir.mkdirs();
            Log.i("Cristina","sha creat el directori");
        }

        // Obtencio de referencies a elements de la pantalla
        fore_ima = (ImageView)findViewById(R.id.ima_fore);
        back_ima = (ImageView)findViewById(R.id.ima_back);
        btn_next = (Button) findViewById(R.id.next_button_choose);

        if(getIntent() != null && getIntent().getExtras() != null) {

            fore_uri = Uri.parse(getIntent().getExtras().getString(fore_uri1));
            back_uri = Uri.parse(getIntent().getExtras().getString(back_uri1));

            if (back_uri1 != null) {
                back_ima.setImageURI(back_uri);
            }

            if (fore_uri1 != null) {
                fore_ima.setImageURI(fore_uri);
            }
        }

        // Boto next
        // Passar a la seguent activitat
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseActivity.this, ChromaActivity.class);

                // Passar dades entre dues activitats
                String back=back_uri.toString();
                String fore=fore_uri.toString();
                intent.putExtra(ChromaActivity.back_uri2, back);
                intent.putExtra(ChromaActivity.fore_uri2, fore);
                ///////////////////////////////////////////

                startActivity(intent);
                finish();
            }
        });

        //Add foreground
        fore_ima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codi_imatge=1;
                chooseCameraGallery();
            }
        });

        // Add background
        back_ima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codi_imatge=0;
                chooseCameraGallery();
            }
        });
    }

    private void chooseCameraGallery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose);
        builder.setMessage(R.string.choose_from);
        builder.setCancelable(true);


        builder.setNegativeButton(R.string.camera, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fileName = dir + "/" +  getPhotoName() + ".jpg";

                File photoFile = new File(fileName);
                try {
                    photoFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                if (codi_imatge==0) {
                    back_uri = Uri.fromFile(photoFile);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, back_uri);
                    startActivityForResult(cameraIntent, 1);
                }
                if (codi_imatge==1) {
                    fore_uri = Uri.fromFile(photoFile);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fore_uri);
                    startActivityForResult(cameraIntent, 1);
                }

            }
        });
        builder.setPositiveButton(R.string.gallery, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 0);//one can be replaced with any action code
            }
        });
        builder.create().show();
    }

    @NonNull
    private String getPhotoName() {
        Calendar calendar= Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return Integer.toString(year) +"_" + Integer.toString(month) + "_" + Integer.toString(day) + "_" + Integer.toString(hour)+ ":"+ Integer.toString(minute)+":" +Integer.toString(second);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        if (codi_imatge==1){
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
            switch(requestCode) {
                case 0:
                    if(resultCode == RESULT_OK){
                        fore_uri = imageReturnedIntent.getData();
                       // fore_ima.setImageURI(selectedImage);
                       // String aux=fore_ima.toString();
                        if (fore_uri==null){
                            Log.d("Cris","null");}
                        else{Log.d("Cris","No null");
                            fore_ima.setImageURI(fore_uri);
                        }
                    }

                    break;
                case 1:
                    if(resultCode == RESULT_OK){
                        Bitmap bMap = BitmapFactory.decodeFile(fileName);
                        fore_ima.setImageBitmap(bMap);
                    }
                    break;
            }
        }
        else if (codi_imatge==0){
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
            switch(requestCode) {
                case 0:
                    if(resultCode == RESULT_OK){
                        back_uri = imageReturnedIntent.getData();
                       // back_ima.setImageURI(selectedImage);
                        //String aux=back_ima.toString();
                        if (back_uri==null){
                            Log.d("Cris","null");}
                        else{Log.d("Cris","No null");
                            back_ima.setImageURI(back_uri);
                        }
                    }

                    break;
                case 1:
                    if(resultCode == RESULT_OK){
                        Bitmap bMap = BitmapFactory.decodeFile(fileName);
                        back_ima.setImageBitmap(bMap);
                    }
                    break;
            }
        }
    }
}
