package com.android.jwjy.wtjyproduct;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by dayuer on 19/7/2.
 * 课程包结构体
 */
public class CoursePacketInfo {
    String mCoursePacketId = "";//课程包id
    String mCoursePacketCover = "";//课程包封面
    String mCoursePacketName = "";//课程包名称
    String mCoursePacketStageNum = "";//课程包阶段数量
    String mCoursePacketCourseNum = "";//课程包课程数量
    String mCoursePacketLearnPersonNum = "";//课程包学习人数
    String mCoursePacketPrice = "";//课程包价格
    String mCoursePacketPriceOld = "";//课程包原价格
    String mCoursePacketMessage = "";//课程包描述
    String mCoursePacketDetails = "";//课程包详情
    String mCoursePacketIsHave = "0";//课程包是否已经购买(0:没买 1:买了)
    List<StageCourseInfo> mStageCourseInfoList = new ArrayList<>();//课程包包含的课程阶段
    List<TeacherInfo> mTeacherInfoList = new ArrayList<>();//课程包包含的师资
    public CoursePacketInfo(){

    }
    public CoursePacketInfo(CoursePacketInfo coursePacketInfo){
        mCoursePacketId = coursePacketInfo.mCoursePacketId;
        mCoursePacketCover = coursePacketInfo.mCoursePacketCover;
        mCoursePacketName = coursePacketInfo.mCoursePacketName;
        mCoursePacketStageNum = coursePacketInfo.mCoursePacketStageNum;//课程包阶段数量
        mCoursePacketCourseNum = coursePacketInfo.mCoursePacketCourseNum;//课程包课程数量
        mCoursePacketLearnPersonNum = coursePacketInfo.mCoursePacketLearnPersonNum;//课程包学习人数
        mCoursePacketPrice = coursePacketInfo.mCoursePacketPrice;//课程包价格
        mCoursePacketPriceOld = coursePacketInfo.mCoursePacketPriceOld;//课程包原价格
        mCoursePacketMessage = coursePacketInfo.mCoursePacketMessage;//课程包描述
        mCoursePacketDetails = coursePacketInfo.mCoursePacketDetails;//课程包详情
        mStageCourseInfoList.addAll(coursePacketInfo.mStageCourseInfoList);//课程包包含的课程阶段
        mTeacherInfoList.addAll(coursePacketInfo.mTeacherInfoList);
    }
}
