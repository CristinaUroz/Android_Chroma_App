package uroz.cristina.chroma_app;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileDescriptor;

import static android.provider.MediaStore.Images.Thumbnails.getThumbnail;

public class ChromaActivity extends AppCompatActivity {
    // Declaracio de referencies a elements de la pantalla
    private Button btn_next, btn_prev, btn_hsl, btn_palete;
    private SeekBar barra_chroma;

    // Variables globals
    public static String KEY_FORE_URI2 = "KEY_FORE_URI2";
    public static String KEY_BACK_URI2 = "KEY_BACK_URI2";
    private int valor_barra;
    private ImageView fore_ima;
    private Uri fore_uri;
    private Uri back_uri;
    private Bitmap bitmap;

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


        // Proba passar dades entre dues activitats
        fore_uri = Uri.parse(getIntent().getExtras().getString(KEY_FORE_URI2));
        back_uri = Uri.parse(getIntent().getExtras().getString(KEY_BACK_URI2));

        fore_ima.setImageURI(fore_uri);

        // Configuracio de la barra
        barra_chroma.setMax(100);
        barra_chroma.setProgress(50);

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

                startActivity(intent);
                finish();
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

    private void mostraPaleta() {
        Palette.from(bitmap).maximumColorCount(15).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                // Get the "vibrant" color swatch based on the bitmap
                Palette.Swatch vibrant = palette.getMutedSwatch();
                if (vibrant != null) {
                    // Set the background color of a layout based on the vibrant color
                    fore_ima.setBackgroundColor(vibrant.getRgb());
                    // Update the title TextView with the proper text color
                    //titleView.setTextColor(vibrant.getTitleTextColor());
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
        builder.setTitle("paleta");

        builder.setItems(R.array.colors, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

    public boolean onTouchEvent(MotionEvent arg1) {
        int[] pos = new int[2];
        //TODO: Si te pixels imperells que???
        //TODO: Arreglar posicio
        fore_ima.getLocationOnScreen(pos);
        int x = (int) arg1.getX() - pos[0] - (fore_ima.getWidth()-bitmap.getWidth())/2;
        int y = (int)arg1.getY()- pos[1] - (fore_ima.getHeight()-bitmap.getHeight())/2;

        //Mirar si esta fora de la imatge
        if (x>bitmap.getWidth()) {x=bitmap.getWidth()-1;}
        else if (x<0) {x=0;}
        if (y>bitmap.getHeight()) {y=bitmap.getHeight()-1;}
        else if (y<0) {y=0;}

        //valor del pixem
        int pixel = bitmap.getPixel(x, y);
        fore_ima.setBackgroundColor(pixel);
        //Toast.makeText(this, Integer.toString(pixel), Toast.LENGTH_SHORT).show();
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

