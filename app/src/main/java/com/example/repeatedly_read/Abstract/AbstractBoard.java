package com.example.repeatedly_read.Abstract;

import com.example.repeatedly_read.Bean.Piece;

import java.util.List;

public abstract class AbstractBoard {

    //定义抽象方法
    protected abstract List<Piece> createPieces (GameConf config ,Piece[][] pieces);

    public Piece[][] create(GameConf config){
        //创建Piece{][] 数组
        Piece[][] pieces = new Piece[config.getXSize()][config.getYSinze()];
    }
}
