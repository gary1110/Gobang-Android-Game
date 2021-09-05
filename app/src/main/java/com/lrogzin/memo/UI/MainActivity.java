package com.lrogzin.memo.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lrogzin.memo.Adapter.NoteListAdapter;
import com.lrogzin.memo.Bean.NoteBean;
import com.lrogzin.memo.DB.NoteDao;
import com.lrogzin.memo.DB.UserDao;
import com.lrogzin.memo.R;
import com.lrogzin.memo.Util.EditTextClearTools;
import com.lrogzin.memo.Util.SpacesItemDecoration;
import com.lrogzin.memo.gomo.GomokuActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private UserDao userDao;
    private NoteDao noteDao;
    private RecyclerView rv_list_main;
    private NoteListAdapter mNoteListAdapter;
    private List<NoteBean> noteList;
    private String login_user;
    private TextView utv;
    private int nav_selected;
    private NavigationView navigationView;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nav_selected=2;
        noteDao = new NoteDao(this);
        userDao = new UserDao(this);

        //Intent获取当前登录用户
        Intent intent = getIntent();
        login_user = intent.getStringExtra("login_user");
        setTitle("APP");
        utv= (TextView) findViewById(R.id.tv_loginuser);

        initData();
        initView();
        registerForContextMenu(rv_list_main);


    }
    private void refreshNoteList(int mark){
        noteList = noteDao.queryNotesAll(login_user,mark);
        mNoteListAdapter.setmNotes(noteList);
        mNoteListAdapter.notifyDataSetChanged();

    }

    //初始化数据库数据
    private void initData() {
        Cursor cursor=noteDao.getAllData(login_user);
        noteList = new ArrayList<>();
        if(cursor!=null){
            while(cursor.moveToNext()){
                NoteBean bean = new NoteBean();
                bean.setId(cursor.getInt(cursor.getColumnIndex("note_id")));
                bean.setTitle(cursor.getString(cursor.getColumnIndex("note_tittle")));
                bean.setContent(cursor.getString(cursor.getColumnIndex("note_content")));
                bean.setType(cursor.getString(cursor.getColumnIndex("note_type")));
                bean.setMark(cursor.getInt(cursor.getColumnIndex("note_mark")));
                bean.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
                bean.setUpdateTime(cursor.getString(cursor.getColumnIndex("updateTime")));
                bean.setOwner(cursor.getString(cursor.getColumnIndex("note_owner")));
                noteList.add(bean);
            }
        }
        cursor.close();

    }
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));


        //抽屉式菜单
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        utv = (TextView)headerLayout.findViewById(R.id.tv_loginuser);
        utv.setText(login_user);


        //设置RecyclerView的属性
        rv_list_main = (RecyclerView) findViewById(R.id.rv_list_main);
        rv_list_main.addItemDecoration(new SpacesItemDecoration(0));
        rv_list_main.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list_main.setLayoutManager(layoutManager);

        mNoteListAdapter = new NoteListAdapter();
        mNoteListAdapter.setmNotes(noteList);
        rv_list_main.setAdapter(mNoteListAdapter);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    //抽屉菜单事件
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all) {
            nav_selected=2;
            refreshNoteList(2);
        } else if (id == R.id.nav_finish) {
//            nav_selected=1;
//            refreshNoteList(1);
            startActivity(new Intent(MainActivity.this, GomokuActivity.class)
            .putExtra("login_user",login_user)
            );

        }  else if (id == R.id.nav_exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Message");
            builder.setMessage("Sure Exit？");
            builder.setCancelable(false);
            builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            builder.setNegativeButton("Cancle", null);
            builder.create().show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();
        refreshNoteList(nav_selected);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            System.exit(0);
            return;
        }
        else { Toast.makeText(getBaseContext(), "Sure Exit", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();
    }
}

