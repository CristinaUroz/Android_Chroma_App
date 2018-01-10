package uroz.cristina.chroma_app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static android.graphics.Color.alpha;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

public class ChromaActivity extends AppCompatActivity {
    // Declaracio de referencies a elements de la pantalla
    private Button btn_next, btn_prev, btn_hsl, btn_palete;
    private SeekBar barra_chroma;

    private ImageView fore_ima;
    private ImageView color_view;

    // Variables globals
    public static String KEY_FORE_URI2 = "KEY_FORE_URI2";
    public static String KEY_BACK_URI2 = "KEY_BACK_URI2";
    public static String KEY_VALOR_BARRA_2 = "KEY_VALOR_BARRA_2";
    public static String KEY_COLOR_CHROMA_2 = "KEY_COLOR_CHROMA_2";
    private int valor_barra;
    private int color_chroma=Color.parseColor("#ffffff");
    private Uri fore_uri;
    private Uri back_uri;
    private Bitmap bitmap;
    private Bitmap bitmap_mutable;
    private int[] pos = new int[2];
    private int[] xy = new int[2];

    private ColorPickerDialog colorPickerDialog;

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chroma_activity);

        // Obtencio de referencies a elements de la pantalla
        btn_next = (Button) findViewById(R.id.next_button_chroma);
        btn_prev = (Button) findViewById(R.id.previous_button_chroma);
        btn_hsl = (Button) findViewById(R.id.hsl_button);
        btn_palete = (Button) findViewById(R.id.palette_button);
        barra_chroma = (SeekBar) findViewById(R.id.tolerance_bar);
        fore_ima = (ImageView) findViewById(R.id.ima_fore2);
        color_view = (ImageView) findViewById(R.id.color_view);

        // Recuperacio de dades de quan tornem d'una altra activitat
        fore_uri = Uri.parse(getIntent().getExtras().getString(KEY_FORE_URI2));
        back_uri = Uri.parse(getIntent().getExtras().getString(KEY_BACK_URI2));

        //com a posicio de click inicial posem -1
        xy[0]=-1;
        xy[1]=-1;

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
        }

        // Configuracio de la barra
        barra_chroma.setMax(100);

        if (getIntent().getExtras().get(KEY_VALOR_BARRA_2) != null) {
            valor_barra = getIntent().getExtras().getInt(KEY_VALOR_BARRA_2);
        } else {
            valor_barra = 0;
        }
        barra_chroma.setProgress(valor_barra);

        if (getIntent().getExtras().get(KEY_COLOR_CHROMA_2) != null) {
            color_chroma = getIntent().getExtras().getInt(KEY_COLOR_CHROMA_2);
        } else {
            color_chroma = 0;
        }

        //fore_ima.setImageURI(fore_uri);
        bitmap = BitmapFactory.decodeFile(getRealPathFromURI(getApplicationContext(), fore_uri));

        //Creem el bitmap modificable i el posem al imageview
        bitmap_mutable=convertToMutable(bitmap);
        fore_ima.setImageBitmap(bitmap_mutable);


        // Accions que s'executaran quan es mogui la barra
        barra_chroma.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                valor_barra = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(ChromaActivity.this, String.valueOf(valor_barra), Toast.LENGTH_SHORT).show();
                if (xy[0]!=-1&&xy[1]!=-1){
                    change_Color_paleta();
                }
            }
        });

        // Boto prev
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChromaActivity.this, ChooseActivity.class);
                String back = back_uri.toString();
                String fore = fore_uri.toString();
                intent.putExtra(ChooseActivity.KEY_FORE_URI1, fore);
                intent.putExtra(ChooseActivity.KEY_BACK_URI1, back);
                startActivity(intent);
                finish();
            }
        });

        // Boto next
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChromaActivity.this, EditActivity.class);
                try {
                    String back = back_uri.toString();
                    String fore = fore_uri.toString();
                    intent.putExtra(EditActivity.KEY_BACK_URI3, back);
                    intent.putExtra(EditActivity.KEY_FORE_URI3, fore);
                    intent.putExtra(EditActivity.KEY_VALOR_BARRA_3, valor_barra);
                    intent.putExtra(EditActivity.KEY_COLOR_CHROMA_3, color_chroma);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(ChromaActivity.this, getString(R.string.missing_data), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Boto hsl
        btn_hsl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorPicker();
            }
        });

        // Boto palete
        btn_palete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mostraPaleta();
            }
        });
    }

    // Mostra el color principal de la imatge utilitzant Palette
    private void mostraPaleta() {


        /*
        Palette.from(bitmap).maximumColorCount(15).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                // Vibrant??? mirar quin es millors
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                if (vibrant != null) {
                    fore_ima.setBackgroundColor(vibrant.getRgb());
                }
            }
        });
*/
        /* FALTA IMPLEMENTAR
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.palette);
        builder.setItems(R.array.colors, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
        */
    }

    // Quan toquem la el ImageView
    public boolean onTouchEvent(MotionEvent arg1) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, //Per fer que no es pugui tornar a premer mentre sesta executant
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        // arg1: posicio de la pantalla on es fa click
        // pos: posicio de la cantonada superior esquerra de fore_ima a la pantalla
        // fore_ima.getWidth() i getHeight(): alt i ample de fore_ima
        // bitmap.getWidth() i getHeight(): alt i ample de la imatge
        //bitmap_mutable=bitmap_mutable_aux;
        int clickX = (int) arg1.getX();
        int clickY = (int) arg1.getY();
        if (pos[0] < clickX && clickX < (pos[0] + fore_ima.getWidth())) {
            if (pos[1] < clickY && clickY < (pos[1] + fore_ima.getHeight())) {
                posicio(clickX, clickY);
                   if (xy[0]>=0&&xy[0]<bitmap.getWidth()&&xy[1]>=0&&xy[1]<bitmap.getHeight()){
                   change_Color();
                }
            }
        }
        else{
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        return true;
    }

    //Poder trobar ladreça de la imatge per tal de convertirla en un bitmap
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //Posar els pixels del mateix color a transparent
    public void change_Color() {
        reiniciar();
        color_chroma= bitmap_mutable.getPixel(xy[0], xy[1]);
        if (color_chroma!=android.R.color.transparent) {
            color_view.setBackgroundColor(color_chroma);
            for (int i = 0; i < bitmap_mutable.getWidth(); i++) {
                for (int j = 0; j < bitmap_mutable.getHeight(); j++) {
                    if (!(i == xy[0] && j == xy[1])) {
                        int c = bitmap_mutable.getPixel(i, j);
                        int tol = valor_barra;
                        //Mirem si els valors del color es troben dins dels parametres de tolerancia
                        if (alpha(color_chroma) + tol >= alpha(c) &&  alpha(color_chroma) - tol <=alpha(c) &&
                                red(color_chroma) + tol >= red(c) &&  red(color_chroma) - tol <=red(c) &&
                                blue(color_chroma) + tol >= blue(c) &&  blue(color_chroma) - tol <=blue(c) &&
                                green(color_chroma) + tol >= green(c) &&  green(color_chroma) - tol <=green(c)
                                ){
                            bitmap_mutable.setPixel(i, j, android.R.color.transparent);
                        }
                    }
                }
            }
        }
        bitmap_mutable.setPixel(xy[0], xy[1], android.R.color.transparent);
        fore_ima.setImageBitmap(bitmap_mutable);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    public void change_Color_paleta() {
        reiniciar();
        if (color_chroma!=android.R.color.transparent) {
            color_view.setBackgroundColor(color_chroma);
            for (int i = 0; i < bitmap_mutable.getWidth(); i++) {
                for (int j = 0; j < bitmap_mutable.getHeight(); j++) {
                    int c = bitmap_mutable.getPixel(i, j);
                    int tol = valor_barra;
                        //Mirem si els valors del color es troben dins dels parametres de tolerancia
                    if (alpha(color_chroma) + tol >= alpha(c) &&  alpha(color_chroma) - tol <=alpha(c) &&
                            red(color_chroma) + tol >= red(c) &&  red(color_chroma) - tol <=red(c) &&
                            blue(color_chroma) + tol >= blue(c) &&  blue(color_chroma) - tol <=blue(c) &&
                            green(color_chroma) + tol >= green(c) &&  green(color_chroma) - tol <=green(c)
                            ){
                        bitmap_mutable.setPixel(i, j, android.R.color.transparent);
                    }
                }
            }
        }
        fore_ima.setImageBitmap(bitmap_mutable);
    }

    //Per poder editar el bitmap
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

    //Passa la posicio de la pantalla a la posicio del bitmap
    public void posicio(int clickX, int clickY) {
        fore_ima.getLocationOnScreen(pos);
        if (bitmap.getHeight()*fore_ima.getWidth()/bitmap.getWidth()<fore_ima.getHeight()) {//la imatge ocupa tot lample del imageView
            xy[0] = (int)( (clickX - pos[0]) * bitmap.getWidth() / fore_ima.getWidth());
            xy[1]= (int) ((clickY - pos[1]) * bitmap.getWidth() / fore_ima.getWidth() - ((fore_ima.getHeight() * bitmap.getWidth() / fore_ima.getWidth() - bitmap.getHeight()) / 2));
        } else {//la imatge ocupa tota l'alçada del imageView
            xy[0] = (int) ((clickX - pos[0]) * bitmap.getHeight() / fore_ima.getHeight() - (fore_ima.getWidth() * bitmap.getHeight() / fore_ima.getHeight() - bitmap.getWidth()) / 2);
            xy[1]= (int) ((clickY - pos[1]) * bitmap.getHeight() / fore_ima.getHeight());
        }
    }

    public void reiniciar(){
        bitmap = BitmapFactory.decodeFile(getRealPathFromURI(getApplicationContext(), fore_uri));
        bitmap_mutable=convertToMutable(bitmap);
        fore_ima.setImageBitmap(bitmap_mutable);
    }

    public void colorPicker(){
        colorPickerDialog=new ColorPickerDialog(this, color_chroma);
        colorPickerDialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                color_chroma =  i;
                change_Color_paleta();
            }
        });
        colorPickerDialog.show();
    }

}


