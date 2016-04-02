package net.coscolla.comicstrip.ui.comics;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import net.coscolla.comicstrip.R;
import net.coscolla.comicstrip.ui.list.adapter.StripAdapter;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ComicsActivityTest {

  @Rule
  public ActivityTestRule<ComicsActivity> mActivityRule = new ActivityTestRule(ComicsActivity.class, false, false);

  @Before
  public void setup() {
    getAppContext().deleteDatabase("db");
  }

  @Test
  public void testBasicXkcd() {
    mActivityRule.launchActivity(null);

    onView(withId(R.id.list))
        .check(matches(hasDescendant(withText("xkcd"))))
        .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("xkcd")), click()));

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    onView(withId(R.id.list))
        .perform(RecyclerViewActions.scrollToHolder(new StripTitleHolderMatcher("Science")))
        .perform(RecyclerViewActions.actionOnHolderItem(new StripTitleHolderMatcher("Science"), click()));


  }

  private Context getAppContext() {
    return InstrumentationRegistry.getInstrumentation().getTargetContext();
  }

  private static class StripTitleHolderMatcher extends TypeSafeMatcher<RecyclerView.ViewHolder> {
    private final String title;

    public StripTitleHolderMatcher(String title) {
      this.title = title;
    }

    @Override
    public boolean matchesSafely(RecyclerView.ViewHolder viewHolder) {
      StripAdapter.StripViewHolder holder = (StripAdapter.StripViewHolder) viewHolder;
      return holder.data.title.equalsIgnoreCase(title);
    }

    @Override
    public void describeTo(Description description) {
      description.appendText("holder contains title " + title);
    }
  }
}
