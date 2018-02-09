package in.snotes.snotes.utils;

public class AppConstants {

    // tags from firebase firestore
    public static final String COLOR_OF_NOTE = "colorOfNote";
    public static final String CONTENT = "content";
    public static final String CREATED_TIMESTAMP = "createdTimestamp";
    public static final String DOCUMENT_REFERENCE = "documentReference";
    public static final String LOCKED = "locked";
    public static final String REMAINDER_SET = "remainderSet";
    public static final String REMAINDER_TIME = "remainderTime";
    public static final String STARRED = "starred";
    public static final String TITLE = "title";

    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_NOTES = "notes";


    // These strings are used in AddNotesBottomSheet
    public static final String TAG_REMAINDER = "remainder";
    public static final String TAG_DELETE = "delete";
    public static final String TAG_EXPORT = "export";
    public static final String TAG_COPY = "copy";
    public static final String TAG_SHARE = "share";
    public static final String TAG_DEFAULT = "default";

    // Constants for notifications
    public static final String REMAINDER_NOTIFICATION_CHANNEL_ID = "remaider-notification-channe-id";
    public static final int NOTIFICATION_ID = 98;

    // constants related to preferences
    public static final String PREFS_PIN = "pin";
    public static final String PREFS_IS_PIN_SET = "isPinSet";
    public static final String NAME = "name";
    public static final String PREFS_IS_NIGHT_MODE = "isNightMode";
}
