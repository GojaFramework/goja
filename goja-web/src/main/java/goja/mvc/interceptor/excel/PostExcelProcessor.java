package goja.rapid.mvc.interceptor.excel;


/**
 * excel解析后置处理器 ,在整个excel对象保存完毕之后调用
 *
 * @author zhoulei
 */
public interface PostExcelProcessor<T> {
    void process(T obj);
}
