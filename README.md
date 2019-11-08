# Repeatedly_Read

疯狂Android 讲义  第一个实例《疯狂连连看》

目前版本V1.0

还是记录一下：

##### 1、书上只给出了部分代码

， 只想吐槽一下，前一个项目第一行代码的《酷欧天气》全程不带脑子有手就行，但是这个连连看的代码作者不是按先后编写顺序排版的。书上的代码基本把99%的部分都写全了，但谁让我们是个弱鸡，最后1%还是得各种翻百度，逛hub。

##### 2、加载连连看小图片 bitmap is recycled?(已解决)

​	按照书上给的代码，在ImageUtil 中

```
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
```

从drawable文件夹下加载图片，如果按这样子写会出现bitmap ......is recycled ? 

本弱鸡一开始想着是放的图片太大了？

尝试着把所有的p_开头图片都缩小成40*40像素，问题依旧没有解决。

后来逛github 找到一位大佬同样是 《疯狂连连看》实例，我就借鉴了他的方法。本文末尾放链接。

###### 解决方案：

1、将"p_" 改为"cell" .

2、将drawable 文件下的图片被用作连连看的小图片 都改成"cell"开头。

如下图：

```
//约定 连连看所有的图片ID 以cell开头
...
for (Field field : drawableFields) {
                //如果Field 名称 以p_开头
                if (field.getName().indexOf("cell") != -1) {
                    resourceValues.add(field.getInt(R.drawable.class));
                }
            }
```

![image-20191108163858344](C:\Users\11347\AppData\Roaming\Typora\typora-user-images\image-20191108163858344.png)

##### 3、同一行/列消除的逻辑有问题(为解决)

- 两个相邻的消不掉
- 两个同行隔几个消不掉
- 同列隔几个消不掉





###### 文末放连接啦：

感谢 《疯狂Android讲义》

感谢 [github主:ouyangpeng](git@github.com:ouyangpeng/MyPictureMatching.git)