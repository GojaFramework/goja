/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.plugins.index;

import com.jfinal.plugin.IPlugin;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * <p>
 * Lucene 索引库.
 * </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-08-24 0:16
 * @since JDK 1.6
 */
public class IndexPlugin implements IPlugin {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IndexHolder.class);

    protected static IndexHolder holder;

    private final String index_path;

    public IndexPlugin(String index_path) {this.index_path = index_path;}


    @Override
    public boolean start() {
        try {
            holder = IndexHolder.init(index_path);
        } catch (IOException e) {
            logger.error("the index plugin has error!", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
