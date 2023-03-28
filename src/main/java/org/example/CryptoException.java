package org.example;

public class CryptoException extends Exception {

    public CryptoException(String msg, Exception ex){
        super(msg, ex);
    }

}
