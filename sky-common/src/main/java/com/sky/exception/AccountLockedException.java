package com.sky.exception;

/**
 * 账号被异常锁定
 */
public class AccountLockedException extends BaseException{

    public AccountLockedException() {
    }

    public AccountLockedException(String msg) {
        super(msg);
    }

}
