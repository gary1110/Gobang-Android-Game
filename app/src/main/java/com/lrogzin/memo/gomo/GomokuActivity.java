package com.lrogzin.memo.gomo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.lrogzin.memo.Bean.NoteBean;
import com.lrogzin.memo.DB.NoteDao;
import com.lrogzin.memo.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class GomokuActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private static final String TAG = "Gomoku_ZZZ";
    private static final String TAG1 = "GOMOKU_onItemClick";

    GridView gridView;
    TextView textview_notify;
    List<Chess> list;
    GomokuAdapter gomokuAdapter;
    List<int[]> repeatList;

    Button button_restart;
    Button button_repeat_display;
    CheckBox checkboxAI;

    //当前落子方
    private String whoMove;
    //黑手先走棋
    private int whoContinue = 1;
    //游戏继续开关
    private boolean isEnd = false;
    //是否复盘标记
    private boolean isRepeat = false;
    //是否复盘结束
    private boolean isRepeatEnd = true;
    //是否开启人工智能 true为开启
    private boolean isAIOpen = true;

    private int[][] chessBoard = null;
    private String  jibie="1";
    private long time;
    private NoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gomoku);
        time= SystemClock.currentThreadTimeMillis();
        gridView = (GridView) findViewById(R.id.gridview);
        textview_notify = (TextView) findViewById(R.id.textview_notify);

        button_restart = (Button) findViewById(R.id.button_restart);
        button_repeat_display = (Button) findViewById(R.id.button_repeat_display);
        checkboxAI = (CheckBox) findViewById(R.id.checkboxAI);

        button_restart.setOnClickListener(this);
        button_repeat_display.setOnClickListener(this);
        checkboxAI.setOnCheckedChangeListener(this);

        //初始化复盘集合
        repeatList = new ArrayList<>();
        //初始化棋盘
        initGomoku();

        gomokuAdapter = new GomokuAdapter(getLayoutInflater(), list);
        gridView.setAdapter(gomokuAdapter);
