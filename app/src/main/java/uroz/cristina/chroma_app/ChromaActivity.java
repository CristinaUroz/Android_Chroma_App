package uroz.cristina.chroma_app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

public class ChromaActivity extends AppCompatActivity {
    // Declaracio de referencies a elements de la pantalla
    private Button btn_next, btn_prev, btn_hsl, btn_palete;
    private SeekBar barra_chroma;

    private ImageView fore_ima;

    // Variables globals
    public static String KEY_FORE_URI2 = "KEY_FORE_URI2";
    public static String KEY_BACK_URI2 = "KEY_BACK_URI2";
    public static String KEY_VALOR_BARRA_2 = "KEY_VALOR_BARRA_2";
    public static String KEY_COLOR_CHROMA_2 = "KEY_COLOR_CHROMA_2";
    private int valor_barra;
    private int color_chroma;
    private Uri fore_uri;
    private Uri back_uri;
    private Bitmap bitmap;

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

        // Recuperacio de dades de quan tornem d'una altra activitat
        fore_uri = Uri.parse(getIntent().getExtras().getString(KEY_FORE_URI2));
        back_uri = Uri.parse(getIntent().getExtras().getString(KEY_BACK_URI2));

        // Recuperacio de dades de quan girem la pantalla
        if (savedInstanceState != null) {
            Bundle b = savedInstanceState;
            if (b.getString("fore_uri") != null) {
                fore_uri = Uri.parse(b.getString("fore_uri"));
            }
            if (b.getString("back_uri") != null) {
                back_uri = Uri.parse(b.getString("back_uri"));
            }
            valor_barra = b.getInt("valor_barra");
            color_chroma = b.getInt("color_chroma");
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

        fore_ima.setImageURI(fore_uri);
        bitmap = BitmapFactory.decodeFile(getRealPathFromURI(getApplicationContext(), fore_uri));

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
        int[] pos = new int[2];
        //TODO: Si te pixels imperells que???
        //TODO: Arreglar posicio
        fore_ima.getLocationOnScreen(pos);

        int clickX = (int) arg1.getX();
        int clickY = (int) arg1.getY();

        /*
        Log.i("kike", "pos0 " + pos[0]);
        Log.i("kike", "pos1 " + pos[1]);
        Log.i("kike", "arg0 " + clickX);
        Log.i("kike", "arg1 " + clickY);
        Log.i("kike", "bit " + bitmap.getWidth());
        */

        if (pos[0] < clickX && clickX < (pos[0] + fore_ima.getWidth())) {
            if (pos[1] < clickY && clickY < (pos[1] + fore_ima.getHeight())) {
                Log.i("kike", "dins");
            }

        }
        // arg1
        // posicio de la pantalla on es fa click

        // pos
        // posicio de la cantonada superior esquerra de fore_ima a la pantalla

        // fore_ima.getWidth() i getHeight()
        // alt i ample de fore_ima

        // bitmap.getWidth() i getHeight()
        // alt i ample de la imatge

        /*
        int x;
        x = (int) arg1.getX() - pos[0] - (fore_ima.getWidth() - bitmap.getWidth()) / 2;

        int y;
        y = (int) arg1.getY() - pos[1] - (fore_ima.getHeight() - bitmap.getHeight()) / 2;


        // Mirar si esta fora de la imatge
        if (x > bitmap.getWidth()) {
            x = bitmap.getWidth() - 1;
        } else if (x < 0) {
            x = 0;
        }
        if (y > bitmap.getHeight()) {
            y = bitmap.getHeight() - 1;
        } else if (y < 0) {
            y = 0;
        }

        // Valor del pixel
        //color_chroma = bitmap.getPixel(x, y);
        fore_ima.setBackgroundColor(color_chroma);
        */
        return true;
    }

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
}
