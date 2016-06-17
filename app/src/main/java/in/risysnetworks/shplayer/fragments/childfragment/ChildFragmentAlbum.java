/*
 * This is the source code of SHPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @ RiSysNetworks 2016.
 */
package in.risysnetworks.shplayer.fragments.childfragment;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import in.risysnetworks.shplayer.R;
import in.risysnetworks.shplayer.activities.AlbumAndArtisDetailsActivity;
import in.risysnetworks.shplayer.adapter.CursorRecyclerViewAdapter;
import in.risysnetworks.shplayer.phonemedia.MusicAlphabetIndexer;
import in.risysnetworks.shplayer.phonemedia.PhoneMediaControl;
import in.risysnetworks.shplayer.phonemedia.SHPlayerUtility;
import in.risysnetworks.shplayer.utils.LogWriter;

public class ChildFragmentAlbum extends Fragment {

    private View rootView;
    private static final String TAG = "ChildFragmentAlbum";
    private static Context context;
    private RecyclerView recyclerView;
    private AlbumRecyclerAdapter mAdapter;
    private Cursor mAlbumCursor;
    private SharedPreferences sharedPreferences;

    //    boolean mIsUnknownArtist,mIsUnknownAlbum;
    //    private String mArtistId;

    public static ChildFragmentAlbum newInstance(int position, Context mContext) {
        ChildFragmentAlbum f = new ChildFragmentAlbum();
        context = mContext;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_child_album, null);
        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        setupView();
        return rootView;
    }

    private void setupView() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        populateData();
    }

    public void resetView() {
        recyclerView.scrollToPosition(0);
    }

    private void populateData() {
        mAdapter = (AlbumRecyclerAdapter) getActivity().getLastNonConfigurationInstance();
        if (mAdapter == null) {
            mAdapter = new AlbumRecyclerAdapter(getActivity(), null);
            recyclerView.setAdapter(mAdapter);
            getAlbumCursor(mAdapter.getQueryHandler(), null);
        } else {
            recyclerView.setAdapter(mAdapter);
            mAlbumCursor = mAdapter.getCursor();
            if (mAlbumCursor != null) {
                init(mAlbumCursor);
            } else {
                getAlbumCursor(mAdapter.getQueryHandler(), null);
            }
        }
    }

    public void init(Cursor c) {
        if (mAdapter == null) {
            return;
        }
        mAdapter.changeCursor(c); // also sets mAlbumCursor
        if (mAlbumCursor == null) {
            SHPlayerUtility.displayDatabaseError(getActivity());
            mReScanHandler.sendEmptyMessageDelayed(0, 1000);
            return;
        }
    }

    private Handler mReScanHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mAdapter != null) {
                getAlbumCursor(mAdapter.getQueryHandler(), null);
            }
        }
    };

    private Cursor getAlbumCursor(AsyncQueryHandler async, String filter) {
        String[] cols = new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ALBUM_ART};

        Cursor ret = null;
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        if (!TextUtils.isEmpty(filter)) {
            uri = uri.buildUpon().appendQueryParameter("filter", Uri.encode(filter)).build();
        }
        if (async != null) {

            System.out.println(" async != null ");

            async.startQuery(0, null, uri, cols, null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        } else {

            System.out.println(" async == null ");

            ret = SHPlayerUtility.query(getActivity(), uri, cols, null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        }
        return ret;
    }

    public class AlbumRecyclerAdapter extends CursorRecyclerViewAdapter<AlbumRecyclerAdapter.ViewHolder> {

        private int mAlbumIdx;
        private int mArtistIdx;
        private final Resources mResources;
        private final String mUnknownAlbum;
        private final String mUnknownArtist;
        private AlphabetIndexer mIndexer;
        private AsyncQueryHandler mQueryHandler;
        private String mConstraint = null;
        private boolean mConstraintIsValid = false;

        private DisplayImageOptions options;
        private ImageLoader imageLoader = ImageLoader.getInstance();

        protected AlbumRecyclerAdapter(Context context, Cursor cursor) {
            super(context, cursor);
            this.mQueryHandler = new QueryHandler(context.getContentResolver());
            this.mUnknownAlbum = context.getString(R.string.unknown_album_name);
            this.mUnknownArtist = context.getString(R.string.unknown_artist_name);
            this.mResources = context.getResources();

            this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                    .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
            getColumnIndices(cursor);
        }

        private class QueryHandler extends AsyncQueryHandler {
            QueryHandler(ContentResolver res) {
                super(res);
            }

            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                init(cursor);
            }
        }

        public AsyncQueryHandler getQueryHandler() {
            return mQueryHandler;
        }

        @Override
        public void changeCursor(Cursor cursor) {
            if (getActivity().isFinishing() && cursor != null) {
                cursor.close();
                cursor = null;
            }
            if (cursor != mAlbumCursor) {
                mAlbumCursor = cursor;
                getColumnIndices(cursor);
                super.changeCursor(cursor);
            }
        }

        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            String s = constraint.toString();
            if (mConstraintIsValid && ((s == null && mConstraint == null) || (s != null && s.equals(mConstraint)))) {
                return getCursor();
            }
            Cursor c = getAlbumCursor(null, s);
            mConstraint = s;
            mConstraintIsValid = true;
            return c;
        }

        private void getColumnIndices(Cursor cursor) {

            if (cursor != null) {
                mAlbumIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
                mArtistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);
                if (mIndexer != null) {
                    mIndexer.setCursor(cursor);
                } else {
                    mIndexer = new MusicAlphabetIndexer(cursor, mAlbumIdx, mResources.getString(R.string.fast_scroll_alphabet));
                }
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

            String displayName = cursor.getString(mAlbumIdx);
            String artistName = cursor.getString(mArtistIdx);

            boolean unknown = displayName == null || displayName.equals(MediaStore.UNKNOWN_STRING);
            viewHolder.albumName.setText(unknown ? mUnknownAlbum : displayName);
            viewHolder.albumName.setTag(mAlbumIdx);

            unknown = (artistName == null || artistName.equals(MediaStore.UNKNOWN_STRING));
            viewHolder.artistName.setText(unknown ? mUnknownArtist : artistName);
            viewHolder.artistName.setTag(mArtistIdx);

            String contentURI = "content://media/external/audio/albumart/" + cursor.getLong(0);
            imageLoader.displayImage(contentURI, viewHolder.icon, options);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewgroup, int position) {
            return new ViewHolder(LayoutInflater.from(viewgroup.getContext()).inflate(R.layout.item_album_grid, viewgroup, false));
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView albumName;
            TextView artistName;
            ImageView icon;

            public ViewHolder(View itemView) {
                super(itemView);
                albumName = (TextView) itemView.findViewById(R.id.line1);
                artistName = (TextView) itemView.findViewById(R.id.line2);
                icon = (ImageView) itemView.findViewById(R.id.icon);
                icon.setScaleType(ScaleType.CENTER_CROP);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                try {
                    long albumID = getAlbumID(getPosition());

                    sharedPreferences.edit().putString("ChildFragment", "1").apply();

                    Intent mIntent = new Intent(context, AlbumAndArtisDetailsActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putLong("id", albumID);
                    mBundle.putLong("tagfor", PhoneMediaControl.SonLoadFor.Album.ordinal());
                    mBundle.putString("albumname", ((TextView) view.findViewById(R.id.line1)).getText().toString().trim());
                    mBundle.putString("title_one", ((TextView) view.findViewById(R.id.line2)).getText().toString().trim());
                    mBundle.putString("title_sec", "");
                    mIntent.putExtras(mBundle);
                    ((Activity) context).startActivity(mIntent);
                    ((Activity) context).overridePendingTransition(0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogWriter.info(TAG, e.toString());
                }
            }
        }

        private long getAlbumID(int position) {
            return getItemId(position);
        }

    }
}
