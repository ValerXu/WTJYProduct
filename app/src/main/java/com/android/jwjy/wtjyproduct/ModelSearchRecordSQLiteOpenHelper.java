package com.android.jwjy.wtjyproduct;

import android.content.Context;

import com.aliyun.svideo.common.PublicCommonUtil;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * Created by Carson_Ho on 17/8/10.
 */

// 继承自SQLiteOpenHelper数据库类的子类
public class ModelSearchRecordSQLiteOpenHelper extends SQLiteOpenHelper {

    private static String DB_NAME  = PublicCommonUtil.DB_NAME;
    private static Integer DB_VERSION  = PublicCommonUtil.DB_VERSION;
    private static ModelSearchRecordSQLiteOpenHelper instance = null;
    private static SQLiteDatabase db = null;
    private static String SECRET_KEY = PublicCommonUtil.SECRET_KEY;
    private static ControlMainActivity mControlMainActivity = null;

    private ModelSearchRecordSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static synchronized ModelSearchRecordSQLiteOpenHelper getInstance(Context context) {
        if (context != null){
            mControlMainActivity = (ControlMainActivity) context;
        }
        if (mControlMainActivity == null){
            return null;
        }
        if (instance == null) {
            SQLiteDatabase.loadLibs(mControlMainActivity);
            instance = new ModelSearchRecordSQLiteOpenHelper(mControlMainActivity, DB_NAME, null, DB_VERSION);
            db = instance.getWritableDatabase(SECRET_KEY);
        }
        return instance;
    }

    public static synchronized SQLiteDatabase getWritableDatabase(Context context) {
        if (context != null){
            mControlMainActivity = (ControlMainActivity) context;
        }
        if (db == null) {
            db = getInstance(mControlMainActivity).getWritableDatabase(SECRET_KEY);
        }
        return db;
    }

    public static synchronized SQLiteDatabase getReadableDatabase(Context context) {
        if (context != null){
            mControlMainActivity = (ControlMainActivity) context;
        }
        if (db == null) {
            db = getInstance(mControlMainActivity).getReadableDatabase(SECRET_KEY);
        }
        return db;
    }

