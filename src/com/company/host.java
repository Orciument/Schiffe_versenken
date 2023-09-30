package com.company;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class host {
    private class player{
        String playerIp;
        int shipCount;
    }

    List<player> playerList = new List<player>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<player> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(player player) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends player> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, Collection<? extends player> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public player get(int index) {
            return null;
        }

        @Override
        public player set(int index, player element) {
            return null;
        }

        @Override
        public void add(int index, player element) {

        }

        @Override
        public player remove(int index) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @Override
        public ListIterator<player> listIterator() {
            return null;
        }

        @Override
        public ListIterator<player> listIterator(int index) {
            return null;
        }

        @Override
        public List<player> subList(int fromIndex, int toIndex) {
            return null;
        }
    };
    private String gameState =  "initialisation";


    //Webendpoint
    //Turns
}
