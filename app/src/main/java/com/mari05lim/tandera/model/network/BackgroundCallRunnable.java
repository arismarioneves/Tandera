package com.mari05lim.tandera.model.network;

public abstract class BackgroundCallRunnable<R> {

    public void preExecute() {}

    public abstract R runAsync();

    public void postExecute(R result) {}

 }