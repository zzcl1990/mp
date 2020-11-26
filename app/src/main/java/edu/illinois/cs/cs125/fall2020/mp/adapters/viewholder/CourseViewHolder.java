package edu.illinois.cs.cs125.fall2020.mp.adapters.viewholder;

import androidx.annotation.NonNull;
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;
import edu.illinois.cs.cs125.fall2020.mp.adapters.CourseListAdapter;
import edu.illinois.cs.cs125.fall2020.mp.databinding.ItemCourseBinding;
import edu.illinois.cs.cs125.fall2020.mp.models.Summary;

/**
 * View holder for a single course summary in the list.
 *
 * <p>You should not need to modify this file.
 */
public final class CourseViewHolder extends SortedListAdapter.ViewHolder<Summary> {
  private final ItemCourseBinding binding;

  /**
   * Create a CourseViewHolder.
   *
   * @param setBinding the course binding to attach to
   * @param setListener the listener for click events
   */
  public CourseViewHolder(
      final ItemCourseBinding setBinding, final CourseListAdapter.Listener setListener) {
    super(setBinding.getRoot());
    binding = setBinding;
    binding.setListener(setListener);
  }

  @Override
  protected void performBind(@NonNull final Summary model) {
    binding.setModel(model);
  }
}
