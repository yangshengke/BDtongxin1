package lenovo.bdtongxin.Status;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedHashMap;

public class HistogramView extends View {
    /**
     * 第一步：声明画笔
     */
    private Paint mPaint_X;//X坐标轴画笔
    private Paint mPaint_Y;//Y坐标轴画笔
    private Paint mPaint_InsideLine;//内部虚线P
    private Paint mPaint_Text;//字体画笔（用于X和Y轴的刻度值）
    private Paint mPaint_TextForXY;//字体画笔（用于X和Y轴的说明）
    private Paint mPaint_Rec1;//矩形画笔
    private Paint mPaint_Rec2;
    private Paint mPaint_Rec3;
    private Paint mPaint_Rec4;
    private Paint mPaint_Rec5;

    //数据
    private int[] data;//信号强度
    //视图的宽高
    private int width;
    private int height;


    //坐标轴数据
    private String[] mText_Y;// Y刻度值数据

    private String[] mText_X = new String[]{"1", "2", "3", "4", "5", "6"};//默认X轴坐标
//    private String[] mText_X;// X轴坐标

    public HistogramView(Context context) {
        super(context);
        init(context, null);
    }

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void upDataTextForXY(LinkedHashMap data) {
        for (int i = 1; i <= 6; i++) {
            if (data.get(i) == null) {
                data.put(i, 0);
            }
        }
        int[] signal = new int[6];
        for (int i = 0; i < 6; i++) {
            signal[i] = (int) data.get(i + 1);
        }
        this.data = signal;//信号强度
        mText_Y = getText_Y(this.data);
//        Object[] objects2 = data.keySet().toArray();
//        String[] num = new String[objects2.length];
//        for (int i = 0; i < num.length; i++) {
//            num[i] = objects2[i].toString();
//            mText_X = num;//卫星编号
//        }
        this.postInvalidate();  //更新视图
    }

    /**
     * 初始化画笔
     */
    private void init(Context context, AttributeSet attrs) {
        mPaint_X = new Paint();
        mPaint_InsideLine = new Paint();
        mPaint_Text = new Paint();
        mPaint_TextForXY = new Paint();
        mPaint_Rec1 = new Paint();
        mPaint_Rec2 = new Paint();
        mPaint_Rec3 = new Paint();
        mPaint_Rec4 = new Paint();
        mPaint_Rec5 = new Paint();
        mPaint_Y = new Paint();


        mPaint_X.setColor(Color.LTGRAY);//深灰色
//        mPaint_X.setStrokeWidth(5);//设置线宽

        mPaint_Y.setColor(Color.LTGRAY);//深灰色

        mPaint_InsideLine.setColor(Color.GRAY);//灰色
        mPaint_InsideLine.setAntiAlias(true);//防止边缘的锯齿

        mPaint_Text.setTextSize(25);
        mPaint_Text.setTextAlign(Paint.Align.CENTER);
        mPaint_Text.setColor(Color.WHITE);

        mPaint_TextForXY.setTextSize(15);
        mPaint_TextForXY.setTextAlign(Paint.Align.CENTER);
        mPaint_TextForXY.setColor(Color.WHITE);
        mPaint_TextForXY.setAntiAlias(true);//防止边缘的锯齿

        mPaint_Rec1.setColor(Color.RED);//红
        mPaint_Rec2.setColor(Color.rgb(255, 97, 0));//橙
        mPaint_Rec3.setColor(Color.YELLOW);//黄
        mPaint_Rec4.setColor(Color.GREEN);//绿
        mPaint_Rec5.setColor(Color.CYAN);//青
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();//获取此view的宽度
        height = getHeight();//获取此view的高度
        int leftHeight_Every = (height - 50) / 4; //Y轴每个数据的间距
        int downWeight_Every = (width - 50) / mText_X.length;//X轴每个数据的间距
//        int downWeight_Every = 50;

        //画X坐标轴-横轴
        canvas.drawLine(50, height - 50, width, height - 50, mPaint_X);//使用当前颜色在点 (startX, startY) 和 (stopX, stopY) 之间画一条线,是以小窗口左上角为参照点
        //画Y坐标轴-数轴
        canvas.drawLine(50, height - 50, 50, 0, mPaint_Y);

        //画内部灰线
        for (int i = 1; i < 5; i++) {
            canvas.drawLine(50, height - 50 - (i * leftHeight_Every), width, height - 50 - (i * leftHeight_Every), mPaint_InsideLine);
        }
        //画X轴坐标上的数
        for (int i = 1; i < mText_X.length + 1; i++) {
            canvas.drawText(mText_X[i - 1], 100 + downWeight_Every * (i - 1), height - 25, mPaint_Text);
        }
        canvas.drawText("0", 35, height-40, mPaint_Text);//从上往下画刻度
        //画坐标轴说明
        canvas.drawText("北斗一代波束", (width - 50) / 2 + 30, height - 5, mPaint_TextForXY);
        canvas.rotate(-90, 15, height / 2 + 10);//绕着（15, height / 2 + 10）中心旋转-90度
        canvas.drawText("信号强度", 15, height / 2 + 10, mPaint_TextForXY);
        canvas.rotate(90, 15, height / 2 + 10);//重新把canvas转正过来 注意要与上面的坐标一致

        if (this.data != null && this.data.length > 0) {
            //画Y轴坐标
            for (int i = 1; i < mText_Y.length + 1; i++) {
                canvas.drawText(mText_Y[i - 1], 35, leftHeight_Every * (i - 1) + 10, mPaint_Text);//从上往下画刻度
            }
            //画矩形
            for (int i = 1; i < data.length + 1; i++) {
                int data_Y_One = Integer.parseInt(mText_Y[3]); //Y轴首个刻度数值，1900
                double data_Yx = (double) data[i - 1] / data_Y_One;
                RectF rect = new RectF();
                // 根据四个点构造出一个矩形，分别为上下左右边的位置
                rect.left = downWeight_Every * i - 30;
                rect.right = downWeight_Every * i + 30;
                rect.top = (height - 50 - (int) (data_Yx * leftHeight_Every));
                rect.bottom = height - 50;

                if (data[i - 1] <= 10) {
                    canvas.drawRoundRect(rect, 5, 5, mPaint_Rec1);//绘制圆角矩形，两个5分别为x和y方向上的圆角半径
                }
                if (10 < data[i - 1] && data[i - 1] <= 20) {
                    canvas.drawRoundRect(rect, 5, 5, mPaint_Rec2);
                }
                if (20 < data[i - 1] && data[i - 1] <= 30) {
                    canvas.drawRoundRect(rect, 5, 5, mPaint_Rec3);
                }
                if (30 < data[i - 1] && data[i - 1] <= 40) {
                    canvas.drawRoundRect(rect, 5, 5, mPaint_Rec4);
                }
                if (40 < data[i - 1]) {
                    canvas.drawRoundRect(rect, 5, 5, mPaint_Rec5);
                }
                //画矩形上的信号强度
                canvas.drawText(String.valueOf(data[i - 1]), 100 + downWeight_Every * (i - 1), (height - 40 - (int) (data_Yx * leftHeight_Every)) - 15, mPaint_Text);
            }
        }
    }


