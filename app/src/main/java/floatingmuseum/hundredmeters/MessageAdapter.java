package floatingmuseum.hundredmeters;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import floatingmuseum.hundredmeters.entities.RemoteMessage;

/**
 * Created by Floatingmuseum on 2017/6/24.
 */

public class MessageAdapter extends BaseQuickAdapter<RemoteMessage, BaseViewHolder> {

    public MessageAdapter(@Nullable List<RemoteMessage> data) {
        super(R.layout.layout_message_text, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RemoteMessage item) {
        helper.setText(R.id.tv_nickname, ">" + item.getNickname() + ": ")
                .setText(R.id.tv_message, item.getMessage());
    }
}
