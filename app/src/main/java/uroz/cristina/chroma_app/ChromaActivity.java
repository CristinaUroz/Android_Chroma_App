package uroz.cristina.chroma_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static android.R.color.transparent;

public class ChromaActivity extends AppCompatActivity {
    // Declaracio de referencies a elements de la pantalla
    private Button btn_next, btn_prev, btn_hsl, btn_palete;
    private SeekBar barra_chroma;
    private ImageView fore_ima;
    private ImageView color_view;
    private ImageView revert;
    private ImageView info_ima;

    // Variables globals
    public static String KEY_VALOR_BARRA_2 = "KEY_VALOR_BARRA_2";
    public static String KEY_COLOR_CHROMA_2 = "KEY_COLOR_CHROMA_2";
    private int valor_barra = 0;
    private int color_chroma = 0;
    private Bitmap bitmap_mutable; // Bitmap editable
    private int[] pos = new int[2]; // Posicio del ImageView
    private int[] xy = new int[2]; // Posicio del "click" en el bitmap
    private ColorPickerDialog colorPickerDialog;
    private int recuperat = 0;
    private File dir;

    // Guardem les dades quan girem la pantalla
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("valor_barra", valor_barra);
        outState.putInt("color_chroma", color_chroma);
        recuperat = 1;
        outState.putInt("recuperat", recuperat);
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
        revert = (ImageView) findViewById(R.id.revert);
        info_ima = (ImageView) findViewById(R.id.ima_info2);

        // Com a posicio de click inicial posem -1
        xy[0] = -1;
        xy[1] = -1;

        // Recuperacio de dades de quan girem la pantalla
        if (savedInstanceState != null) {
            valor_barra = savedInstanceState.getInt("valor_barra");
            color_chroma = savedInstanceState.getInt("color_chroma");
            recuperat = savedInstanceState.getInt("recuperat");
        }

