package us.mindbuilders.petemit.timegoalie.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Date;

/**
 * Created by Peter on 9/20/2017.
 */

public class TimeGoalieContract {

    public static final String AUTHORITY = "us.mindbuilders.petemit.timegoalie";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static class Goals implements BaseColumns {
        public static final String GOALS_TABLE_NAME = "goals";
        public static final String GOALS_COLUMN_NAME = "name";
        public static final String GOALS_COLUMN_TIMEGOALHOURS = "time_goal_hours";
        public static final String GOALS_COLUMN_TIMEGOALMINUTES = "time_goal_minutes";
        public static final String GOALS_COLUMN_ISDAILY = "is_daily";
        public static final String GOALS_COLUMN_ISWEEKLY = "is_weekly";
        public static final String GOALS_COLUMN_ISTODAYONLY = "is_today_only";
        public static final String GOALS_COLUMN_ISDISABLED = "is_disabled";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(GOALS_TABLE_NAME).build();
    }

    public static class GoalTypes implements BaseColumns {

        public static final String GOALTYPES_TABLE_NAME = "goaltypes";
        public static final String GOALTYPES_COLUMN_NAME = "name";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(GOALTYPES_TABLE_NAME).build();
    }

    public static class Days implements BaseColumns {
        public static final String DAYS_TABLE_NAME = "days";
        public static final String DAYS_COLUMN_NAME = "name";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(DAYS_TABLE_NAME).build();
    }

    public static class GoalsGoalTypes implements BaseColumns {
        public static final String GOALS_GOALTYPES_TABLE_NAME = "goals_goaltypes";
        public static final String GOALS_GOALTYPES_COLUMN_GOAL_ID = "goal_id";
        public static final String GOALS_GOALTYPES_COLUMN_GOALTYPE_ID = "goaltype_id";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(GOALS_GOALTYPES_TABLE_NAME).build();
    }

    public static class GoalsDays implements BaseColumns {
        public static final String GOALS_DAYS_TABLE_NAME = "goals_days";
        public static final String GOALS_DAYS_COLUMN_GOAL_ID = "goal_id";
        public static final String GOALS_DAYS_COLUMN_DAY_ID = "day_id";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(GOALS_DAYS_TABLE_NAME).build();
    }

    public static class GoalsDatesAccomplished implements BaseColumns {
        public static final String GOALS_DATES_ACCOMPLISHED_TABLE_NAME = "goals_dates_accomplished";
        public static final String GOALS_DATES_ACCOMPLISHED_COLUMN_GOAL_ID = "goal_id";
        public static final String GOALS_DATES_ACCOMPLISHED_COLUMN_DATE = "date";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(GOALS_DATES_ACCOMPLISHED_TABLE_NAME).build();
    }

    //get all goals for a specific day of the week
    public static Uri buildGetAllGoalsForASpecificDayQueryUri(long dayid){
        return ContentUris.withAppendedId(GoalsDays.CONTENT_URI,dayid);
    }

    //get a goal by id
    public static Uri buildGetaGoalByIdUri(long goalid){
        return ContentUris.withAppendedId(Goals.CONTENT_URI,goalid);
    }

    //get a goaltype by id
    public static Uri buildGetaGoalTypeByIdUri(long goaltypeid){
        return ContentUris.withAppendedId(GoalTypes.CONTENT_URI,goaltypeid);
    }

    // //get a day by id
    public static Uri buildGetaDaybyIdUri(long dayid){
        return ContentUris.withAppendedId(Days.CONTENT_URI,dayid);
    }


            //find a day of the week associated with today's date
            //get all goals with a is_today_only with the date of today
            //get a count of dates accomplished for a goal for a week
            //get a count of dates accomplished for a goal for a month
            //Find a date accomplished associated with a specific goal for a specific day

}
