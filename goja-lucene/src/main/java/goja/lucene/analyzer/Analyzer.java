/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.lucene.analyzer;

import com.google.common.collect.Lists;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-09-05 22:17
 * @since JDK 1.6
 */
public class Analyzer {

    private boolean useSmart;

    private Analyzer() {
        useSmart = true;
    }


    private static final Analyzer ANALYZER = new Analyzer();

    public static Analyzer getInstance() {
        return ANALYZER;
    }

    public Analyzer disableSmart() {
        ANALYZER.useSmart = false;
        return ANALYZER;
    }

    public List<Lexeme> analyzer(String txt) {
        final List<Lexeme> lexemes = Lists.newArrayList();
        final IKSegmenter ikSeg = new IKSegmenter(new StringReader(txt), useSmart);
        try {
            Lexeme l;
            while ((l = ikSeg.next()) != null) {
                lexemes.add(l);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lexemes;
    }


}
