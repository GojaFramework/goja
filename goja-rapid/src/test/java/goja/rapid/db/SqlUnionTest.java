package goja.rapid.db;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class SqlUnionTest {

    @Test
    public void testOrderBy() throws Exception {
        final SqlSelect first_select =
                SqlSelect.create().select("f1,f2").from("t1").andWhere("f1=2").orderBy("order desc");
        SqlUnion union = new SqlUnion().union(first_select).orderBy("f1");
        String dest = "(SELECT f1,f2 FROM t1 WHERE f1=2 ORDER BY order desc) ORDER BY f1";
        MatcherAssert.assertThat(dest, Matchers.equalTo(union.toString()));
    }

    @Test
    public void testLimit() throws Exception {
        final SqlSelect first_select =
                SqlSelect.create().select("f1,f2").from("t1").andWhere("f1=2").orderBy("order desc");
        SqlUnion union = new SqlUnion().union(first_select).limit(10);
        String dest = "(SELECT f1,f2 FROM t1 WHERE f1=2 ORDER BY order desc) LIMIT 10";
        MatcherAssert.assertThat(dest, Matchers.equalTo(union.toString()));
    }

    @Test
    public void testUnion() throws Exception {
        final SqlSelect first_select =
                SqlSelect.create().select("f1,f2").from("t1").andWhere("f1=2").orderBy("order desc");
        final SqlSelect sencond_select =
                SqlSelect.create().select("f1,f2").from("t2").andWhere("f2=2").orderBy("order desc");
        SqlUnion union = new SqlUnion().union(first_select).union(sencond_select).orderBy("f1");
        String dest =
                "(SELECT f1,f2 FROM t1 WHERE f1=2 ORDER BY order desc) UNION (SELECT f1,f2 FROM t2 WHERE f2=2 ORDER BY order desc) ORDER BY f1";
        MatcherAssert.assertThat(dest, Matchers.equalTo(union.toString()));
    }

    @Test
    public void testUnionAll() throws Exception {
        final SqlSelect first_select =
                SqlSelect.create().select("f1,f2").from("t1").andWhere("f1=2").orderBy("order desc");
        final SqlSelect sencond_select =
                SqlSelect.create().select("f1,f2").from("t2").andWhere("f2=2").orderBy("order desc");
        SqlUnion union = new SqlUnion().unionAll(first_select, sencond_select);
        String dest =
                "(SELECT f1,f2 FROM t1 WHERE f1=2 ORDER BY order desc) UNION ALL (SELECT f1,f2 FROM t2 WHERE f2=2 ORDER BY order desc)";
        MatcherAssert.assertThat(dest, Matchers.equalTo(union.toString()));
    }
}