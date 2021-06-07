package com.saket.rxjavasampleapp;

import android.util.Log;

import java.util.concurrent.ThreadLocalRandom;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by sshriwas on 2020-03-15
 */
public class TestSchedulers {
    
    private static final String TAG = "Schedulers";

    //Schedulers.io() - unbound thread pool which will start with creating a new thread for
    //a given observable. But for new observables it may re-use an existing idle thread or create
    //another new thread in the pool. Most commonly used scheduler.
    public void useSchedulersio() {
        Integer[] arrInteger = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        Observable.fromArray(arrInteger)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer integer) throws Exception {
                        //Introduce some random delay here
                        int delay = ThreadLocalRandom.current().nextInt(1000,5000);
                        Log.d(TAG, "delay: " + delay);
                        Thread.sleep(delay);
                        return Observable.just(integer)
                                .subscribeOn(Schedulers.newThread());
                    }
                })
                .doAfterNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "accept: " + Thread.currentThread());
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }

    //Schedulers.computation()

    //Schedulers.newThread()

    //Schedulers.from(Executor executor)

    //Schedulers.Single()

    //Schedulers.trampoline()

    //AndroidSchedulers.maintThread()
}
