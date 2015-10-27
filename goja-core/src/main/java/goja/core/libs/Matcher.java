/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.libs;

import goja.core.libs.base.Option;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-09-11 13:36
 * @since JDK 1.6
 */
public abstract class Matcher<T,R> {

    public static Matcher<Object, String> String = new Matcher<Object, String>() {

        @Override
        public Option<String> match(Object o) {
            if (o instanceof String) {
                return Option.Some((String) o);
            }
            return Option.None();
        }
    };

    public static <K> Matcher<Object, K> ClassOf(final Class<K> clazz) {
        return new Matcher<Object, K>() {

            @Override
            public Option<K> match(Object o) {
                if (o instanceof Option && ((Option) o).isDefined()) {
                    o = ((Option) o).get();
                }
                if (clazz.isInstance(o)) {
                    return Option.Some((K) o);
                }
                return Option.None();
            }
        };
    }

    public static Matcher<String, String> StartsWith(final String prefix) {
        return new Matcher<String, String>() {

            @Override
            public Option<String> match(String o) {
                if (o.startsWith(prefix)) {
                    return Option.Some(o);
                }
                return Option.None();
            }
        };
    }

    public static Matcher<String, String> Re(final String pattern) {
        return new Matcher<String, String>() {

            @Override
            public Option<String> match(String o) {
                if (o.matches(pattern)) {
                    return Option.Some(o);
                }
                return Option.None();
            }
        };
    }

    public static <X> Matcher<X, X> Equals(final X other) {
        return new Matcher<X, X>() {

            @Override
            public Option<X> match(X o) {
                if (o.equals(other)) {
                    return Option.Some(o);
                }
                return Option.None();
            }
        };
    }

    public abstract Option<R> match(T o);

    public Option<R> match(Option<T> o) {
        if (o.isDefined()) {
            return match(o.get());
        }
        return Option.None();
    }

    public <NR> Matcher<T, NR> and(final Matcher<R, NR> nextMatcher) {
        final Matcher<T, R> firstMatcher = this;
        return new Matcher<T, NR>() {

            @Override
            public Option<NR> match(T o) {
                for (R r : firstMatcher.match(o)) {
                    return nextMatcher.match(r);
                }
                return Option.None();
            }
        };
    }
}