        // Configuracio de la barra
        barra_chroma.setMax(100);
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().get(KEY_VALOR_BARRA_2) != null) {
                valor_barra = getIntent().getExtras().getInt(KEY_VALOR_BARRA_2);
            }
            barra_chroma.setProgress(valor_barra);

            if (getIntent().getExtras().get(KEY_COLOR_CHROMA_2) != null) {
                color_chroma = getIntent().getExtras().getInt(KEY_COLOR_CHROMA_2);
            }
        }

        // Creacio del bitmap, el bitmap mutable i display a l'imageview
        dir = new File(getCacheDir(), "Fore");
        iniciar();
        change_Color();

        revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valor_barra = 0;
                color_chroma = 0;
                iniciar();
            }
        });

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
                // Per fer que no es pugui tornar a premer mentre sesta executant
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                // Mostra el valor de la barra
                Toast.makeText(ChromaActivity.this, String.valueOf(valor_barra), Toast.LENGTH_SHORT).show();

                // Quan s'aixeca el click s'aplica l'efecte
                if (xy[0] != -1 && xy[1] != -1) {
                    iniciar();
                    change_Color();
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });

        // Boto prev
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChromaActivity.this, ChooseActivity.class);
                intent.putExtra(ChooseActivity.RETORNAR, true);
                startActivity(intent);
                finish();
            }
        });

        // Boto next
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File file_chroma = new File(getCacheDir(), "Chroma");
                    // = File.createTempFile("Fore", null, getCacheDir());
                    OutputStream outStream = new FileOutputStream(file_chroma);
                    bitmap_mutable.compress(Bitmap.CompressFormat.PNG, 85, outStream);
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // bitmap.recycle();
                bitmap_mutable.recycle();
                Intent intent = new Intent(ChromaActivity.this, EditActivity.class);
                try {
                    intent.putExtra(EditActivity.KEY_VALOR_BARRA_3, valor_barra);
                    intent.putExtra(EditActivity.KEY_COLOR_CHROMA_3, color_chroma);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(ChromaActivity.this, getString(R.string.ErrorSomething), Toast.LENGTH_LONG).show();
                }
            }
        });

        //Boto d'informacio
        info_ima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChromaActivity.this);
                builder.setTitle(R.string.chroma_title);
                builder.setMessage(R.string.info_2);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
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
                //Creem un nou quadre de dialeg amb 8 botons de diferents colors per escollir
                AlertDialog.Builder pBuilder = new AlertDialog.Builder(ChromaActivity.this);
                View pView = getLayoutInflater().inflate(R.layout.palette_dialog, null);
                Button btn_r = (Button) pView.findViewById(R.id.btn_r);
                Button btn_g = (Button) pView.findViewById(R.id.btn_g);
                Button btn_b = (Button) pView.findViewById(R.id.btn_b);
                Button btn_white = (Button) pView.findViewById(R.id.btn_white);
                Button btn_black = (Button) pView.findViewById(R.id.btn_black);

                pBuilder.setView(pView);
                AlertDialog dialog = pBuilder.create();
                dialog.show();

                btn_r.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Per fer que no es pugui tornar a premer mentre sesta executant
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        // Red
                        color_chroma = Color.parseColor("#ff0000");
                        iniciar();
                        change_Color();
                    }
                });

                btn_g.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        // Green
                        color_chroma = Color.parseColor("#00ff00");
                        iniciar();
                        change_Color();
                    }
                });

                btn_b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        // Blue
                        color_chroma = Color.parseColor("#0000ff");
                        iniciar();
                        change_Color();
                    }
                });

                btn_white.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        // White
                        color_chroma = Color.parseColor("#ffffff");
                        iniciar();
                        change_Color();
                    }
                });
                btn_black.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        // Black
                        color_chroma = Color.parseColor("#000000");
                        iniciar();
                        change_Color();
                    }
                });
            }
        });
    }

    // Quan es toca el ImageView
    public boolean onTouchEvent(MotionEvent arg1) {
        // Deshabilitem que es pugui tornar a tocar la pantalla fins acabar la funcio
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        /**
         arg1: posicio de la pantalla on es fa click
         pos: posicio de la cantonada superior esquerra de fore_ima a la pantalla
         fore_ima.getWidth() i getHeight(): alt i ample de fore_ima
         bitmap.getWidth() i getHeight(): alt i ample de la imatge
         bitmap_mutable=bitmap_mutable_aux;
         */

        int clickX = (int) arg1.getX();
        int clickY = (int) arg1.getY();

        // Mirem que el click estigui dins l'ImageView
        if (pos[0] < clickX && clickX < (pos[0] + fore_ima.getWidth())) {
            if (pos[1] < clickY && clickY < (pos[1] + fore_ima.getHeight())) {
                // Trobem la posicio equivalent al bitmap
                posicio(clickX, clickY);
                if (xy[0] >= 0 && xy[0] < bitmap_mutable.getWidth() && xy[1] >= 0 && xy[1] < bitmap_mutable.getHeight()) {
                    // Tornem a iniciar el bitmap per seleccionar un nou color
                    iniciar();
                    // Guardem el color del pixel clickat del bitmap per comparar-lo amb la resta
                    color_chroma = bitmap_mutable.getPixel(xy[0], xy[1]);
                    change_Color();
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        return true;
    }

    public void change_Color() {
        // Tornem a iniciar el bitmap per seleccionar un nou color i posem el color en el quadre per
        // informar quin es el color seleccionat
        color_view.setBackgroundColor(color_chroma);
        int mPhotoWidth = bitmap_mutable.getWidth();
        int mPhotoHeight = bitmap_mutable.getHeight();
        int[] pix = new int[mPhotoWidth * mPhotoHeight];
        int tol = valor_barra;
        int index, r, g, b, yy, R, G, B, Y;
        R = (color_chroma >> 16) & 0xff;
        G = (color_chroma >> 8) & 0xff;
        B = color_chroma & 0xff;
        Y = (30 * R + 59 * G + 11 * B) / 100;
        bitmap_mutable.getPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);
        for (int y = 0; y < mPhotoHeight; y++) {
            for (int x = 0; x < mPhotoWidth; x++) {
                index = y * mPhotoWidth + x;
                r = (pix[index] >> 16) & 0xff;
                g = (pix[index] >> 8) & 0xff;
                b = pix[index] & 0xff;
                yy = (30 * r + 59 * g + 11 * b) / 100;
                //Mirem si els valors del color es troben dins dels parametres de tolerancia
                if (Y + tol >= yy && Y - tol <= yy &&
                        R + tol >= r && R - tol <= r &&
                        B + tol >= b && B - tol <= b &&
                        G + tol >= g && G - tol <= g
                        ) {
                    pix[index] = getResources().getColor(transparent);
                }
            }
        }

        bitmap_mutable.setPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);
        // Fem visible el nou bitmap
        fore_ima.setImageBitmap(bitmap_mutable);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    // Per poder editar el bitmap - EXTRET DE INTERNET
    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            // Fitxer temporal de treball que conte els bits de la imatge (no es una imatge)
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            // Es crea un RandomAccessFile
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // Ample i alt del bitmap
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            // Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
            imgIn.copyPixelsToBuffer(map);
            imgIn.recycle();
            System.gc();

            // Es crea el bitmap que es podra editar i s'hi carrega l'anterior
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            // load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            // close the temporary file and channel , then delete that also
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

    // Passa la posicio de la pantalla a la posicio del bitmap
    public void posicio(int clickX, int clickY) {
        fore_ima.getLocationOnScreen(pos);
        if (bitmap_mutable.getHeight() * fore_ima.getWidth() / bitmap_mutable.getWidth() < fore_ima.getHeight()) {
            // L'imatge ocupa tot lample del imageView
            xy[0] = (int) ((clickX - pos[0]) * bitmap_mutable.getWidth() / fore_ima.getWidth());
            xy[1] = (int) ((clickY - pos[1]) * bitmap_mutable.getWidth() / fore_ima.getWidth() - ((fore_ima.getHeight() * bitmap_mutable.getWidth() / fore_ima.getWidth() - bitmap_mutable.getHeight()) / 2));
        } else {
            // L'imatge ocupa tota l'alçada del imageView
            xy[0] = (int) ((clickX - pos[0]) * bitmap_mutable.getHeight() / fore_ima.getHeight() - (fore_ima.getWidth() * bitmap_mutable.getHeight() / fore_ima.getHeight() - bitmap_mutable.getWidth()) / 2);
            xy[1] = (int) ((clickY - pos[1]) * bitmap_mutable.getHeight() / fore_ima.getHeight());
        }
    }

    public void iniciar() {
        //AQUEST CODI ES PER COMPRIMIR EL BITMAP EN CAS DE TENIR PROBLEMES DE MEMORIA
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1; //Ha de ser multiple de 2, si es 1 no fa res
        bitmap_mutable = convertToMutable(BitmapFactory.decodeFile(dir.getAbsolutePath(), options));
        fore_ima.setImageBitmap(bitmap_mutable);
    }

    public void colorPicker() {
        if (color_chroma == 0) {
            color_chroma = Color.parseColor("#00ff00");
        }
        colorPickerDialog = new ColorPickerDialog(this, color_chroma);
        colorPickerDialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                color_chroma = i;
                iniciar();
                change_Color();
            }
        });
        colorPickerDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChromaActivity.this, ChooseActivity.class);
        intent.putExtra(ChooseActivity.RETORNAR, true);
        startActivity(intent);
        finish();
    }
}
