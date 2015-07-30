/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lucene.analyzer;

import org.junit.Test;
import org.wltea.analyzer.core.Lexeme;

import java.util.List;

public class AnalyzerTest {

    @Test
    public void testAnalyzer() throws Exception {
        List<Lexeme> words = Analyzer.getInstance().analyzer("字典内的是否能够自动匹配呢,还是买入05建元1s这个,这个也可以放心的00定向国债2把唉，那就买入10000万，收益率:8%,评级为AAA");
        for (Lexeme word : words) {
            System.out.println(word);
        }
    }

    @Test
    public void testDisableSmart() throws Exception {
        List<Lexeme> words = Analyzer.getInstance().disableSmart()
                .analyzer("字典内的是否能够自动匹配呢,还是买入05建元1s这个,这个也可以放心的00定向国债2把唉，那就买入10000万，收益率:8%");
        for (Lexeme word : words) {
            System.out.println(word);
        }

    }
}