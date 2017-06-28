package floatingmuseum.hundredmeters;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;

import java.util.List;

import floatingmuseum.hundredmeters.entities.RemoteMessage;
import floatingmuseum.hundredmeters.entities.RemoteUser;
import floatingmuseum.hundredmeters.entities.User;
import floatingmuseum.hundredmeters.utils.NicknameUtil;
import floatingmuseum.hundredmeters.utils.ResUtil;

/**
 * Created by Floatingmuseum on 2017/6/24.
 */

public class MessageAdapter extends BaseQuickAdapter<RemoteMessage, BaseViewHolder> {

    public MessageAdapter(@Nullable List<RemoteMessage> data) {
        super(R.layout.layout_message_text, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RemoteMessage item) {
        User user = item.getUser();
        String name = NicknameUtil.getSimpleName(user.getNickname());
        helper.setText(R.id.tv_nickname, ">" + name + ": ")
                .setText(R.id.tv_message, item.getMessage())
                .setTextColor(R.id.tv_nickname, "Bot-Y".equals(name) ? ResUtil.getColor(R.color.text_dark_green) : ResUtil.getColor(R.color.text_light_green))
                .setTextColor(R.id.tv_message, "Bot-Y".equals(name) ? ResUtil.getColor(R.color.text_dark_green) : ResUtil.getColor(R.color.text_light_green));
    }
}
