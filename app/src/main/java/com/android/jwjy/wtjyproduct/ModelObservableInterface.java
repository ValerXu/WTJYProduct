package com.android.jwjy.wtjyproduct;

//import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
//import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
//import retrofit2.http.Field;
//import retrofit2.http.FieldMap;
//import retrofit2.http.FormUrlEncoded;
//import retrofit2.http.GET;
//import retrofit2.http.Header;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
//import retrofit2.http.QueryMap;
/**
 * Created by dayuer on 19/7/2.
 * 接口文件
 */
//当POST请求时，@FormUrlEncoded和@Field简单的表单键值对。两个需要结合使用，否则会报错
public interface ModelObservableInterface {
//   String urlHead = "http://wangxiaojk.16hz.net/";

    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new HeaderInterceptor()) //这部分
            .build();

    /**
     * 查询app首页数据
     **/
    @GET("app/homePage/queryHomePageInfo")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Observable<ModelHomePage.HomePageBean> queryHomePageInfo();

    //社区问答--列表   http://localhost:8080/app/homePage/queryHomePageInfo/Community
    @POST("app/user/queryCommunityQuestions")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelCommunityAnswer.CommunityBean> queryAllCoursePackageCommunity(@Body RequestBody body);

    //社区问答-回复
    @POST("app/user/ReplyQuestion")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<BaseBean> queryCommunityDetilsreplyBean(@Body RequestBody body);

    //社区问答--发布
    @POST("app/user/askQuestions")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<BaseBean> queryMyCommunityissue(@Body RequestBody body);

    //社区问答--查询标签
    @POST("app/user/queryClassificationName")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelCommunityAnswer.CommunityQuerytagsBean> queryMyCommunityQuerytags(@Body RequestBody body);

    //社区问答详情
    @POST("app/user/queryCommunityQuestionsDetails")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelCommunityAnswer.CommunityDetilsBean> QueryCommunityQuestionsDetails(@Body RequestBody body);

    // 我的课程列表   http://localhost:8080/app/my/queryMyCourseList
    @POST("app/my/queryMyCourseList")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelMy.QueryMyCourseListBean> QueryMyCourseList(@Body RequestBody body);


    //我的订单列表 MyOrderlistBean
    @POST("app/my/queryMyOrder")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelMy.MyOrderlistBean> queryMyPackageOrderList(@Body RequestBody body);

    //我的订单列表 MyOrderlistBean
    @POST("app/my/findMyOrderDetail")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelMy.MyOrderDetailsBean> queryMyOrderDetails(@Body RequestBody body);

    //我的订单列表-取消订单的支付状态
    @POST("app/user/cancelOrder")
    Call<BaseBean> queryMyCancelOrderStates(@Body RequestBody body);

    //我的收藏列表(课程)
    @POST("app/my/queryMyCollection")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelMy.QueryMyCollectionListBean> queryMyCollectionList(@Body RequestBody body);

    //我的消息列表
    @POST("app/user/queryMyNews")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelMy.MymessageBean> queryMyMessageList(@Body RequestBody body);

    //我的消息列表-单个已读
    @POST("app/user/readMyNews")
    Call<BaseBean> ReadMyNews(@Body RequestBody body);

    //我的消息列表-全部已读
    @POST("app/user/readMyNewsALL")
    Call<BaseBean> ReadMyNewsAll(@Body RequestBody body);

    //我的消息列表-删除
    @POST("app/user/deleteMyNews")
    Call<BaseBean> queryMyMessageDelectList(@Body RequestBody body);

    //我的题库
    @POST("app/user/queryMyItemBank")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.MyQuestionBankBean> queryMyQuestionBankList(@Body RequestBody body);

    //题库  (包括子题库)
    @POST("app/user/ItemBank")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.QuestionBankBean> queryQuestionBankList(@Body RequestBody body);

    //题库   查看试卷
    @POST("app/user/queryTestPaper")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.QuestionBankTestPaperBean> queryMyQuestionBankTestPaper(@Body RequestBody body);

     //题库   答题卡
    @POST("app/user/queryAnswerCard")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.QuestionBankAnswerSheetBean>queryQuestionBankAnswerSheet(@Body RequestBody body);

    //题库  做题记录
      @POST("app/user/queryMyItemBankAnswer")
      @Headers("Content-Type:application/json;charset=UTF-8")
      Call<ModelQuestionBank.QuestionBankAnswerRecordBean>queryQuestionBankAnswerRecord(@Body RequestBody body);

    //题库  做题记录-再做一遍
    @POST("app/user/doItOver")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.MyTestPageIssueBean>queryQuestionBankAnswerRecordAgain(@Body RequestBody body);

    //题库  做题记录-查看解析试卷
    @POST("app/user/viewParsingPage")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.MyTestPageIssueBean>queryQuestionBankAnswerRecordLook(@Body RequestBody body);

    //题库  做题记录-查看解析章节考点、快速做题
    @POST("app/user/viewParsingChapter")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.MyQuestionBankExercises>queryQuestionBankAnswerRecordLookChapter(@Body RequestBody body);

     //题库  我的收藏题
    @POST("app/user/queryMyItemBankCollection")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.QuestionBankMyFavoriteQuestionBean>queryQuestionBankMyFavoriteQuestion(@Body RequestBody body);

    //题库  判断当前的任务是否继续做题
    @POST("app/user/ibsStuTfContinue")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.MyQuestionBankGoonBean> queryMyQuestionBankGoon(@Body RequestBody body);

    //题库  章节练习、快速出题的继续做题
    @POST("app/user/continueTime")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.MyQuestionBankExercises> queryMyQuestionBankContinue(@Body RequestBody body);

    //题库  试卷出题的继续做题
    @POST("app/user/pageContinueTime")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.MyTestPageIssueBean> queryPageContinueTime(@Body RequestBody body);

    //题库-出题   是不是练习模式
    @POST("app/user/Issue")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.MyQuestionBankExercises> queryMyQuestionBankIssue(@Body RequestBody body);

    //题库-试卷出题
    @POST("app/user/testPageIssue")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.MyTestPageIssueBean> queryTestPageIssue(@Body RequestBody body);

    //题库-快速做题
    @POST("app/user/quickTask")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.MyQuestionBankExercises> queryQuickTask(@Body RequestBody body);

    //题库  交卷
    @POST("app/user/handInHand")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<BaseBean>  queryMyQuestionBankHandIn(@Body RequestBody body);

    //题库    标记和取消标记  收藏和取消收藏
    @POST("app/user/CollectMarkQuestions")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<BaseBean> queryMyQuestionBankflag(@Body RequestBody body);

    //题库    查询章节考点
    @POST("app/user/queryCTP")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.MyQuestionBankChapterTestBean> queryMyQuestionBankChapterTest(@Body RequestBody body);

    //题库    做题设置-查询题型错题和未做的题数
    @POST("app/user/QueryTopicSetting")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelQuestionBank.QueryTopicSettingBean> QueryTopicSetting(@Body RequestBody body);

    //我的问答（我的提问）
    @POST("app/user/queryMyQuestionWen")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelMy.MyQuestionsBean> queryMyQuestionList(@Body RequestBody body);

    //我的问答（我的回答）
    @POST("app/user/queryMyQuestionDa")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelMy.MyAnswerBean> queryMyAnswerList(@Body RequestBody body);

    //我的问答-删除
    @POST("app/user/deleteMyQuestion")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<BaseBean> DeleteMyQuestion(@Body RequestBody body);

    //我的收藏列表(课程包)
    @POST("app/my/queryMyCollection")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelMy.QueryMyCollectionPacketListBean> queryMyCollectionPacketList(@Body RequestBody body);

    //新闻资讯列表
    @POST("app/newsAndInformation/queryAllNewsAndInformation")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelNews.ModelNewsBean> queryCoursePackageModelNews(@Body RequestBody body);


    //新闻详情
    @POST("app/newsAndInformation/queryNewsAndInformationDetails")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelNews.ModelNewsDetilsBean> queryCoursePackageModelNewsDetils(@Body RequestBody body);

    //课程表
    @POST("app/schoolTimeTable/queryAllSchoolTimeTableFromOneStu")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelClassCheduleCard.SchoolTimeTableBean> QueryAllSchoolTimeTableFromOneStu(@Body RequestBody body);

    //我的课程包
    @POST("app/my/queryMyCoursePackageList")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelMy.MyclassPacketList> QueryMyCoursePackageList(@Body RequestBody body);

    //我的协议
    @POST("app/my/viewSingleAgreement")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelMy.MyClassMeent> queryCourseModelNewsAgreeMent(@Body RequestBody body);

    //我的优惠券列表
    @POST("app/my/queryAllMyCoupons")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelMy.MyCoupon> getMyCouponListMessage(@Body RequestBody body);

    //公开课
    @POST("app/publicClass/queryAllPublicClassInfo")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelOpenClass.ModelOpenclassBean> queryCoursePackageOpenclass(@Body RequestBody body);

    //欢拓-直播
    @POST("app/user/huanTuoLive")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<BaseBean> getHuanTuoLiveToken(@Body RequestBody body);

    //欢拓-回放
    @POST("app/user/huanTuoRec")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<BaseBean> getHuanTuoRecToken(@Body RequestBody body);

    //阿里-录播
    @POST("app/user/stuCourseAccessVideo")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<BaseBean> getAliCourseAccessVideo(@Body RequestBody body);

    //0元欢拓-直播
    @POST("app/user/huanTuoLiveZero")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<BaseBean> gethuanTuoLiveZeroToken(@Body RequestBody body);

    //0元欢拓-回放
    @POST("app/user/huanTuoRecZero")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<BaseBean> gethuanTuoRecZeroToken(@Body RequestBody body);

    //课程包
    @POST("app/homePage/queryAllCoursePackageInfo")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelCoursePacket.CoursePacketBean> queryAllCoursePackageInfo(@Body RequestBody body);

    //名字的模糊查询(搜索关键字)
    @POST("app/homePage/queryCoursePackageInfoByName")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelCoursePacket.CoursePacketBean> queryAllCoursePackageSelectName(@Body RequestBody body);


    //课程包详情
    @POST("app/homePage/coursePackageDetail")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelCoursePacketCover.DataPacketDetailsBean> queryCoursePackageDetails(@Body RequestBody body);

    //阶段课程    http://localhost:8080/app/homePage/stageCourseList     请求头数据的设置
    @POST("app/homePage/stageCourseList")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelCoursePacketCover.StageCourseListBean> queryStageofcoursecurriculum(@Body RequestBody body);

    //师资   http://localhost:8080/app/homePage/queryOneCpJsUserList
    @POST("app/homePage/queryOneCpJsUserList")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Observable<ModelCoursePacketCover.CourseTeacherBean> queryCoursePackageTeacher(@Query("course_package_id") int course_package_id);    //course_package_id参数id

    //订单支付    http://localhost:8080/app/homePage/generateOrderNumber
    @POST("order/pay")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelOrderDetails.BuyCode> querygenerateOrderNumber(@Body RequestBody body);

    //订单支付-重新支付    http://localhost:8080/app/homePage/generateOrderNumber
    @POST("order/paySend")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<BaseBean> orderRepay(@Body RequestBody body);

    //订单支付-重新支付    http://localhost:8080/app/homePage/generateOrderNumber
    @POST("order/paySend")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<BaseBean1> orderRepayAli(@Body RequestBody body);

    //收藏课程包   http://localhost:8080/app/homePage/collectionCoursePackage
    @POST("app/homePage/collectionCoursePackage")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelCoursePacketCover.collectionBean> queryStageofcourseCoursecollection(@Body RequestBody body);

    // 条件筛选
    @POST("app/homePage/coursePackageSearchBox")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<ModelCoursePacket.CoursePacketBean> queryAllcoursePackageSearchBox(@Body RequestBody body);

    /**
     * 查询个人信息详情（我的界面）
     *
     * @param body 请求体（token：用户标识)
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/my/accessToPersonalInformation")
    Call<ModelMy.PersonalInfoBean> queryModelMyPersonInfo(@Body RequestBody body);

    /**
     * 查询个人信息详情（设置界面）
     *
     * @param body 请求体（token：用户标识)
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/my/accessToPersonalInformation")
    Call<ModelSetting.PersonalInfoBean> queryModelSettingPersonInfo(@Body RequestBody body);

    /**
     * 修改个人信息详情（设置界面）
     *
     * @param body 请求体（token：用户标识；username：用户名;user_sign 用户签名;phone 手机号;email 邮箱地址;avatar 头像;login_num 账号（暂不可修改）;idCardNum 证件号码）
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/my/updateToPersonalInformation")
    Call<BaseBean> updataModelSettingPersonInfo(@Body RequestBody body);

    /**
     * 修改个人信息详情（设置界面）
     *
     * @param body 请求体
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/app/my/checkModifyingTel")
    Call<BaseBean> checkModifyingTel(@Body RequestBody body);

    /**
     * 修改个人信息详情（修改头像）
     * //     * @param body 请求体（str：学生id的json格式）
     **/
    @Multipart
    @POST("app/my/modifyingHead")
    Call<BaseBean> modifyingHead(@PartMap Map<String, RequestBody> params);

    /**
     * 修改学生密码（修改头像）
     * //     * @param body 请求体
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/setup/updateStuPass")
    Call<BaseBean> updateStuPass(@Body RequestBody body);

    /**
     * 登录
     *
     * @param body 请求体（tel：用户名；stu_pass：密码）
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/user/passwordLogin")
    Call<BaseBean> PasswordLogin(@Body RequestBody body);

    /**
     * 注册
     *
     * @param body 请求体（phone：手机号；password：密码；phoneCode：验证码）
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/user/telRegister")
    Call<BaseBean> telRegister(@Body RequestBody body);

    /**
     * 找回密码
     *
     * @param body 请求体（tel：手机号；stu_pass：密码；sms_code：验证码）
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/user/retrievePassword")
    Call<BaseBean> retrievePassword(@Body RequestBody body);

    /**
     * 获取验证码
     *
     * @param body 请求体（tel：手机号；）
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/user/getVerificationCode")
    Call<BaseBean> getVerificationCode(@Body RequestBody body);

    /**
     * 查看手机号是否存在
     *
     * @param body 请求体（tel：手机号；）
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/user/verifyPhoneNumber")
    Call<BaseBean> getVerifyPhoneNumber(@Body RequestBody body);

    /**
     * 关于我们
     **/
    @POST("api/about/")
    Observable<ModelSetting.AboutUsInfoBean> queryAboutUsInfo();

    /**
     * 查询版本号以及新版本的下载链接
     **/
    @POST("app/setup/queryVersionNo")
    @Headers({"Content-type:application/json;charset=UTF-8"})
    Observable<BaseBean> queryAndroidVersion();

    /**
     * 查询课程列表
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/course/queryAllCourseBySeachBox")
    Call<ModelCourse.CourseBean> queryAllCourseInfo(@Body RequestBody body);

    /**
     * 查询项目列表
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/relevant/queryAllProject")
    Call<ModelCourse.ProjectBean> queryAllProject(@Body RequestBody body);

    /**
     * 查询科目列表
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/relevant/queryAllSubjectFromOneProject")
    Call<ModelCourse.ProjectBean> queryAllSubjectFromOneProject(@Body RequestBody body);

    /**
     * 查询课程详情
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/course/findSingleCourseDetails")
    Call<BaseBean> findSingleCourseDetails(@Body RequestBody body);

    /**
     * 查询课程目录
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/course/findSingleCourseCatalog")
    Call<ModelCourseCover.CourseCatalogBean> findSingleCourseCatalog(@Body RequestBody body);

    /**
     * 查询课程资料
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/user/queryCourseResData")
    Call<ModelCourseCover.materialsBean> queryCourseResData(@Body RequestBody body);

    /**
     * 查询课程目录new;2020.06.10 对课程目录进行改版，改为分页查询，每个章节最多显示3节课程
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/course/queryCourseREC")
    Call<ModelCourseCover.CourseCatalogBeanNew> findSingleCourseCatalogRecNew(@Body RequestBody body);

    /**
     * 查询课程目录new;2020.06.10 对课程目录进行改版，改为分页查询，获取更多节信息
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/course/queryCourseRECSection")
    Call<ModelCourseCover.CourseCatalogSectionBeanNew> findSingleCourseCatalogRecSection(@Body RequestBody body);

    /**
     * 查询课程目录new;2020.06.10 对课程目录进行改版，改为分页查询，获取直播课程
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/course/queryCourseLive")
    Call<ModelCourseCover.CourseCatalogLiveBeanNew> findSingleCourseCatalogLive(@Body RequestBody body);

    /**
     * 收藏或取消收藏课程
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/course/collectOrNotCollectCourses")
    Call<BaseBean> collectOrNotCollectCourses(@Body RequestBody body);

    /**
     * 购买课程-查询优惠券
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/course/queryDiscountFromOneStuCourse")
    Call<ModelOrderDetails.CouponBean> queryDiscountFromOneStuCourse(@Body RequestBody body);

    /**
     * 购买课程包-查询优惠券
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/homePage/queryDiscountFromOneStu")
    Call<ModelOrderDetails.CouponBean> queryDiscountFromOneStuCoursePacket(@Body RequestBody body);

    /**
     * 上传录播视频播放进度
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/user/stuCourseVideoDuration")
    Call<BaseBean> SetCourseVideoDuration(@Body RequestBody body);

    /**
     * 课程问答-添加
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/user/insertCourseQuestions")
    Call<BaseBean> addStuCourseQuestion(@Body RequestBody body);

    /**
     * 课程问答-回复
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/user/ReplyCourseQuestions")
    Call<BaseBean> replyStuCourseQuestion(@Body RequestBody body);

    /**
     * 兑换优惠券-先查
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/relevant/checkBeforeExchangingCoupons")
    Call<BaseBean> checkBeforeExchangingCoupons(@Body RequestBody body);

    /**
     * 兑换优惠券
     **/
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("app/relevant/redeemCoupons")
    Call<BaseBean> redeemCoupons(@Body RequestBody body);

    /**
     * 上传图片（多张上传）
     **/
    @Multipart
    @POST("Ex/upload")
    Call<BaseBean> upLoadImage(@PartMap Map<String, RequestBody> params);

    /**
     * 上传崩溃日志
     **/
    @POST("app/setup/getAPPlog")
    @Headers("Content-Type:application/json;charset=UTF-8")
    Call<BaseBean> UploadUncaughtException(@Body RequestBody body);

    class BaseBean {
        private Map<String, Object> data;
        private int code;
        private String message;
        private String msg;

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }

        public int getErrorCode() {
            return code;
        }

        public void setErrorCode(int code) {
            this.code = code;
        }

        public String getErrorMsg() {
            return message;
        }

        public void setErrorMsg(String message) {
            this.message = message;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    class BaseBean1 {
        private String data;
        private int code;
        private String message;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public int getErrorCode() {
            return code;
        }

        public void setErrorCode(int code) {
            this.code = code;
        }

        public String getErrorMsg() {
            return message;
        }

        public void setErrorMsg(String message) {
            this.message = message;
        }
    }

}
