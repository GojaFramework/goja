package goja.rapid.db;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import static goja.rapid.db.FindBy.findBy;

public class FindByTest {

    @Test
    public void testMain() throws Exception {

        MatcherAssert.assertThat("title = ?", Matchers.equalTo(findBy("byTitle")));
        MatcherAssert.assertThat("LOWER(title) LIKE ?", Matchers.equalTo(findBy("byTitleLike")));
        MatcherAssert.assertThat("author IS NULL", Matchers.equalTo(findBy("byAuthorIsNull")));
        MatcherAssert.assertThat("LOWER(title) LIKE ? AND author = ?",
                Matchers.equalTo(findBy("byTitleLikeAndAuthor")));
        MatcherAssert.assertThat("name = ? ORDER BY name",
                Matchers.equalTo(findBy("ByNameOrderByName")));
        MatcherAssert.assertThat("name < ?", Matchers.equalTo(findBy("ByNameLessThan")));
        MatcherAssert.assertThat("name <= ?", Matchers.equalTo(findBy("ByNameLessThanEquals")));
        MatcherAssert.assertThat("name > ?", Matchers.equalTo(findBy("ByNameGreaterThan")));
        MatcherAssert.assertThat("name >= ?", Matchers.equalTo(findBy("ByNameGreaterThanEquals")));
        MatcherAssert.assertThat("LOWER(name) LIKE LOWER(?)", Matchers.equalTo(findBy("ByNameIlike")));
        MatcherAssert.assertThat("name LIKE ?", Matchers.equalTo(findBy("ByNameElike")));
        MatcherAssert.assertThat("name <> ?", Matchers.equalTo(findBy("ByNameNotEqual")));
        MatcherAssert.assertThat("name BETWEEN ? AND ?", Matchers.equalTo(findBy("ByNameBetween")));
        MatcherAssert.assertThat("name IS NOT NULL", Matchers.equalTo(findBy("ByNameIsNotNull")));
    }

    @Test
    public void testBetween() throws Exception {
        System.out.println("findBy(\"byDateBetween\") = " + findBy("byDateBetween"));
        System.out.println("findBy(\"byDateIlike\") = " + findBy("byDateIlike"));
    }
}