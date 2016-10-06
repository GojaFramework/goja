package goja.rapid.db;

import com.google.common.collect.Lists;

import goja.core.db.FindBy;
import goja.core.db.SqlQuery;
import goja.core.db.SqlSelect;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class SqlNodeSelectTest {
    @Test
    public void testSelect() throws Exception {
        String s = SqlSelect.create().select("f1,f2").from("t1").toString();
        String dest = "SELECT f1,f2 FROM t1";
        MatcherAssert.assertThat(dest, Matchers.equalTo(s));
    }

    @Test
    public void testFrom() throws Exception {
        String s = SqlSelect.create().select("f1,f2").from("t1").toString();
        String dest = "SELECT f1,f2 FROM t1";
        MatcherAssert.assertThat(dest, Matchers.equalTo(s));
    }

    @Test
    public void testInnerJoin() throws Exception {
        String s = SqlSelect.create().select("f1,f2").from("t1").innerJoin("t1=t2").toString();
        String dest = "SELECT f1,f2 FROM t1 INNER JOIN t1=t2";
        MatcherAssert.assertThat(dest, Matchers.equalTo(s));
    }

    @Test
    public void testLeftJoin() throws Exception {
        String s = SqlSelect.create().select("f1,f2").from("t1").leftJoin("t1=t2").toString();
        String dest = "SELECT f1,f2 FROM t1 LEFT JOIN t1=t2";
        MatcherAssert.assertThat(dest, Matchers.equalTo(s));
    }

    @Test
    public void testWhere() throws Exception {
        String s = SqlSelect.create().select("f1,f2").from("t1").where("f1=1").toString();
        String dest = "SELECT f1,f2 FROM t1 WHERE f1=1";
        MatcherAssert.assertThat(dest, Matchers.equalTo(s));
    }

    @Test
    public void testAndWhere() throws Exception {
        String s =
                SqlSelect.create().select("f1,f2").from("t1").where("f1=1").andWhere("f2=?").toString();
        String dest = "SELECT f1,f2 FROM t1 WHERE f1=1 AND f2=?";
        MatcherAssert.assertThat(dest, Matchers.equalTo(s));
    }

    @Test
    public void testOrWhere() throws Exception {
        String s =
                SqlSelect.create().select("f1,f2").from("t1").where("f1=1").orWhere("f2=?").toString();
        String dest = "SELECT f1,f2 FROM t1 WHERE f1=1 OR f2=?";
        MatcherAssert.assertThat(dest, Matchers.equalTo(s));
    }

    @Test
    public void testGroupBy() throws Exception {
        String s = SqlSelect.create().select("f1,f2").from("t1").groupBy("f2").toString();
        String dest = "SELECT f1,f2 FROM t1 GROUP BY f2";
        MatcherAssert.assertThat(dest, Matchers.equalTo(s));
    }

    @Test
    public void testOrderBy() throws Exception {
        String s = SqlSelect.create().select("f1,f2").from("t1").orderBy("f2").toString();
        String dest = "SELECT f1,f2 FROM t1 ORDER BY f2";
        MatcherAssert.assertThat(dest, Matchers.equalTo(s));
    }

    @Test
    public void testLimit() throws Exception {
        String s = SqlSelect.create().select("f1,f2").from("t1").limit(10).toString();
        String dest = "SELECT f1,f2 FROM t1 LIMIT 10";
        MatcherAssert.assertThat(dest, Matchers.equalTo(s));
    }

    @Test
    public void testWhereIn() throws Exception {
        String s = SqlSelect.create()
                .select("f1,f2")
                .from("t1")
                .where(SqlQuery.whereIn("f1", Lists.newArrayList(1, 2, 3)))
                .toString();
        String dest = "SELECT f1,f2 FROM t1 WHERE f1 IN (1, 2, 3)";
        MatcherAssert.assertThat(dest, Matchers.equalTo(s));
    }

    @Test
    public void testFindBy() throws Exception {

        String s =
                SqlSelect.create().select("f1,f2").from("t1").where(FindBy.findBy("ByF1Equal")).toString();
        String dest = "SELECT f1,f2 FROM t1 WHERE f1 = ?";
        MatcherAssert.assertThat(dest, Matchers.equalTo(s));
    }
}