package com.example.repeatedly_read.Board;

import com.example.repeatedly_read.Abstract.AbstractBoard;
import com.example.repeatedly_read.Bean.Piece;
import com.example.repeatedly_read.Util.GameConf;

import java.util.ArrayList;
import java.util.List;

public class VerticalBoard extends AbstractBoard {
    @Override
    protected List<Piece> createPieces(GameConf config, Piece[][] pieces) {

        //创建一个Piece集合，该集合里面存放初始化游戏时所需要的Piece对象
        List<Piece> notNullPieces = new ArrayList<>();
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                if (i % 2 == 0) {

                    /**
                     * 只有偶数列 才能显示
                     */

                    Piece piece = new Piece(i,j);
                    notNullPieces.add(piece);
                }
            }
        }
        return notNullPieces;
    }
}
