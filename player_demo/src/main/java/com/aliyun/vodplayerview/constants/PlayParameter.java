package com.aliyun.vodplayerview.constants;

import com.aliyun.vodplayerview.playlist.AlivcVideoInfo;

/**
 * 播放参数, 包含:
 *  vid, vidSts, akId, akSecre, scuToken
 */
public class PlayParameter {


    /**
     * type, 用于区分播放类型, 默认为vidsts播放
     * vidsts: vid类型
     * localSource: url类型
     *
     */
    public static  String PLAY_PARAM_TYPE = "vidsts";

    private static final String PLAY_PARAM_VID_DEFAULT = "9fb028c29acb421cb634c77cf4ebe078";

    /**
     * vid, 初始为: 9fb028c29acb421cb634c77cf4ebe078
     */
    public static  String PLAY_PARAM_VID = "28b8e6b1e87340c2a9dcac78729ed24c";

    public static String PLAY_PARAM_REGION = "cn-shanghai";

    /**
     * akId
     */
    public static  String PLAY_PARAM_AK_ID = "STS.NSzqJZW5p4QfzXeLAhvHScKhc";

    /**
     * akSecre
     */
    public static  String PLAY_PARAM_AK_SECRE = "CBze1UETonf3UGTJFqKvdCnVActEefxNnmyeeJv4sGuZ";

    /**
     * scuToken
     */
    public static  String PLAY_PARAM_SCU_TOKEN = "CAISlwJ1q6Ft5B2yfSjIr5DPOvDuuupRg5ONeH7UqEE9esd/jI7DgTz2IHxEeHZqAOoetP00lGpQ5/8TlrptSpNIQnTAZNpZ9ox+9w+7ZIvN4y0LWGn5zs/LI3OaLjKm9vuwCryLc7GwU/OpbE++000X6LDmdDKkckW4OJmS8/BOZcgWWQ/KEFgmGtBadCRvtI14VHzKLqSJMwX2wBCnbixStxF7lHl05Imm38SY8WC+tlDhzfIPrImDSfrNLesUZcwnA4brhrwtLPacgHEPsyInrvkm0PZ2nh7cpcyYDlVr5BGLDvHZ6NUHLnUiOvhmRPQd9qCmzKYo4LSCztXtsAhMMexSSyDWVYm42MLCFPmuN9opjxYPlvZqE7oagAELmQjrV2bvO70eZC8nHim1tHSA0WkfNJuaSbEDpcxEXh/BAgv+/Xe8ESukSSYsYYFbB0sAANM/H4J8RxE8fDU5tUupCdb4FFktVLfdZAC+Elgo8buc9w2oDlqcCTgd6Gm0UAtRM77zAnXMCcWX/KzKoEL8CiKukAHuU69FnW7gzg==";



    /**
     * url类型的播放地址, 初始为:http://player.alicdn.com/video/aliyunmedia.mp4
     */
    public static String PLAY_PARAM_URL = "http://player.alicdn.com/video/aliyunmedia.mp4";
    /**
     * sts请求地址
     */
    public static String STS_GET_URL = "app/user/stuCourseAccessVideo/";
}
