package east.orientation.microlesson.local.adapter;

import android.content.Context;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import east.orientation.microlesson.local.model.LocalVoice;

/**
 * @author ljq
 * @date 2018/12/25
 * @description
 */

public class MakeVoiceAdapter extends MultiItemTypeAdapter<LocalVoice> {


    public MakeVoiceAdapter(Context context, List<LocalVoice> datas) {
        super(context, datas);

        addItemViewDelegate(new VoiceItemViewDelegate());
        addItemViewDelegate(new VoiceTapItemViewDelegate());
    }
}
