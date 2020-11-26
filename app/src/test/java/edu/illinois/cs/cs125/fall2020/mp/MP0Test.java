package edu.illinois.cs.cs125.fall2020.mp;

import static android.os.Looper.getMainLooper;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.truth.Truth.assertThat;
import static edu.illinois.cs.cs125.fall2020.mp.RecyclerViewMatcher.withRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.robolectric.Shadows.shadowOf;

import android.view.View;
import android.widget.SearchView;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import edu.illinois.cs.cs125.fall2020.mp.activities.MainActivity;
import edu.illinois.cs.cs125.fall2020.mp.application.CourseableApplication;
import edu.illinois.cs.cs125.fall2020.mp.models.Summary;
import edu.illinois.cs.cs125.gradlegrader.annotations.Graded;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.annotation.LooperMode;

/*
 * This is the MP0 test suite.
 * These are the same tests that we will run on your code during official grading.
 *
 * You do not need to necessarily understand all of the code below.
 * However, we have tried to write these tests in a way similar to how we would for a real Android project.
 *
 * The test suite below tests your code in two ways.
 * First, we directly test certain methods or other functionality.
 * For example, for MP0 we test that your summary comparator and filter function work as expected.
 * These are sometimes known as unit tests, since they test one unit of functionality.
 *
 * Second, we test your app's UI and overall functionality by simulating direct interaction with its UI.
 * For example, for MP0, `testSummaryView` checks to make sure that the right number of summaries is loaded when the
 * activity starts, and that the summary text is displayed properly.
 * `testSearch` checks whether changes to the search box cause the list of displayed summaries to update properly.
 * These are sometimes known as integration or end-to-end tests, since they test the multiple parts of your app
 * at the same time.
 *
 * In general you should finish the unit tests before working on the integration tests, since the integration
 * tests will usually rely on functionality tested separately by the unit tests.
 *
 * You may modify these tests if it helps you during your development.
 * For example, you may want to add test cases, or improve the error messages.
 * However, your changes will be discarded during official grading.
 * So please be careful, since this can be a point of confusion for students when your local grade does not match up
 * with your official grade.
 */
@RunWith(Enclosed.class)
public final class MP0Test {

  public static class UnitTests {
    Summary BADM100 = new Summary("2020", "Fall", "BADM", "100", "Introduction to Badminton");
    Summary BADM200 = new Summary("2020", "Fall", "BADM", "200", "Intermediate Badminton");
    Summary CS125 = new Summary("2020", "Fall", "CS", "125", "Introduction to Computer Science");
    Summary CS125Spring =
        new Summary("2021", "Spring", "CS", "125", "Introduction to Computer Science");
    Summary CS225 = new Summary("2020", "Fall", "CS", "225", "Data Structures and Algorithms");
    Summary CS498VR = new Summary("2020", "Fall", "CS", "498", "Virtual Reality");
    Summary CS498CB = new Summary("2020", "Fall", "CS", "498", "Computational Badminton");

    /** Test the summary comparator (Summary.COMPARATOR). */
    @Test(timeout = 1000L)
    @Graded(points = 20)
    public void testSummaryComparator() {
      // Self-comparisons should return 0
      assertThat(Summary.COMPARATOR.compare(CS125, CS125)).isEqualTo(0);
      assertThat(Summary.COMPARATOR.compare(BADM100, BADM100)).isEqualTo(0);
      assertThat(Summary.COMPARATOR.compare(CS225, CS225)).isEqualTo(0);

      // Various comparisons
      assertThat(Summary.COMPARATOR.compare(CS125, BADM100)).isGreaterThan(0);
      assertThat(Summary.COMPARATOR.compare(BADM100, CS125)).isLessThan(0);

      assertThat(Summary.COMPARATOR.compare(CS125, BADM200)).isGreaterThan(0);
      assertThat(Summary.COMPARATOR.compare(BADM200, CS125)).isLessThan(0);

      assertThat(Summary.COMPARATOR.compare(CS225, CS125)).isGreaterThan(0);
      assertThat(Summary.COMPARATOR.compare(CS125, CS225)).isLessThan(0);

      assertThat(Summary.COMPARATOR.compare(CS498VR, CS498CB)).isGreaterThan(0);
      assertThat(Summary.COMPARATOR.compare(CS498CB, CS498VR)).isLessThan(0);

      // Should ignore semester
      assertThat(Summary.COMPARATOR.compare(CS125, CS125Spring)).isEqualTo(0);
      assertThat(Summary.COMPARATOR.compare(CS125Spring, CS125)).isEqualTo(0);
    }

