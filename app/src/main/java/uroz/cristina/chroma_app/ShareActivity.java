package uroz.cristina.chroma_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ShareActivity extends AppCompatActivity {

    // Declaracio de referencies a elements de la pantalla
    private Button btn_share, btn_save, btn_restart, btn_finish, btn_prev;
    private ImageView ima_final;

    // Variables globals
    private File dir;
    public static String KEY_VALOR_BARRA_4 = "KEY_VALOR_BARRA_4";
    public static String KEY_COLOR_CHROMA_4 = "KEY_COLOR_CHROMA_4";
    public static String KEY_VALORS_FORE_4 = "KEY_VALORS_FORE_4";
    public static String KEY_VALORS_BACK_4 = "KEY_VALORS_BACK_4";
    private String image_name = getPhotoName(); // Inclou '.jpg'
    private String image_dir = "/ChromAppPhotos/SavedPhotos/";
    private int valor_barra;
    private int color_chroma;
    private int[] valors_fore;
    private int[] valors_back;

    // Guardem les dades quan girem la pantalla
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("valor_barra", valor_barra);
        outState.putInt("color_chroma", color_chroma);
        outState.putIntArray("fore_val", valors_fore);
        outState.putIntArray("back_val", valors_back);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);

        // Obtencio de referencies a elements de la pantalla
        btn_prev = (Button) findViewById(R.id.previous_button_share);
        btn_share = (Button) findViewById(R.id.share_button);
        btn_restart = (Button) findViewById(R.id.restart_button);
        btn_finish = (Button) findViewById(R.id.finish_button);
        btn_save = (Button) findViewById(R.id.save_button);
        ima_final = (ImageView) findViewById(R.id.ima_final);

        // Recuperacio de dades de quan tornem d'una altra activitat
        valor_barra = getIntent().getExtras().getInt(KEY_VALOR_BARRA_4);
        color_chroma = getIntent().getExtras().getInt(KEY_COLOR_CHROMA_4);
        valors_fore = getIntent().getExtras().getIntArray(KEY_VALORS_FORE_4);
        valors_back = getIntent().getExtras().getIntArray(KEY_VALORS_BACK_4);

        //Llegim la imatge del cache i la fem visible
        File dir = new File(getCacheDir(), "Final");
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap b = BitmapFactory.decodeFile(dir.getAbsolutePath(), options);
        ima_final.setImageBitmap(b);

        // Recuperacio de dades de quan girem la pantalla
        if (savedInstanceState != null) {
            valor_barra = savedInstanceState.getInt("valor_barra");
            color_chroma = savedInstanceState.getInt("color_chroma");
            valors_fore = savedInstanceState.getIntArray("fore_val");
            valors_back = savedInstanceState.getIntArray("back_val");
        }


        // Boto prev
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShareActivity.this, EditActivity.class);
                intent.putExtra(EditActivity.KEY_VALOR_BARRA_3, valor_barra);
                intent.putExtra(EditActivity.KEY_COLOR_CHROMA_3, color_chroma);
                intent.putExtra(EditActivity.KEY_VALORS_FORE_3, valors_fore);
                intent.putExtra(EditActivity.KEY_VALORS_BACK_3, valors_back);
                startActivity(intent);
                finish();
            }
        });

        // Boto share
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpeg");
                saveImage(image_name);
                Uri uriToImage = Uri.parse("file://" + Environment.getExternalStorageDirectory() + image_dir + image_name);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_to)));
            }
        });

        // Boto save
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveImage(image_name)) {
                    Toast.makeText(ShareActivity.this, R.string.image_saved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShareActivity.this, R.string.image_not_saved, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Boto restart
        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File dir = new File(getCacheDir(), "Fore");
                dir.delete();
                dir = new File(getCacheDir(), "Back");
                dir.delete();
                dir = new File(getCacheDir(), "Chroma");
                dir.delete();
                dir = new File(getCacheDir(), "Final");
                dir.delete();
                Intent intent = new Intent(ShareActivity.this, ChooseActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Boto finish
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File dir = new File(getCacheDir(), "Fore");
                dir.delete();
                dir = new File(getCacheDir(), "Back");
                dir.delete();
                dir = new File(getCacheDir(), "Chroma");
                dir.delete();
                dir = new File(getCacheDir(), "Final");
                dir.delete();
                finish();
            }
        });
    }

    // Guardar una foto en un arxiu JPG
    private boolean saveImage(String name) {
        boolean guardat_ok = true;
        BitmapDrawable draw = (BitmapDrawable) ima_final.getDrawable();
        Bitmap bitmap = draw.getBitmap();
        FileOutputStream outStream;
        dir = new File(Environment.getExternalStorageDirectory(), image_dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File outFile = new File(dir, name);
        try {
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            // Per actualitzar les fotos a la galeria
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(outFile));
            sendBroadcast(intent);
        } catch (FileNotFoundException e) {
            guardat_ok = false;
            e.printStackTrace();
        } catch (IOException e) {
            guardat_ok = false;
            e.printStackTrace();
        }
        return guardat_ok;
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ShareActivity.this, EditActivity.class);
        intent.putExtra(EditActivity.KEY_VALOR_BARRA_3, valor_barra);
        intent.putExtra(EditActivity.KEY_COLOR_CHROMA_3, color_chroma);
        intent.putExtra(EditActivity.KEY_VALORS_FORE_3, valors_fore);
        intent.putExtra(EditActivity.KEY_VALORS_BACK_3, valors_back);
        startActivity(intent);
        finish();
    }
}
