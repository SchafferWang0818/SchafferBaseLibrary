package com.schaffer.base.test;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.schaffer.base.R;
import com.schaffer.base.common.base.BasePageAdapter;
import com.schaffer.base.common.transformer.ViewPagerTransformer;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        testViewPage();
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
            pager.setAdapter(new BasePageAdapter(this,null,views));
            pager.setPageTransformer(false,new ViewPagerTransformer());
        }
    }


}
