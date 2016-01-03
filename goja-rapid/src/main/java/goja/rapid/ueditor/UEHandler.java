package goja.rapid.ueditor;

import com.google.common.base.Strings;
import com.jfinal.core.Controller;
import goja.rapid.ueditor.define.BaseState;

/**
 * <p> </p>
 *
 * @author sogYF
 * @version 1.0
 * @since JDK 1.6
 */
public class UEHandler {

    private final Controller controller;

    private UEHandler(Controller controller) {
        this.controller = controller;
    }

    public static UEHandler build(Controller controller) {
        return new UEHandler(controller);
    }

    public void exec() {
        String action = controller.getPara("action");
        String callback = controller.getPara("callback");
        if (!Strings.isNullOrEmpty(action)) {
            final UEAction ueAction = UEAction.valueOf(action.toUpperCase());
            switch (ueAction) {
                case CONFIG:
                    controller.renderText(ueAction.invoke());
                    break;
                case UPLOADIMAGE:
                case UPLOADFILE:
                case UPLOADSCRAWL:
                case UPLOADVIDEO:
                    controller.renderText(ueAction.invoke(controller));
                    break;
                case CATCHIMAGE:
                    break;
                case LISTIMAGE:
                case LISTFILE:
                    controller.renderText(ueAction.invoke(controller));
                    break;
            }
        } else {
            controller.renderText(new BaseState(false).toJSONString());
        }
    }
}
