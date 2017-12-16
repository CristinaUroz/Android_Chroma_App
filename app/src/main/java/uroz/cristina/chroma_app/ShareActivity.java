package uroz.cristina.chroma_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
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
    private String fileName;

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


        // Carpeta on es guarden les imatges finals
        dir = new File(Environment.getExternalStorageDirectory(), "/ChromAppPhotos/SavedPhotos/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Boto prev
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShareActivity.this, EditActivity.class);

                startActivity(intent);
                finish();
            }
        });

        // Boto share
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Boto save
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileName = dir + "/ChromApp_" + getPhotoName() + ".jpg";
                File photoFile = new File(fileName);
                try {
                    photoFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                // Es crea l'arxiu, pero no es guarden les dades
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

}
