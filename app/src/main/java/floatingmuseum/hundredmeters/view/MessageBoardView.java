package floatingmuseum.hundredmeters.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.orhanobut.logger.Logger;

import floatingmuseum.hundredmeters.R;

/**
 * Created by Floatingmuseum on 2017/6/24.
 */

public class MessageBoardView extends RelativeLayout {

    public MessageBoardView(Context context) {
        super(context);
    }

    public MessageBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Logger.d("MessageBoardView...onSizeChanged...width:" + w + "...height:" + h + "...oldWidth:" + oldw + "...oldHeight:" + oldh);
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
