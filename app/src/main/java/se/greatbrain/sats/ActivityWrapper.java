package se.greatbrain.sats;

import se.greatbrain.sats.model.realm.TrainingActivity;

public class ActivityWrapper
{
    public static final int COMPLETED = 0;
    public static final int PLANNED = 1;
    public static final int GROUP = 2;
    public static final int PRIVATE = 3;

    public final int year;
    public final int week;
    public final int activityType;
    public final int activityStatus;
    public final TrainingActivity trainingActivity;

    public ActivityWrapper(final int year, final int week, TrainingActivity activity)
    {
        this.year = year;
        this.week = week;
        this.trainingActivity = activity;

        if (activity.getStatus().equalsIgnoreCase("completed"))
        {
            activityStatus = COMPLETED;
        }
        else
        {
            activityStatus = PLANNED;
        }
        if (activity.getType().equalsIgnoreCase("group") && activity.getBooking() != null)
        {
            activityType = GROUP;
        }
        else
        {
            activityType = PRIVATE;
        }
    }
}
