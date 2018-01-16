package uroz.cristina.chroma_app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

public class ChooseActivity extends AppCompatActivity {
    // Declaracio de referencies a elements de la pantalla
    private Button btn_next;
    private ImageView fore_ima;
    private ImageView back_ima;

    // Variables globals
    private int codi_imatge = 1;
    private Uri fore_uri;
    private Uri back_uri;
    private String fileName;
    private File dir;
    private String image_dir = "/ChromAppPhotos/data/";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private boolean fore_guardada=false;
    private boolean back_guardada=false;

    // Guardem les dades quan girem la pantalla
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fore_uri != null) {
            outState.putString("fore_uri", fore_uri.toString());
        }
        if (back_uri != null) {
            outState.putString("back_uri", back_uri.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_activity);

        // PERMISOS
        // Es demana el permis per utilitzar la camera, i quan s'acepta, es demana el permis per escriure fitxers
        // Demana permisos de camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Log.i("kike", "demana camera opcio 1");
            } else {
                Log.i("kike", "demana camera opcio 2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }

        // Creació del directori on es guarden les fotos que es fan en aquesta activitat
        dir = new File(Environment.getExternalStorageDirectory(), image_dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Obtencio de referencies a elements de la pantalla
        fore_ima = (ImageView) findViewById(R.id.ima_fore);
        back_ima = (ImageView) findViewById(R.id.ima_back);
        btn_next = (Button) findViewById(R.id.next_button_choose);

        // Recuperacio de dades de quan tornem d'una altra activitat
        //if (getIntent() != null && getIntent().getExtras() != null) {
        if (getIntent() != null) {
                try {
                    File dir = new File(getCacheDir(), "Fore");
                    Log.i("Cris",dir.getAbsolutePath());
                    Bitmap fore = BitmapFactory.decodeFile(dir.getAbsolutePath());
                    if (fore.getHeight()!=0){
                    fore_ima.setImageBitmap(fore);
                    fore_guardada=true;}

                    dir = new File(getCacheDir(), "Back");
                    Log.i("Cris",dir.getAbsolutePath());
                    Bitmap back = BitmapFactory.decodeFile(dir.getAbsolutePath());
                    if (back.getHeight()!=0){
                    back_ima.setImageBitmap(back);
                    back_guardada=true;}
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        // Recuperacio de dades de quan girem la pantalla
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("fore_uri") != null) {
                fore_uri = Uri.parse(savedInstanceState.getString("fore_uri"));
                fore_ima.setImageURI(fore_uri);
            }
            if (savedInstanceState.getString("back_uri") != null) {
                back_uri = Uri.parse(savedInstanceState.getString("back_uri"));
                back_ima.setImageURI(back_uri);
            }
        }

        // Boto next
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Passar dades entre dues activitats
                guardarImatgesCache();

                Intent intent = new Intent(ChooseActivity.this, ChromaActivity.class);

                if(fore_guardada&&back_guardada){
                startActivity(intent);
                finish();}
                else {

                Toast.makeText(ChooseActivity.this, getString(R.string.missing_data), Toast.LENGTH_LONG).show();}

            }
        });

        // Afegeix la imatge foreground
        fore_ima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codi_imatge = 1;
                chooseCameraGallery();
            }
        });

        // Afegeix la imatge background
        back_ima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codi_imatge = 0;
                chooseCameraGallery();
            }
        });
    }

    // Metode per carregar la imatge desde on es seleccioni
    private void chooseCameraGallery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose);
        builder.setMessage(R.string.choose_from);
        builder.setCancelable(true);

        // Si s'agafa la imatge de la camera
        builder.setNegativeButton(R.string.camera, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fileName = dir + "/" + getPhotoName();
                File photoFile = new File(fileName);
                try {
                    photoFile.createNewFile();
                    if (codi_imatge == 0) {
                        back_uri = Uri.fromFile(photoFile);
                        Log.i("cris", back_uri.toString());
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, back_uri);
                        startActivityForResult(cameraIntent, 1);
                    } else if (codi_imatge == 1) {
                        fore_uri = Uri.fromFile(photoFile);
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fore_uri);
                        startActivityForResult(cameraIntent, 1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

            }
        });

        // Si s'agafa la imatge de la galeria
        builder.setPositiveButton(R.string.gallery, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 0);
            }
        });

        builder.create().show();
    }

    // Genera un nom per una imatge EX: '2017.12.15_20.31.46.jpg'
    private String getPhotoName() {
        Calendar calendar = Calendar.getInstance();
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = Integer.toString(calendar.get(Calendar.MINUTE));
        String second = Integer.toString(calendar.get(Calendar.SECOND));

        day = (day.length() == 1) ? "0" + day : day;
        month = (month.length() == 1) ? "0" + month : month;
        hour = (hour.length() == 1) ? "0" + hour : hour;
        minute = (minute.length() == 1) ? "0" + minute : minute;
        second = (second.length() == 1) ? "0" + second : second;

        return year + "." + month + "." + day + "_" + hour + "." + minute + "." + second + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        if (codi_imatge == 1) {
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK) {
                        fore_uri = imageReturnedIntent.getData();
                        if (fore_uri != null) {
                            fore_ima.setImageURI(fore_uri);
                            fore_guardada=false;
                        }
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK) {
                        Bitmap bMap = BitmapFactory.decodeFile(fileName);
                        fore_ima.setImageBitmap(bMap);
                        fore_guardada=false;
                    }
                    break;
            }
        } else if (codi_imatge == 0) {
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK) {
                        back_uri = imageReturnedIntent.getData();
                        if (back_uri != null) {
                            back_ima.setImageURI(back_uri);
                            back_guardada=false;
                        }
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK) {
                        Bitmap bMap = BitmapFactory.decodeFile(fileName);
                        back_ima.setImageBitmap(bMap);
                        back_guardada=false;
                    }
                    break;
            }
        }
    }

    // Metode per demanar permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("kike", "permis camera concedit");
                    // Demana permisos d'escriptura
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Log.i("kike", "demana escriptura opcio 1");
                        } else {
                            Log.i("kike", "demana escriptura opcio 2");
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    }
                } else {
                    Log.i("kike", "permis camera denegat");
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("kike", "permis escriptura concedit");
                } else {
                    Log.i("kike", "permis escriptura denegat");
                }
            }
            // Altres permisos, afegir 'case'
        }
    }

    public void guardarImatgesCache() {

        try {
            if (!fore_guardada){
            Bitmap fore = Resize(MediaStore.Images.Media.getBitmap(this.getContentResolver(), fore_uri));
                File file_fore = new File(this.getCacheDir(), "Fore");
                OutputStream outStream = new FileOutputStream(file_fore);
                fore.compress(Bitmap.CompressFormat.PNG, 85, outStream);
                outStream.close();
            fore_guardada=true;}
            if (!back_guardada) {
                Bitmap back = Resize(MediaStore.Images.Media.getBitmap(this.getContentResolver(), back_uri));
                File file_back = new File(this.getCacheDir(), "Back");
                OutputStream outStream = new FileOutputStream(file_back);
                back.compress(Bitmap.CompressFormat.PNG, 85, outStream);
                outStream.close();
                back_guardada=true;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Bitmap Resize (Bitmap bitmap) {
        // Es fa un resize de la imatge a un tamany mes petit fent que el costat mes gran de la
        // imatge no sigui superior a pix_max
        int pix_max = 200;
        int H;
        int W;
        Bitmap resizedBitmap;

        if (bitmap.getHeight() > bitmap.getWidth()) {
            if (bitmap.getHeight() > pix_max) {
                H = pix_max;
                W = (int) ((double) (bitmap.getWidth()) * ((double) (pix_max) / (double) (bitmap.getHeight())));
                resizedBitmap = getResizedBitmap(bitmap, W, H);
               // Log.i("Cris", "1. Maxim ample/alt: " + pix_max + " Sense comprimir:" + bitmap.getWidth() + "x" + bitmap.getHeight() + "| Comprimit:" + resizedBitmap.getWidth() + "x" + resizedBitmap.getHeight());

            }
            else {
                resizedBitmap=bitmap;
            }
        } else {
            if (bitmap.getWidth() > pix_max) {
                W = pix_max;
                H = (int) ((double) (bitmap.getHeight()) * ((double) (pix_max) / (double) (bitmap.getWidth())));
                resizedBitmap = getResizedBitmap(bitmap, W, H);
              //  Log.i("Cris", "2. Maxim ample/alt: " + pix_max + " Sense comprimir:" + bitmap.getWidth() + "x" + bitmap.getHeight() + "| Comprimit:" + resizedBitmap.getWidth() + "x" + resizedBitmap.getHeight());

            }
            else {
                resizedBitmap=bitmap;
            }
        }
        return resizedBitmap;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // Crea una matriu, mes facil de manipular
        Matrix matrix = new Matrix();
        // Canvia el tamany de la matriu
        matrix.postScale(scaleWidth, scaleHeight);
        // Log.i("cris", "parametre W:" + newWidth + " H:" + newHeight + " W2:" + width + " H2:" + height);
        // Emplena el bitmap amb la matriu amb el tamany nou
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        // bm.recycle();
        return resizedBitmap;
    }

}
