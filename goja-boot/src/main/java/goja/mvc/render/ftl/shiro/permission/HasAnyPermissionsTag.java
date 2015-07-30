package goja.mvc.render.ftl.shiro.permission;

import goja.StringPool;
import org.apache.shiro.subject.Subject;

/**
 * <p>
 * A plurality of permission, as long as the only one.
 * </p>
 *
 * Note: From the <a href="https://github.com/springside/springside4">SpringSide4</a>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class HasAnyPermissionsTag extends PermissionTag {
    @Override
    protected boolean showTagBody(String permissionNames) {
        boolean hasAnyPermission = false;

        Subject subject = getSubject();

        if (subject != null) {
            // Iterate through permissions and check to see if the user has one of the permissions
            for (String permission : permissionNames.split(StringPool.COMMA)) {

                if (subject.isPermitted(permission.trim())) {
                    hasAnyPermission = true;
                    break;
                }

            }
        }

        return hasAnyPermission;
    }
}
