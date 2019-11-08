package com.example.repeatedly_read.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.repeatedly_read.Bean.Piece;
import com.example.repeatedly_read.Bean.PieceImage;
import com.example.repeatedly_read.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ImageUtil {
    //保存所有连连看图片资源
    private static List<Integer> imageValues = getImageValues();

    //约定 连连看所有的图片ID 以p_开头
    public static List<Integer> getImageValues(){
        try {
            //得到drawable 所有的属性，获取drawable 目录下的所有图片
            Field[] drawableFields = R.drawable.class.getFields();
            List<Integer> resourceValues = new ArrayList<>();
            for (Field field : drawableFields) {
                //如果Field 名称 以p_开头
                if (field.getName().indexOf("cell") != -1) {
                    resourceValues.add(field.getInt(R.drawable.class));
                }
            }
            return resourceValues;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 随机从sourceValues 集合中随机获取size 个图片ID,返回结果为图片id集合
     * @param sourceValues  从中获取集合
     * @param size  需要获取的个数
     * @return  size个图片ID集合
     */
    public static List<Integer> getRandomValues(List<Integer> sourceValues
    ,int size) {
        //随机生成
        Random random = new Random();
        //创建结果集合
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                //随机获取一个数字，大于、小于sourceValues.size()的数值
                int index = random.nextInt(sourceValues.size());
                //从图片ID集合中获取该图片对象
                Integer image = sourceValues.get(index);
                result.add(image);
            }catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return result;
    }

    /**
     * 从drawable 目录中获取size个图片资源ID 其中size为游戏数量
     * @param size 需要获取图片ID的数量
     * @return  size个图片的ID集合
     */
    public static List<Integer> getPlayValues (int size) {
        if (size % 2 != 0) {
            //
            size += 1;
        }
        List<Integer> playImageValues = getRandomValues(imageValues , size/2);
        //将playImageValues集合的元素增加一倍（保证所有图片都有与之配对的图片）
        playImageValues.addAll(playImageValues);

        //将所有图片ID随机"洗牌"
        Collections.shuffle(playImageValues);
        return playImageValues;
    }

    /**
     * 将图片ID集合转换成PieceImage对象集合，PieceImage封装了图片ID与图片本身
     * @param context
     * @param size
     * @return
     */
    public static List<PieceImage> getPlayImages (Context context ,int size){
        //获取图片ID组成的集合
        List<Integer> resourceValues = getPlayValues(size);
        List<PieceImage> result = new ArrayList<>();

        //遍历每一个图片ID
        for (Integer value : resourceValues) {
            //加载图片
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources()
            ,value);
            //封装图片ID与图片本身
            PieceImage pieceImage = new PieceImage(bitmap , value);
            result.add(pieceImage);
        }
        return result;
    }

    /**
     * 获取选中的表示图片
     * @return
     */
    public static Bitmap getSelectImage (Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources()
        ,R.drawable.selected);
        return bitmap;
    }


}
