package us.mindbuilders.petemit.timegoalie;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

import us.mindbuilders.petemit.timegoalie.dummy.DummyContent;

/**
 * Created by Peter on 9/15/2017.
 */

public class GoalRecyclerViewAdapter extends RecyclerView.Adapter<GoalRecyclerViewAdapter.GoalViewHolder> {

    private final List<DummyContent.DummyItem> mValues;
    private View.OnClickListener onClickListener;


    public GoalRecyclerViewAdapter(List<DummyContent.DummyItem> items, View.OnClickListener onClickListener) {
        mValues = items;
        this.onClickListener = onClickListener;
    }

    @Override
    public GoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.goal_list_content_less, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GoalViewHolder holder, int position) {
          holder.mItem = mValues.get(position);
     //   holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class GoalViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private ToggleButton pencil;
        private LinearLayout editButtons;
       // public final TextView mIdView;
        public final TextView mContentView;
        public DummyContent.DummyItem mItem;

        public GoalViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.tv_goal_title);
            pencil=(ToggleButton) view.findViewById(R.id.pencil_button);
            editButtons=(LinearLayout) view.findViewById(R.id.edit_button_ll);
            pencil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editButtons.getVisibility()!=View.VISIBLE) {
                        editButtons.setVisibility(View.VISIBLE);
                    }
                    else {
                        editButtons.setVisibility(View.GONE);
                    }
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}

