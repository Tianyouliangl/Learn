
package com.codebear.keyboard.emoji;

import com.codebear.keyboard.data.EmojiBean;

import java.util.HashMap;

/**
 * description:
 * <p>
 * 参照w446108264提供的XhsEmoticonsKeyboard开源键盘解决方案
 * github:https://github.com/w446108264/XhsEmoticonsKeyboard
 * <p>
 * Created by CodeBear on 2017/6/30.
 */

public class DefEmoticons {

    public static final HashMap<String, Integer> emojiMap = new HashMap<>();
    public static final EmojiBean[] sEmojiArray;

    public DefEmoticons() {
    }

    static {
        sEmojiArray = new EmojiBean[]{
//           new EmojiBean(R.mipmap.sy_emoji_0001, "[得意]")
        };

    }

    static {
        for(EmojiBean emojiBean : sEmojiArray) {
            emojiMap.put(emojiBean.emoji, emojiBean.icon);
        }
    }

}
