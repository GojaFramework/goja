package goja.mvc.easyui.rsp;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class ValidRsp {

    public final boolean success;

    public final String msg;

    private ValidRsp(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }


    public static class Builder {
        private boolean success;
        private String  msg;


        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }


        public ValidRsp build() {
            return new ValidRsp(success, msg);
        }
    }

}
