/*
 * This is the source code of SHPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @ RiSysNetworks 2016.
 */
package in.risysnetworks.shplayer.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import in.risysnetworks.shplayer.R;
import in.risysnetworks.shplayer.activities.SHPlayerMainActivity;
import in.risysnetworks.shplayer.beans.SongDetail;
import in.risysnetworks.shplayer.fastscroll.RecyclerViewFastScroller;
import in.risysnetworks.shplayer.mediacontroller.MediaController;
import in.risysnetworks.shplayer.phonemedia.PhoneMediaControl;
import in.risysnetworks.shplayer.phonemedia.PhoneMediaControl.PhoneMediaControlINterface;
import in.risysnetworks.shplayer.phonemedia.SHPlayerUtility;

public class FragmentAllSongs extends Fragment {

    //    private ListView recycler_songslist;

    private View rootView;
    private RecyclerViewFastScroller fastScroller;
    private RecyclerView recycler_songslist;
    private AllSongsListAdapter mAllSongsListAdapter;
    private ArrayList<SongDetail> songList = new ArrayList<SongDetail>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_allsongs, null);
        setupViews();
        loadAllSongs();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupViews() {

        recycler_songslist = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mAllSongsListAdapter = new AllSongsListAdapter(getActivity());
        recycler_songslist.setAdapter(mAllSongsListAdapter);

        fastScroller = (RecyclerViewFastScroller) rootView.findViewById(R.id.fastscroller);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            recycler_songslist.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
                @Override
                public void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
                    super.onLayoutChildren(recycler, state);
                    //TODO if the items are filtered, considered hiding the fast scroller here
                    final int firstVisibleItemPosition = findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition != 0) {
                        // this avoids trying to handle un-needed calls
                        if (firstVisibleItemPosition == -1)
                            //not initialized, or no items shown, so hide fast-scroller
                            fastScroller.setVisibility(View.GONE);
                        return;
                    }
                    final int lastVisibleItemPosition = findLastVisibleItemPosition();
                    int itemsShown = lastVisibleItemPosition - firstVisibleItemPosition + 1;
                    //if all items are shown, hide the fast-scroller
                    fastScroller.setVisibility(mAllSongsListAdapter.getItemCount() > itemsShown ? View.VISIBLE : View.GONE);
                }
            });

            fastScroller.setRecyclerView(recycler_songslist);
            fastScroller.setViewsToUse(R.layout.recycler_view_fast_scroller__fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);

        } else {

            fastScroller.setVisibility(View.GONE);
        }
    }

    private void loadAllSongs() {
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                songList = songsList_;
                mAllSongsListAdapter.notifyDataSetChanged();
            }
        });
        mPhoneMediaControl.loadMusicList(getActivity(), -1, PhoneMediaControl.SonLoadFor.All, "");
    }

    public class AllSongsListAdapter extends RecyclerView.Adapter<AllSongsListAdapter.ViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {
        private Context context = null;
        private LayoutInflater layoutInflater;
        private DisplayImageOptions options;
        private ImageLoader imageLoader = ImageLoader.getInstance();


        public AllSongsListAdapter(Context mContext) {
            this.context = mContext;
            this.layoutInflater = LayoutInflater.from(mContext);
            this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                    .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            //View.OnCreateContextMenuListener

            TextView textViewSongName;
            ImageView imageSongThm, imagemore;
            TextView textViewSongArtisNameAndDuration;
            LinearLayout song_row;

            private ViewHolder(View itemView) {
                super(itemView);

                song_row = (LinearLayout) itemView.findViewById(R.id.inflate_allsong_row);
                textViewSongName = (TextView) itemView.findViewById(R.id.inflate_allsong_textsongname);
                textViewSongArtisNameAndDuration = (TextView) itemView.findViewById(R.id.inflate_allsong_textsongArtisName_duration);
                imageSongThm = (ImageView) itemView.findViewById(R.id.inflate_allsong_imgSongThumb);
                imagemore = (ImageView) itemView.findViewById(R.id.img_moreicon);

            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_allsongs, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.imagemore.setVisibility(View.VISIBLE);

            final SongDetail mDetail = songList.get(position);

            String audioDuration = "";
            try {
                audioDuration = SHPlayerUtility.getAudioDuration(Long.parseLong(mDetail.getDuration()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            holder.textViewSongArtisNameAndDuration.setText((audioDuration.isEmpty() ? "" : audioDuration + " | ") + mDetail.getArtist());
            holder.textViewSongName.setText(mDetail.getTitle());
            String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
            imageLoader.displayImage(contentURI, holder.imageSongThm, options);

            holder.song_row.setTag(position);

            holder.song_row.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    SongDetail mDetail = songList.get((int) v.getTag());
                    ((SHPlayerMainActivity) getActivity()).loadSongsDetails(mDetail);

                    if (mDetail != null) {
                        if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                            MediaController.getInstance().pauseAudio(mDetail);
                        } else {
                            MediaController.getInstance().setPlaylist(songList, mDetail, PhoneMediaControl.SonLoadFor.All.ordinal(), -1);
                        }
                    }

                }
            });

            holder.imagemore.setTag(position);

            holder.imagemore.setColorFilter(Color.DKGRAY);
            if (Build.VERSION.SDK_INT > 15) {
                holder.imagemore.setImageAlpha(255);
            } else {
                holder.imagemore.setAlpha(255);
            }

            // Todo : Option Menu
            holder.imagemore.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    try {
                        PopupMenu popup = new PopupMenu(context, v);
                        popup.getMenuInflater().inflate(R.menu.list_item_option, popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.playnext:
                                        break;
                                    case R.id.addtoque:
                                        break;
                                    case R.id.addtoplaylist:
                                        break;
                                    case R.id.gotoartis:
                                        break;
                                    case R.id.gotoalbum:
                                        break;
                                    case R.id.delete:

//                                        File file = new File(songList.get((int) v.getTag()).getPath());
//                                        boolean delete = file.delete();
//
//                                        if (delete) {
//
//                                            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                                                    MediaStore.MediaColumns.DATA + "='" + songList.get((int) v.getTag()).getPath() + "'", null);
//
//                                            Toast.makeText(context, "Song delete..", Toast.LENGTH_SHORT).show();
//
//                                            songList.remove((int) v.getTag());
//
//                                            notifyDataSetChanged();
//
//                                        } else {
//                                            Toast.makeText(context, "Problem in deleting song!!", Toast.LENGTH_SHORT).show();
//                                        }

                                        break;
                                    default:
                                        break;
                                }

                                return true;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public String getTextToShowInBubble(final int position) {
            return String.valueOf(songList.get(position).getTitle().charAt(0));
        }

        @Override
        public int getItemCount() {
            return (songList != null) ? songList.size() : 0;
        }
    }
}