package com.example.repeatedly_read.Util;

import android.content.Context;

public class GameConf {

    /**
     * x 轴有几个方块
     */
    public final static int PIECE_X_SUM = 8;
    /**
     * y 轴有几个方块
     */
    public final static int PIECE_Y_SUM = 8;

    /**
     * 从哪里 开始画第一张图片 的x坐标
     */
    public final static int BEGIN_IMAGE_X = 25;

    /**
     * 从哪里开始画第一张图片的y坐标
     */

    public final static int BEGIN_IMAGE_Y = 75;

    /**
     * 每个方块的宽，启动时候的赋值
     */
    public static int PIECE_WIDTH;
    /**
     * 每个方块的高
     */
    public static int PIECE_HEIGHT;
    /**
     * 游戏总时间
     */
    public static int DEFAULT_TIME = 100;

    /**
     * Piece[][] 数组一维长度
     */
    private int xSize;
    /**
     * Piece[][] 数组二维长度
     */
    private int ySize;

    /**
     * Board中第一张照片出现的x坐标
     */
    private int beginImageX;

    /**
     * Board中 第一张图片出现的y坐标
     */
    private int beginImageY;

    /**
     * 记录游戏的总时间 s
     */
    private long gameTime;

    /**
     * 应用上下文
     */
    private Context context;

    public GameConf(int xSize, int ySize, int beginImageX, int beginImageY, long gameTime, Context context) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.beginImageX = beginImageX;
        this.beginImageY = beginImageY;
        this.gameTime = gameTime;
        this.context = context;
    }

    public int getxSize() {
        return xSize;
    }

    public void setxSize(int xSize) {
        this.xSize = xSize;
    }

    public int getySize() {
        return ySize;
    }

    public void setySize(int ySize) {
        this.ySize = ySize;
    }

    public int getBeginImageX() {
        return beginImageX;
    }

    public void setBeginImageX(int beginImageX) {
        this.beginImageX = beginImageX;
    }

    public int getBeginImageY() {
        return beginImageY;
    }

    public void setBeginImageY(int beginImageY) {
        this.beginImageY = beginImageY;
    }

    public long getGameTime() {
        return gameTime;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
