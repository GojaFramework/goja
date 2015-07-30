/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lucene.analyzer;

import com.google.common.collect.Maps;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.lucene.IKTokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 * <p> IK同义词. </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-09-11 11:37
 * @since JDK 1.6
 */
public class SynonymAnalyzer extends org.apache.lucene.analysis.Analyzer {

    private final boolean useSmart;
    private final Version luceneMatchVersion;

    public SynonymAnalyzer(Version version) {
        this(version, false);
    }

    public SynonymAnalyzer(Version version, boolean useSmart) {
        this.luceneMatchVersion = version;
        if (!version.onOrAfter(Version.LUCENE_4_9)) {
            throw new IllegalArgumentException("This class only works with Lucene 4.0+.");
        }
        this.useSmart = useSmart;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer token = new IKTokenizer(reader, useSmart);
        Map<String, String> paramsMap = Maps.newHashMap();
        Configuration cfg = DefaultConfig.getInstance();

        paramsMap.put("luceneMatchVersion", luceneMatchVersion.toString());
        paramsMap.put("synonyms", cfg.getExtSynonymDictionarys());
        paramsMap.put("ignoreCase", "true");
        SynonymFilterFactory factory = new SynonymFilterFactory(paramsMap);
        ResourceLoader loader = new ClasspathResourceLoader();
        try {
            factory.inform(loader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new TokenStreamComponents(token, factory.create(token));
    }
}
