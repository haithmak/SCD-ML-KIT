package coding.academy.scd_ml_kit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ViewPager2 viewPager2 = findViewById(R.id.pager);

        viewPager2.setAdapter(new ViewPagerAdapter(this));

        TabLayout tabLayout = findViewById(R.id.tab_layout);


        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText("الكامير");
                                tab.setIcon(R.drawable.ic_baseline_camera_alt_24);
                                break;

                            case 1:
                                tab.setText("ادخال يدوي");
                                tab.setIcon(R.drawable.ic_baseline_keyboard_24);
                                break;
                            case 2:
                                tab.setText("حول");
                                tab.setIcon(R.drawable.ic_baseline_info_24);
                                break;

                        }
                    }
                });

        tabLayoutMediator.attach();


    }





}












