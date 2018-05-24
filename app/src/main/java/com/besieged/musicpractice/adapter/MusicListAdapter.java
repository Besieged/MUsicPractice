package com.besieged.musicpractice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.besieged.musicpractice.R;
import com.besieged.musicpractice.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio
 * User: yuanxiaoru
 * Date: 2018/5/18.
 */

public class MusicListAdapter extends BaseAdapter {

    List<Song> songList = new ArrayList<Song>();
    private LayoutInflater layoutInflater;
    private Context mContext;

    MoreItemOnclickListener onclickListener;

    public MusicListAdapter(List<Song> songList, Context mContext,MoreItemOnclickListener onclickListener) {
        this.songList = songList;
        this.mContext = mContext;
        this.onclickListener = onclickListener;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.item_list_music, null);

            viewHolder = new ViewHolder();
            viewHolder.id = (TextView) convertView.findViewById(R.id.MusicID);
            viewHolder.title = (TextView) convertView.findViewById(R.id.Musictitle);
            viewHolder.subtitle = (TextView) convertView.findViewById(R.id.MusicArtist);
            viewHolder.MusicIcon = (ImageView) convertView.findViewById(R.id.MusicIcon);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.id.setText(String.valueOf(songList.get(position).getId()));
        viewHolder.title.setText(songList.get(position).getTitle());
        viewHolder.subtitle.setText(songList.get(position).getArtist());
        viewHolder.MusicIcon.setImageResource(R.drawable.ic_more_vert_gray_24dp);

        viewHolder.MusicIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickListener.onMoreItemclick(v,position);
            }
        });
        return convertView;
    }

    public interface MoreItemOnclickListener{
        void onMoreItemclick(View v,int position);
    }

    public class ViewHolder{
        TextView title;
        TextView subtitle;
        TextView id;
        ImageView MusicIcon;
    }
}
