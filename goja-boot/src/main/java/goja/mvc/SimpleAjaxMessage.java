package goja.mvc;

import java.io.Serializable;

/**
 * <p> 简单格式的Ajax数据封装类 </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class SimpleAjaxMessage implements Serializable {

    private static final long serialVersionUID = -8792299524734422099L;
    /**
     * 请求是否成功
     */
    private final boolean success;

    /**
     * 消息
     */
    private final String msg;

    public SimpleAjaxMessage(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }
}
