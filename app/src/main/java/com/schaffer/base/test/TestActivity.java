package com.schaffer.base.test;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.schaffer.base.R;
import com.schaffer.base.common.base.BasePagerSingleViewAdapter;
import com.schaffer.base.common.transformer.ViewPagerTransformer;
import com.schaffer.base.db.PersonalSQLiteHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AndroidSchaffer
 */
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        testViewPage();
        setAnimation();
    }

    public void testViewPage() {
        ViewPager pager = (ViewPager) findViewById(R.id.test_vp_page);
        if (pager != null) {
            List<ImageView> views = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                ImageView view = new ImageView(this);
                view.setImageResource(R.mipmap.ic_launcher);
                views.add(view);
            }
            pager.setAdapter(new BasePagerSingleViewAdapter(this, null, views));
            pager.setPageTransformer(false, new ViewPagerTransformer());
        }
    }


    public void test() {

        PersonalSQLiteHelper helper = new PersonalSQLiteHelper(this, "db_name", 1);
        SQLiteDatabase db0 = helper.getReadableDatabase();
        List<Pair<String, String>> attachedDbs = db0.getAttachedDbs();
        String path = attachedDbs.get(0).first + attachedDbs.get(0).second;
        /*SQLiteDatabase db1 = SQLiteDatabase.openOrCreateDatabase(file, null);*/

        SQLiteDatabase db2 = this.openOrCreateDatabase("db_name", MODE_PRIVATE, null, null);
        db2.close();
        String path1 = db2.getPath();
        File file = new File(path1);
        if (file.exists() && file.isFile()) {
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setAnimation() {
        LottieAnimationView viewById = findViewById(R.id.test_lav_anim);
        viewById.setAnimation("data.json");
        viewById.loop(true);
        viewById.playAnimation();
    }
}
