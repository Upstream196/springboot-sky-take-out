package com.sky.exception;

import com.sky.context.BaseContext;

public class AddressBookBusinessException extends BaseException {

    public AddressBookBusinessException(String msg){
        super(msg);
    }
}
