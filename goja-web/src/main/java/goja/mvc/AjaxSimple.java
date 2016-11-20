package goja.mvc;

import java.io.Serializable;

/**
 * <p> 简单格式的Ajax数据封装类 </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class AjaxSimple<E> implements Serializable {

    private static final long serialVersionUID = -8792299524734422099L;
    /**
     * 请求是否成功
     */
    private final boolean success;

    /**
     * 消息
     */
    private final String msg;

    private final String code;

    private final E data;

    private AjaxSimple(boolean success, String msg, String code, E data) {
        this.success = success;
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public static <D> Builder<D> Builder() {
        return new Builder<D>();
    }

    public E getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

    public static class Builder<B> {
        private boolean success;
        private String  msg;
        private String  code;
        private B       data;

        public Builder setSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public Builder setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder setData(B data) {
            this.data = data;
            return this;
        }
        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public AjaxSimple<B> create() {
            return new AjaxSimple<B>(success, msg, code, data);
        }
    }
}
