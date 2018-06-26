package com.besieged.musicpractice.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.besieged.musicpractice.R;
import com.besieged.musicpractice.adapter.MusicListAdapter;
import com.besieged.musicpractice.base.BaseActivity;
import com.besieged.musicpractice.model.Song;
import com.besieged.musicpractice.player.MusicPlayer;
import com.besieged.musicpractice.utils.AlbumUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.musicList)
    ListView musicList;

    String[] arr = new String[]{
            "http://p1.music.126.net/hT0qB_Wi-w5a0qhIumB59g==/18619129906686749.jpg?param=600y600",
            "http://p1.music.126.net/a1uV59DV8OJzn4OVXRlLPw==/109951163071276550.jpg?param=600y600",
            "http://p1.music.126.net/34YW1QtKxJ_3YnX9ZzKhzw==/2946691234868155.jpg?param=600y600",
            "http://p1.music.126.net/8GXkHa3eq63ikMMZ2nY8Gw==/109951163249852201.jpg?param=600y600",
            "http://p1.music.126.net/j_pJoQzzc_QX-mNE7cp_iw==/58274116284396.jpg?param=600y600",
            "http://p1.music.126.net/GJHxEbDIYWEYROWooTHP1Q==/109951163071269824.jpg?param=600y600",
            "http://p1.music.126.net/j_pJoQzzc_QX-mNE7cp_iw==/58274116284396.jpg?param=600y600",
            "http://p1.music.126.net/PsjQ1vycEVG4nhkXQWhwhQ==/52776558146680.jpg?param=600y600",
            "http://p1.music.126.net/j_pJoQzzc_QX-mNE7cp_iw==/58274116284396.jpg?param=600y600",
            "http://p1.music.126.net/nghrV1_ZW6lht9Ue7r4Ffg==/7697680906845029.jpg?param=600y600",
            "http://p1.music.126.net/c0nzSObvxF_DftayAJjXsw==/109951162864860506.jpg?param=600y600",
            "http://p1.music.126.net/B1ePGczwQUZueJl70TITWQ==/3287539775420245.jpg?param=600y600",
            "http://p1.music.126.net/f0oeK_W3VPHMUz_a-FX9UA==/109951163144626488.jpg?param=600y600",
            "http://p1.music.126.net/gWpnVz8ykfQJ-H57rI3gdA==/7887896418087327.jpg?param=600y600"
    };
    private MusicListAdapter MusicListAdapter;

    List<Song> songList;
    MusicPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setToolbar();
        mediaPlayer = MusicPlayer.getInstance();
        //动态申请权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);
        } else {
            songList = getSongList();
        }
        MusicListAdapter = new MusicListAdapter(songList, MainActivity.this,mocl);

        musicList.setAdapter(MusicListAdapter);
        musicList.setOnItemClickListener(oicl);
    }

    com.besieged.musicpractice.adapter.MusicListAdapter.MoreItemOnclickListener mocl = new MusicListAdapter.MoreItemOnclickListener() {
        @Override
        public void onMoreItemclick(View v, int position) {

        }
    };


    AdapterView.OnItemClickListener oicl = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(MainActivity.this,MusicPlayerActivity.class);

            i.putExtra("position",position);
            startActivity(i);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                songList = getSongList();

                MusicListAdapter = new MusicListAdapter(songList, this,mocl);

                musicList.setAdapter(MusicListAdapter);
                musicList.setOnItemClickListener(oicl);
            }
        }
    }

    private List<Song> getSongList(){
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null
        ,null ,null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        List<Song> songs = new ArrayList<Song>();
        int i = 0;
        while (cursor.moveToNext()){
            Song song = new Song();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            String artist  = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            byte[] bytes = AlbumUtils.parseAlbumByte(url);

            if (isMusic != 0){
                song.setId(id);
                song.setAlbum(album);
                song.setArtist(artist);
                song.setDuration(duration);
                song.setName(name);
                song.setTitle(title);
                song.setUrl(url);
                song.setSize(size);
                song.setImage("");
                song.setImgBytes(bytes);
                songs.add(song);
                i++;
            }
        }
        //设置playing list
        mediaPlayer.setPlayList(songs);
        return songs;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setToolbar() {
        toolbar.setTitle("MusicPractice");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add:
                        Toast.makeText(getBaseContext(), "add onclick", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }
}
