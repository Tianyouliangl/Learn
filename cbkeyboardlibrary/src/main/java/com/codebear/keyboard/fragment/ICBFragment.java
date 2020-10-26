package com.codebear.keyboard.fragment;


import androidx.fragment.app.Fragment;

import com.codebear.keyboard.data.EmoticonsBean;
import com.codebear.keyboard.widget.CBEmoticonsView;
import com.learn.commonalitylibrary.body.GifBean;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/6/30.
 */

public interface ICBFragment {
    void setSeeItem(int which);
    Fragment getFragment();
    void setEmoticonsBean(EmoticonsBean emoticonsBean);
    void setLikePhotoBean(GifBean gifBean);
    void setOnEmoticonClickListener(CBEmoticonsView.OnEmoticonClickListener listener);
}
