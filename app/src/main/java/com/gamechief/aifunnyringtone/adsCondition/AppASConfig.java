package com.gamechief.aifunnyringtone.adsCondition;

public class AppASConfig {

    public static boolean IS_GROUPS_LISTING_ALTERED = true;
    public static final String SHOULD_DGA_SHOW_PURCHASE_DIALOG = "shouldShowPurchaseDialog";

    public static final String HISTORY_TIMER_UPDATED_BROAD_CAST = "historyTimerUpdatedBR";
    public static final String NO_STEP_COUNTER_SENSOR_AVAILABLE = "noStepCounterSensorAvailable";
    public static String SP_TIME_OF_VIDEO_VIEWED_TO_UNLOCK_HISTORY = "timeOfVideoViewedToUnlockHistory";
    public static String SP_TIME_OF_VIDEO_VIEWED_TO_UNLOCK_WEATHER = "timeOfVideoViewedToUnlockWeather";
    public static String SP_TIME_OF_VIDEO_VIEWED_TO_UNLOCK_AUTOMATIC_ALTITUDE_HISTORY = "timeOfVideoViewedToUnlockAutomaticAltitudeHistory";
    public static String SP_LAST_RATING_API_DIALOG_VIEW_TIME = "lastRatingAPIDialogViewTime";
    public static long SP_UNLOCK_TIME_AFTER_VIDEO_VIEWED = 24 * 60 * 60 * 1000;
    public static long SP_RATING_API_DIALOG_INTERVAL = 2 * 24 * 60 * 60 * 1000;
    public static int INTERSTITIAL_AD_ON_BOTTOM_TABS_CLICKS_LIMIT = 3;
    public static int INTERSTITIAL_AD_FOR_SETTINGS_LIMIT = 1;
    public static final String COUNTER_FOR_INTERSTITIAL_AD_FOR_SETTINGS = "interAdForSettingsCounter";
    public static int FIRST_TIME_RATINGS_DIALOG_LIMIT = 3;
    public static final String COUNTER_FOR_FIRST_TIME_RATINGS_DIALOG = "ratingDialogFirstTimeCounter";
    public static final String IS_OLD_HISTORY_INSERTED = "isOldHistoryInserted";
    public static final String SHOULD_SHOW_OLD_HISTORY_MENU = "shouldShowOldHistoryMenu";
    public static final String IMAGES_DIRECTORY = "saved_images";
    public static final String SOS_NUMBER = "sosNumber";
    public static final String SOS_MESSAGE = "sosMessage";
    public static final String LOCATION_UPDATED_BCR = "locationUpdatedBCR";
    public static final String ADDRESS_UPDATED_BCR = "addressUpdatedBCR";

    public static final int ACTION_NONE = -1;
    public static final int ACTION_SETTINGS = 0;
    public static final int ACTION_HOURLY = 1;
    public static final int ACTION_DAILY = 2;

    public static final String EXERCISE_TYPE_WALK = "Walk";
    public static final String EXERCISE_TYPE_RUNNING = "Running";
    public static final String EXERCISE_TYPE_CYCLING = "Cycling";
    public static final String EXERCISE_TYPE_HIKING = "Hiking";

    public static final String TEMP_UNIT = "tempUnit";
    public static final int TEMP_UNIT_CELSIUS = 0;
    public static final int TEMP_UNIT_FAHRENHEIT = 1;
    public static final int TEMP_UNIT_KALVIN = 2;

    public static final String PRESSURE_UNIT = "pressureUnit";
    public static final int PRESSURE_UNIT_MB = 0;
    public static final int PRESSURE_UNIT_ATM = 1;
    public static final int PRESSURE_UNIT_PSI = 2;
    public static final int PRESSURE_UNIT_INHG = 3;

    public static final String SPEED_UNIT = "speedUnit";
    public static final int SPEED_UNIT_METER_PER_SECONDS = 0;
    public static final int SPEED_UNIT_KM_PER_HOUR = 1;
    public static final int SPEED_UNIT_MILES_PER_HOUR = 2;

