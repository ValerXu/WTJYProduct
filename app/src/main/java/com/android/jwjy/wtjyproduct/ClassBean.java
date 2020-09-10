package com.android.jwjy.wtjyproduct;

import java.io.Serializable;
import java.util.Date;

/**
 * 课程表的bean
 * Created by dayuer on 2019/12/4.
 */
@SuppressWarnings("all")
public class ClassBean implements Serializable {
    private int id;
    private String className;  //课程名称
    private String classTime;   //上课时间
    private String classData;   //上课日期
    private String classTeacher;    //上课教师
    private String classTeacherImg;     //上课教师头像
    private Integer course_times_id;    //课时id
    private String status;              //课程状态
    private Date begin_class_date;      //课程开始时间
    private Date end_time_datess;       //课程结束时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getClassData() {
        return classData;
    }

    public void setClassData(String classData) {
        this.classData = classData;
    }

    public String getClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(String classTeacher) {
        this.classTeacher = classTeacher;
    }

    public String getClassTeacherImg() {
        return classTeacherImg;
    }

    public void setClassTeacherImg(String classTeacherImg) {
        this.classTeacherImg = classTeacherImg;
    }

    public Integer getCourse_times_id() {
        return course_times_id;
    }

    public void setCourse_times_id(Integer course_times_id) {
        this.course_times_id = course_times_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getBegin_class_date() {
        return begin_class_date;
    }

    public void setBegin_class_date(Date begin_class_date) {
        this.begin_class_date = begin_class_date;
    }

    public Date getEnd_time_datess() {
        return end_time_datess;
    }

    public void setEnd_time_datess(Date end_time_datess) {
        this.end_time_datess = end_time_datess;
    }
}
