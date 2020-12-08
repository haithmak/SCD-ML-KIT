package coding.academy.scd_ml_kit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import coding.academy.scd_ml_kit.fragments.AboutUsFragment;
import coding.academy.scd_ml_kit.fragments.CameraFragment;
import coding.academy.scd_ml_kit.fragments.ManualInputFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {


    public ViewPagerAdapter(FragmentActivity fa) {
        super(fa);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new CameraFragment();
            case 1:
                return new ManualInputFragment();
            case 2:
                return new AboutUsFragment();
            default:
                return null;
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
