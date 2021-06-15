package mods.ltr.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class LRUCache<R, T> {
    LRUNode<R, T> head;
    LRUNode<R, T> tail;
    Object2ObjectOpenHashMap<R, LRUNode<R, T>> map;
    int cap;

    public LRUCache(int capacity) {
        this.cap = capacity;
        this.map = new Object2ObjectOpenHashMap<>();
    }

    public T get(R key) {
        if (map.get(key) == null) {
            return null;
        }

        //move to tail
        LRUNode<R, T> t = map.get(key);

        removeNode(t);
        offerNode(t);

        return t.value;
    }

    public void put(R key, T value) {
        if (map.containsKey(key)) {
            LRUNode<R, T> t = map.get(key);
            t.value = value;

            //move to tail
            removeNode(t);
            offerNode(t);
        } else {
            if (map.size() >= cap) {
                //delete head
                map.remove(head.key);
                removeNode(head);
            }

            //add to tail
            LRUNode<R, T> node = new LRUNode<>(key, value);
            offerNode(node);
            map.put(key, node);
        }
    }

    public void remove(R key) {
        map.remove(key);
    }

    private void removeNode(LRUNode<R, T> n) {
        if (n.prev != null) {
            n.prev.next = n.next;
        } else {
            head = n.next;
        }

        if (n.next != null) {
            n.next.prev = n.prev;
        } else {
            tail = n.prev;
        }
    }

    private void offerNode(LRUNode<R, T> n) {
        if (tail != null) {
            tail.next = n;
        }

        n.prev = tail;
        n.next = null;
        tail = n;

        if (head == null) {
            head = tail;
        }
    }

    private static class LRUNode<R, T> {
        R key;
        T value;
        LRUNode<R, T> prev;
        LRUNode<R, T> next;

        public LRUNode(R key, T value) {
            this.key = key;
            this.value = value;
        }
    }
}