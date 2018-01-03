package uroz.cristina.chroma_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private Uri fore_uri;
    private Uri back_uri;
    private File dir;
    public static String KEY_FORE_URI4 = "KEY_FORE_URI4";
    public static String KEY_BACK_URI4 = "KEY_BACK_URI4";
    private String image_name = getPhotoName(); // Inclou '.jpg'
    private String image_dir = "/ChromAppPhotos/SavedPhotos/";

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

        // Variables


        /////
        //// Dades activitat anterior
        fore_uri = Uri.parse(getIntent().getExtras().getString(KEY_FORE_URI4));
        back_uri = Uri.parse(getIntent().getExtras().getString(KEY_BACK_URI4));

        ima_final.setImageURI(fore_uri);

        // Boto prev
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShareActivity.this, EditActivity.class);
                String back = back_uri.toString();
                String fore = fore_uri.toString();
                intent.putExtra(EditActivity.KEY_FORE_URI3, fore);
                intent.putExtra(EditActivity.KEY_BACK_URI3, back);
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
                }
                else {
                    Toast.makeText(ShareActivity.this, R.string.image_not_saved, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Boto restart
        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShareActivity.this, ChooseActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Boto finish
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean saveImage(String name) {
        boolean guardat_ok = true;
        BitmapDrawable draw = (BitmapDrawable) ima_final.getDrawable();
        Bitmap bitmap = draw.getBitmap();
        FileOutputStream outStream = null;
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

        return year + "." + month + "." + day + "_" + hour + "." + minute + "." + second + ".jpg";
    }

}