    public static final String DISTANCE_UNIT = "distanceUnit";
    public static final int DISTANCE_UNIT_METER = 0;
    public static final int DISTANCE_UNIT_KM = 1;
    public static final int DISTANCE_UNIT_MILE = 2;

    public static final String WIND_SPEED_UNIT = "windSpeedUnit";
    public static final int WIND_SPEED_UNIT_KMPH = 0;
    public static final int WIND_SPEED_UNIT_MPH = 1;
    public static final int WIND_SPEED_UNIT_MPS = 2;
    public static final int WIND_SPEED_UNIT_FPS = 3;

    public static final String VISIBILITY_UNIT = "visibilityUnit";
    public static final int VISIBILITY_UNIT_KILOMETER = 0;
    public static final int VISIBILITY_UNIT_MILE = 1;
    public static final int VISIBILITY_UNIT_METER = 2;
    public static final int VISIBILITY_UNIT_FOOT = 3;

    public static final String GPS_COORD_FORMAT = "gpsCoordFormat";
    public static final int GPS_COORD_FORMAT_DECIMAL = 0;
    public static final int GPS_COORD_FORMAT_SEXAGESIMAL = 1;

    public static final String TIME_FORMAT = "timeFormat";
    public static final int TIME_FORMAT_12_HOURS = 0;
    public static final int TIME_FORMAT_24_HOURS = 1;

    public static final String PHOTO_LABEL_SIZE = "photoLabelSize";
    public static final int PHOTO_LABEL_SIZE_SMALL = 0;
    public static final int PHOTO_LABEL_SIZE_NORMAL = 1;
    public static final int PHOTO_LABEL_SIZE_LARGE = 2;

    public static final String PHOTO_LABEL_COLOR = "photoLabelColor";

    public static final String MEASURES_LISTING_SORTING_MODE = "measuresListingSortingMode";
    public static final int MEASURES_LISTING_SORTING_A_TO_Z = 1;
    public static final int MEASURES_LISTING_SORTING_Z_TO_A = 2;
    public static final int MEASURES_LISTING_SORTING_NEWEST_FIRST = 3;
    public static final int MEASURES_LISTING_SORTING_OLDEST_FIRST = 4;


    public static int DEVICE_WIDTH = 0;
    public static int DEVICE_HEIGHT = 0;

    public static final String IMAGE_PROFILE_NAME = "IMG_MP_Profile_Temp.jpg";


    public static final String PREF_MAP_TYPE = "mapType";
    public static final String MAIN_SCREEN_SHOW_AREAS = "showAreas";
    public static final String MAIN_SCREEN_SHOW_DISTANCES = "showDistances";
    public static final String MAIN_SCREEN_SHOW_POI_S = "showPOIs";
    public static final String UNITS_SETTINGS_CHANGED_BROAD_CAST = "unitsSettingsChanged";
    public static final String ALTITUDE_HISTORY_NODE_ADDED_BROAD_CAST = "historyNodeAdded";
    public static final String ALTITUDE_HISTORY_TRIAL_EXPIRED_BROAD_CAST = "historyTrialExpired";
    public static final String ACTION_STOP_ALTITUDE_HISTORY = "actionStopAltitudeHistory";

    public static final String USER_ID = "userID";
    public static final String USER_TYPE = "userType";

    public static final String USER_IS_FB_NOTIFICATIONS_ENABLED = "isFBNotificationsEnabled";
    public static final String IS_AUTO_HISTORY_ENABLED = "isAutoHistoryEnabled";
    public static final String AUTO_HISTORY_INTERVAL = "autoHistoryInterval";
    public static final String IS_APP_CLOSED = "isAppClosed";

    public static final String PREF_IS_FIRST_RUN = "prefIsFirstRun";
    public static final String PREF_IS_APP_TOUR_SHOWN = "isAppTourShown";
    public static final String SHOULD_SHOW_NOTIFICATION_PERMISSION_ALERT = "shouldShowNotificationsPermissionAlert";

    public static final String IMAGE_DIRECTORY = "Images";
    public static final String BASE_DIRECTORY = "GPS Altimeter";
    public static final String NEXT_NOTIFICATION_ID = "nextNotificationID";
}