    public static synchronized void closeDatabase() {
        if (db != null) {
            db.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 打开数据库 & 建立了一个叫coursepacketsearchrecords的表，里面只有一列name来存储历史记录：
        db.execSQL("CREATE TABLE IF NOT EXISTS  coursepacketsearchrecords(id integer primary key autoincrement,name varchar(200))");
        //建立了一个叫coursesearchrecords的表，里面只有一列name来存储历史记录：
        db.execSQL("CREATE TABLE IF NOT EXISTS  coursesearchrecords(id integer primary key autoincrement,name varchar(200))");
        //建立了一个叫communityanswersearchrecords的表，里面只有一列name来存储历史记录：
        db.execSQL("CREATE TABLE IF NOT EXISTS  communityanswersearchrecords(id integer primary key autoincrement,name varchar(200))");
        //建立了一个叫CommunityAnswerDraftBox的表，里面存储社区问答的草稿箱：
        db.execSQL("CREATE TABLE IF NOT EXISTS  communityanswerdraftbox(id integer primary key autoincrement,title varchar(100),content text,photospath text,sign text)");
        //创建题库
        db.execSQL("CREATE TABLE IF NOT EXISTS  `item_bank_edu` (" +
                "                  `item_bank_id` integer primary key autoincrement," +    //题库的唯一标识
                "                  `item_bank_name` varchar(64) DEFAULT NULL ," +           //题库的名字
                "                  `brief_introduction` varchar(128) DEFAULT NULL ," +      //题库介绍
                "                  `tf_enable` tinyint(4) DEFAULT NULL ," +                 //是否启用状态_1是_2_否_默认不启用
                "                 `tf_delete` tinyint(4) DEFAULT NULL ," +                  //是否删除_1是_2_否
                "                  `icon` varchar(128) DEFAULT NULL ," +                    //显示的图标路径
                "                  `project_id` int(11) DEFAULT NULL," +                    //绑定项目ID
                "                  `subject_id` int(11) DEFAULT NULL ," +                   //绑定科目ID(学科子类)
                "                  `creation_time` datetime DEFAULT NULL ," +               //创建当前时间
                "                  `founder_id` int(11) DEFAULT NULL " +                    //由谁创建
                "                )");
        //创建题库-子题库
        db.execSQL("CREATE TABLE IF NOT EXISTS `sub_library_edu` (" +
                "                 `ibs_id` integer NOT NULL primary key   AUTOINCREMENT ," +        //子题库唯一标识
                "                `ibs_name` varchar(64) DEFAULT NULL ," +                           //科目的名字
                "               `tf_delete` tinyint(4) DEFAULT NULL ," +                            //是否删除_1是_2否
                "                `item_bank_id` int(11) DEFAULT NULL ," +                           //外键链接着题库的ID
                "               `creation_time` datetime DEFAULT NULL ," +                          //创建当前时间
                "                `operator_id` int(11) DEFAULT NULL " +                             //由谁创建
                "                ) ;");
        //创建题库-章节考点表
        db.execSQL("CREATE TABLE IF NOT EXISTS `chapter_test_point_edu` (\n" +
                "  `chapter_test_point_id` integer NOT NULL primary key AUTOINCREMENT ,\n" +    //章节考点id
                "  `ibs_id` int(11) DEFAULT NULL  ,\n" +                                        //外键链接着科目表中科目ID
                "  `name` varchar(64) DEFAULT NULL ,\n" +                                       //科目的名字
                "  `tf_delete` tinyint(4) DEFAULT NULL  ,\n" +                                  //是否删除_1是_2否
                "  `creation_time` datetime DEFAULT NULL  ,\n" +                                //创建当前时间
                "  `operator_id` int(11) DEFAULT NULL  ,\n" +                                   //由谁创建
                "  `type` tinyint(4) DEFAULT NULL  ,\n" +                                       //1表示章_2表示节_3表示考点
                "  `father_id` int(11) DEFAULT NULL  \n" +                                      //表示他的上一级是哪个_章_为_null
                ") ;");
        //创建题库-试题表
        db.execSQL("CREATE TABLE IF NOT EXISTS `test_questions_edu` (\n" +
                "  `question_id` integer NOT NULL primary key AUTOINCREMENT ,\n" +              //试题唯一的标识
                "  `question_name` text ,\n" +                              //试题名称
                "  `optionanswer` text ,\n" +                                                   //选项答案
                "  `question_type` tinyint(4) DEFAULT NULL ,\n" +                               //1单选题2多选题_3判断题_4简答题_5不定项_6填空题_7材料题___
                "  `question_analysis` text ,\n" +                                              //题目的解答文本
                "  `audio_analysis` text DEFAULT NULL,\n" +                              //题目的解答音频
                "  `video_analysis` text DEFAULT NULL,\n" +                              //题目的解答视频
                "  `question_sub_id` varchar(64) DEFAULT NULL,\n" +                             //材料题专有_,存一组试题ID
                "  `state` tinyint(4) DEFAULT NULL ,\n" +                                       //1正在编辑2等待审核3发布成功4审核失败
                "  `creation_time` datetime DEFAULT NULL,\n" +                                  //创建当前时间
                "  `founder_id` varchar(64) DEFAULT NULL ,\n" +                                 //由谁创建
                "  `auditor_id` varchar(64) DEFAULT NULL,\n" +                                  //由谁审核的
                "  `tf_delete` tinyint(4) DEFAULT NULL ,\n" +                                   //是否删除_1是_2否
                "  `difficulty` tinyint(4) DEFAULT NULL ,\n" +                                  //1简单_2_一般_3困难
                "  `chapter_id` int(11) DEFAULT NULL ,\n" +                                     //绑定_章
                "  `section_id` int(11) DEFAULT NULL ,\n" +                                     //绑定_节
                "  `examination_site_id` int(11) DEFAULT NULL ,\n" +                            //绑定_考点
                "  `ibs_id` int(11) DEFAULT NULL \n" +                                          //子题库ID
                ");");
        //创建题库-试卷表
        db.execSQL("CREATE TABLE IF NOT EXISTS `test_paper_edu` (\n" +
                "  `test_paper_id` integer NOT NULL primary key AUTOINCREMENT ,\n" +            //试卷唯一的标识
                "  `test_paper_name` varchar(64) DEFAULT NULL ,\n" +                            //试卷名称
                "  `test_paper_type` tinyint(4) DEFAULT NULL ,\n" +                             //试卷类型__1真题2模拟题_3课后练习4课后作业
                "  `answer_time` int(11) DEFAULT NULL ,\n" +                                    //以分钟为单位
                "  `total_score` decimal(8,2) DEFAULT NULL ,\n" +                               //总分小数形式
                "  `area` varchar(64) DEFAULT NULL ,\n" +                                       //地区标识
                "  `question_type_score` varchar(64) DEFAULT NULL ,\n" +                        //内容格式#单选#5.00;#多选#10.00;#简答题#2.00;#材料题#3.00;
                "  `state` tinyint(4) DEFAULT NULL ,\n" +                                       //1正在编辑2等待审核3发布成功4审核失败
                "  `ibs_id` int(11) DEFAULT NULL ,\n" +                                         //外键_链接着科目表中的科目ID_标识在那个科目下
                "  `creation_time` datetime DEFAULT NULL ,\n" +                                 //创建当前时间
                "  `founder_id` int(11) DEFAULT NULL ,\n" +                                     //由谁创建
                "  `auditor` int(11) DEFAULT NULL ,\n" +                                        //由谁审核的
                "  `tf_delete` tinyint(4) DEFAULT NULL,\n" +                                    //是否删除
                "  `audit_time` datetime DEFAULT NULL ,\n" +                                    //也就是审核之后的
                "  `question_id_group` varchar(64) DEFAULT NULL ,\n" +                          //试题所有的试题ID/按顺序来分号分割
                "  `tf_temporary` tinyint(4) DEFAULT NULL \n" +                                 //是不是临时试卷
                ") ;");
        //创建题库-学习记录表
        db.execSQL("CREATE TABLE IF NOT EXISTS `answer_edu` (\n" +
                "  `answer_id` integer NOT NULL primary key AUTOINCREMENT ,\n" +                //记录的唯一标识
                "  `test_paper_id` int(11) NOT NULL ,\n" +                                      //外键链接着试卷ID
                "  `student_id` int(11) NOT NULL ,\n" +                                         //外键链接着学生ID
                "  `time` datetime DEFAULT NULL ,\n" +                                          //答题时创建的时间
                "  `type` tinyint(4) DEFAULT NULL ,\n" +                                        //1快速做题_2章节练习_3模拟题/真_4课后练习_5章节测验_6课后作业_7每日一练_8智能组卷_9快速15道
                "  `question_num` int(11) DEFAULT NULL ,\n" +                                   //做题总数
                "  `score` int(11) DEFAULT NULL ,\n" +                                          //总得分
                "  `error_num` text ,\n" +                                                      //错了几题，数组  #错题id#他的答案后者留空;
                "  `state` tinyint(4) DEFAULT NULL ,\n" +                                       //1已完成,2正在答题
                "  `tf_delete` tinyint(4) DEFAULT NULL ,\n" +                                   //1是_2否
                "  `used_answer_time` int(11) DEFAULT NULL \n" +                                //秒为单位
                ") ;");
        //创建题库-已答题但是未提交的题建表
        db.execSQL("  CREATE TABLE IF NOT EXISTS `stu_un_answer_edu` (\n" +
                "  `student_id` int(11) NOT NULL DEFAULT '0' ,\n" +                             //外键链接着学生ID
                "  `question_id` int(11) NOT NULL DEFAULT '0' ,\n" +                            //外键链接着试题ID
                "  `student_answers` text ,\n" +                                                //学生答案
                "  `tf_delete` tinyint(4) DEFAULT NULL ,\n" +                                   //1是_2否
                "  `time` datetime DEFAULT NULL ,\n" +                                          //答题时创建的时间
                "primary key (student_id,question_id )\n" +
                ");");
        //创建题库-学生标记 收藏  错题表
        db.execSQL("CREATE TABLE IF NOT EXISTS `student_question_edu` (\n" +
                "  `student_id` int(11) NOT NULL ,\n" +                                         //外键链接着学生ID
                "  `question_id` int(11) NOT NULL ,\n" +                                        //外键链接着试题ID
                "  `time` datetime DEFAULT NULL ,\n" +                                          //操作时间
                "  `wrong_answer` text COMMENT ,\n" +                                           //错题答案
                "  `tf_delete` tinyint(4) DEFAULT NULL ,\n" +                                   //是否删除
                "  `tf_collection` tinyint(4) DEFAULT NULL ,\n" +                               //是否收藏
                "  `tf_wrong` tinyint(4) DEFAULT NULL ,\n" +                                    //是否错题
                "  `tf_marked` tinyint(4) DEFAULT NULL ,\n" +                                   //是否标记
                "  PRIMARY KEY (`student_id`,`question_id`)\n" +
                ") ;");
        //创建视频缓存表
        db.execSQL("CREATE TABLE IF NOT EXISTS `video_download_table` (\n" +
                "  `video_download_id` integer NOT NULL primary key AUTOINCREMENT ,\n" +                       //视频缓存表id
                "  `video_download_time` datetime DEFAULT NULL ,\n" +                                          //操作时间
                "  `video_download_url` varchar(200) DEFAULT NULL ,\n" +                                       //下载链接
                "  `video_download_name` varchar(64) DEFAULT NULL ,\n" +                                       //视频原名称
                "  `video_download_localname` varchar(64) DEFAULT NULL ,\n" +                                  //视频本地存储名称（时间戳加原名称）
                "  `chapter_id` int(11) DEFAULT NULL  ,\n" +                                                     //章id
                "  `section_id` int(11) DEFAULT NULL  ,\n" +                                                     //节id
                "  `video_len` int(200) DEFAULT NULL \n" +                                                     //视频总长度（）
                ") ;");
        //创建token存储表
        db.execSQL("CREATE TABLE IF NOT EXISTS `token_table` (\n" +
                "  `token_id` integer NOT NULL primary key AUTOINCREMENT ,\n" +                       //表id
                "  `token` varchar(200) DEFAULT NULL,\n" +                                       //token
                "  `ipadress` varchar(200) DEFAULT NULL,\n" +                                       //访问地址
                "  `stu_id` varchar(200) DEFAULT NULL" +                                            //学生id
                ") ;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}