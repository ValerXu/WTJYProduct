package com.android.jwjy.wtjyproduct.interfaces;


import com.talkfun.sdk.module.ChapterEntity;

import java.util.List;

/**
 */
public interface IDispatchChapter {
    void getChapterList(List<ChapterEntity> chapterEntityList);

    void switchToChapter();

}
