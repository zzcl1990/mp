package edu.illinois.cs.cs125.fall2020.mp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import edu.illinois.cs.cs125.fall2020.mp.adapters.viewholder.CourseViewHolder;
import edu.illinois.cs.cs125.fall2020.mp.databinding.ItemCourseBinding;
import edu.illinois.cs.cs125.fall2020.mp.models.Summary;

/**
 * Adapter for the course list.
 *
 * <p>You should not need to modify this file.
 */
public final class CourseListAdapter extends SortedListAdapter<Summary> {

  /** Listener interface for course list click events. */
  public interface Listener {
    /**
     * Called when a course is clicked.
     *
     * @param course the course that was clicked
     */
    void onCourseClicked(Summary course);
  }

  private final Listener listener;

  /**
   * Create a CourseListAdapter.
   *
   * @param context activity context
   * @param setListener listener for click events
   */
  public CourseListAdapter(final Context context, final Listener setListener) {
    super(context, Summary.class, Summary.COMPARATOR);
    listener = setListener;
  }

  @NonNull
  @Override
  protected ViewHolder<? extends Summary> onCreateViewHolder(
      @NonNull final LayoutInflater inflater, @NonNull final ViewGroup parent, final int viewType) {
    final ItemCourseBinding binding = ItemCourseBinding.inflate(inflater, parent, false);
    return new CourseViewHolder(binding, listener);
  }
}
