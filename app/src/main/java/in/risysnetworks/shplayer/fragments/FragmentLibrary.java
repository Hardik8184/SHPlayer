/*
 * This is the source code of SHPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @ RiSysNetworks 2016.
 */
package in.risysnetworks.shplayer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.risysnetworks.shplayer.R;
import in.risysnetworks.shplayer.fragments.childfragment.ChildFragmentFevorite;
import in.risysnetworks.shplayer.slidungtablayout.SlidingTabLayout;
import in.risysnetworks.shplayer.fragments.childfragment.ChildFragmentAlbum;
import in.risysnetworks.shplayer.fragments.childfragment.ChildFragmentArtists;
import in.risysnetworks.shplayer.fragments.childfragment.ChildFragmentGenres;
import in.risysnetworks.shplayer.fragments.childfragment.ChildFragmentMostPlay;

public class FragmentLibrary extends Fragment {

    private View rootView;

    private final String[] TITLES = {"ALBUMS", "ARTISTS", "GENRES", "FAVORITE"};
    private TypedValue typedValueToolbarHeight = new TypedValue();
    private ChildFragmentGenres childFragmentGenres;
    private ChildFragmentArtists childFragmentArtists;
    private ChildFragmentAlbum childFragmentAlbum;
    private ChildFragmentMostPlay childFragmentMostplay;
    private ChildFragmentFevorite childFragmentFevorite;

    private MyPagerAdapter pagerAdapter;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    private int tabsPaddingTop;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_library, null);
        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        setupView();
        return rootView;
    }

    private void setupView() {
        pager = (ViewPager) rootView.findViewById(R.id.pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getFragmentManager());
        pager.setAdapter(pagerAdapter);
        tabs = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(false);
        // Tab indicator color
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.md_white_1000);
            }
        });
        tabs.setViewPager(pager);

        if (sharedPreferences.getString("ChildFragment", "").equals("1")) {

            sharedPreferences.edit().putString("ChildFragment", "1").apply();
            pager.setCurrentItem(0);

        } else if (sharedPreferences.getString("ChildFragment", "").equals("2")) {

            sharedPreferences.edit().putString("ChildFragment", "1").apply();
            pager.setCurrentItem(1);

        } else if (sharedPreferences.getString("ChildFragment", "").equals("3")) {

            sharedPreferences.edit().putString("ChildFragment", "1").apply();
            pager.setCurrentItem(2);
        }
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    childFragmentAlbum = ChildFragmentAlbum.newInstance(position, getActivity());
                    return childFragmentAlbum;
                case 1:
                    childFragmentArtists = ChildFragmentArtists.newInstance(position, getActivity());
                    return childFragmentArtists;
                case 2:
                    childFragmentGenres = ChildFragmentGenres.newInstance(position, getActivity());
                    return childFragmentGenres;
                case 3:
                    childFragmentFevorite = ChildFragmentFevorite.newInstance(position, getActivity());
                    return childFragmentFevorite;
            }
            return null;
        }
    }

    public int convertToPx(int dp) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }
}
