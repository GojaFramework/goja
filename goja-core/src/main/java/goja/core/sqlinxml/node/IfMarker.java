package goja.core.sqlinxml.node;

import com.google.common.collect.Lists;
import goja.core.StringPool;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import ru.lanwen.verbalregex.VerbalExpression;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class IfMarker {

  /**
   * Sql if 开始指令
   *
   * #_if (param==1)
   */
  public static final String SQL_IF_MARKER = "#_if";

  /**
   * Sql elseif 判断指令
   *
   * #_elseif (param==1)
   */
  public static final String SQL_ELSEIF_MARKER = "#_elseif";
  /**
   * sql 不匹配指令
   *
   * #_else
   */
  public static final String SQL_ELSE_MARKER = "#_else";

  /**
   * Sql if 结束指令
   *
   * #_endif
   */
  public static final String SQL_ENDIF_MARKER = "#_endif";
  public static final String SQLCOMMITPIRFIX = "--";

  /**
   * 指令的表达式与Sql片段 映射
   */
  public final List<Pair<String, String>> expressionSqlFragments = Lists.newArrayList();

  /**
   * 构建SQL的 IF 指令
   *
   * @param originalSql 原始SQL
   */
  public IfMarker(String originalSql) {
    final VerbalExpression.Builder regBuilder = VerbalExpression.regex()
        .then(SQLCOMMITPIRFIX).space().oneOrMore()
        .then(SQL_IF_MARKER).space().oneOrMore().then("(").anything().then(")");
    final VerbalExpression ifRegex = regBuilder.build();

    final VerbalExpression ifSqlRegex =
        VerbalExpression.regex()
            .find(SQL_IF_MARKER)
            .something()
            .then(StringPool.RIGHT_BRACKET).anything()
            .then(SQLCOMMITPIRFIX).space()
            .zeroOrMore()
            .build();

    final String ifExpression =
        StringUtils.trim(StringUtils.replaceEach(ifRegex.getText(originalSql),
            new String[] {SQLCOMMITPIRFIX, SQL_IF_MARKER},
            new String[] {StringPool.EMPTY, StringPool.EMPTY}));
    final String ifSql = StringUtils.replaceEach(ifSqlRegex.getText(originalSql),
        new String[] {SQLCOMMITPIRFIX, SQL_IF_MARKER, ifExpression},
        new String[] {StringPool.EMPTY, StringPool.EMPTY, StringPool.EMPTY});

    expressionSqlFragments.add(Pair.of(
        StringUtils.replace(ifExpression, StringPool.SPACE, StringPool.EMPTY),
        StringUtils.trim(ifSql)));
  }
}
