package lenovo.bdtongxin.UI;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 可根据字符串所占的长度（非字符串长度）自动缩小字体大小， 以适应显示区域的宽度
 */
public class CustomTextView extends TextView {

	private Paint testPaint;
	private float minTextSize, maxTextSize;

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialise();
	}

	private void initialise() {
		testPaint = new Paint();
		testPaint.set(this.getPaint());

		maxTextSize = this.getTextSize();

	};

	/**
	 * Re size the font so the specified text fits in the text box * assuming
	 * the text box is the specified width.
	 */
	private void refitText(String text, int textWidth) {
		if (textWidth > 0) {
			int availableWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();////获得当前TextView的有效宽度
			float trySize = maxTextSize;
			while ((trySize > minTextSize) && (testPaint.measureText(text) > availableWidth)) {
				trySize -= 1;
				if (trySize <= minTextSize) {
					trySize = minTextSize;
					break;
				}
			}
		}
	};

	@Override
	protected void onTextChanged(CharSequence text, int start, int before,
			int after) {
		super.onTextChanged(text, start, before, after);
		refitText(text.toString(), this.getWidth());
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w != oldw) {
			refitText(this.getText().toString(), w);
		}
	}
}
