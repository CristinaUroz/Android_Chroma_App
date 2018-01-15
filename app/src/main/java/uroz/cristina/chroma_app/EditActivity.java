package uroz.cristina.chroma_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
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

public class EditActivity extends AppCompatActivity implements View.OnTouchListener {


    //VARIABLES PEL MOVIMENT
    // these matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    // we can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    // remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;


    // Declaracio de referencies a elements de la pantalla
    private Button btn_prev, btn_next, btn_fore, btn_back, btn_contrast, btn_brillo, btn_temp, btn_rot, btn_satu, btn_opac;
    private SeekBar barra_edit;
    private ImageView ima_mixed;
    private ImageView ima_fons;
    private ImageView ima_restart;

    // Variables globals
    private Uri fore_uri;
    private Uri back_uri;
    public static String KEY_IMA_CHROMA = "KEY_IMA_CHROMA";
    public static String KEY_FORE_URI3 = "KEY_FORE_URI3";
    public static String KEY_BACK_URI3 = "KEY_BACK_URI3";
    public static String KEY_VALOR_BARRA_3 = "KEY_VALOR_BARRA_3";
    public static String KEY_COLOR_CHROMA_3 = "KEY_COLOR_CHROMA_3";
    public static String KEY_VALORS_FORE_3 = "KEY_VALORS_FORE_3";
    public static String KEY_VALORS_BACK_3 = "KEY_VALORS_BACK_3";
    private String ima_chroma;
    private int valor_barra;
    private int color_chroma;
    private int EDIT_IMAGE_CODE = 0;
    private int EDIT_VARIABLE_CODE = 0;
    private Bitmap bitmap;
    private Bitmap bitmap_mutable; //bitmap editable
    private Bitmap bitmap_b; // backgorund
    private Bitmap bitmap_mutable_b; //bitmap editable //background
    private Bitmap b_final;
    private static int pix_max=500;
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
        ima_restart = (ImageView) findViewById(R.id.restart);


        // Recuperacio de dades de quan tornem d'una altra activitat
        fore_uri = Uri.parse(getIntent().getExtras().getString(KEY_FORE_URI3));
        back_uri = Uri.parse(getIntent().getExtras().getString(KEY_BACK_URI3));
        valor_barra = getIntent().getExtras().getInt(KEY_VALOR_BARRA_3);
        color_chroma = getIntent().getExtras().getInt(KEY_COLOR_CHROMA_3);
        ima_chroma = getIntent().getExtras().getString(KEY_IMA_CHROMA);

