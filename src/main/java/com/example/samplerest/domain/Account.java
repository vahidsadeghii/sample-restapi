package com.example.samplerest.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
@Builder
public class Account {
    private String id;
    private LocalDateTime createDate;
    /**
     * Keeps last available balance of account
     */
    private long balance;
    /**
     * All INPROGRESS withdraw amounts will hold here and will be release when transaction state is called
     */
    private long withdrawPendingBalance;

    /**
     * All INPROGRESS deposit amounts will hold here and will be release when transaction state is called
     */
    private long depositPendingBalance;
    private LocalDateTime updateDate;

    private final ReentrantLock lock = new ReentrantLock();

    public synchronized boolean decreaseBalance(long amount) {
        lock.lock();
        if (this.balance - this.withdrawPendingBalance >= amount) {
            this.balance -= amount;

            lock.unlock();

            this.updateDate = LocalDateTime.now();
            return true;
        } else {
            lock.unlock();
            return false;
        }
    }

    public synchronized void increaseBalance(long amount) {
        lock.lock();
        this.balance += amount;
        lock.unlock();

        this.updateDate = LocalDateTime.now();
    }

    public synchronized boolean addPendingWithdraw(long amount) {
        lock.lock();
        if (this.balance >= amount) {
            this.withdrawPendingBalance += amount;
            this.balance -= amount;

            lock.unlock();

            this.updateDate = LocalDateTime.now();
            return true;

        } else {
            lock.unlock();
            return false;
        }
    }

    public synchronized boolean removePendingWithdraw(long amount) {
        lock.lock();
        if (this.withdrawPendingBalance >= amount) {
            this.withdrawPendingBalance -= amount;
            this.balance += amount;

            lock.unlock();

            this.updateDate = LocalDateTime.now();

            return true;
        } else {
            lock.unlock();
            return false;
        }
    }

    public synchronized boolean releasePendingWithdraw(long amount) {
        lock.lock();
        if (this.withdrawPendingBalance >= amount) {
            this.withdrawPendingBalance -= amount;

            lock.unlock();

            this.updateDate = LocalDateTime.now();
            return true;
        } else {

            lock.unlock();
            return false;
        }
    }

    public synchronized void addPendingDeposit(long amount) {
        lock.lock();
        this.depositPendingBalance += amount;
        lock.unlock();

        this.updateDate = LocalDateTime.now();
    }

    public synchronized boolean removePendingDeposit(long amount) {
        lock.lock();
        if (this.depositPendingBalance >= amount) {
            this.depositPendingBalance -= amount;

            lock.unlock();

            this.updateDate = LocalDateTime.now();
            return true;

        } else {
            lock.unlock();
            return false;
        }
    }

    public synchronized boolean releasePendingDeposit(long amount) {
        lock.lock();
        if (this.depositPendingBalance >= amount) {
            this.depositPendingBalance -= amount;
            this.balance += amount;

            lock.unlock();

            this.updateDate = LocalDateTime.now();

            return true;
        } else {
            return false;
        }
    }
}
