package se.greatbrain.sats.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.greatbrain.sats.R;
import se.greatbrain.sats.tabs.SlidingTabLayout;

public class TopViewPagerFragment extends Fragment
{
    private GraphFragment graphFragment = new GraphFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.top_fragment_view_pager, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager_container);
        viewPager.setAdapter(new PagerAdapter(getFragmentManager()));

        SlidingTabLayout tabs = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);

        tabs.setDistributeEvenly(true);
        tabs.setViewPager(viewPager);

        return view;
    }

    public void onPageClicked(int page)
    {
        graphFragment.mPager.setCurrentItem(page - (GraphFragment.NUM_SIMULTANEOUS_PAGES / 2),
                true);
    }

    private final class PagerAdapter extends FragmentPagerAdapter
    {
        public PagerAdapter (FragmentManager fm)
        {
            super( fm );
        }

        @Override
        public android.support.v4.app.Fragment getItem (int position)
        {
            if (position == 0)
            {
                return graphFragment;
            }
            else
            {
                return new GraphFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            if (position == 0)
            {
                return "Kalender";
            }
            else
            {
                return "Statistik";
            }
        }

        @Override
        public int getCount ()
        {
            return 2;
        }

    }
}