    /**
     * 获取一组数据的最大值
     */
    public static int getMax(int[] arr) {
        int max = arr[0];
        for (int x = 1; x < arr.length; x++) {
            if (arr[x] > max)
                max = arr[x];
        }
        return max;
    }

    /**
     * 功能：根据传入的数据动态的改变Y轴的坐标
     * 返回：取首数字的前两位并除以3，后面变成0。作为Y轴的基坐标
     */
    public static String[] getText_Y(int[] data) {
        String[] text_Y;
        int textY = 0;

        String numMax = getMax(data) + "";
        char[] charArray = numMax.toCharArray();//将字符串转换为字符数组
        int dataLength = charArray.length;//数据长度 eg：5684：4位
        String twoNumString = "";
        if (dataLength >= 2) {
            for (int i = 0; i < 2; i++) {
                twoNumString += charArray[i];//取了5和6两位，字符串形式
            }
            int twoNum = Integer.parseInt(twoNumString);//得到：56
            textY = (int) Math.ceil(twoNum / 3);//math.ceil(x)返回大于参数x的最小整数，得到 19
            //将数据前两位后加上“0” 用于返回前两位的整数
            if (dataLength - 2 == 1) {
                textY *= 10;
            } else if (dataLength - 2 == 2) {
                textY *= 100;//textY=19*100=1900
            } else if (dataLength - 2 == 3) {
                textY *= 1000;
            } else if (dataLength - 2 == 4) {
                textY *= 10000;
            } else if (dataLength - 2 == 5) {
                textY *= 100000;
            }
            //四个数的数组：  ;5700 ;3800 ;1900 ;  第一个为空，即Y坐标刻度为空；第二个坐标刻度为5700，以此类推
            text_Y = new String[]{"", textY * 3 + "", textY * 2 + "", textY + ""};
        } else {
            text_Y = new String[]{"", 15 + "", 10 + "", 5 + ""};
        }
        return text_Y;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

//        if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) {
//            width = widthSpecSize;
//        } else {
//            width = 0;
//        }
        if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) {
            width = widthSpecSize;
        } else {
            width = 0;
        }
        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.EXACTLY) {
            height = heightSpecSize;
        }
// else {
//            height = heightSpecSize;
//        }
        setMeasuredDimension(width, height);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param dip
     * @return
     */
    private int dipToPx(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

}


