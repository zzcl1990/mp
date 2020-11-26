package edu.illinois.cs.cs125.fall2020.mp;

import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewAssertion;

import static androidx.test.espresso.action.ViewActions.click;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.robolectric.Shadows.shadowOf;

import org.robolectric.annotation.LooperMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import edu.illinois.cs.cs125.fall2020.mp.activities.CourseActivity;
import edu.illinois.cs.cs125.fall2020.mp.activities.MainActivity;
import edu.illinois.cs.cs125.fall2020.mp.application.CourseableApplication;
import edu.illinois.cs.cs125.fall2020.mp.models.Course;
import edu.illinois.cs.cs125.fall2020.mp.models.Summary;
import edu.illinois.cs.cs125.fall2020.mp.network.Client;
import edu.illinois.cs.cs125.fall2020.mp.network.Server;
import edu.illinois.cs.cs125.gradlegrader.annotations.Graded;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.truth.Truth.assertThat;
import static edu.illinois.cs.cs125.fall2020.mp.RecyclerViewMatcher.withRecyclerView;

/*
 * This is the MP1 test suite.
 * These are the same tests that we will run on your code during official grading.
 *
 * You do not need to necessarily understand all of the code below.
 * However, we have tried to write these tests in a way similar to how we would for a real Android project.
 *
 * The MP1 unit tests test that your Course model and server route work properly.
 * The MP1 integration tests test your network `Client`, your `CourseActivity`, and confirm that the course detail
 * view launches properly from the MainActivity.
 *
 * As in MP0 we strongly suggest that you work through these test in order.
 * However, unlike MP0 you will need to create some stub methods initially before anything will work.
 * The MP0 writeup will walk you through this process.
 * Once your code is compiling, next:
 *
 * 1. Create your `Course.java` model and pass `testCourseClass`
 * 2. Modify `Server.java` to add the course detail route and pass `testServerCourseRoute`
 * 3. Improve `Client.java` so that it passes `testClientGetCourse`
 * 4. Complete `CourseActivity` so that you pass `testCourseView`
 * 5. Finally, return to `MainActivity` and add the onClick handler, although you may want to complete the last
 * two steps in the opposite order
 *
 * You may modify these tests if it helps you during your development.
 * For example, you may want to add test cases, or improve the error messages.
 * However, your changes will be discarded during official grading.
 * So please be careful, since this can be a point of confusion for students when your local grade does not match up
 * with your official grade.
 */
@RunWith(Enclosed.class)
public final class MP1Test {
  private static final ObjectMapper mapper = new ObjectMapper();

  private static final List<String> summaries = new ArrayList<>();
  private static final List<String> courses = new ArrayList<>();

  @BeforeClass
  public static void setup() throws IOException {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // Load summaries JSON
    String summaryJson =
        new Scanner(MP1Test.class.getResourceAsStream("/2020_fall_summary.json"), "UTF-8").useDelimiter("\\A").next();
    JsonNode summaryNodes = mapper.readTree(summaryJson);
    for (Iterator<JsonNode> it = summaryNodes.elements(); it.hasNext(); ) {
      JsonNode node = it.next();
      summaries.add(node.toPrettyString());
    }

    // Load courses JSON
    String coursesJson =
        new Scanner(MP1Test.class.getResourceAsStream("/2020_fall.json"), "UTF-8").useDelimiter("\\A").next();
    JsonNode coursesNodes = mapper.readTree(coursesJson);
    for (Iterator<JsonNode> it = coursesNodes.elements(); it.hasNext(); ) {
      JsonNode node = it.next();
      courses.add(node.toPrettyString());
    }
  }

  public static class UnitTests {
    @BeforeClass
    public static void setup() throws IOException {
      MP1Test.setup();
    }

    /**
     * Test the Course class.
     */
    @Test(timeout = 1000L)
    @Graded(points = 10)
    public void testCourseClass() throws JsonProcessingException {
      for (String courseString : courses) {
        Course course = mapper.readValue(courseString, Course.class);
        compareCourseToSerializedCourse(course, courseString);
      }
    }

    /**
     * Test the course server route.
     */

    @Test(timeout = 10000L)
    @Graded(points = 20)
    public void testServerCourseRoute() throws IOException {
      Server.start();
      // Check the backend to make sure its responding to requests correctly
      OkHttpClient client = new OkHttpClient();
      Request request = new Request.Builder().url(CourseableApplication.SERVER_URL).head().build();
      Response response = client.newCall(request).execute();
      assertThat(response.code()).isEqualTo(HttpStatus.SC_OK);

      for (String courseString : courses) {
        ObjectNode node = (ObjectNode) mapper.readTree(courseString);
        String url = CourseableApplication.SERVER_URL + "course/" +
            node.get("year").asText() + "/" +
            node.get("semester").asText() + "/" +
            node.get("department").asText() + "/" +
            node.get("number").asText();
        Request courseRequest = new Request.Builder().url(url).build();
        Response courseResponse = client.newCall(courseRequest).execute();
        assertThat(courseResponse.code()).isEqualTo(HttpStatus.SC_OK);
        ResponseBody body = courseResponse.body();
        assertThat(body).isNotNull();
        Course course = mapper.readValue(body.string(), Course.class);
        compareCourseToSerializedCourse(course, courseString);
      }

      // Test some bad requests
      // Bad course
      request = new Request.Builder().url(CourseableApplication.SERVER_URL + "course/2020/fall/CS/88/").build();
      response = client.newCall(request).execute();
      assertThat(response.code()).isEqualTo(HttpStatus.SC_NOT_FOUND);

      // Bad URL
      request = new Request.Builder().url(CourseableApplication.SERVER_URL + "courses/2020/fall/CS/125/").build();
      response = client.newCall(request).execute();
      assertThat(response.code()).isEqualTo(HttpStatus.SC_NOT_FOUND);
    }
  }


