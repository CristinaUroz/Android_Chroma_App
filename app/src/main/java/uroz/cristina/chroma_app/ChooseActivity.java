package uroz.cristina.chroma_app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import java.io.IOException;
import java.util.Calendar;

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
    public static String KEY_FORE_URI1 = "KEY_FORE_URI1";
    public static String KEY_BACK_URI1 = "KEY_BACK_URI1";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_activity);

        ////////////////////////////////////////// Permisos
        // Es demana el permis per a utilitzar la camera, i quan s'acepta, es demana el permis per escriure fitxers
        // Demana permisos de camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Log.i("kike", "demana camera 1");
            } else {
                Log.i("kike", "demana camera 2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }

        ////////////////////////////////////////////////////

        // Creació del directori on es guarden les fotos que es fan
        dir = new File(Environment.getExternalStorageDirectory(), "/ChromAppPhotos/data/");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Obtencio de referencies a elements de la pantalla
        fore_ima = (ImageView) findViewById(R.id.ima_fore);
        back_ima = (ImageView) findViewById(R.id.ima_back);
        btn_next = (Button) findViewById(R.id.next_button_choose);

        if (getIntent() != null && getIntent().getExtras() != null) {
            fore_uri = Uri.parse(getIntent().getExtras().getString(KEY_FORE_URI1));
            back_uri = Uri.parse(getIntent().getExtras().getString(KEY_BACK_URI1));
            back_ima.setImageURI(back_uri);
            fore_ima.setImageURI(fore_uri);
        }

        // Boto next
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Passar dades entre dues activitats
                Intent intent = new Intent(ChooseActivity.this, ChromaActivity.class);
                try {
                    String back = back_uri.toString();
                    String fore = fore_uri.toString();
                    intent.putExtra(ChromaActivity.KEY_BACK_URI2, back);
                    intent.putExtra(ChromaActivity.KEY_FORE_URI2, fore);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    //String msg = e.toString();
                    String msg = getString(R.string.missing_data);
                    Toast.makeText(ChooseActivity.this, msg, Toast.LENGTH_LONG).show();
                }
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

    private void chooseCameraGallery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose);
        builder.setMessage(R.string.choose_from);
        builder.setCancelable(true);

        // Agafa la imatge de la camera
        builder.setNegativeButton(R.string.camera, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fileName = dir + "/" + getPhotoName() + ".jpg";
                File photoFile = new File(fileName);
                try {
                    photoFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                if (codi_imatge == 0) {
                    back_uri = Uri.fromFile(photoFile);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, back_uri);
                    startActivityForResult(cameraIntent, 1);
                } else if (codi_imatge == 1) {
                    fore_uri = Uri.fromFile(photoFile);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fore_uri);
                    startActivityForResult(cameraIntent, 1);
                }
            }
        });

        // Agafa la imatge de la galeria
        builder.setPositiveButton(R.string.gallery, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 0);
            }
        });

        builder.create().show();
    }

    @NonNull
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

        return year + "." + month + "." + day + "_" + hour + "." + minute + "." + second;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        if (codi_imatge == 1) {
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK) {
                        fore_uri = imageReturnedIntent.getData();
                        if (fore_uri == null) {
                        } else {
                            fore_ima.setImageURI(fore_uri);
                        }
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK) {
                        Bitmap bMap = BitmapFactory.decodeFile(fileName);
                        fore_ima.setImageBitmap(bMap);
                    }
                    break;
            }
        } else if (codi_imatge == 0) {
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK) {
                        back_uri = imageReturnedIntent.getData();
                        if (back_uri == null) {
                        } else {
                            back_ima.setImageURI(back_uri);
                        }
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK) {
                        Bitmap bMap = BitmapFactory.decodeFile(fileName);
                        back_ima.setImageBitmap(bMap);
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("kike", "permis camera concedit");
                    // Demana permisos d'escriptura
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Log.i("kike", "demana escriure 1");
                        } else {
                            Log.i("kike", "demana escriure 2");
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
                    Log.i("kike", "permis escriure concedit");
                } else {
                    Log.i("kike", "permis escriure denegat");
                }
                return;
            }
            // Altres permisos, fer un 'case'
        }
    }
}
