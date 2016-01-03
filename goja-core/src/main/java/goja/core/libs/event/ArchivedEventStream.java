/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 sagyf Yang. The Four Group.
 */

package goja.core.libs.event;

import goja.core.libs.Promise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <p> . </p>
 *
 * @author sagyf yang
 * @version 1.0 2014-09-11 13:25
 * @since JDK 1.6
 */
public class ArchivedEventStream<T> {


    final int archiveSize;
    final ConcurrentLinkedQueue<IndexedEvent<T>> events = new ConcurrentLinkedQueue<IndexedEvent<T>>();
    final List<FilterTask<T>> waiting = Collections.synchronizedList(new ArrayList<FilterTask<T>>());
    final List<EventStream<T>> pipedStreams = new ArrayList<EventStream<T>>();

    public ArchivedEventStream(int archiveSize) {
        this.archiveSize = archiveSize;
    }

    public synchronized EventStream<T> eventStream() {
        final EventStream<T> stream = new EventStream<T>(archiveSize);
        for (IndexedEvent<T> event : events) {
            stream.publish(event.data);
        }
        pipedStreams.add(stream);
        return stream;
    }

    public synchronized Promise<List<IndexedEvent<T>>> nextEvents(long lastEventSeen) {
        FilterTask<T> filter = new FilterTask<T>(lastEventSeen);
        waiting.add(filter);
        notifyNewEvent();
        return filter;
    }

    public synchronized List<IndexedEvent> availableEvents(long lastEventSeen) {
        List<IndexedEvent> result = new ArrayList<IndexedEvent>();
        for (IndexedEvent event : events) {
            if (event.id > lastEventSeen) {
                result.add(event);
            }
        }
        return result;
    }

    public List<T> archive() {
        List<T> result = new ArrayList<T>();
        for (IndexedEvent<T> event : events) {
            result.add(event.data);
        }
        return result;
    }

    public synchronized void publish(T event) {
        if (events.size() >= archiveSize) {
            events.poll();
        }
        events.offer(new IndexedEvent<T>(event));
        notifyNewEvent();
        for (EventStream<T> eventStream : pipedStreams) {
            eventStream.publish(event);
        }
    }

    void notifyNewEvent() {
        for (ListIterator<FilterTask<T>> it = waiting.listIterator(); it.hasNext(); ) {
            FilterTask<T> filter = it.next();
            for (IndexedEvent<T> event : events) {
                filter.propose(event);
            }
            if (filter.trigger()) {
                it.remove();
            }
        }
    }

    static class FilterTask<K> extends Promise<List<IndexedEvent<K>>> {

        final Long lastEventSeen;
        final List<IndexedEvent<K>> newEvents = new ArrayList<IndexedEvent<K>>();

        public FilterTask(Long lastEventSeen) {
            this.lastEventSeen = lastEventSeen;
        }

        public void propose(IndexedEvent<K> event) {
            if (event.id > lastEventSeen) {
                newEvents.add(event);
            }
        }

        public boolean trigger() {
            if (newEvents.isEmpty()) {
                return false;
            }
            invoke(newEvents);
            return true;
        }
    }
}
