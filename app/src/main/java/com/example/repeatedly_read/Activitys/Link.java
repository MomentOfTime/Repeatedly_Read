package com.example.repeatedly_read.Activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.repeatedly_read.Bean.Piece;
import com.example.repeatedly_read.LinkInfo;
import com.example.repeatedly_read.R;
import com.example.repeatedly_read.Service.GameService;
import com.example.repeatedly_read.Service.GameServiceImpl;
import com.example.repeatedly_read.Util.GameConf;
import com.example.repeatedly_read.Views.GameView;

import java.util.Timer;
import java.util.TimerTask;


public class Link extends Activity {

    private static final String TAG = "Link";

    //游戏配置对象
    private GameConf config;

    private GameService gameService;

    private GameView gameView;

    private Button startButton;

    private TextView timeTextView;

    private AlertDialog.Builder lostDialog;

    private AlertDialog.Builder successDialog;

    private Timer timer = new Timer();

    private int gameTime;

    private boolean isPlaying;

    private Vibrator vibrator;

    private Piece selected = null;

    private Handler handler = new Handler(){

        public void handleMessage(Message msg){
            switch (msg.what) {
                case 0x123:
                    timeTextView.setText("剩余时间:"+gameTime);
                    gameTime--;
                    // 时间小于0 lost
                    if (gameTime < 0){
                        stopTimer();
                        isPlaying = false;
                        lostDialog.show();
                        return;
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){

        //适配不同的屏幕,dp 转换px
//        int beginImageX = SizeUtils.dp2Px(this, GameConf.BEGIN_IMAGE_X);
//        int beginImageY = SizeUtils.dp2Px(this, GameConf.BEGIN_IMAGE_Y);

        config = new GameConf( 8 , 9 ,2 ,10 ,100000 ,this);

        gameView = findViewById(R.id.gameView);

        timeTextView = findViewById(R.id.timeText);

        startButton = findViewById(R.id.startButton);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        gameService = new GameServiceImpl(this.config);
        gameView.setGameService(gameService);

        //为开始按钮绑定点击
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(GameConf.DEFAULT_TIME);
            }
        });

        this.gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!isPlaying) {
                    return false;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    gameViewTouchDown(motionEvent);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    gameViewTouchUp(motionEvent);
                }
                return true;
            }
        });

        //初始化 游戏失败对话框
        lostDialog = createDialog(getString(R.string.lost),getString(R.string.lost_restart),R.drawable.lost)
                .setPositiveButton(R.string.dialog_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startGame(GameConf.DEFAULT_TIME);
                    }
                });

        //初始化游戏胜利对话框
        successDialog = createDialog(getString(R.string.success),getString(R.string.success_restart)
        ,R.drawable.success).setPositiveButton(R.string.dialog_sure
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startGame(GameConf.DEFAULT_TIME);
                    }
                });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        WindowManager wm = (WindowManager) Link.this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        Log.d(TAG, " width = " + width + "，height =" + height);

        int gameViewWidth = gameView.getWidth();
        int gameViewHeight = gameView.getHeight();
        Log.e("Link:", " gameViewWidth = " + gameViewWidth + "，gameViewHeight =" + gameViewHeight);


        // 每个 方块的 宽度 等于 公共画盘的宽度 / x方向的个数
        int tempWidth = (gameViewWidth - GameConf.BEGIN_IMAGE_X) / GameConf.PIECE_X_SUM;
        // 每个 方块的 高度 等于 公共画盘的高度 / y方向的个数
        int tempHeight = (gameViewHeight - GameConf.BEGIN_IMAGE_Y) / GameConf.PIECE_Y_SUM;
        int sideLengthOfSquare = tempWidth > tempHeight ? tempHeight : tempWidth;
        GameConf.PIECE_WIDTH = sideLengthOfSquare;
        // 每个 方块的 高度 等于 公共画盘的高度 / y方向的个数
        GameConf.PIECE_HEIGHT = sideLengthOfSquare;

        Log.d(TAG, " tempWidth =" + tempWidth + "， tempHeight =" + tempHeight);
        Log.d(TAG, " GameConf.PIECE_WIDTH =" + GameConf.PIECE_WIDTH + "， GameConf.PIECE_HEIGHT =" + GameConf.PIECE_HEIGHT);
    }

    @Override
    protected void onPause() {
        // 暂停游戏
        stopTimer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // 如果处于游戏状态中
        if (isPlaying) {
            // 以剩余时间重新开始游戏
            startGame(gameTime);
        }
        super.onResume();
    }


    /**
     * 触屏游戏区域处理方式
     * @param event
     */
    private void gameViewTouchDown (MotionEvent event) {
        Piece[][] pieces = gameService.getPieces();
        float touchX = event.getX();
        float touchY = event.getY();

        Piece currentPiece = gameService.findPiece(touchX , touchY);
        //如果没有选中任何Piece对象不再执行
        if (currentPiece == null)
            return;
        //设置为当前方块
        this.gameView.setSelectedPiece(currentPiece);
        //表示之前没有选中任何一个Piece
        if (this.selected == null) {
            //将当前方块设置为已选中的方块,重新将GamePanel绘制，并不再执行
            this.selected = currentPiece;
            this.gameView.postInvalidate();
            return;
        }

        //表示之前已经选中一个
        if (this.selected != null) {
            LinkInfo linkInfo = this.gameService.link(this.selected
            ,currentPiece);

            if (linkInfo == null){
                //连接不成功
                this.selected = currentPiece;
                this.gameView.postInvalidate();
            }else{

                //处理成功连接
                handleSuccessLink(linkInfo ,this.selected,currentPiece,pieces);
            }
        }
    }


    //触屏游戏外区域的处理方法
    private void gameViewTouchUp (MotionEvent e) {
        this.gameView.postInvalidate();
    }

    //以gameTime作为剩余时间开始或者恢复游戏
    private void startGame(int gameTime) {
        //如果之前timer 还未取消，则取消timer
        if (this.timer != null ){
            stopTimer();
        }

        //重新设置游戏时间
        this.gameTime = gameTime;

        if (gameTime == GameConf.DEFAULT_TIME){
            gameView.startGame();
        }
        isPlaying = true;
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x123);
            }
        },0,1000);
        this.selected = null;
    }



    /**
     * 成功连接后处理
     * @param linkInfo
     * @param prePiece
     * @param currentPiece
     * @param pieces
     */
    private void handleSuccessLink (LinkInfo linkInfo ,Piece prePiece ,Piece currentPiece
    ,Piece[][] pieces) {
        this.gameView.setLinkInfo(linkInfo);
        this.gameView.setSelectedPiece(null);
        this.gameView.postInvalidate();

        //消除
        pieces[prePiece.getIndexX()][prePiece.getIndexY()] = null;
        pieces[currentPiece.getIndexX()][currentPiece.getIndexY()] = null;

        //选中的方块设置null
        this.selected = null;
        //手机震动100ms
        this.vibrator.vibrate(100);
        //判断是否还有剩下的方块，如果没有游戏胜利
        if (!this.gameService.hasPieces()) {
            //游戏胜利
            this.successDialog.show();
            stopTimer();
            isPlaying=false;
        }
    }

    private AlertDialog.Builder createDialog (String title,String message,int imageResource) {
        return new AlertDialog.Builder(this).setTitle(title).setMessage(message).setIcon(imageResource);
    }

    private void stopTimer(){
        this.timer.cancel();
        this.timer = null;
    }

}
