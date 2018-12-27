package east.orientation.microlesson.local.adapter;

import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import east.orientation.microlesson.R;
import east.orientation.microlesson.local.model.LocalVoice;

/**
 * @author ljq
 * @date 2018/12/25
 * @description
 */

public class VoiceItemViewDelegate implements ItemViewDelegate<LocalVoice> {

    @Override
    public int getItemViewLayoutId() {

        return R.layout.layout_voice_make_item;
    }

    @Override
    public boolean isForViewType(LocalVoice item, int position) {

        return !item.isPageFirst();
    }

    @Override
    public void convert(ViewHolder holder, LocalVoice o, int position) {

    }
}
