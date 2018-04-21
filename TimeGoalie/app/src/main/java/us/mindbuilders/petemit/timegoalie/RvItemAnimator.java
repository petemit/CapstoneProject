package us.mindbuilders.petemit.timegoalie;

import android.animation.AnimatorSet;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Peter on 4/21/2018.
 */

public class RvItemAnimator extends DefaultItemAnimator {
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    public static final String ACTION_CHECKED_GOALBOX = "check_goal_box";
    public static final String ACTION_UNCHECKED_GOALBOX = "uncheck_goal_box";
    Map<RecyclerView.ViewHolder, AnimatorSet> ballAnimationsMap = new HashMap<>();

    @Override
    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state,
                                                     @NonNull RecyclerView.ViewHolder viewHolder,
                                                     int changeFlags, @NonNull List<Object> payloads) {
            if (changeFlags == FLAG_CHANGED) {
                for (Object payload : payloads) {
                    if (payload instanceof String) {
                        return new BallItemHolderInfo((String) payload);
                    }
                }
            }

            return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
    }


    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder,
                                 @NonNull RecyclerView.ViewHolder newHolder,
                                 @NonNull ItemHolderInfo preInfo,
                                 @NonNull ItemHolderInfo postInfo) {
        cancelCurrentAnimationIfExists(newHolder);

        if (preInfo instanceof BallItemHolderInfo) {
            BallItemHolderInfo ballItemHolderInfo = (BallItemHolderInfo) preInfo;
            GoalRecyclerViewAdapter.GoalViewHolder holder = (GoalRecyclerViewAdapter.GoalViewHolder) newHolder;

            animateBall(holder);

        }

        return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
    }

    private void animateBall(GoalRecyclerViewAdapter.GoalViewHolder holder) {

    }

    private void cancelCurrentAnimationIfExists(RecyclerView.ViewHolder item) {
        if (ballAnimationsMap.containsKey(item)) {
            ballAnimationsMap.get(item).cancel();
        }
    }

    public static class BallItemHolderInfo extends ItemHolderInfo {
        public String updateAction;

        public BallItemHolderInfo(String updateAction) {
            this.updateAction = updateAction;
        }
    }
}
