package goja.mvc;

import com.alibaba.fastjson.JSON;

import org.junit.Test;

/**
 * <p> </p>
 *
 * @author sog
 * @version 1.0
 * @since JDK 1.6
 */
public class AjaxSimpleTest {

    @Test
    public void testJSON() throws Exception {

        AjaxSimple.Builder<String> stringBuilder = new AjaxSimple.Builder<String>();
        stringBuilder.setData("abc");
        stringBuilder.setMsg("ok");
        stringBuilder.setSuccess(true);

        System.out.println("JSON.toJSONString(stringBuilder.createAjaxSimple()) = " + JSON.toJSONString(stringBuilder.createAjaxSimple()));

    }
}