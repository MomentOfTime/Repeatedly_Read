package com.example.repeatedly_read.Util;

import com.example.repeatedly_read.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
                if (field.getName().indexOf("p_") != -1) {
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
     * 随机从sourceValues 集合中获取size 个图片ID,返回结果为图片id集合
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
                return null
            }
        }
        return result;
    }



}
