package se.greatbrain.sats.fragment;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import se.greatbrain.sats.view.CalendarRowView;
import se.greatbrain.sats.R;

public class CalendarColumnFragment extends Fragment
{
    public static final int PAST_WEEK = -1;
    public static final int THIS_WEEK = 0;
    public static final int UPCOMING_WEEK = 1;

    private static final String TAG = "ScreenSlidePageFragment";

    private float calendarHeight;
    private int numActivities;

    private OnPageClickedListener listenerOnPageClicked_MainActivity;

    public interface OnPageClickedListener
    {
        void onPageClicked(int page);
    }

    //TODO use Eventbus instead of normal callback
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            listenerOnPageClicked_MainActivity = (OnPageClickedListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement" + OnPageClickedListener.class.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        Resources r = getResources();

        calendarHeight = r.getDimension(R.dimen.calendar_height);

        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_calendar_column,
                container, false);

        final int indexInAdapter = getArguments().getInt(CalendarFragment.CalendarPagerAdapter
                .ADAPTER_POSITION);
        numActivities = getArguments().getInt(CalendarFragment.CalendarPagerAdapter
                .NUMBER_OF_ACTIVITIES);
        // DATA FOR TESTING: numActivities = indexInAdapter % (NUM_ROWS + 3);

        if (indexInAdapter % 2 == 0)
        {
            rootView.setBackgroundColor(getResources().getColor(R.color.primary_calendar));
        }
        else
        {
            rootView.setBackgroundColor(getResources().getColor(R.color.secondary_calendar));
        }

        rootView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                listenerOnPageClicked_MainActivity.onPageClicked(indexInAdapter);
            }
        });

        addTopRow(rootView);
        addRows(rootView);
        addHalfRow(rootView);
        addDateRow(rootView,
                getArguments().getString(CalendarFragment.CalendarPagerAdapter.DATE_STRING));

        return rootView;
    }

    private void addTopRow(LinearLayout rootView)
    {
        View topRow = new View(rootView.getContext());
        topRow.setBackgroundColor(getResources().getColor(R.color.green));
        topRow.setBackground(getResources().getDrawable(R.drawable.calendar_toprow_background));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, getHeightOfOneRow(CalendarFragment.NUM_ROWS));
        rootView.addView(topRow, params);
    }

    private void addRows(LinearLayout rootView)
    {
        final boolean isPastActivity = true; //TODO - replace dummy data
        boolean weekHasMoreActivitiesThanRows = false;

        if (numActivities > CalendarFragment.NUM_ROWS)
        {
            weekHasMoreActivitiesThanRows = true;
        }

        // Start at row 'NUM_ROWS' and add all rows through 1, the zero row will be added in daterow
        // and is not part of the number of rows
        for (int rowIndex = CalendarFragment.NUM_ROWS; rowIndex > 0; --rowIndex)
        {
            CalendarRowView.Builder rowBuilder = new CalendarRowView.Builder(rootView.getContext());

            if (shouldDrawCircleOnThisRow(rowIndex))
            {
                int circleType = isPastActivity ?
                        CalendarRowView.PAST_ACTIVITY : CalendarRowView.FUTURE_OR_CURRENT_ACTIVITY;

                rowBuilder.drawCircle(circleType);
            }

            CalendarRowView row = rowBuilder.build();

            row.setText((weekHasMoreActivitiesThanRows ? "+" : "") + String.valueOf(numActivities));
            row.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getHeightOfOneRow(CalendarFragment
                    .NUM_ROWS));

            rootView.addView(row, params);
        }
    }

    private void addHalfRow(LinearLayout rootView)
    {
        View halfRow = new View(rootView.getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, getHeightOfOneRow(CalendarFragment.NUM_ROWS) / 2);
        rootView.addView(halfRow, params);
    }

    private void addDateRow(LinearLayout rootView, String date)
    {
        TextView row = new TextView(rootView.getContext());
        row.setText(date);
        row.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        row.setBackground(getResources().getDrawable(R.drawable.calendar_column_row_date_bg));
        row.setPadding(0, 7, 0, 0);
        row.setGravity(Gravity.CENTER | Gravity.TOP);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, getHeightOfOneRow(CalendarFragment.NUM_ROWS));

        rootView.addView(row, params);
    }

    private int getHeightOfOneRow(int rows)
    {
        return (int) (calendarHeight / (rows + 2.5));
    }

    private boolean shouldDrawCircleOnThisRow(int rowIndex)
    {
        return (rowIndex == numActivities) || (rowIndex < numActivities && rowIndex ==
                CalendarFragment.NUM_ROWS);
    }

}