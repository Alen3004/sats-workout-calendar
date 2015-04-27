package se.greatbrain.sats.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.greatbrain.sats.Activiteee;
import se.greatbrain.sats.R;
import se.greatbrain.sats.model.realm.TrainingActivity;

public class WorkoutListAdapter extends BaseAdapter implements StickyListHeadersAdapter
{
    private static final String TAG = "WorkoutListAdapter";

    private static final int NUMBER_OF_VIEW_TYPES_SERVED_BY_ADAPTER = 4;

    private final List<Activiteee> listItems;
    private int numberOfListItems;
    private final Map<Integer, Integer> listItemPositionToWeek = new HashMap<>();

    private final LayoutInflater inflater;

    public WorkoutListAdapter(Activity activity, List<Activiteee> listItems)
    {
        this.listItems = listItems;
        inflater = activity.getLayoutInflater();

        numberOfListItems = listItems.size();
        for (int i = 0; i < listItems.size(); ++i)
        {
            Activiteee activiteee = listItems.get(i);
            final int weekHash = (activiteee.year * 100) + activiteee.week;
            listItemPositionToWeek.put(i, weekHash);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Activiteee activiteee = ((Activiteee) getItem(position));

        final boolean activityOnPositionIsCompletedOrInThePast = activiteee.activityStatus ==
                Activiteee.COMPLETED || activiteee.dateHasPassed();

        if (convertView == null)
        {
            if (activityOnPositionIsCompletedOrInThePast)
            {
                convertView = inflatePastActivityView(parent);
            }
            else
            {
                if (activiteee.activityType == Activiteee.GROUP)
                {
                    convertView = inflateGroupActivityView(parent);
                }
                else // trainingActivity type is private
                {
                    convertView = inflatePrivateActivityView(parent);
                }
            }
        }
        if (activityOnPositionIsCompletedOrInThePast)
        {
            setUpPastActivityView(convertView, position);
        }
        else
        {
            if (activiteee.activityType == Activiteee.GROUP)
            {
                setUpGroupActivityView(convertView);
            }
            else // trainingActivity type is private
            {
                setUpPrivateActivityView(convertView, position);
            }
        }

        return convertView;
    }

    /**
     * View inflation
     */

    private View inflatePastActivityView(ViewGroup parent)
    {
        View inflateMe = inflater.inflate(R.layout.listrow_detail_completed, parent,
                false);

        PastActivityViewHolder pastActivityViewHolder = new PastActivityViewHolder();
        pastActivityViewHolder.title = ((TextView) inflateMe.findViewById(R.id
                .listrow_detail_completed_class_name));
        pastActivityViewHolder.date = ((TextView) inflateMe.findViewById(R.id
                .listrow_detail_completed_class_date));
        pastActivityViewHolder.comment = ((TextView) inflateMe.findViewById(R.id
                .listrow_detail_completed_class_comment));
        pastActivityViewHolder.completed = ((TextView) inflateMe.findViewById(R.id
                .listrow_detail_completed_class_completed));

        inflateMe.setTag(pastActivityViewHolder);

        return inflateMe;
    }

    private View inflateGroupActivityView(ViewGroup parent)
    {
        View inflateMe = inflater.inflate(R.layout.listrow_detail_booked_class, parent,
                false);

        GroupActivityViewHolder bookedClassViewHolder = new GroupActivityViewHolder();
        bookedClassViewHolder.title = ((TextView) inflateMe.findViewById(R.id
                .listrow_detail_booked_class_name));
        bookedClassViewHolder.duration = ((TextView) inflateMe.findViewById(R.id
                .listrow_detail_booked_class_duration_minutes));
        //TODO add more

        inflateMe.setTag(bookedClassViewHolder);

        return inflateMe;
    }

    private View inflatePrivateActivityView(ViewGroup parent)
    {
        View inflateMe = inflater.inflate(R.layout.listrow_detail_booked_private, parent,
                false);

        PrivateActivityViewHolder viewHolder = new PrivateActivityViewHolder();
        viewHolder.title = ((TextView) inflateMe.findViewById(R.id
                .listrow_detail_booked_private_name));
        viewHolder.duration = ((TextView) inflateMe.findViewById(R.id
                .listrow_detail_booked_private_duration));
        viewHolder.comment = ((TextView) inflateMe.findViewById(R.id
                .listrow_detail_booked_private_comment));

        inflateMe.setTag(viewHolder);

        return inflateMe;
    }

    /**
     * View setup
     */

    private void setUpPastActivityView(View convertView, int position)
    {
        Activiteee activiteee = (Activiteee) getItem(position);
        TrainingActivity trainingActivity = activiteee.trainingActivity;

        String title = trainingActivity.getSubType();
        String date = trainingActivity.getDate();
        String comment = "Kommentar: " + trainingActivity.getComment();
        String completed = activiteee.activityStatus == Activiteee.COMPLETED ?
                "Avklarad!" : "Avklarad?";

        PastActivityViewHolder pastActivityViewHolder =
                (PastActivityViewHolder) convertView.getTag();
        pastActivityViewHolder.title.setText(title);
        pastActivityViewHolder.date.setText(date);
        pastActivityViewHolder.comment.setText(comment);
        pastActivityViewHolder.completed.setText(completed);
    }

    private void setUpGroupActivityView(View convertView)
    {
        //TODO get data (like the method above/below)

        GroupActivityViewHolder groupActivityViewHolder =
                (GroupActivityViewHolder) convertView.getTag();
        groupActivityViewHolder.title.setText("FISTK");
        groupActivityViewHolder.duration.setText("69");
    }

    private void setUpPrivateActivityView(View convertView, int position)
    {
        Activiteee activiteee = (Activiteee) getItem(position);
        TrainingActivity trainingActivity = activiteee.trainingActivity;

        String title = trainingActivity.getSubType();
        String duration = trainingActivity.getDurationInMinutes() + " min";
        String comment = "Kommentar: " + trainingActivity.getComment();

        PrivateActivityViewHolder privateActivityViewHolder =
                (PrivateActivityViewHolder) convertView.getTag();
        privateActivityViewHolder.title.setText(title);
        privateActivityViewHolder.duration.setText(duration);
        privateActivityViewHolder.comment.setText(comment);

    }

    /**
     * Below are view holders that contain all the different views that will be set in the
     * different layouts.
     * <p/>
     * These are used like this:
     * bookedClassActivityViewHolder.title.setText("Some text");
     * Because it is faster than using findViewById, like this:
     * ((TextField)view.findViewById(R.id.listrow_detail_booked_class_name)).setText("Some text");
     */

    class HeaderViewHolder
    {
        TextView text;
    }

    class PastActivityViewHolder
    {
        TextView title;
        TextView date;
        TextView comment;
        TextView completed;
    }

    class GroupActivityViewHolder
    {
        TextView title;
        TextView duration;
        //TODO add more
    }

    class PrivateActivityViewHolder
    {
        TextView title;
        TextView duration;
        TextView comment;
    }

    /**
     * Overrides required by BaseAdapter
     */

    @Override
    public int getCount()
    {
        return numberOfListItems;
    }

    @Override
    public Object getItem(int position)
    {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * Overrides required by StickyListHeaders
     */

    @Override
    public long getHeaderId(int position)
    {
        return listItemPositionToWeek.get(position);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        HeaderViewHolder holder;
        if (convertView == null)
        {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.listrow_group, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.listrow_group_title);
            convertView.setTag(holder);
        }
        else
        {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        String headerText = String.valueOf("Year " + listItems.get(position).year + " Week " +
                listItems.get(position).week);
        holder.text.setText(headerText);
        return convertView;
    }

    /**
     * Overrides required in lists containing views of multiple different layouts
     */

    @Override
    public int getViewTypeCount()
    {
        return NUMBER_OF_VIEW_TYPES_SERVED_BY_ADAPTER;
    }

    @Override
    public int getItemViewType(int position)
    {
        Activiteee activiteee = (Activiteee) getItem(position);
        final boolean activityOnPositionIsCompletedOrInThePast = activiteee.activityStatus ==
                Activiteee.COMPLETED || activiteee.dateHasPassed();

        if (activityOnPositionIsCompletedOrInThePast)
        {
            return activiteee.COMPLETED;
        }
        else
        {
            return activiteee.activityType;
        }

    }
}
