package com.example.repeatedly_read.Bean;

import android.graphics.Point;

/**
 * 定义一个方块
 */
public class Piece {

    //保存方块图片
    private PieceImage pieceImage;

    private int beginX;

    private int beginY;

    private int indexX;

    private int indexY;

    public Piece(int indexX, int indexY) {
        this.indexX = indexX;
        this.indexY = indexY;
    }

    public PieceImage getPieceImage() {
        return pieceImage;
    }

    public void setPieceImage(PieceImage image) {
        this.pieceImage = image;
    }

    public int getBeginX() {
        return beginX;
    }

    public void setBeginX(int beginX) {
        this.beginX = beginX;
    }

    public int getBeginY() {
        return beginY;
    }

    public void setBeginY(int beginY) {
        this.beginY = beginY;
    }

    public int getIndexX() {
        return indexX;
    }

    public void setIndexX(int indexX) {
        this.indexX = indexX;
    }

    public int getIndexY() {
        return indexY;
    }

    public void setIndexY(int indexY) {
        this.indexY = indexY;
    }

    public boolean isSameImage(Piece other) {

        if (pieceImage == null) {
            if (other.pieceImage != null) {
                return false;
            }
        }
        return pieceImage.getImageId() == other.pieceImage.getImageId();
    }

    public Point getCenter(){
        return new Point(getBeginX() +getPieceImage().getImage().getWidth()/2
        ,getBeginY() + getPieceImage().getImage().getHeight() /2);
    }

}
