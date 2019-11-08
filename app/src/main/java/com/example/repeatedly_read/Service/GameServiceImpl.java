package com.example.repeatedly_read.Service;

import android.graphics.Point;

import com.example.repeatedly_read.Abstract.AbstractBoard;
import com.example.repeatedly_read.Bean.Piece;
import com.example.repeatedly_read.Board.FullBoard;
import com.example.repeatedly_read.Board.HorizontalBoard;
import com.example.repeatedly_read.Board.VerticalBoard;
import com.example.repeatedly_read.LinkInfo;
import com.example.repeatedly_read.Util.GameConf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameServiceImpl implements GameService {

    //定义一个Piece[][] 数组 只提供getter
    private Piece[][] pieces;
    //游戏配置对象
    private GameConf config;

    public GameServiceImpl(GameConf config) {
        this.config = config;
    }

    @Override
    public void start() {
        //定义一个AbstractBoard
        AbstractBoard board = null;
        Random random = new Random();
        //获取 随机数 可取0、1、2、3 四个值
        int index = random.nextInt(4);
        //随机生成 AbstractBoard 子类实例
        switch (index) {
            case 0:
                // 0 返回VerticalBoard(竖向)
                board = new VerticalBoard();
                break;
            case 1:
                // 1 返回HorizontalBoard (横向)
                board = new HorizontalBoard();
                break;
            default:
                //默认返回FullBoard
                board = new FullBoard();
                break;
        }
        //初始化Piece[][]
        this.pieces = board.create(config);
    }

    //直接返回本对象Piece[][]
    @Override
    public Piece[][] getPieces() {
        return this.pieces;
    }

    //实现接口的hasPices
    //是否还有存在的方块
    @Override
    public boolean hasPieces() {

        //遍历Piece[][] 数组的每一个元素
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[i].length; j++) {
                //只要任意一个数组元素不为null，也就是还有非空Piece对象
                if (pieces[i][j] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasPiece(int x, int y) {
        if (findPiece(x, y) == null) {
            return false;
        }
        return true;
    }


    @Override
    public Piece findPiece(float touchX, float touchY) {

        //根据 触碰点的位置查找相应的方块
        //由于在创建Piece对象的时候，将每个Pice的坐标都加了
        //GameConf中设置的beginImageX/beginImageY的值，因此这里要减去
        int relativeX = (int) touchX - this.config.getBeginImageX();
        int relativeY = (int) touchY - this.config.getBeginImageY();

        //如果鼠标点击的地方比board中第一张图片的开始x坐标和开始y坐标要小
        //没有找到相应的方块
        if (relativeX < 0 || relativeY < 0) {
            return null;
        }

        //获取relativeX坐标在Piece[][] 数组中的第一维的所引致
        //第一参数为每张照片的宽
        int indexX = getIndex(relativeX, GameConf.PIECE_WIDTH);
        int indexY = getIndex(relativeY, GameConf.PIECE_HEIGHT);

        if (indexX < 0 || indexY < 0) {
            return null;
        }

        if (indexX >= this.config.getxSize()
                || indexY >= this.config.getySize()) {
            return null;
        }

        //返回Piece[][]数组的指定元素
        return this.pieces[indexX][indexY];
    }

    /**
     * 工具方法，根据relative坐标计算想读与Piece[][] 数组的第一维
     * 或第二维的索引值，size为每张图片的长宽
     *
     * @param relative
     * @param size
     * @return
     */
    private int getIndex(int relative, int size) {
        //表示坐标relative 不在数组
        int index = -1;

        /**
         * 让坐标除以边长，没有余数，索引减1
         * 例如点 x = 20,边宽为10 ，20%10 没有余数
         * index = 1 即在索引为1
         */
        if (relative % size == 0) {
            index = relative / size - 1;
        } else {

            //有余数
            index = relative / size;
        }
        return index;
    }

    /**
     * 判断两个方块是否可以相连
     *
     * @param p1 第一个Piece对象
     * @param p2 第二个Piece对象
     * @return
     */
    @Override
    public LinkInfo link(Piece p1, Piece p2) {

        //两个Piece是同一个，即选中了同一个 返回null
        if (p1.equals(p2))
            return null;
        //如果p1图片与p2图片不相同，返回null
        if (!p1.isSameImage(p2))
            return null;
        //如果p2在p1 左边，重新执行此方法，参数互换
        if (p2.getIndexX() < p1.getIndexX())
            return link(p2, p1);
        //获取 p1 ，p2 中心
        Point p1Point = p1.getCenter();
        Point p2Point = p2.getCenter();
        //如果在同一行
        if (p1.getIndexY() == p2.getIndexY()) {

            //同一行可以相连
            if (!isXBlock(p1Point, p2Point, GameConf.PIECE_WIDTH)) {
                return new LinkInfo(p1Point, p2Point);
            }
        }
        //如果两个在同一列
        if (p1.getIndexX() == p2.getIndexY()) {
            if (!isYBlock(p1Point, p2Point, GameConf.PIECE_HEIGHT)) {
                //他们之间没有直接障碍，没有转折点
                return new LinkInfo(p1Point, p2Point);
            }
        }

        //有一个转折点
        //获取两个点的直角相连的点
        Point cornerPoint = getCornerPoint(p1Point, p2Point
                , GameConf.PIECE_WIDTH, GameConf.PIECE_HEIGHT);

        if (cornerPoint != null) {
            return new LinkInfo(p1Point, cornerPoint, p2Point);
        }

        //该map的key存放第一个转折点
        //value存放第二个转折点
        //map 的size 说明有多少种可以连的方式
        Map<Point, Point> turns = getLinkPoints(p1Point, p2Point
                , GameConf.PIECE_HEIGHT, GameConf.PIECE_WIDTH);
        if (turns.size() != 0) {
            return getShortcut(p1Point, p2Point, turns, getDistance(p1Point, p2Point));
        }
        return null;
    }

    /**
     * 获取坐标点四周通道的四个方法
     * 给一个Point对象，返回它的左通道
     * <p>
     * p
     * pieceWidth piece图片的宽
     * min 向左遍历时的最小界限
     * return 给定Point的左边通道
     */
    private List<Point> getLeftChanel(Point p, int min, int pieceWidth) {
        List<Point> result = new ArrayList<>();
        //获取向左通道，由一个点向左遍历，步长为piece图片的宽
        for (int i = p.x - pieceWidth; i >= min; i = i - pieceWidth) {
            //遇到障碍，表示通道已经到尽头，直接返回
            if (hasPiece(i, p.y)) {
                return result;
            }
            result.add(new Point(i, p.y));
        }
        return result;
    }


    private List<Point> getRightChanel(Point p, int max, int pieceWidth) {
        List<Point> result = new ArrayList<>();
        //获取向右通道
        for (int i = p.x + pieceWidth; i <= max; i = i + pieceWidth) {

            if (hasPiece(i, p.y)) {
                return result;
            }
            result.add(new Point(i, p.y));
        }
        return result;
    }


    private List<Point> getUpChanel(Point p, int min, int pieceHeight) {
        List<Point> result = new ArrayList<>();

        for (int i = p.y - pieceHeight; i >= min; i = i - pieceHeight) {
            if (hasPiece(p.x, i)) {
                return result;
            }
            result.add(new Point(p.x, i));
        }
        return result;
    }

    private List<Point> getDownChanel(Point p, int max, int pieceHeight) {
        List<Point> result = new ArrayList<>();
        for (int i = p.y + pieceHeight; i <= max; i++) {
            if (hasPiece(p.x, i)) {
                return result;
            }
            result.add(new Point(p.x, i));
        }
        return result;
    }

    /**
     * 判断两个Y坐标相同的点对象之间是否有障碍，以p1为中心向右遍历
     *
     * @param p1
     * @param p2
     * @param pieceWidth
     * @return
     */
    private boolean isXBlock(Point p1, Point p2, int pieceWidth) {
        if (p2.x < p1.x) {
            return isXBlock(p2, p1, pieceWidth);
        }
        if (p1.x + pieceWidth == p2.x)
            return true;
        for (int i = p1.x + pieceWidth; i < p2.x; i = i + pieceWidth) {
            if (hasPiece(i, p1.y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断两个X坐标相同的点对象之间是否存在障碍，以p1为中心向下遍历
     *
     * @param p1
     * @param p2
     * @param pieceHeight
     * @return
     */
    private boolean isYBlock(Point p1, Point p2, int pieceHeight) {
        if (p2.y < p1.y) {
            return isYBlock(p2, p1, pieceHeight);
        }
        if (p1.y + pieceHeight == p2.y)
            return true;

        for (int i = p1.y + pieceHeight; i < p2.y; i = i + pieceHeight) {
            if (hasPiece(p1.x, i)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 遍历两个通道，获得他们的交点
     *
     * @param p1Chanel
     * @param p2Chanel
     * @return
     */
    private Point getWrapPoint(List<Point> p1Chanel, List<Point> p2Chanel) {
        for (int i = 0; i < p1Chanel.size(); i++) {
            Point temp1 = p1Chanel.get(i);
            for (int j = 0; j < p2Chanel.size(); j++) {
                Point temp2 = p2Chanel.get(j);
                if (temp1.equals(temp2)) {
                    return temp1;
                }
            }
        }
        return null;
    }

    /**
     * 获取两个不在同一行或者同一列的坐标点的直角连接点，即只有一个转折点
     *
     * @param point1
     * @param point2
     * @param pieceWidth
     * @param pieceHeight
     * @return
     */
    private Point getCornerPoint(Point point1, Point point2, int pieceWidth, int pieceHeight) {

        //先判断这两个点的位置
        //p2在p1的左上角,p2在p1的左下角
        if (isLeftUp(point1, point2) || isLeftDown(point1, point2)) {
            return getCornerPoint(point2, point1, pieceWidth, pieceHeight);
        }

        //获取p1 向右，向上，向下的三个通道
        List<Point> point1RightChanel = getRightChanel(point1, point2.x, pieceWidth);
        List<Point> point1UpChanel = getUpChanel(point1, point2.y, pieceHeight);
        List<Point> point1DownChanel = getDownChanel(point1, point2.y, pieceHeight);

        //获取p2 向下，向左，向上的三个通道
        List<Point> point2DownChanel = getDownChanel(point2, point1.y, pieceHeight);
        List<Point> point2LeftChanel = getLeftChanel(point2, point1.x, pieceWidth);
        List<Point> point2UpChanel = getUpChanel(point2, point1.y, pieceHeight);

        if (isRightUp(point1, point2)) {
            //p2 在p1右上
            //获取p1向右 和p2向下
            Point linkPoint1 = getWrapPoint(point1RightChanel, point2DownChanel);

            //获取p1向上 和 p2 向左的交点
            Point linkPoint2 = getWrapPoint(point1UpChanel, point2LeftChanel);
            //返回其中一个交点
            return (linkPoint1 == null) ? linkPoint2 : linkPoint1;
        }

        if (isRightDown(point1, point2)) {
            //p2 在p1右下
            //获取p1向下 和p2向左
            Point linkPoint1 = getWrapPoint(point1DownChanel, point2LeftChanel);

            //获取p1向右 和 p2 向上的交点
            Point linkPoint2 = getWrapPoint(point1RightChanel, point2UpChanel);
            //返回其中一个交点
            return (linkPoint1 == null) ? linkPoint2 : linkPoint1;
        }
        return null;
    }

    /**
     * p2 在p1左上角 返回true,否则false
     *
     * @param point1
     * @param point2
     * @return
     */
    private boolean isLeftUp(Point point1, Point point2) {

        return (point2.x < point1.x && point2.y > point1.y);
    }

    private boolean isLeftDown(Point point1, Point point2) {
        return (point2.x < point1.x && point2.y < point1.y);
    }

    /**
     * 右上
     *
     * @param point1
     * @param point2
     * @return
     */
    private boolean isRightUp(Point point1, Point point2) {

        return (point2.x > point1.x && point2.y > point1.y);
    }

    /**
     * 右下
     *
     * @param point1
     * @param point2
     * @return
     */
    private boolean isRightDown(Point point1, Point point2) {
        return (point2.x > point1.x && point2.y < point1.y);
    }

    /**
     * 获取两个转折点的情况
     *
     * @param point1
     * @param point2
     * @param pieceWidth
     * @param pieceHeight
     * @return
     */

    private Map<Point, Point> getLinkPoints(Point point1, Point point2
            , int pieceWidth, int pieceHeight) {
        Map<Point, Point> result = new HashMap<>();
        List<Point> p1UpChanel = getUpChanel(point1, point2.y, pieceHeight);
        List<Point> p1RightChanel = getRightChanel(point1, point2.x, pieceWidth);
        List<Point> p1DownChanel = getDownChanel(point1, point2.y, pieceHeight);


        List<Point> p2DownChanel = getDownChanel(point2, point1.y, pieceHeight);
        List<Point> p2LeftChanel = getLeftChanel(point2, point1.x, pieceWidth);
        List<Point> p2UpChanel = getUpChanel(point2, point1.y, pieceHeight);

        //Board 有方块的最大高度 和 宽度
        int heightMax = (this.config.getySize() + 1) * pieceHeight +
                this.config.getBeginImageY();
        int widthMax = (this.config.getxSize() + 1) * pieceWidth
                + this.config.getBeginImageX();

        //确定两个点的关系
        //point2 在point1 的左上 或者左下
        if (isLeftUp(point1, point2) || isLeftDown(point1, point2)) {
            return getLinkPoints(point2, point1, pieceWidth, pieceHeight);
        }

        // p1 、 p2 位于同一行不能直接相连
        if (point1.y == point2.y) {
            /**
             * 在同一行，向上遍历
             * 以p1的中心点向上遍历获取点集合
             */
            p1UpChanel = getUpChanel(point1, 0, pieceHeight);
            p2UpChanel = getUpChanel(point2, 0, pieceHeight);

            Map<Point, Point> upLinkPoints = getXLinkPoints(p1UpChanel, p2UpChanel, pieceHeight);

            //向下遍历 不超过Board(方块)的边框
            p1DownChanel = getDownChanel(point1, heightMax, pieceHeight);
            p2DownChanel = getDownChanel(point2, heightMax, pieceHeight);

            Map<Point, Point> downLinkPoints = getXLinkPoints(p1DownChanel, p2DownChanel, pieceHeight);

            result.putAll(upLinkPoints);
            result.putAll(downLinkPoints);
        }

        /**
         * 同一列不能直接相连
         */
        if (point1.x == point2.x) {
            /**
             * 在同一列，向左遍历
             * 以p1的中心点向左遍历获取点集合
             */
            List<Point> p1LeftChanel = getLeftChanel(point1, 0, pieceWidth);
            p2LeftChanel = getLeftChanel(point2, 0, pieceWidth);

            Map<Point, Point> leftLinkPoints = getXLinkPoints(p1LeftChanel, p2LeftChanel, pieceWidth);

            //向右遍历 不超过Board(方块)的边框
            p1RightChanel = getRightChanel(point1, widthMax, pieceWidth);
            List<Point> p2RightChanel = getRightChanel(point2, widthMax, pieceWidth);

            Map<Point, Point> rightLinkPoints = getXLinkPoints(p1RightChanel, p2RightChanel, pieceWidth);

            result.putAll(leftLinkPoints);
            result.putAll(rightLinkPoints);
        }

        //ponit 2位于point 1的右上角
        if (isRightUp(point1, point2)) {

            //获取point1向上遍历，point2向下遍历时横向可以连接的点。
            Map<Point, Point> upDownLinkPoints = getXLinkPoints(p1UpChanel, p2DownChanel, pieceWidth);
            //获取point1向右遍历，point2向左遍历时纵向可以连接的点
            Map<Point, Point> rightLeftLinkPoints = getYLinkPoints(p1RightChanel, p2LeftChanel, pieceHeight);

            //获取以p1为中心的向上通道
            p1UpChanel = getUpChanel(point1, 0, pieceHeight);
            //获取以p2为中心的向上通道
            p2UpChanel = getUpChanel(point2, 0, pieceHeight);
            //获取p1向上遍历，p2向上遍历可以横向连接的点
            Map<Point, Point> upUpLinkPoints = getXLinkPoints(p1UpChanel, p2UpChanel, pieceWidth);

            //获取p1为中心向下通道
            p1DownChanel = getDownChanel(point1, heightMax, pieceHeight);
            p2DownChanel = getDownChanel(point2, heightMax, pieceHeight);
            //获取p1向下遍历，p2向下遍历可以横向连接的点

            Map<Point ,Point> downDownLinkPoints = getXLinkPoints(p1DownChanel,p2DownChanel,pieceWidth);

            //获取以p1为中心的向右通道
            p1RightChanel = getRightChanel(point1, widthMax, pieceWidth);
            //获取以p2为中心的向右通道
            List<Point> p2RightChanel = getRightChanel(point2, widthMax, pieceWidth);
            //获取p1向右遍历，p2向右遍历可纵向连接的点
            Map<Point, Point> rightRightLinkPoints = getYLinkPoints(p1RightChanel, p2RightChanel, pieceHeight);

            //获取以p1为中心的向左通道
            List<Point> p1LeftChanel = getLeftChanel(point1, 0, pieceWidth);
            //获取以p2为中心的向左通道
            p2LeftChanel = getLeftChanel(point2, 0, pieceWidth);
            //获取p1向左遍历，p2向左遍历可以纵向连接的点
            Map<Point, Point> leftLefetLinkPoints = getYLinkPoints(p1LeftChanel, p2LeftChanel, pieceHeight);

            result.putAll(upDownLinkPoints);
            result.putAll(rightLeftLinkPoints);
            result.putAll(upUpLinkPoints);
            result.putAll(downDownLinkPoints);
            result.putAll(rightRightLinkPoints);
            result.putAll(leftLefetLinkPoints);
        }

        //ponit 2位于point 1的右下角
        if (isRightDown(point1, point2)) {

            //获取point1向上遍历，point2向下遍历时横向可以连接的点。
            Map<Point, Point> upDownLinkPoints = getXLinkPoints(p1UpChanel, p2DownChanel, pieceWidth);
            //获取point1向右遍历，point2向左遍历时纵向可以连接的点
            Map<Point, Point> rightLeftLinkPoints = getYLinkPoints(p1RightChanel, p2LeftChanel, pieceHeight);

            //获取以p1为中心的向上通道
            p1UpChanel = getUpChanel(point1, 0, pieceHeight);
            //获取以p2为中心的向上通道
            p2UpChanel = getUpChanel(point2, 0, pieceHeight);
            //获取p1向上遍历，p2向上遍历可以横向连接的点
            Map<Point, Point> upUpLinkPoints = getXLinkPoints(p1UpChanel, p2UpChanel, pieceWidth);

            //获取p1为中心向下通道
            p1DownChanel = getDownChanel(point1, heightMax, pieceHeight);
            p2DownChanel = getDownChanel(point2, heightMax, pieceHeight);
            //获取p1向下遍历，p2向下遍历可以横向连接的点

            Map<Point ,Point> downDownLinkPoints = getXLinkPoints(p1DownChanel,p2DownChanel,pieceWidth);

            //获取以p1为中心的向右通道
            p1RightChanel = getRightChanel(point1, widthMax, pieceWidth);
            //获取以p2为中心的向右通道
            List<Point> p2RightChanel = getRightChanel(point2, widthMax, pieceWidth);
            //获取p1向右遍历，p2向右遍历可纵向连接的点
            Map<Point, Point> rightRightLinkPoints = getYLinkPoints(p1RightChanel, p2RightChanel, pieceHeight);

            //获取以p1为中心的向左通道
            List<Point> p1LeftChanel = getLeftChanel(point1, 0, pieceWidth);
            //获取以p2为中心的向左通道
            p2LeftChanel = getLeftChanel(point2, 0, pieceWidth);
            //获取p1向左遍历，p2向左遍历可以纵向连接的点
            Map<Point, Point> leftLefetLinkPoints = getYLinkPoints(p1LeftChanel, p2LeftChanel, pieceHeight);

            result.putAll(upDownLinkPoints);
            result.putAll(rightLeftLinkPoints);
            result.putAll(upUpLinkPoints);
            result.putAll(downDownLinkPoints);
            result.putAll(rightRightLinkPoints);
            result.putAll(leftLefetLinkPoints);
        }
        return result;
    }

    /**
     * 遍历两个集合，先判断第一个集合的元素 与 第二个集合的元素x坐标相同
     * 如果相同在同一列，再判断是否有障碍，如果没有障碍则加入Map
     *
     * @param p1Chanel
     * @param p2Chanel
     * @param pieceHeight
     * @return
     */

    private Map<Point, Point> getYLinkPoints(List<Point> p1Chanel, List<Point> p2Chanel
            , int pieceHeight) {
        Map<Point, Point> result = new HashMap<>();
        for (int i = 0; i < p1Chanel.size(); i++) {
            Point temp1 = p1Chanel.get(i);
            for (int j = 0; j < p2Chanel.size(); j++) {
                Point temp2 = p2Chanel.get(j);
                if (temp1.x == temp2.x) {
                    if (!isYBlock(temp1, temp2, pieceHeight)) {
                        result.put(temp1, temp2);
                    }
                }
            }
        }
        return result;
    }


    /**
     * 遍历两个集合，先判断第一个集合的元素的y坐标与另一个集合中元素的
     * y坐标相同（横向），如果相同，在同一行，再判断是否有障碍，没有就加到map中去
     *
     * @param p1Chanel
     * @param p2Chanel
     * @param pieceWidth
     * @return
     */
    private Map<Point, Point> getXLinkPoints(List<Point> p1Chanel
            , List<Point> p2Chanel, int pieceWidth) {

        Map<Point, Point> result = new HashMap<>();
        for (int i = 0; i < p1Chanel.size(); i++) {
            Point temp1 = p1Chanel.get(i);
            for (int j = 0; j < p2Chanel.size(); j++) {
                Point temp2 = p2Chanel.get(j);
                if (temp1.y == temp2.y) {
                    if (!isXBlock(temp1, temp2, pieceWidth))
                        result.put(temp1, temp2);
                }
            }
        }
        return result;
    }


    /**
     * 获取p1 和 p2 的最短连接
     *
     * @param p1
     * @param p2
     * @param turns
     * @param shortDistance
     * @return
     */
    private LinkInfo getShortcut(Point p1, Point p2, Map<Point, Point> turns, int shortDistance) {
        List<LinkInfo> infos = new ArrayList<>();
        //遍历结果Map
        for (Point point1 : turns.keySet()) {
            Point point2 = turns.get(point1);
            //将转折点与选择点封装成LinkInfo(p1,point1,point2,p2)
            infos.add(new LinkInfo(p1, point1, point2, p2));
        }
        return getShortcut(infos, shortDistance);
    }

    /**
     * 从infos 中获取连接线最短的哪个LinkInfo
     *
     * @param infos
     * @param shortDistance
     * @return
     */
    private LinkInfo getShortcut(List<LinkInfo> infos, int shortDistance) {
        int temp1 = 0;
        LinkInfo result = null;
        for (int i = 0; i < infos.size(); i++) {
            LinkInfo info = infos.get(i);
            //计算出几个点的总距离
            int distance = countAll(info.getLinkPoints());
            //将循环第一个的差距用temp1保存
            if (i == 0) {
                temp1 = distance - shortDistance;
                result = info;
            }

            //如果下一次循环的值比temp1还小，则用当前的值作为temp1
            if (distance - shortDistance < temp1) {
                temp1 = distance - shortDistance;
                result = info;
            }
        }
        return result;
    }

    /**
     * 计算List<Point>中所有点的距离总和
     *
     * @param points 需要计算的连接点
     * @return 所有点的距离总和
     */
    private int countAll(List<Point> points) {
        int result = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            Point point1 = points.get(i);
            Point point2 = points.get(i + 1);
            result += getDistance(point1, point2);
        }
        return result;
    }

    /**
     * 获取两个LinkPoint之间的最短距离
     *
     * @param p1 第一个点
     * @param p2 第二个点
     * @return 两个点的距离总和
     */
    private int getDistance(Point p1, Point p2) {
        int xDistance = Math.abs(p1.x - p2.x);
        int yDistance = Math.abs(p1.y - p2.y);
        return xDistance + yDistance;
    }
}
