/*
 * This is the source code of SHPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @ RiSysNetworks 2016.
 */
package in.risysnetworks.shplayer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.risysnetworks.shplayer.R;

public class FragmentEqualizer extends Fragment {

    private View rootview;

    public FragmentEqualizer() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_equalizer, null);
        setupViews();
        return rootview;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupViews() {

    }
}