//        saveNoteDate();
        gridView.setOnItemClickListener(this);
    }
    public void initGomoku(){
        isEnd = false;
        isRepeat = false;
        whoContinue = 1;
        textview_notify.setText("Please black move later");
        chessBoard = new int[16][14];
        list = new ArrayList<>();
        for(int i = 0; i < 224; i++){
            Chess chess = new Chess();
            chess.who = 0;
            list.add(chess);
        }
    }
    private String getNowTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateNowStr = sdf.format(d);
        return dateNowStr;
    }


    private void saveNoteDate() {

        String notecreateTime = getNowTime();
        String noteupdateTime = getNowTime();
        noteDao=new NoteDao(this);
        NoteBean note=new NoteBean();

        time= SystemClock.currentThreadTimeMillis();

        note.setTitle(getIntent().getStringExtra("login_user"));
//        note.setContent("109");
        note.setContent((SystemClock.currentThreadTimeMillis()-time)/1000+"");
        note.setCreateTime(notecreateTime);
        note.setUpdateTime(noteupdateTime);
        note.setMark(0);
        note.setRemindTime("");
        note.setType("");
        note.setOwner(getIntent().getStringExtra("login_user"));
        noteDao.insertNote(note);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!isEnd){
            int[] clickXY = postion2XY(position);
            Chess chess = gomokuAdapter.getItem(position);
            if(isValid(chess)){
                //改变棋盘二维数组的数据
                chessBoard[clickXY[0]][clickXY[1]] = whoContinue;
                //添加本次操作到复盘数据
                if(!isRepeat){
                    repeatList.add(clickXY);
                    //复盘数据日志
                    StringBuilder sb = new StringBuilder("Analyse the data:");
                    for(int[] repeatXY : repeatList){
                        sb.append(Arrays.toString(repeatXY) + "-");
                    }
                    Log.i("GOMOKU_onItemClick", sb.toString());
                }


                switch(whoContinue){
                    case 1:
                        chess.who = 1;
                        //当前落子方
                        whoMove = "black";
                        //下次落子方提示语
                        textview_notify.setText("White, please move later");
                        break;
                    case 2:
                        chess.who = 2;
                        //当前落子方
                        whoMove = "white";
                        //下次落子方提示语
                        textview_notify.setText("black, please move later");
                        break;
                }
                whoContinue = whoContinue == 1 ? 2 : 1;
                gomokuAdapter.notifyDataSetChanged();

                if(!isContinue(clickXY)){
                    isEnd = true;
                    textview_notify.setText("finish，" + whoMove + "wine!");
                    if ("black".equals(whoMove)){
                        saveNoteDate();
                    }

                }else{
                    if(isAIOpen && whoContinue == 2 && !isRepeat){
                        int positionAI = doAICalculate(whoContinue, clickXY);
                        onItemClick(gridView, null, positionAI, positionAI);
                    }
                }
            }
        }
    }



    /**
     * 判断落子是否有效
     * @param chess
     * @return
     */
    public boolean isValid(Chess chess){
        return chess.who == 0;
    }

    /**
     * 判断游戏是否继续
     * @return
     */
    public boolean isContinue(int[] xy){
        int continuousCount = 1;
        //左
        int x = xy[0];
        int y = xy[1] - 1;

        while(y >= 0 && chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
            continuousCount++;
            y--;
        }
        //右
        x = xy[0];
        y = xy[1] + 1;
        while(y < chessBoard[x].length && chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
            continuousCount++;
            y++;
        }

        if(continuousCount >= 5){
            return false;
        }

        //上
        x = xy[0] - 1;
        y = xy[1];
        continuousCount = 1;
        while(x >= 0 && chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
            continuousCount++;
            x--;
        }
        //下
        x = xy[0] + 1;
        y = xy[1];
        while(x < chessBoard.length && chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
            continuousCount++;
            x++;
        }

        if(continuousCount >= 5){
            return false;
        }

        //左上
        x = xy[0] - 1;
        y = xy[1] - 1;
        continuousCount = 1;
        while(x >= 0 && y >= 0 && chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
            continuousCount++;
            x--;
            y--;
        }
        //右下
        x = xy[0] + 1;
        y = xy[1] + 1;
        while(x < chessBoard.length && y < chessBoard[x].length && chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
            continuousCount++;
            x++;
            y++;
        }

        if(continuousCount >= 5){
            return false;
        }

        //右上
        x = xy[0] - 1;
        y = xy[1] + 1;
        continuousCount = 1;
        while(x >= 0 && y < chessBoard[x].length && chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
            continuousCount++;
            x--;
            y++;
        }
        //左下
        x = xy[0] + 1;
        y = xy[1] - 1;
        while(x < chessBoard.length && y >= 0 && chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
            continuousCount++;
            x++;
            y--;
        }

        if(continuousCount >= 5){
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_restart://重新开始
                isRepeatEnd = true;
                repeatList.clear();
                initGomoku();
                gomokuAdapter.initData(list);
                gridView.setEnabled(true);
                break;
            case R.id.button_repeat_display://复盘
                gridView.setEnabled(false);
                repeat();
                break;
        }
    }

    /**
     * 将position转化为二维坐标
     * @param postion
     * @return
     */
    public int[] postion2XY(int postion){
        int[] xy= new int[2];
        xy[0] = postion / 14;
        xy[1] = postion % 14;
        return xy;
    }

    /**
     * 将XY坐标转化为position
     */
    public int xy2Position(int[] xy){
        int position = xy[0] * 14 + xy[1];
        return position;
    }

    /**
     * 复盘
     */
    public void repeat(){
        initGomoku();
        gomokuAdapter.initData(list);
        isRepeat = true;
        isRepeatEnd = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                repeatFor:
                for(int[] xy : repeatList){
                    int position = xy2Position(xy);
                    Message msg = handler.obtainMessage();
                    msg.arg1 = position;
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(isRepeatEnd){
                        break repeatFor;
                    }
                }
                isRepeatEnd = true;
            }
        }).start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            onItemClick(gridView, null, msg.arg1, msg.arg1);
        }
    };

    /**
     * 人工智能算法，最终返回计算机将要落子的坐标XY的position位置
     * @param who
     * @param xy
     * @return
     */
    public int doAICalculate(int who, int[] xy){
        TreeSet<AIPosition> treeSet = doAIConsider(who, xy);
        if(treeSet != null && treeSet.size() > 0){

            if ("1".equals(jibie)){
                return filtrateAIPosition1(treeSet);
            }else {
                return filtrateAIPosition(treeSet);
            }

        }else{
            Toast.makeText(this, "Artificial intelligence is running out of options", Toast.LENGTH_SHORT).show();

            return 0;
        }
    }

    /**
     * @return 简单
     */
    public int filtrateAIPosition1(TreeSet<AIPosition> treeSet){
        List<AIPosition> listAIPosition = new ArrayList<>();
        for(AIPosition aiPosition : treeSet){
            int[] xy = postion2XY(aiPosition.position);
            int blankX = xy[0];
            int blankY = xy[1];
            //如果可落子点附近有对方棋子，则优先级+0.1
            float d = isHaveOpponent(1, blankX, blankY);
            aiPosition.level= d;
        }
        //重新排序递增了优先级的落子方案
        TreeSet<AIPosition> resultTreeSet = new TreeSet<>();
        resultTreeSet.addAll(treeSet);

        listAIPosition.addAll(resultTreeSet);

        return listAIPosition.get(listAIPosition.size() - 1).position;
    }

    /**
     * 困难
     * @return
     */
    public int filtrateAIPosition(TreeSet<AIPosition> treeSet){
        List<AIPosition> listAIPosition = new ArrayList<>();
        for(AIPosition aiPosition : treeSet){
            int[] xy = postion2XY(aiPosition.position);
            int blankX = xy[0];
            int blankY = xy[1];
            //如果可落子点附近有对方棋子，则优先级+0.1
            float d = isHaveOpponent(1, blankX, blankY);
            aiPosition.level += d;
        }
        //重新排序递增了优先级的落子方案
        TreeSet<AIPosition> resultTreeSet = new TreeSet<>();
        resultTreeSet.addAll(treeSet);

        listAIPosition.addAll(resultTreeSet);

        return listAIPosition.get(listAIPosition.size() - 1).position;
    }

    /**
     * 判断可落子点附近是否有对方棋子
     * @param blankX
     * @param blankY
     * @param opponet 对方棋子，目前：1 ：黑棋，2：白旗 0 ：空棋
     * @return
     */
    public float isHaveOpponent(int opponet, int blankX, int blankY){
        int displayCount = 0;
        //遍历可落子点附近有多少个对方棋子，计算出优先级，用于递增
        for(int line = blankX - 1; line <= blankX + 1; line++){
            for(int col = blankY - 1; col <= blankY + 1; col++){
                if(line >= 0 && line < chessBoard.length
                        && col >= 0 && col < chessBoard[blankX].length
                        && chessBoard[line][col] == opponet){
                    displayCount++;
                }
            }
        }
        //根据对方棋子出现次数，增加优先级增量
        switch (displayCount){
            case 1:
                return 0.0f;
            case 2:
                return 0.1f;
            case 3:
                return 0.2f;
            case 4:
                return 0.3f;
            case 5:
                return 0.4f;
            case 6:
                return 0.5f;
            case 7:
                return 0.6f;
            case 8:
                return 0.7f;
            default:
                return 0.0f;
        }
    }

    /**
     * 人工智能思考
     * @param who
     * @param xy
     * @return
     */
    public TreeSet<AIPosition> doAIConsider(int who, int[] xy){
        //按照优先等级进行落子方案的排序
        TreeSet<AIPosition> treeSet = new TreeSet<>();

        //标记该方位是否有空当可落子
        boolean leftBlank = false;
        boolean rightBlank = false;

        boolean topBlank = false;
        boolean bottomBlank = false;

        boolean leftTopBlank = false;
        boolean rightBottomBlank = false;

        boolean leftBottomBlank = false;
        boolean rightTopBlank = false;

        Map<String, Integer> map = new HashMap<>();

        //左
        int x = xy[0];
        int y = xy[1] - 1;
        int continuousCount = 1;

        while(y >= 0){
            if(chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
                continuousCount ++;
                y--;
            }else if(chessBoard[x][y] == who){
                //左边已经遇到己方棋子
                break;
            }else if(chessBoard[x][y] == 0){
                //左边遇到空棋子
                leftBlank = true;
                map.put("LEFT", xy2Position(new int[]{x, y}));
                break;
            }
        }

        //右
        x = xy[0];
        y = xy[1] + 1;

        while(y < chessBoard[x].length){
            if(chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
                continuousCount ++;
                y++;
            }else if(chessBoard[x][y] == who){
                //左边已经遇到己方棋子
                break;
            }else if(chessBoard[x][y] == 0){
                //左边遇到空棋子
                rightBlank = true;
                map.put("RIGHT", xy2Position(new int[]{x, y}));
                break;
            }
        }

        if(leftBlank || rightBlank){
            AIPosition aiPosition1 = null;
            AIPosition aiPosition2 = null;
            //需要算法细化
            if(leftBlank){
                aiPosition1 = new AIPosition();
                aiPosition1.position = map.get("LEFT");
                aiPosition1.level = continuousCount;
            }
            if(rightBlank){
                aiPosition2 = new AIPosition();
                aiPosition2.position = map.get("RIGHT");
                aiPosition2.level = continuousCount;
            }
            if(aiPosition1 != null){
                treeSet.add(aiPosition1);
            }
            if(aiPosition2 != null){
                treeSet.add(aiPosition2);
            }
        }

        //上
        x = xy[0] - 1;
        y = xy[1];
        continuousCount = 1;

        while(x >= 0){
            if(chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
                continuousCount ++;
                x--;
            }else if(chessBoard[x][y] == who){
                //上边已经遇到己方棋子
                break;
            }else if(chessBoard[x][y] == 0){
                //上边遇到空棋子
                topBlank = true;
                map.put("TOP", xy2Position(new int[]{x, y}));
                break;
            }
        }

        //下
        x = xy[0] + 1;
        y = xy[1];

        while(x < chessBoard.length){
            if(chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
                continuousCount ++;
                x++;
            }else if(chessBoard[x][y] == who){
                //下边已经遇到己方棋子
                break;
            }else if(chessBoard[x][y] == 0){
                //下边遇到空棋子
                bottomBlank = true;
                map.put("BOTTOM", xy2Position(new int[]{x, y}));
                break;
            }
        }

        if(topBlank || bottomBlank){
            AIPosition aiPosition1 = null;
            AIPosition aiPosition2 = null;
            //需要算法细化
            if(topBlank){
                aiPosition1 = new AIPosition();
                aiPosition1.position = map.get("TOP");
                aiPosition1.level = continuousCount;
            }

            if(bottomBlank){
                aiPosition2 = new AIPosition();
                aiPosition2.position = map.get("BOTTOM");
                aiPosition2.level = continuousCount;
            }
            if(aiPosition1 != null){
                treeSet.add(aiPosition1);
            }
            if(aiPosition2 != null){
                treeSet.add(aiPosition2);
            }
        }

        //左上
        x = xy[0] - 1;
        y = xy[1] - 1;
        continuousCount = 1;

        while(x >= 0 && y >= 0){
            if(chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
                continuousCount ++;
                x--;
                y--;
            }else if(chessBoard[x][y] == who){
                //左上遇到己方棋子
                break;
            }else if(chessBoard[x][y] == 0){
                //左上遇到空棋子
                leftTopBlank = true;
                map.put("LEFTTOP", xy2Position(new int[]{x, y}));
                break;
            }
        }

        //右下
        x = xy[0] + 1;
        y = xy[1] + 1;

        while(x < chessBoard.length && y < chessBoard[x].length){
            if(chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
                continuousCount ++;
                x++;
                y++;
            }else if(chessBoard[x][y] == who){
                //右下遇到己方棋子
                break;
            }else if(chessBoard[x][y] == 0){
                //右下遇到空棋子
                rightBottomBlank = true;
                map.put("RIGHTBOTTOM", xy2Position(new int[]{x, y}));
                break;
            }
        }

        if(leftTopBlank || rightBottomBlank){
            AIPosition aiPosition1 = null;
            AIPosition aiPosition2 = null;
            //需要算法细化
            if(leftTopBlank){
                aiPosition1 = new AIPosition();
                aiPosition1.position = map.get("LEFTTOP");
                aiPosition1.level = continuousCount;
            }
            if(rightBottomBlank){
                aiPosition2 = new AIPosition();
                aiPosition2.position = map.get("RIGHTBOTTOM");
                aiPosition2.level = continuousCount;
            }
            if(aiPosition1 != null){
                treeSet.add(aiPosition1);
            }
            if(aiPosition2 != null){
                treeSet.add(aiPosition2);
            }
        }

        //左下
        x = xy[0] + 1;
        y = xy[1] - 1;
        continuousCount = 1;

        while(x <chessBoard.length && y >= 0){
            if(chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
                continuousCount ++;
                x++;
                y--;
            }else if(chessBoard[x][y] == who){
                //左下遇到己方棋子
                break;
            }else if(chessBoard[x][y] == 0){
                //左下遇到空棋子
                leftBottomBlank = true;
                map.put("LEFTBOTTOM", xy2Position(new int[]{x, y}));
                break;
            }
        }

        //右上
        x = xy[0] - 1;
        y = xy[1] + 1;

        while(x >= 0 && y < chessBoard[x].length){
            if(chessBoard[x][y] == chessBoard[xy[0]][xy[1]]){
                continuousCount ++;
                x--;
                y++;
            }else if(chessBoard[x][y] == who){
                //右上遇到己方棋子
                break;
            }else if(chessBoard[x][y] == 0){
                //右上遇到空棋子
                rightTopBlank = true;
                map.put("RIGHTTOP", xy2Position(new int[]{x, y}));
                break;
            }
        }

        if(leftBottomBlank || rightTopBlank){
            AIPosition aiPosition1 = null;
            AIPosition aiPosition2 = null;
            //需要算法细化
            if(leftBottomBlank){
                aiPosition1 = new AIPosition();
                aiPosition1.position = map.get("LEFTBOTTOM");
                aiPosition1.level = continuousCount;
            }
            if(rightTopBlank){
                aiPosition2 = new AIPosition();
                aiPosition2.position = map.get("RIGHTTOP");
                aiPosition2.level = continuousCount;
            }
            if(aiPosition1 != null){
                treeSet.add(aiPosition1);
            }
            if(aiPosition2 != null){
                treeSet.add(aiPosition2);
            }
        }
        return treeSet;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isAIOpen = isChecked;
    }
}
