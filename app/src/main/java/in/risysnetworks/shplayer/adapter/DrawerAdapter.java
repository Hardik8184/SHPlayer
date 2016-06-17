package in.risysnetworks.shplayer.adapter;


import android.app.Activity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.risysnetworks.shplayer.R;
import in.risysnetworks.shplayer.activities.SHPlayerMainActivity;
import in.risysnetworks.shplayer.beans.DrawerItem;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private ArrayList<DrawerItem> drawerItems;
    private Activity activity;
    private int[] images = {R.drawable.ic_allsongs, R.drawable.ic_most_play, R.drawable.ic_music_library,
            R.drawable.ic_setting, R.drawable.ic_equalizer, R.drawable.ic_content_send};

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public ViewHolder(View view) {
            super(view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DrawerAdapter(SHPlayerMainActivity activity, ArrayList<DrawerItem> drawerItems) {
        this.drawerItems = drawerItems;
        this.activity = activity;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawer, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final TextView title = (TextView) holder.itemView.findViewById(R.id.textViewDrawerItemTitle);
        AppCompatImageView icon = (AppCompatImageView) holder.itemView.findViewById(R.id.imageViewDrawerIcon);

        FrameLayout item_divider = (FrameLayout) holder.itemView.findViewById(R.id.item_divider);
        item_divider.setVisibility((position == 3) ? View.VISIBLE : View.GONE);

//        Drawable myVectorDrawable = ResourcesCompat.getDrawable(activity.getResources(), drawerItems.get(position).getIcon(), activity.getTheme());

        title.setText(drawerItems.get(position).getTitle());
        icon.setBackgroundResource(images[position]);
//        icon.setImageDrawable(drawerItems.get(position).getIcon());

//        icon.setVisibility((position < 3) ? View.VISIBLE : View.GONE);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return drawerItems.size();
    }
}


