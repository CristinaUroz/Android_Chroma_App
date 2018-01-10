package uroz.cristina.chroma_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static android.R.color.transparent;
import static android.R.color.white;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.alpha;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

public class EditActivity extends AppCompatActivity {
    // Declaracio de referencies a elements de la pantalla
    private Button btn_prev, btn_next, btn_fore, btn_back, btn_contrast, btn_brillo, btn_temp, btn_rot, btn_satu, btn_opac;
    private SeekBar barra_edit;
    private ImageView ima_mixed;
    private ImageView ima_fons;

    // Variables globals
    private Uri fore_uri;
    private Uri back_uri;
    public static String KEY_FORE_URI3 = "KEY_FORE_URI3";
    public static String KEY_BACK_URI3 = "KEY_BACK_URI3";
    public static String KEY_VALOR_BARRA_3 = "KEY_VALOR_BARRA_3";
    public static String KEY_COLOR_CHROMA_3 = "KEY_COLOR_CHROMA_3";
    public static String KEY_VALORS_FORE_3 = "KEY_VALORS_FORE_3";
    public static String KEY_VALORS_BACK_3 = "KEY_VALORS_BACK_3";
    private int valor_barra;
    private int color_chroma;
    private int EDIT_IMAGE_CODE = 0;
    private int EDIT_VARIABLE_CODE = 0;
    private Bitmap bitmap;
    private Bitmap bitmap_mutable; //bitmap editable
    private Bitmap bitmap_b; // backgorund
    private Bitmap bitmap_mutable_b; //bitmap editable //background
    private Bitmap b_final;
    private int[] ids_effect = {R.id.contrast_button, R.id.brightness_button, R.id.temperature_button, R.id.rotation_button, R.id.saturation_button, R.id.opacity_button};