  @SuppressWarnings("SameParameterValue")
  @RunWith(AndroidJUnit4.class)
  @LooperMode(LooperMode.Mode.PAUSED)
  public static class IntegrationTests {
    @BeforeClass
    public static void setup() throws IOException {
      MP1Test.setup();
    }

    /**
     * Test the client getCourse method
     */

    @Test(timeout = 20000L)
    @Graded(points = 20)
    public void testClientGetCourse() throws JsonProcessingException, InterruptedException, ExecutionException {
      Client client = Client.start();

      for (String summaryString : summaries) {
        Summary summary = mapper.readValue(summaryString, Summary.class);
        CompletableFuture<Course> completableFuture = new CompletableFuture<>();
        client.getCourse(summary, new Client.CourseClientCallbacks() {
          @Override
          public void courseResponse(Summary summary, Course course) {
            completableFuture.complete(course);
          }
        });
        Course course = completableFuture.get();
        compareCourseToSerializedSummary(course, summaryString);
      }
    }

    /**
     * Test CourseActivity with intent.
     */
    @Test(timeout = 10000L)
    @Graded(points = 20)
    public void testCourseView() throws JsonProcessingException {
      for (String summaryString : summaries.subList(0, 4)) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CourseActivity.class);
        intent.putExtra("COURSE", summaryString);
        ActivityScenario<CourseActivity> courseScenario = ActivityScenario.launch(intent);
        courseScenario.moveToState(Lifecycle.State.CREATED);
        courseScenario.moveToState(Lifecycle.State.RESUMED);
        ObjectNode summary = (ObjectNode) mapper.readTree(summaryString);
        onView(ViewMatchers.withText(summary.get("description").asText())).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
      }
    }

    /**
     * Test onClick CourseActivity launch from MainActivity
     */
    @Test(timeout = 10000L)
    @Graded(points = 10)
    public void testOnClickLaunch() {
      // Launch the main activity and confirm correct transition to CourseActivity
      ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
      scenario.moveToState(Lifecycle.State.CREATED);
      scenario.moveToState(Lifecycle.State.RESUMED);

      scenario.onActivity(activity -> {
        // Sanity checks
        onView(withId(R.id.recycler_view)).check(countRecyclerView(62));
        onView(withRecyclerView(R.id.recycler_view).atPosition(0))
            .check(matches(hasDescendant(withText("CS 100: Freshman Orientation"))));
        onView(withRecyclerView(R.id.recycler_view).atPosition(0)).perform(click());
        Intent started = shadowOf(activity).getNextStartedActivity();
        String courseExtra = started.getStringExtra("COURSE");
        try {
          ObjectNode node = (ObjectNode) mapper.readTree(courseExtra);
          assertThat(node.get("year").asText()).isEqualTo("2020");
          assertThat(node.get("semester").asText()).isEqualTo("fall");
          assertThat(node.get("department").asText()).isEqualTo("CS");
          assertThat(node.get("number").asText()).isEqualTo("100");
        } catch (JsonProcessingException e) {
          throw new IllegalStateException(e.getMessage());
        }
      });

    }

    // Helper functions for the test suite above.
    private ViewAssertion countRecyclerView(int expected) {
      return (v, noViewFoundException) -> {
        if (noViewFoundException != null) {
          throw noViewFoundException;
        }
        RecyclerView view = (RecyclerView) v;
        RecyclerView.Adapter<?> adapter = view.getAdapter();
        assert adapter != null;
        int a = adapter.getItemCount();
        assertThat(a).isEqualTo(expected);
      };
    }
  }

  private static void compareCourseToSerializedCourse(Course course, String serializedCourse) throws JsonProcessingException {
    compareCourseToSerializedSummary(course, serializedCourse);
    ObjectNode node = (ObjectNode) mapper.readTree(serializedCourse);
    assertThat(course.getDescription()).isEqualTo(node.get("description").asText());
  }

  private static void compareCourseToSerializedSummary(Course course, String serializedSummary) throws JsonProcessingException {
    ObjectNode node = (ObjectNode) mapper.readTree(serializedSummary);
    assertThat(course.getYear()).isEqualTo(node.get("year").asText());
    assertThat(course.getSemester()).isEqualTo(node.get("semester").asText());
    assertThat(course.getDepartment()).isEqualTo(node.get("department").asText());
    assertThat(course.getNumber()).isEqualTo(node.get("number").asText());
    assertThat(course.getTitle()).isEqualTo(node.get("title").asText());
  }
}