    /** Test summary filtering (Summary.filter). */
    @Test(timeout = 1000L)
    @Graded(points = 20)
    public void testSummaryFilter() {
      assertThat(Summary.filter(Collections.emptyList(), "test")).hasSize(0);
      assertThat(Summary.filter(Collections.singletonList(BADM100), "intro")).hasSize(1);
      assertThat(Summary.filter(Collections.singletonList(BADM100), "xyz")).hasSize(0);
      assertThat(Summary.filter(Arrays.asList(CS125, CS125Spring), "125")).hasSize(2);
      assertThat(Summary.filter(Arrays.asList(CS125, CS125Spring), "Terrible")).hasSize(0);
      assertThat(Summary.filter(Arrays.asList(CS125, CS125Spring, CS225), "25")).hasSize(3);
      assertThat(
              Summary.filter(
                  Arrays.asList(BADM100, CS125, CS125Spring, CS225, CS498VR, CS498CB), "Badminton"))
          .hasSize(2);
    }
  }

  @RunWith(AndroidJUnit4.class)
  @LooperMode(LooperMode.Mode.PAUSED)
  public static class IntegrationTests {
    /** Test summary view to make sure that the correct courses are displayed in the right order. */
    @Test(timeout = 10000L)
    @Graded(points = 20)
    public void testSummaryView() {
      // Check that the right number of summaries are displayed
      onView(withId(R.id.recycler_view)).check(countRecyclerView(62));

      // Check that full summary titles are shown, and the the order is correct
      onView(withRecyclerView(R.id.recycler_view).atPosition(0))
          .check(matches(hasDescendant(withText("CS 100: Freshman Orientation"))));
      onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.scrollToPosition(61));
      onView(withRecyclerView(R.id.recycler_view).atPosition(61))
          .check(matches(hasDescendant(withText("CS 598: Special Topics"))));
    }

    /**
     * Test search interaction to make sure that the correct courses are shown when the search
     * feature is used.
     *
     * @throws InterruptedException if Thread.sleep is interrupted
     */
    @Test(timeout = 10000L)
    @Graded(points = 20)
    public void testSearch() throws InterruptedException {
      // Check that the right number of courses are displayed initially
      onView(withId(R.id.recycler_view)).check(countRecyclerView(62));

      // CS has no super boring courses!
      // Some manual delay is required for these tests to run reliably
      onView(withId(R.id.search)).perform(searchFor("Super Boring Course", false));
      shadowOf(getMainLooper()).runToEndOfTasks();
      Thread.sleep(100);
      onView(withId(R.id.recycler_view)).check(countRecyclerView(0));

      // CS 125 should return one result
      onView(withId(R.id.search)).perform(searchFor("CS 125", false));
      shadowOf(getMainLooper()).runToEndOfTasks();
      Thread.sleep(100);
      onView(withId(R.id.recycler_view)).check(countRecyclerView(1));

      // intro matches several courses
      onView(withId(R.id.search)).perform(searchFor("intro", true));
      shadowOf(getMainLooper()).runToEndOfTasks();
      Thread.sleep(100);
      onView(withId(R.id.recycler_view)).check(countRecyclerView(8));
    }

    // Start the MainActivity for testing
    @Before
    public void startActivity() throws IOException {
      // Create a new activity scenario and start a MainActivity
      ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
      scenario.moveToState(Lifecycle.State.CREATED);
      scenario.moveToState(Lifecycle.State.RESUMED);

      // Check the backend to make sure its responding to requests correctly
      OkHttpClient client = new OkHttpClient();
      Request request = new Request.Builder().url(CourseableApplication.SERVER_URL).head().build();
      Response response = client.newCall(request).execute();
      assertThat(response.code()).isEqualTo(HttpStatus.SC_OK);
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
        assertThat(adapter.getItemCount()).isEqualTo(expected);
      };
    }

    private ViewAction searchFor(String query, boolean submit) {
      return new ViewAction() {
        @SuppressWarnings("unchecked")
        @Override
        public Matcher<View> getConstraints() {
          return allOf(isDisplayed());
        }

        @Override
        public String getDescription() {
          if (submit) {
            return "Set query to " + query + " and submit";
          } else {
            return "Set query to " + query + " but don't submit";
          }
        }

        @Override
        public void perform(UiController uiController, View view) {
          SearchView v = (SearchView) view;
          v.setQuery(query, submit);
        }
      };
    }
  }
}
