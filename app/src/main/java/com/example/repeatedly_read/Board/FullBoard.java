package com.example.repeatedly_read.Board;

import com.example.repeatedly_read.Abstract.AbstractBoard;
import com.example.repeatedly_read.Bean.Piece;
import com.example.repeatedly_read.Util.GameConf;

import java.util.ArrayList;
import java.util.List;

public class FullBoard extends AbstractBoard {

    @Override
    protected List<Piece> createPieces(GameConf config, Piece[][] pieces) {

        //创建一个Piece集合，该集合里面存放初始化游戏所需的Piece对象
        List<Piece> notNullPieces = new ArrayList<>();
        for (int i = 0; i < pieces.length - 1; i++) {
            for (int j = 0; j < pieces[i].length - 1; j++) {
                //先构造一个Piece对象，只设置它在Piece[][]数组中的索引值
                //所需要的PieceImage由其父类设置
                Piece piece = new Piece(i ,j );
                notNullPieces.add(piece);
            }
        }
        return notNullPieces;
    }
}
