package com.example.reverb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class allvideos extends AppCompatActivity {
    TextView noofvid;
    SwipeRefreshLayout s;

    final static ArrayList<ModelVideo> videosList = new ArrayList<>();
    static ArrayList<ModelVideo> folder= new ArrayList<>();
    ArrayList<String> duplicate1 = new ArrayList<>();
    private AdapterVideoList adapterVideoList;
    //private FolderAdapter folderAdapter;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allvideos);
       s=findViewById(R.id.refresh);
       //s.setProgressBackgroundColorSchemeColor(Integer.parseInt("#fff"));
       s.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
            public void onRefresh() {
               if(videosList.size()!=0)
               {
                videosList.clear();
                   loadVideos();
               }
               s.setRefreshing(false);

           }
        });
        noofvid = findViewById(R.id.vidsize);
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
       // getActionBar().setElevation(0);
        View view = getSupportActionBar().getCustomView();


        checkPermissions();

        initializeViews();




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1,menu);
        MenuItem searchitem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchitem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               filter(newText);
                return false;

            }
        });

        return true;

    }
    private void filter(String text)
    {
        ArrayList<ModelVideo> filteredlist = new ArrayList<>();
        for(ModelVideo m:videosList)
        {
            if(m.getTitle().toLowerCase().contains(text.toLowerCase()))
            {
                filteredlist.add(m);
            }
        }
        if(filteredlist.isEmpty())
        {
            Toast.makeText(this,"No Results",Toast.LENGTH_SHORT).show();
        }
        else {
            adapterVideoList.filterList(filteredlist);
        }
    }



    private void initializeViews() {



        RecyclerView recyclerView = findViewById(R.id.recyclerView_videos);

       // recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); //3 = column count
        adapterVideoList = new AdapterVideoList(this, videosList);

        recyclerView.setAdapter(adapterVideoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        //folderAdapter = new FolderAdapter(this,videosList);



    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            } else {
                if(videosList.size()==0){
                    loadVideos();
                    //setsize(videosList.size());
                }

                if(videosList.size()!=0){
                 setsize(videosList.size());}
            }
        } else {
            if(videosList.size()==0)
            {
                loadVideos();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadVideos();
            } else {
                Toast.makeText(this, "Please accept the permission", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void loadVideos() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION,MediaStore.Video.Media.ALBUM};
                String sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC";


                Cursor cursor = getApplication().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);
                if (cursor != null) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                    int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                    int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                    int albumColumn =cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM);
                    //int pathColumn= cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(idColumn);
                        String title = cursor.getString(titleColumn);
                        int duration = cursor.getInt(durationColumn);
                        String album = cursor.getString(albumColumn);


                        //String  path = cursor.getString(pathColumn);

                        Uri data = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                       // Log.e("DATA"+data.getPath(),"Path");


                        String duration_formatted;
                        int sec = ((duration / 1000) % 60);
                        int min = (duration / (1000 * 60)) % 60;
                        int hrs = duration / (1000 * 60 * 60);

                        if (hrs == 0) {
                            duration_formatted = String.valueOf(min).concat(":".concat(String.format(Locale.UK, "%02d", sec)));
                        } else {
                            duration_formatted = String.valueOf(hrs).concat(":".concat(String.format(Locale.UK, "%02d", min).concat(":".concat(String.format(Locale.UK, "%02d", sec)))));
                        }
//                        int slashFirstIndex = data.getPath().lastIndexOf("/");
//                        String subString = data.getPath().substring(0,slashFirstIndex);
//
//                        int index= subString.lastIndexOf("/");
//                        String folderName = subString.substring(index+1,slashFirstIndex);

                        ModelVideo m=new ModelVideo(id, data, title, duration_formatted,album);

                         videosList.add(m);
//                        Set<ModelVideo> set = new LinkedHashSet<ModelVideo>();
//                        set.addAll(videosList);
//                        videosList.clear();
//                        videosList.addAll(set);
                        if (!duplicate1.contains(album)){
                            folder.add(m);
                            duplicate1.add(album);

                        }
//                        setsize(videosList.size());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapterVideoList.notifyItemInserted(videosList.size() - 1);
                            }
                        });
                        //setsize(videosList.size());
                    }


                }
                setsize(videosList.size());

            }
        }.start();

    }
    private void setsize(int s)
    {
        noofvid.setText(Integer.toString(s));
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(this,HomePage.class);
        startActivity(intent);
    }

}