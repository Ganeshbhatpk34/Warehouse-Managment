package loading.miscell.flyingcross;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by chenjishi on 15/3/24.
 */
public class AnimateView extends AppCompatImageView{

    private loading.miscell.flyingcross.PathPoint point;

    public AnimateView(Context context) {
        super(context);
    }

    public AnimateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public loading.miscell.flyingcross.PathPoint getPosition() {
        return point;
    }

    public void setPosition(loading.miscell.flyingcross.PathPoint point) {
        this.point = point;
        setTranslationX(point.x);
        setTranslationY(point.y);
    }
}
