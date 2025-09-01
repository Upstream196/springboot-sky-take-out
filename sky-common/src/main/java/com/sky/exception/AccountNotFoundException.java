package com.sky.exception;

public class AccountNotFoundException extends BaseException {

    /**
     * 账号不存在异常
     */
    public AccountNotFoundException() {
    }

    public AccountNotFoundException(String msg) {
        super(msg);
    }
}
