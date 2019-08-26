package com.smnetinfo.savememory.extras;

public interface WebConstants {

    //URLs
    String BASE_URL = "http://54.169.97.25:3000/api/";

    String USER_SIGN_UP = BASE_URL + "user/create";
    String USER_LOGIN = BASE_URL + "user/login";
    String USER_CHECK = BASE_URL + "user/check-account-completion";
    String USER_EMAIL_VERIFIED = BASE_URL + "user/check-email-verification";
    String USER_PROFILE_COMPLETE = BASE_URL + "user/selected-update";
    String USER_UPDATE = BASE_URL + "user/update";
    String USER_PASSWORD_FORGOT = BASE_URL + "user/reset";
    String CREATE_POST = BASE_URL + "posts/create-post";
    String CREATE_WILL = BASE_URL + "will/create";
    String CREATE_BENEFICIARY = BASE_URL + "beneficiary/create";
    String WILL_UPDATE = BASE_URL + "will/update";
    String WILL_DELETE = BASE_URL + "will/delete";
    String GET_WILL = BASE_URL + "will";
    String GET_POST = BASE_URL + "posts";
    String GET_SPACE = BASE_URL + "space";
    String GET_BENEFICIARY = BASE_URL + "beneficiary";
    String POST_DELETE = BASE_URL + "posts/delete";
    String POST_UPDATE = BASE_URL + "posts/update";
    String GET_FAQ = BASE_URL + "faq";
    String ASK = BASE_URL + "faq/create-faq";
    String SEARCH = BASE_URL + "search";

    String TOC_URL = "http://54.169.97.25/verify/tos";
    String ABOUT_URL = "http://54.169.97.25/verify/about";
    String PRIVACY_URL = "http://54.169.97.25/verify/privacy";

    //Extensions
    String EXT_IMAGE = ".jpg";
    String EXT_AUDIO = ".mp3";
    String EXT_VIDEO = ".mp4";

    //S3
    String BUCKET_NAME = "save-memory";
    String S3_BUCKET_URL = "https://s3-us-west-2.amazonaws.com/save-memory/";
    String USER_IMAGE_PATH = "Users/";
    String POST_IMAGE_PATH = "Images/";
    String POST_AUDIO_PATH = "Audios/";
    String POST_VIDEO_PATH = "Videos/";
    String POST_THUMB_PATH = "Thumbs/";
    String POST_DOC_PATH = "Doc/";

    //Paths
    String ROOT_PATH = "/Android/data/com.smnetinfo.savememory/";
    String TEMP_PROFILE_IMAGE_PATH = "/Android/data/com.smnetinfo.savememory/temp_profile.jpg";
    String TEMP_AUDIO_PATH = "/Android/data/com.smnetinfo.savememory/temp_audio" + EXT_AUDIO;
    String TEMP_VIDEO_PATH = "/Android/data/com.smnetinfo.savememory/temp_video" + EXT_VIDEO;
    String AUDIO_PATH = "/SaveMemory/Audio/";
    String VIDEO_PATH = "/SaveMemory/Video/";
    String IMAGE_PATH = "/SaveMemory/Image/";
    String THUMB_PATH = "/SaveMemory/Thumb/";

    //Intent Extras
    String POST_ID = "post_id";
    String POST_URL = "post_url";
    String POST_PATH = "post_path";
    String POST_EXT = "post_ext";

    //Tables
    String TABLE_USER = "user";
    String TABLE_SETTINGS = "settings";
    String TABLE_POSTS = "posts";

    //Constants
    int FONT_SIZE_SMALL = 12;
    int FONT_SIZE_MEDIUM = 15;
    int FONT_SIZE_LARGE = 18;
    int FONT_SIZE_VLARGE = 20;
    int LANG_ENGLISH = 0;
    int LANG_KOREAN = 1;
    int LANG_JAPANESE = 2;
    int POST_TYPE_VIDEO = 1;
    int POST_TYPE_TEXT = 2;
    int POST_TYPE_IMAGE = 3;
    int POST_TYPE_AUDIO = 4;
    int POST_TYPE_DOC = 5;
    int POST_TYPE_DATE = 6;
    String STATUS = "status";
    String POST_DELETE_STATUS = "postDeleteStatus";
    String ID = "id";
    String DATA = "data";
    String POSTS = "posts";
    String IS_ACTIVE = "is_active";
    String POSITION = "position";
    String NAME = "name";
    String TITLE = "title";
    String DESCRIPTION = "description";
    String LAST_NAME = "last_name";
    String FIRST_NAME = "first_name";
    String KEEP_LOGGED = "keep_logged";
    String TOC_ACCEPTED = "toc_accepted";
    String PRIVACY_ACCEPTED = "privacy_accepted";
    String LOGIN_STATUS = "login_status";
    String LANGUAGE_TYPE = "language_type";
    String FONT_SIZE = "font_size";
    String WALLPAPER_PATH = "wallpaper_path";
    String EMPTY = "empty";
    String EMAIL = "email";
    String PASS = "pass";
    String AVATAR_PATH = "avatar_path";
    String DOB = "dob";
    String GENDER = "gender";
    String NATIONALITY = "nationality";
    String CURRENT_COUNTRY = "curr_country";
    String OCCUPATION = "occupation";
    String ADDRESS = "address";
    String MARITAL_STATUS = "marital_status";
    String PHONE_NO = "ph_no";
    String ISD_CODE = "isd_code";
    String USER_ID = "user_id";
    String IS_VOICE = "is_voice";
    String DURATION = "duration";
    String TYPE = "type";
    String CONTENT_ID = "content_id";
    String CONTENT_URL = "content_url";
    String CONTENT_ARRAY = "content_array";
    String CONTENT_SIZE = "content_size";
    String URL = "url";
    String URL_SIZE = "url_size";
    String THUMB_URL = "thumb_url";
    String CREATED_AT = "createdAt";
    String USED_SPACE = "used_space";
    String REMAINING_SPACE = "remaining_space";
    String RELATIONSHIP = "relationship";
    String RELATIONSHIP_INFO = "relationship_info";
    String BENEFICIARY = "beneficiary";
    String BENEFICIARY_ID = "beneficiary_id";
    String BENEFICIARY_AVATAR = "beneficiary_avatar";
    String INFO = "info";
    String SEARCH_TEXT = "search_text";

    String BROADCAST_SEARCH_RESULT = "search_result";
    String BROADCAST_MY_INHERITOR_PAGE = "select_inheritor";

    //Utils
    String TIME_FORMAT = "hh:mm a";
    String DATE_FORMAT = "dd MMMM yyyy";
    String DATE_SUB_FORMAT = "yyyy-MM-dd";
    String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    String DATE_TIME_FORMAT_WITH_AM_PM = "yyyy-MM-dd HH:mm:ss a";

}
