/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.libs.event;

import goja.libs.Promise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-09-11 13:24
 * @since JDK 1.6
 */
public class EventStream<T> {

    final int bufferSize;
    final ConcurrentLinkedQueue<T> events  = new ConcurrentLinkedQueue<T>();
    final List<Promise<T>>         waiting = Collections.synchronizedList(new ArrayList<Promise<T>>());

    public EventStream() {
        this.bufferSize = 100;
    }

    public EventStream(int maxBufferSize) {
        this.bufferSize = maxBufferSize;
    }

    public synchronized Promise<T> nextEvent() {
        if (events.isEmpty()) {
            LazyTask task = new LazyTask();
            waiting.add(task);
            return task;
        }
        return new LazyTask(events.peek());
    }

    public synchronized void publish(T event) {
        if (events.size() > bufferSize) {
            events.poll();
        }
        events.offer(event);
        notifyNewEvent();
    }

    void notifyNewEvent() {
        T value = events.peek();
        for (Promise<T> task : waiting) {
            task.invoke(value);
        }
        waiting.clear();
    }

    class LazyTask extends Promise<T> {

        public LazyTask() {
        }

        public LazyTask(T value) {
            invoke(value);
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            T value = super.get();
            markAsRead(value);
            return value;
        }

        @Override
        public T getOrNull() {
            T value = super.getOrNull();
            markAsRead(value);
            return value;
        }

        private void markAsRead(T value) {
            if (value != null) {
                events.remove(value);
            }
        }
    }
}
