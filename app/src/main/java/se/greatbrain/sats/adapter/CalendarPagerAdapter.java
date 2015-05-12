package se.greatbrain.sats.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import se.greatbrain.sats.ActivityWrapper;
import se.greatbrain.sats.fragment.CalendarColumnFragment;
import se.greatbrain.sats.fragment.CalendarFragment;
import se.greatbrain.sats.model.CalendarDate;
import se.greatbrain.sats.realm.RealmClient;
import se.greatbrain.sats.util.DateUtil;

public class CalendarPagerAdapter extends FragmentStatePagerAdapter
{
    public static final String ADAPTER_POSITION = "item_index";
    public static final String DATE_STRING = "date string";
    public static final String NUMBER_OF_ACTIVITIES = "number_activities";
    public static final String POINT_IN_TIME = "week in time";
    public static final String NEXT_NUMBER_OF_ACTIVITIES = "next activity num";
    public static final String PREVIOUS_NUMBER_OF_ACTIVITIES = "previous activity num";
    public static final String HAS_NEXT_WEEK_PASSED = "has next week passed";

    public static int NUM_PAGES;
    public static int NUM_ROWS;
    public static int CURRENT_WEEK;

    private List<ActivityWrapper> activities;
    private List<CalendarDate> dates = new ArrayList<>();
    private Map<Integer, Integer> numberOfActivitiesInWeek = new HashMap<>();

    public CalendarPagerAdapter(FragmentManager fm, Context context)
    {
        super(fm);
        activities = RealmClient.getInstance(context).getAllActivitiesWithWeek();

        if(activities.size() > 0)
        {
            String fromDate = activities.get(0).trainingActivity.getDate();
            dates = DateUtil.getDatesInWeekFrom(fromDate);
        }

        NUM_PAGES = dates.size();
        mapPositionToNumberOfActivities();
        setThisWeeksPosition();
        NUM_ROWS = getHighestActivityCount();
    }

    @Override
    public Fragment getItem(int position)
    {
        CalendarColumnFragment fragment = new CalendarColumnFragment();

        Bundle bundle = new Bundle(position);

        bundle.putBoolean(HAS_NEXT_WEEK_PASSED, hasNextWeekPassed(position));
        bundle.putInt(NUMBER_OF_ACTIVITIES, getNumberOfActivities(position));
        bundle.putInt(POINT_IN_TIME, DateUtil.getWeekPointOfTime(dates.get(position)));
        bundle.putInt(ADAPTER_POSITION, position);
        bundle.putString(DATE_STRING, dates.get(position).mDate);
        bundle.putInt(NEXT_NUMBER_OF_ACTIVITIES, getNextWeeksActivityCount(position));
        bundle.putInt(PREVIOUS_NUMBER_OF_ACTIVITIES, getPreviousWeeksActivityCount(position));

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount()
    {
        return NUM_PAGES;
    }

    /* For multiple pages */

    @Override
    public float getPageWidth(int position)
    {
        return 1f / CalendarFragment.NUM_SIMULTANEOUS_PAGES;
    }

    public int getThisWeeksPosition()
    {
        return CURRENT_WEEK;
    }

    private void setThisWeeksPosition()
    {
        for (int i = 0; i < dates.size(); i++)
        {
            if (DateUtil.getWeekPointOfTime(dates.get(i)) == 0)
            {
                CURRENT_WEEK = i;
                break;
            }
        }
    }

    public int getWeekHashForPosition(int position)
    {
        CalendarDate date = dates.get(position);
        return (date.mYear * 100) + date.mWeek;
    }

    private int getNumberOfActivities(int position)
    {
        if (numberOfActivitiesInWeek.get(position) == null)
        {
            return 0;
        }
        else
        {
            return numberOfActivitiesInWeek.get(position);
        }
    }

    private boolean hasNextWeekPassed(int position)
    {
        return position < CURRENT_WEEK - 1;
    }

    private int getNextWeeksActivityCount(int position)
    {
        if (numberOfActivitiesInWeek.get(position + 1) == null)
        {
            return 0;
        }

        return numberOfActivitiesInWeek.get(position + 1);
    }

    private int getPreviousWeeksActivityCount(int position)
    {
        if (numberOfActivitiesInWeek.get(position - 1) == null)
        {
            return 0;
        }

        return numberOfActivitiesInWeek.get(position - 1);
    }

    private int getHighestActivityCount()
    {
        int highestCount = 0;
        for (Integer count : numberOfActivitiesInWeek.values())
        {
            if (count > highestCount)
            {
                highestCount = count;

                if (highestCount >= 7)
                {
                    return 7;
                }
            }
        }

        if(highestCount < 4)
        {
            return 4;
        }

        return highestCount;
    }

    private void mapPositionToNumberOfActivities()
    {
        for (int i = 0; i < activities.size(); i++)
        {
            for (int j = 0; j < dates.size(); j++)
            {
                if (activities.get(i).year == dates.get(j).mYear)
                {
                    if (activities.get(i).week == dates.get(j).mWeek)
                    {
                        if (numberOfActivitiesInWeek.get(j) != null)
                        {
                            int lastNumberOfActivities = numberOfActivitiesInWeek.get(j);
                            int newNumberOfActivities = ++lastNumberOfActivities;
                            numberOfActivitiesInWeek.remove(j);
                            numberOfActivitiesInWeek.put(j, newNumberOfActivities);
                        }
                        else
                        {
                            numberOfActivitiesInWeek.put(j, 1);
                        }
                    }
                }
            }
        }
    }
}