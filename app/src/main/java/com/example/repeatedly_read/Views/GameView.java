package com.example.repeatedly_read.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.repeatedly_read.Bean.Piece;

public class GameView extends View {

    //游戏逻辑实现
//    private GameService gameService;
    private Piece selectedPiece;

//    private LinkInfo linkInfo;

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
//        this.selectedImage =
    }




}
