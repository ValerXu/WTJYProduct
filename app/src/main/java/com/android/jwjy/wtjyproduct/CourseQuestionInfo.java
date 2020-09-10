package com.android.jwjy.wtjyproduct;
/**
 * Created by dayuer on 19/7/2.
 * 课程问答结构体
 */
public class CourseQuestionInfo {
    String mCourseQuestionId = "";//课程问答id
    String mCourseAnswerId = "";//课程问答(回答的主问题id)(为0的时候：一级提问)
    String mCourseQuestionCommentId1 = "";//提问者id(为0的时候：一级提问)
    String mCourseQuestionCommentName1 = "";//提问者名字
    String mCourseQuestionCommentHead1 = "";//提问者头像
    String mCourseQuestionCommentId2 = "";//回答者id
    String mCourseQuestionCommentName2 = "";//回答者名字
    String mCourseQuestionCommentHead2 = "";//回答者头像
    String mCourseQuestionTime = "";//课程问答时间
    String mCourseQuestionContent = "";//课程问答内容
    String mCourseQuestionImage = "";//课程问答图片（中间用，分割）
    String mCourseQuestionLookNum = "";//课程问答浏览人数
    public CourseQuestionInfo(){

    }
    public CourseQuestionInfo(CourseQuestionInfo courseQuestionInfo){
        mCourseQuestionId = courseQuestionInfo.mCourseQuestionId;//课程问答id
        mCourseAnswerId = courseQuestionInfo.mCourseAnswerId;//课程问答(回答的主问题id)
        mCourseQuestionCommentId1 = courseQuestionInfo.mCourseQuestionCommentId1;//提问者id(为0的时候：一级提问)
        mCourseQuestionCommentName1 = courseQuestionInfo.mCourseQuestionCommentName1;//提问者名字
        mCourseQuestionCommentHead1 = courseQuestionInfo.mCourseQuestionCommentHead1;//提问者头像
        mCourseQuestionCommentId2 = courseQuestionInfo.mCourseQuestionCommentId2;//回答者id
        mCourseQuestionCommentName2 = courseQuestionInfo.mCourseQuestionCommentName2;//回答者名字
        mCourseQuestionCommentHead2 = courseQuestionInfo.mCourseQuestionCommentHead2;//回答者头像
        mCourseQuestionTime = courseQuestionInfo.mCourseQuestionTime;//课程问答时间
        mCourseQuestionContent = courseQuestionInfo.mCourseQuestionContent;//课程问答内容
        mCourseQuestionImage = courseQuestionInfo.mCourseQuestionImage;//课程问答图片（中间用，分割）
        mCourseQuestionLookNum = courseQuestionInfo.mCourseQuestionLookNum;//课程问答浏览人数
    }
}
