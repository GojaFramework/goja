package goja.rapid.mvc.interceptor.excel;

import java.util.List;

public interface PostListProcessor<T> {
    void process(List<T> list);
}
