package com.example.repeatedly_read.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.example.repeatedly_read.Bean.Piece;
import com.example.repeatedly_read.LinkInfo;
import com.example.repeatedly_read.Service.GameService;
import com.example.repeatedly_read.Util.ImageUtil;

import java.util.List;

public class GameView extends View {

    //游戏逻辑实现
    private GameService gameService;

    private Piece selectedPiece;

    private LinkInfo linkInfo;

    private Paint paint;

    private Bitmap selectedImage;

    /**
     * @param context
     * @param attrs
     */

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.paint = new Paint();

        this.paint.setColor(Color.RED);

        this.paint.setStrokeWidth(3);

        this.selectedImage = ImageUtil.getSelectImage(context);
    }

    public void setLinkInfo (LinkInfo linkInfo) {
        this.linkInfo = linkInfo;
    }



    public void setGameService (GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.gameService == null)
            return;
        Piece[][] pieces = gameService.getPieces();
        if (pieces != null) {
            //遍历pieces 二维数组
            for (int i = 0; i < pieces.length; i++) {
                for (int j = 0; j < pieces[i].length; j++) {
                    if (pieces[i][j] != null) {
                        Piece piece = pieces[i][j];
                        if (piece.getPieceImage() != null) {
                            //根据方块左上角X,Y坐标绘制方块
                            canvas.drawBitmap(piece.getPieceImage().getImage()
                                    ,piece.getBeginX()
                                    ,piece.getBeginY()
                                    ,null);
                        }
                    }
                }
            }
        }

        if (this.linkInfo != null) {
            drawLine (this.linkInfo,canvas);
            this.linkInfo = null;
        }

        if (this.selectedPiece != null) {
            canvas.drawBitmap(this.selectedImage,this.selectedPiece.getBeginX()
            ,this.selectedPiece.getBeginY() , null);
        }
    }

    private void drawLine (LinkInfo linkInfo ,Canvas canvas) {

        List<Point> points = linkInfo.getLinkPoints();
        for (int i = 0; i < points.size() -1 ; i++) {
            Point currentPoint = points.get(i);
            Point nextPoint = points.get(i+1);

            canvas.drawLine(currentPoint.x
            ,currentPoint.y
            ,nextPoint.x
            , nextPoint.y
            ,this.paint);
        }
    }

    //选择方块的方法
    public void setSelectedPiece(Piece piece) {
        this.selectedPiece = piece;
    }

    //开始游戏
    public void startGame () {
        this.gameService.start();
        this.postInvalidate();
    }
}
