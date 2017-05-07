package goja.rapid.mvc.interceptor.excel;


/**
 * excel解析前置处理器，在每一个元素 保存之前调用
 *
 * @author zhoulei
 */
public interface PreExcelProcessor<T> {
    void process(T obj);
}
