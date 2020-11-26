package edu.illinois.cs.cs125.fall2020.mp.models;

import androidx.annotation.NonNull;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Iterator;
import java.io.Serializable;

/**
 * Model holding the course summary information shown in the course list.
 *
 * <p>You will need to complete this model for MP0.
 */
public class Summary implements SortedListAdapter.ViewModel, Serializable {
  private String year;

  /**
   * Get the year for this Summary.
   *
   * @return the year for this Summary
   */
  public final String getYear() {
    return year;
  }

  private String semester;

  /**
   * Get the semester for this Summary.
   *
   * @return the semester for this Summary
   */
  public final String getSemester() {
    return semester;
  }

  private String department;

  /**
   * Get the department for this Summary.
   *
   * @return the department for this Summary
   */
  public final String getDepartment() {
    return department;
  }

  private String number;

  /**
   * Get the number for this Summary.
   *
   * @return the number for this Summary
   */
  public final String getNumber() {
    return number;
  }

  private String title;

  /**
   * Get the title for this Summary.
   *
   * @return the title for this Summary
   */
  public final String getTitle() {
    return title;
  }

  private String formatText;

  /**
   * Get the formatText for this Summary.
   *
   * @return the formatText for this Summary
   */
  public final String getFormatText() {
    return department + " " + number + ":" + " " + title;
  }

  /**
   * Create an empty Summary.
   */
  @SuppressWarnings({"unused", "RedundantSuppression"})
  public Summary() {}

  /**
   * Create a Summary with the provided fields.
   *
   * @param setYear       the year for this Summary
   * @param setSemester   the semester for this Summary
   * @param setDepartment the department for this Summary
   * @param setNumber     the number for this Summary
   * @param setTitle      the title for this Summary
   */
  public Summary(
      final String setYear,
      final String setSemester,
      final String setDepartment,
      final String setNumber,
      final String setTitle) {
    year = setYear;
    semester = setSemester;
    department = setDepartment;
    number = setNumber;
    title = setTitle;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof Summary)) {
      return false;
    }
    Summary course = (Summary) o;
    return Objects.equals(year, course.year)
        && Objects.equals(semester, course.semester)
        && Objects.equals(department, course.department)
        && Objects.equals(number, course.number);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(year, semester, department, number);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> boolean isSameModelAs(@NonNull final T model) {
    return equals(model);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> boolean isContentTheSameAs(@NonNull final T model) {
    return equals(model);
  }

  /**
   * comparator.
   */
  public static final Comparator<Summary> COMPARATOR = new Comparator<Summary>() {
    @Override
    public int compare(final Summary o1, final Summary o2) {
      // compare department
      int resByCompareDepartment = o1.getDepartment().compareTo(o2.getDepartment());
      if (resByCompareDepartment == 0) {
        // compare number
        int resByCompareNumber = o1.number.compareTo(o2.number);
        if (resByCompareNumber == 0) {
          // compare title
          return o1.title.compareTo(o2.title);
        } else {
          return resByCompareNumber;
        }
      } else {
        return resByCompareDepartment;
      }
    }
  };


  public static List<Summary> filter(
          @NonNull final List<Summary> courses, @NonNull final String text) {
    // save the result of filter
    List<Summary> result = new ArrayList<Summary>();
    // 遍历
    Iterator<Summary> iterator = courses.iterator();
    while (iterator.hasNext()) {
      Summary summary = iterator.next();
      if (summary.department.toLowerCase().contains(text.toLowerCase())
              || summary.number.toLowerCase().contains(text.toLowerCase())
              || summary.title.toLowerCase().contains(text.toLowerCase())) {
        result.add(summary);
      }
    }
    return result;
  }
}