    // On es guarden els valors de la seekbar per cada efecte i imatge (fore i back)
    private int[][] valors_editables = new int[2][6];
    // Al acabar les probes, es pot borrar aixo d'aqui sota
    private String[] edit_image = {"Foreground", "Background"};
    private String[] edit_variable = {"Contrast", "Bright", "Warmth", "Rotation", "Saturation", "Opacity"};

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
        outState.putInt("valor_barra", valor_barra);
        outState.putInt("color_chroma", color_chroma);
        outState.putIntArray("fore_val", valors_editables[0]);
        outState.putIntArray("back_val", valors_editables[1]);
    }

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
        ima_fons = (ImageView) findViewById(R.id.ima_fons);

        // Recuperacio de dades de quan tornem d'una altra activitat
        fore_uri = Uri.parse(getIntent().getExtras().getString(KEY_FORE_URI3));
        back_uri = Uri.parse(getIntent().getExtras().getString(KEY_BACK_URI3));
        valor_barra = getIntent().getExtras().getInt(KEY_VALOR_BARRA_3);
        color_chroma = getIntent().getExtras().getInt(KEY_COLOR_CHROMA_3);

        // Inicialitzacio del vector dels valors
        if (getIntent().getExtras().get(KEY_VALORS_FORE_3) == null) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 6; j++) {
                    valors_editables[i][j] = 50;
                }
            }
        } else {
            valors_editables[0] = getIntent().getExtras().getIntArray(KEY_VALORS_FORE_3);
            valors_editables[1] = getIntent().getExtras().getIntArray(KEY_VALORS_BACK_3);
        }

        // Recuperacio de dades de quan girem la pantalla
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("fore_uri") != null) {
                fore_uri = Uri.parse(savedInstanceState.getString("fore_uri"));
            }
            if (savedInstanceState.getString("back_uri") != null) {
                back_uri = Uri.parse(savedInstanceState.getString("back_uri"));
            }
            valor_barra = savedInstanceState.getInt("valor_barra");
            color_chroma = savedInstanceState.getInt("color_chroma");
            valors_editables[0] = savedInstanceState.getIntArray("fore_val");
            valors_editables[1] = savedInstanceState.getIntArray("back_val");
        }

        // Configuracio de la barra
        barra_edit.setMax(100);
        barra_edit.setProgress(valors_editables[0][0]);

        //Colocar imatges
        ima_fons.setImageURI(back_uri);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),fore_uri);
            bitmap_mutable=convertToMutable(bitmap);
            bitmap_b = MediaStore.Images.Media.getBitmap(this.getContentResolver(),back_uri);
            bitmap_mutable_b=convertToMutable(bitmap_b);
            change_Color_paleta();
        } catch (IOException e) {
            e.printStackTrace();
        }
        changeButtonTextColor();

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
                intent.putExtra(ChromaActivity.KEY_VALOR_BARRA_2, valor_barra);
                intent.putExtra(ChromaActivity.KEY_COLOR_CHROMA_2, color_chroma);
                startActivity(intent);
                finish();
            }
        });

        // Boto next
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardar_imatge_final ();
                Intent intent = new Intent(EditActivity.this, ShareActivity.class);
                try {
                    String prova = BitMapToString(b_final);
                    String back = back_uri.toString();
                    String fore = fore_uri.toString();
                    // Matriu de propietats
                    intent.putExtra(ShareActivity.KEY_PROVA, prova);
                    intent.putExtra(ShareActivity.KEY_BACK_URI4, back);
                    intent.putExtra(ShareActivity.KEY_FORE_URI4, fore);
                    intent.putExtra(ShareActivity.KEY_VALOR_BARRA_4, valor_barra);
                    intent.putExtra(ShareActivity.KEY_COLOR_CHROMA_4, color_chroma);
                    intent.putExtra(ShareActivity.KEY_VALORS_FORE_4, valors_editables[0]);
                    intent.putExtra(ShareActivity.KEY_VALORS_BACK_4, valors_editables[1]);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(EditActivity.this, getString(R.string.missing_data), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Boto foreground
        btn_fore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_IMAGE_CODE = 0;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
                changeButtonTextColor();
            }
        });

        // Boto background
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_IMAGE_CODE = 1;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
                changeButtonTextColor();
            }
        });

        // Boto contrast
        btn_contrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_VARIABLE_CODE = 0;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
                changeButtonTextColor();
            }
        });

        // Boto brillo
        btn_brillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_VARIABLE_CODE = 1;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
                changeButtonTextColor();
            }
        });

        // Boto temperatura
        btn_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_VARIABLE_CODE = 2;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
                changeButtonTextColor();
            }
        });

        // Boto rotacio
        btn_rot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_VARIABLE_CODE = 3;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
                changeButtonTextColor();
            }
        });

        // Boto saturacio
        btn_satu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_VARIABLE_CODE = 4;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
                changeButtonTextColor();
            }
        });

        // Boto opacitat
        btn_opac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EDIT_VARIABLE_CODE = 5;
                barra_edit.setProgress(valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE]);
                changeButtonTextColor();
            }
        });
    }

    // Cambia el color del text del boto per saber que s'esta editant
    private void changeButtonTextColor() {
        for (int i = 0; i < 6; i++) {
            Button btn = (Button) findViewById(ids_effect[i]);
            if (EDIT_VARIABLE_CODE == i) {
                btn.setTextColor(Color.WHITE);
            } else {
                btn.setTextColor(Color.BLACK);
            }
        }

        if (EDIT_IMAGE_CODE == 0) {
            btn_fore.setTextColor(Color.WHITE);
            btn_back.setTextColor(Color.BLACK);
        } else {
            btn_fore.setTextColor(Color.BLACK);
            btn_back.setTextColor(Color.WHITE);
        }
    }

    private void edit_image() {
        String msg = edit_image[EDIT_IMAGE_CODE] + " " + edit_variable[EDIT_VARIABLE_CODE] + " " + valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE];
        Toast t = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        t.show();
    }

    public void change_Color_paleta() {
        for (int i = 0; i < bitmap_mutable.getWidth(); i++) {
            for (int j = 0; j < bitmap_mutable.getHeight(); j++) {
                int c = bitmap_mutable.getPixel(i, j);
                int tol = valor_barra;
                //Mirem si els valors del color es troben dins dels parametres de tolerancia
                if (alpha(color_chroma) + tol >= alpha(c) &&  alpha(color_chroma) - tol <=alpha(c) &&
                        red(color_chroma) + tol >= red(c) &&  red(color_chroma) - tol <=red(c) &&
                        blue(color_chroma) + tol >= blue(c) &&  blue(color_chroma) - tol <=blue(c) &&
                        green(color_chroma) + tol >= green(c) &&  green(color_chroma) - tol <=green(c)
                        ) {
                    bitmap_mutable.setPixel(i, j, getResources().getColor(transparent));}
            }

        }
        //Fem visible el nou bitmap
        ima_mixed.setImageBitmap(bitmap_mutable);
    }

    //Per poder editar el bitmap - EXTRET DE INTERNET
    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }

    private void guardar_imatge_final () {
        int w = ima_mixed.getWidth(), h = ima_mixed.getHeight();
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        b_final = Bitmap.createBitmap(w, h, conf);
        b_final=convertToMutable(b_final);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int c=color(i, j, bitmap_mutable);
               // int tol = valor_barra;
                if (c !=getResources().getColor(transparent)
                      /*  && !(alpha(color_chroma) + tol >= alpha(c) &&  alpha(color_chroma) - tol <=alpha(c) &&
                        red(color_chroma) + tol >= red(c) &&  red(color_chroma) - tol <=red(c) &&
                        blue(color_chroma) + tol >= blue(c) &&  blue(color_chroma) - tol <=blue(c) &&
                        green(color_chroma) + tol >= green(c) &&  green(color_chroma) - tol <=green(c))*/
                        ) {
                    b_final.setPixel(i, j, color(i, j, bitmap_mutable));
                } else {
                    if (color(i, j, bitmap_mutable_b) != getResources().getColor(transparent)) {
                        b_final.setPixel(i, j, color(i, j, bitmap_mutable_b));
                    } else {
                        b_final.setPixel(i, j, getResources().getColor(white));
                    }
                }
            }
        }
    }

    public int color(int X, int Y, Bitmap b) {
        int col=getResources().getColor(transparent);
        int x,y;
        if (bitmap.getHeight()*ima_mixed.getWidth()/b.getWidth()<ima_mixed.getHeight()) {
            //la imatge ocupa tot lample del imageView
            x = (int)( (X) * b.getWidth() / ima_mixed.getWidth());
            y= (int) ((Y) * b.getWidth() / ima_mixed.getWidth() - ((ima_mixed.getHeight() * b.getWidth() / ima_mixed.getWidth() - b.getHeight()) / 2));

        } else {
            //la imatge ocupa tota l'alçada del imageView
            x = (int) ((X) * b.getHeight() / ima_mixed.getHeight() - (ima_mixed.getWidth() * b.getHeight() / ima_mixed.getHeight() - b.getWidth()) / 2);
            y= (int) ((Y) * b.getHeight() /ima_mixed.getHeight());
        }
        try {
            col=b.getPixel(x,y);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return col;
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

}
