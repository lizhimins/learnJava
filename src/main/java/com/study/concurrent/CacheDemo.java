package com.study.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheDemo {
    public volatile boolean update = false;
    private Map<String, Object> cache = new HashMap<String, Object>();

    private ReadWriteLock rwl = new ReentrantReadWriteLock();

    public ReentrantReadWriteLock.ReadLock rdl = (ReentrantReadWriteLock.ReadLock) rwl.readLock();
    public ReentrantReadWriteLock.WriteLock wl = (ReentrantReadWriteLock.WriteLock) rwl.writeLock();

    public void processData() {
        rdl.lock();//获取读锁
        if (!update) {
            rdl.unlock();//释放读锁
            wl.lock();//获取写锁

            try {
                if (!update) {
                    update = true;
                }
                rdl.lock();//获取读锁
            } finally {
                wl.unlock();//释放写锁
            }
        }
        rdl.unlock();//释放读锁
    }
}
