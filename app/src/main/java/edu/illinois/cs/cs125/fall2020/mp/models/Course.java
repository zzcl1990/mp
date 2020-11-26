package edu.illinois.cs.cs125.fall2020.mp.models;

/**  Model holding the course information shown in the course activity. */
public class Course extends Summary {
  // Course description
  private String description;

  /**
   * Get the description for this Course.
   *
   * @return the description for this Course
   */
  public String getDescription() {
    return description;
  }

  // Course creditHours
  private String creditHours;

  /**
   * Get the creditHours for this Course.
   *
   * @return the creditHours for this Course
   */
  public final  String getCreditHours() {
    return creditHours;
  }

  // Course courseSectionInformation
  private String courseSectionInformation;

  /**
   * Get the courseSectionInformation for this Course.
   *
   * @return the courseSectionInformation for this Course
   */
  public final String getCourseSectionInformation() {
    return courseSectionInformation;
  }
}
