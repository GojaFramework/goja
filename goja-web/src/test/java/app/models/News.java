package app.models;

import com.jfinal.plugin.activerecord.Model;
import goja.plugins.tablebind.TableBind;

/**
 * <p> The database xx Model. </p>
 *
 * @author sagyf yang
 * @version 1.0
 * @since JDK 1.6
 */
@TableBind(tableName = "xx")
public class News extends Model<News> {

    /**
     * The public dao.
     */
    public static final News dao = new News();
}