package lenovo.bdtongxin.SMS;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 不管是自定义View还是系统提供的TextView这些，它们都必须放置在LinearLayout等一些ViewGroup中，因此理论上我们可以很好的理解onMeasure()，onLayout()，onDraw()这三个函数：
 * 1.View本身大小多少，这由onMeasure()决定；2.View在ViewGroup中的位置如何，这由onLayout()决定；3.绘制View，onDraw()定义了如何绘制这个View。
 */
public class MyViewGroup extends ViewGroup {
    private final static int VIEW_MARGIN = 2;
    private int maxWidth = 0;
    private int maxHeight = 60;

    public MyViewGroup(Context context) {
        super(context);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//大小

        for (int index = 0; index < getChildCount(); index++) {
            final View child = getChildAt(index);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {//位置

        final int count = getChildCount();
        int row = 0;// which row lay you view relative to parent
        int lengthX = arg1;    // right position of child relative to parent　应该是left position吧？
        int lengthY = arg2;    // bottom position of child relative to parent 应该是top position吧？
        for (int i = 0; i < count; i++) {
            final View child = this.getChildAt(i);
            int width = child.getMeasuredWidth();//获取此view的宽度，注意与getWidth()区别
            // int height = child.getMeasuredHeight();
            int height = maxHeight; //限制子节点的高度
            lengthX += width + VIEW_MARGIN;

            lengthY = row * (height + VIEW_MARGIN) + VIEW_MARGIN + height + arg2;
            if (width + VIEW_MARGIN > maxWidth) {
                maxWidth = width + VIEW_MARGIN;
            }

            if (lengthX > arg3) {
                lengthX = width + VIEW_MARGIN + arg1;
                row++;
                lengthY = row * (height + VIEW_MARGIN) + VIEW_MARGIN + height + arg2;

            }
            child.layout(lengthX - width, lengthY - height, lengthX, lengthY);//设置显示位置：左、上、右、下边
        }
    }
}
