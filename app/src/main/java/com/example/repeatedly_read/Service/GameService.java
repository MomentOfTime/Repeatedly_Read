package com.example.repeatedly_read.Service;

import com.example.repeatedly_read.Bean.Piece;
import com.example.repeatedly_read.LinkInfo;

public interface GameService {

    /**
     * 控制游戏开始
     */
    void start();

    /**
     * 定义一个接口方法，用于返回一个二维数组
     * @return  存放方块对象的二维数组
     */
    Piece[][] getPieces();

    /**
     * 判断参数Piece[][] 数组中是否还存在非空的Piece对象
     * @return  如果还剩Piece对象 则返回true,没有则返回false
     */
    boolean hasPieces();

    /**
     * 根据鼠标的x坐标和y坐标，查找出一个Piece对象
     * @param touchX
     * @param touchY
     * @return
     */
    Piece findPiece (float touchX ,float touchY);

    /**
     *
     * 判断两个Piece是否可以相连，如果可以链接，则返回LinkInfo对象
     * @param p1 第一个Piece对象
     * @param p2    第二个Piece对象
     * @return  如果可以相连 ，返回LinkInfo对象，如果两个Piece不可以链接，返回null
     */
    LinkInfo link (Piece p1,Piece p2);
}