        // Inicialitzacio del vector dels valors
        if (getIntent().getExtras().get(KEY_VALORS_FORE_3) == null) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 6; j++) {
                    valors_editables[i][j] = 50;
                }
            }
            valors_editables[1][5] = 0;
            valors_editables[0][5] = 0;
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
            int H=bitmap_b.getHeight();
            int W=bitmap_b.getWidth();
            if(bitmap_b.getHeight()>bitmap_b.getWidth()){
                if(bitmap_b.getHeight()>pix_max){
                    H=pix_max;
                    W= (int) ( (double)(bitmap_b.getWidth())*((double)(pix_max)/(double)(bitmap_b.getHeight())));
                    Bitmap resizedBitmap = getResizedBitmap(bitmap_b, W, H);
                    bitmap_mutable_b=convertToMutable(resizedBitmap);
                }
                else { bitmap_mutable_b=convertToMutable(bitmap_b);}
            }
            else {
                if(bitmap_b.getWidth()>pix_max){
                    W=pix_max;
                    H= (int) ((double)(bitmap_b.getHeight())*((double)(pix_max)/(double)(bitmap_b.getWidth())));
                    Bitmap resizedBitmap = getResizedBitmap(bitmap_b, W, H);
                    bitmap_mutable_b=convertToMutable(resizedBitmap);
                }
                else { bitmap_mutable_b=convertToMutable(bitmap_b);}
            }
            //change_Color_paleta();

            //Fem visible el nou bitmap
            bitmap_mutable=StringToBitMap(ima_chroma);
            ima_mixed.setImageBitmap(bitmap_mutable);

            //Habilitar moviment
            ima_mixed.setOnTouchListener(this);
            ima_fons.setOnTouchListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        changeButtonTextColor();
        Bitmap aux;
        aux=all_transformations(bitmap_mutable,0);
        ima_mixed.setImageBitmap(aux);
        aux=all_transformations(bitmap_mutable_b,1);
        ima_fons.setImageBitmap(aux);

        // Accions que s'executaran quan es mogui la barra
        barra_edit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                valors_editables[EDIT_IMAGE_CODE][EDIT_VARIABLE_CODE] = i;
                Bitmap aux;
                if (EDIT_IMAGE_CODE==0){
                aux = all_transformations(bitmap_mutable,EDIT_IMAGE_CODE);
                    ima_mixed.setImageBitmap(aux);}
                else {aux= all_transformations(bitmap_mutable_b,EDIT_IMAGE_CODE);
                    ima_fons.setImageBitmap(aux);}
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
                //try {
                    String ima_final = BitMapToString(b_final);
                    String back = back_uri.toString();
                    String fore = fore_uri.toString();
                    // Matriu de propietats
                    intent.putExtra(ShareActivity.KEY_IMA_CHROMA, ima_chroma);
                    intent.putExtra(ShareActivity.KEY_IMA_FINAL, ima_final);
                    intent.putExtra(ShareActivity.KEY_BACK_URI4, back);
                    intent.putExtra(ShareActivity.KEY_FORE_URI4, fore);
                    intent.putExtra(ShareActivity.KEY_VALOR_BARRA_4, valor_barra);
                    intent.putExtra(ShareActivity.KEY_COLOR_CHROMA_4, color_chroma);
                    intent.putExtra(ShareActivity.KEY_VALORS_FORE_4, valors_editables[0]);
                    intent.putExtra(ShareActivity.KEY_VALORS_BACK_4, valors_editables[1]);

                    startActivity(intent);
                    finish();
               /* } catch (Exception e) {
                    Toast.makeText(EditActivity.this, getString(R.string.missing_data), Toast.LENGTH_LONG).show();
                }*/
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
        ima_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 6; j++) {
                        valors_editables[i][j] = 50;
                    }
                }
                valors_editables[1][5] = 0;
                valors_editables[0][5] = 0;
                Bitmap aux;
                if (EDIT_IMAGE_CODE==0){
                    aux = all_transformations(bitmap_mutable,EDIT_IMAGE_CODE);
                    ima_mixed.setImageBitmap(aux);}
                else {aux= all_transformations(bitmap_mutable_b,EDIT_IMAGE_CODE);
                    ima_fons.setImageBitmap(aux);}
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
            //imgIn.recycle();
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

    private void guardar_imatge_final (    ) {
        int[] pos = new int[2];
        ima_mixed.getLocationOnScreen(pos);
        // create bitmap screen capture
        View v1 = getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        b_final = Bitmap.createBitmap(v1.getDrawingCache());
        b_final = Bitmap.createBitmap(b_final, pos[0], pos[1], ima_mixed.getWidth(), ima_mixed.getHeight());
/**
        try {
            int[] pos = new int[2];
            ima_mixed.getLocationOnScreen(pos);
            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            b_final = Bitmap.createBitmap(v1.getDrawingCache());
            b_final = Bitmap.createBitmap(b_final, pos[0], pos[1], ima_mixed.getWidth(), ima_mixed.getHeight());
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
 */
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

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public boolean onTouch(View v, MotionEvent event) {

// handle touch events here
        ImageView view;
        if (EDIT_IMAGE_CODE==0){
        view = (ImageView) v;}
        else {view = (ImageView) ima_fons;}
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                lastEvent = null;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }

                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;

            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_POINTER_UP:

                mode = NONE;
                lastEvent = null;
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {

                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    matrix.postTranslate(dx, dy);

                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = (newDist / oldDist);
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }

                    if (lastEvent != null && event.getPointerCount() == 3) {
                        newRot = rotation(event);
                        float r = newRot - d;
                        float[] values = new float[9];
                        matrix.getValues(values);
                        float tx = values[2];
                        float ty = values[5];
                        float sx = values[0];
                        float xc = (view.getWidth() / 2) * sx;
                        float yc = (view.getHeight() / 2) * sx;
                        matrix.postRotate(r, tx + xc, ty + yc);
                    }
                }
                break;
        }
        view.setImageMatrix(matrix);
        return true;
    }

    /**
     * Determine the space between the first two fingers
     */

    private float spacing(MotionEvent event) {

        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);

    }

    /**
     * Calculate the mid point of the first two fingers
     */

    private void midPoint(PointF point, MotionEvent event) {

        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */

    private float rotation(MotionEvent event) {

        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);

    }

    /**
    private Bitmap contrast (Bitmap bitmap, int EDIT_IMAGE_CODE){
        double val = (valors_editables[EDIT_IMAGE_CODE][0])/50 ;
        int mPhotoWidth = bitmap.getWidth();
        int mPhotoHeight = bitmap.getHeight();
        int[] pix = new int[mPhotoWidth * mPhotoHeight];
        bitmap.getPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);

        int r, g, b, index;
        int RY, BY, R, G, B, Y;

        for (int y = 0; y < mPhotoHeight; y++) {
            for (int x = 0; x < mPhotoWidth; x++) {
                index = y * mPhotoWidth + x;
                if(pix[index]!= getResources().getColor(transparent)){
                //Extreiem els components rgb
                r = (pix[index] >> 16) & 0xff;
                g = (pix[index] >> 8) & 0xff;
                b = pix[index] & 0xff;
                //Lluminancia Cr Cb?
                RY = (70 * r - 59 * g - 11 * b) / 100;
                BY = (-30 * r - 59 * g + 89 * b) / 100;
                Y = (30 * r + 59 * g + 11 * b) / 100;
                //Transformaciona
                double aux = (double) Y;
                aux=(aux-255/2)*val + 255/2;
                Y=(int)aux;
                //Obtenim RGB
                R = Y + RY;
                R = (R < 0) ? 0 : ((R > 255) ? 255 : R);
                G = Y + (-51 * RY - 19 * BY) / 100;
                G = (G < 0) ? 0 : ((G > 255) ? 255 : G);
                B = Y + BY;
                B = (B < 0) ? 0 : ((B > 255) ? 255 : B);
                pix[index] = 0xff000000 | (R << 16) | (G << 8) | B;}
            }
        }
        Bitmap bm = Bitmap.createBitmap(mPhotoWidth, mPhotoHeight, Bitmap.Config.ARGB_8888);
        bm.setPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);
        return bm;
    }
    private Bitmap bright(Bitmap bitmap, int EDIT_IMAGE_CODE){

        int val = (valors_editables[EDIT_IMAGE_CODE][1]-50)*2;
        int mPhotoWidth = bitmap.getWidth();
        int mPhotoHeight = bitmap.getHeight();
        int[] pix = new int[mPhotoWidth * mPhotoHeight];
        bitmap.getPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);
        int r, g, b, index;
        int RY, BY, R, G, B, Y;

        for (int y = 0; y < mPhotoHeight; y++) {
            for (int x = 0; x < mPhotoWidth; x++) {

                index = y * mPhotoWidth + x;
                if(pix[index] != getResources().getColor(transparent)){
                //Extreiem els components rgb
                r = (pix[index] >> 16) & 0xff;
                g = (pix[index] >> 8) & 0xff;
                b = pix[index] & 0xff;
                //Lluminancia Cr Cb?
                RY = (70 * r - 59 * g - 11 * b) / 100;
                BY = (-30 * r - 59 * g + 89 * b) / 100;
                Y = (30 * r + 59 * g + 11 * b) / 100;
                //Transformaciona
                Y=Y+val;
                //Obtenim RGB
                R = Y + RY;
                R = (R < 0) ? 0 : ((R > 255) ? 255 : R);
                G = Y + (-51 * RY - 19 * BY) / 100;
                G = (G < 0) ? 0 : ((G > 255) ? 255 : G);
                B = Y + BY;
                B = (B < 0) ? 0 : ((B > 255) ? 255 : B);
                pix[index] = 0xff000000 | (R << 16) | (G << 8) | B;}
                else {pix[index]=transparent;}
            }
        }

        Bitmap bm = Bitmap.createBitmap(mPhotoWidth, mPhotoHeight, Bitmap.Config.ARGB_8888);
        bm.setPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);
        return bm;
    }
    private Bitmap warmth (Bitmap bitmap, int EDIT_IMAGE_CODE){
        int val = (valors_editables[EDIT_IMAGE_CODE][2]-50)*5;
        int mPhotoWidth = bitmap.getWidth();
        int mPhotoHeight = bitmap.getHeight();
        int[] pix = new int[mPhotoWidth * mPhotoHeight];
        bitmap.getPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);
        int r, g, b, index;
        int  R, B;
        for (int y = 0; y < mPhotoHeight; y++) {
            for (int x = 0; x < mPhotoWidth; x++) {
                index = y * mPhotoWidth + x;
                if(pix[index]!= getResources().getColor(transparent)){
                //Extreiem els components rgb
                r = (pix[index] >> 16) & 0xff;
                g = (pix[index] >> 8) & 0xff;
                b = pix[index] & 0xff;

                //Transformem R i G
                R = r+val;
                R = (R < 0) ? 0 : ((R > 255) ? 255 : R);
                B = b-val;
                B = (B < 0) ? 0 : ((B > 255) ? 255 : B);

                pix[index] = 0xff000000 | (R << 16) | (g << 8) | B;}
            }
        }

        Bitmap bm = Bitmap.createBitmap(mPhotoWidth, mPhotoHeight, Bitmap.Config.ARGB_8888);
        bm.setPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);

        return bm;}
    private Bitmap rotation (Bitmap bitmap, int EDIT_IMAGE_CODE){

        int val = (int) ((valors_editables[EDIT_IMAGE_CODE][3]-50)*2*1.8);


        Matrix matrix = new Matrix();

        matrix.postRotate(val);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);

        return rotatedBitmap;
    }
    private Bitmap saturation (Bitmap bitmap, int EDIT_IMAGE_CODE){
        int val = (valors_editables[EDIT_IMAGE_CODE][4])/50;
        // get image size
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        float[] HSV = new float[3];
        // get pixel array from source
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int index = 0;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {

                // get current index in 2D-matrix
                index = y * width + x;
                // convert to HSV
                Color.colorToHSV(pixels[index], HSV);
                // increase Saturation level
                HSV[1] *= val;
                HSV[1] = (float) Math.max(0.0, Math.min(HSV[1], 1.0));
                // take color back
                pixels[index] |= Color.HSVToColor(HSV);
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }
    private Bitmap opacity (Bitmap bitmap, int EDIT_IMAGE_CODE ){
        int val= (int)-(valors_editables[EDIT_IMAGE_CODE][5]*2.55 - 255);
        Bitmap bitmap2 = makeTransparent(bitmap, val);
        return bitmap2;}
    private Bitmap all (Bitmap bm, int EDIT_IMAGE_CODE){
          Bitmap bitmap=bm;
          bitmap = contrast(bitmap, EDIT_IMAGE_CODE);
          bitmap = warmth(bitmap, EDIT_IMAGE_CODE);
          bitmap = saturation(bitmap, EDIT_IMAGE_CODE);
          bitmap = bright(bitmap, EDIT_IMAGE_CODE);
          bitmap = rotation(bitmap, EDIT_IMAGE_CODE);
        bitmap = opacity(bitmap, EDIT_IMAGE_CODE);
        return bitmap;
    }
*/

    private Bitmap all_transformations (Bitmap bmap, int EDIT_IMAGE_CODE){
        Bitmap bitmap=bmap;

        double val_contrast = (double)(valors_editables[EDIT_IMAGE_CODE][0])/50;
        int val_bright = (valors_editables[EDIT_IMAGE_CODE][1]-50)*2;
        int val_warmth = (valors_editables[EDIT_IMAGE_CODE][2]-50)*5;
        int val_rotation = (int) ((valors_editables[EDIT_IMAGE_CODE][3]-50)*2*1.8);
        double val_saturation = (double)(valors_editables[EDIT_IMAGE_CODE][4])/50;
        int val_opacity= (int)-((double)valors_editables[EDIT_IMAGE_CODE][5]*2.55 - 255);

        int mPhotoWidth = bitmap.getWidth();
        int mPhotoHeight = bitmap.getHeight();
        int[] pix = new int[mPhotoWidth * mPhotoHeight];
        bitmap.getPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);
        int trans=getResources().getColor(transparent);
        int r, g, b, index;
        int RY, BY, R, G, B, Y;
        for (int y = 0; y < mPhotoHeight; y++) {
            for (int x = 0; x < mPhotoWidth; x++) {
                index = y * mPhotoWidth + x;
                if(pix[index]!=trans){
                    //Extreiem els components rgb
                    r = (pix[index] >> 16) & 0xff;
                    g = (pix[index] >> 8) & 0xff;
                    b = pix[index] & 0xff;

                    //Temperatura i saturacio
                    RY = (int)((((70 * r - 59 * g - 11 * b) / 100)+ val_warmth)*val_saturation);
                    BY = (int)((((-30 * r - 59 * g + 89 * b) / 100)- val_warmth)*val_saturation);

                    Y = (30 * r + 59 * g + 11 * b) / 100;
                    //Transformacio contrast
                    double aux = (double) Y;
                    aux=(aux-255/2)*val_contrast + 255/2;
                    Y=(int)aux;

                    //Transformació brillo
                    Y=Y+val_bright;

                    //Obtenim RGB
                    R = Y + RY;
                    R = (R < 0) ? 0 : ((R > 255) ? 255 : R);
                    G = Y + (-51 * RY - 19 * BY) / 100;
                    G = (G < 0) ? 0 : ((G > 255) ? 255 : G);
                    B = Y + BY;
                    B = (B < 0) ? 0 : ((B > 255) ? 255 : B);

                    //RGB a exadecimal
                    pix[index] = 0xff000000 | (R << 16) | (G << 8) | B;
                }
                else {pix[index]=transparent;}

            }
        }
        Bitmap bm = Bitmap.createBitmap(mPhotoWidth, mPhotoHeight, Bitmap.Config.ARGB_8888);
        bm.setPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);



        Matrix matrix = new Matrix();
        matrix.postRotate(val_rotation);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm,bitmap.getWidth(),bitmap.getHeight(),true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);

        Bitmap bitmap_final = makeTransparent(rotatedBitmap, val_opacity);
        return bitmap_final;
    }


    public Bitmap makeTransparent(Bitmap src, int value) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap transBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(transBitmap);
        canvas.drawARGB(0, 0, 0, 0);
        // config paint
        final Paint paint = new Paint();
        paint.setAlpha(value);
        canvas.drawBitmap(src, 0, 0, paint);
        return transBitmap;
    }



    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        Log.i("cris","parametre W:" + newWidth + " H:" + newHeight + " W2:" + width + " H2:" + height);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        // bm.recycle();
        return resizedBitmap;
    }

}
