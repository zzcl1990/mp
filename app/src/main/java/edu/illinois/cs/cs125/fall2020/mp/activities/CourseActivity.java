package edu.illinois.cs.cs125.fall2020.mp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.illinois.cs.cs125.fall2020.mp.application.CourseableApplication;
import edu.illinois.cs.cs125.fall2020.mp.databinding.ActivityCourseBinding;
import edu.illinois.cs.cs125.fall2020.mp.models.Course;
import edu.illinois.cs.cs125.fall2020.mp.R;
import edu.illinois.cs.cs125.fall2020.mp.models.Rating;
import edu.illinois.cs.cs125.fall2020.mp.models.Summary;
import edu.illinois.cs.cs125.fall2020.mp.network.Client;

/** CourseActivity activity showing the course detail. */
public class CourseActivity extends AppCompatActivity
        implements Client.CourseClientCallbacks {
  // binding to the layout in activity_course.xml
  private ActivityCourseBinding binding;

  // mapper to handle properties
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Called when this activity is created.
   *
   * <p>Because this is the main activity for this app, this method is called when the app is
   * started, and any time that this view is shown.
   *
   * @param savedInstanceState saved instance state, currently unused and always empty or null
   */
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Retrieve the API client from the application and initiate a course  request
    CourseableApplication application = (CourseableApplication) getApplication();
    String courseExtra = getIntent().getStringExtra("COURSE");
    binding = DataBindingUtil.setContentView(this, R.layout.activity_course);
    ObjectNode node = null;
    try {
      node = (ObjectNode) objectMapper.readTree(courseExtra);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    String year = node.get("year").asText();
    String semester = node.get("semester").asText();
    String department = node.get("department").asText();
    String number = node.get("number").asText();
    Summary summary = new Summary(year, semester, department, number, "title");
    application.getCourseClient().getCourse(summary, this);

    application.getCourseClient().getRating(summary, application.getClientID(), this);

    // ceshi
    application.getCourseClient().postRating(summary, new Rating("1111", 3), this);
  }

  /**
   * Callback called when the client has retrieved the course.
   *
   * @param summary the summary that was retrieved
   * @param course the course returned from getCourseAPI
   */
  @Override
  public void courseResponse(final Summary summary, final Course course) {
    binding.textView2.setText(course.getFormatText());
    binding.textView.setText(course.getDescription());
  }

  @Override
  public void yourRating(final Summary summary, final Rating rating) {

  }
}










