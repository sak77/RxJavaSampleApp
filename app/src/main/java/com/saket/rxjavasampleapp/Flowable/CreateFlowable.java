package com.saket.rxjavasampleapp.Flowable;

import android.os.SystemClock;
import android.util.Log;

import org.reactivestreams.Subscription;

import java.lang.reflect.Array;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Flowables:
 * 0..N flows, supporting Reactive-Streams and backpressure
 */
public class CreateFlowable {

    private static final String TAG = "CreateFlowable";
    Subscription mSubscription;

    public void createFlowableJust() {
        Flowable.just("Hello World")
                .delay(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(subscription -> {
                    mSubscription = subscription;
                    Log.d(TAG, "onSubscribed");
                })
                .doOnNext(value -> Log.d(TAG, value))
        .doOnError(throwable -> Log.d(TAG, "Error: " + throwable.getLocalizedMessage()))
        .doOnComplete(() -> Log.d(TAG, "doOnComplete"))
                .doOnCancel(() -> Log.d(TAG, "doOnCancel called."))
        .subscribe();
        SystemClock.sleep(2000);
        mSubscription.cancel();
        //.subscribe(value -> Log.d(TAG, value));
    }


    public void createFlowable() {
        //Now we create a flowable
        Flowable<String> source = Flowable.create(emitter -> {
            emitter.onNext("Hello");

            //Some blocking operation
            SystemClock.sleep(1000);

            if (emitter.isCancelled()) {
                return;
            }
            emitter.onNext("World");

            SystemClock.sleep(1000);

            // the end-of-sequence has to be signaled, otherwise the
            // consumers may never finish
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);

        source.subscribe(s -> Log.d(TAG,"Create flowable subscribe - " + s));
    }

    public void createFlowableFrom() {
        //First we create an imaginary collection
        Integer[] sampleData = new Integer[1000];
        for (int i = 0; i < 1000; i++) {
            sampleData[i] = i;
        }

        Flowable.fromArray(sampleData)
                .debounce(1, TimeUnit.MICROSECONDS)  //Backpressure Strategy debounce
                .subscribeOn(Schedulers.computation())
                .subscribe(integer -> Log.d(TAG, "Flowable from : " + integer));
    }

    /*
    If we use the BackpressureStrategy.BUFFER,
    the source will buffer all the events until the subscriber can consume them:
     */
    public void createFlowableWithBuffer() {
        List<Integer> testList = IntStream.range(0, 10000)
                .boxed()
                .collect(Collectors.toList());

/*
        Flowable<Integer> flowable = Observable.fromIterable(testList)
                .toFlowable(BackpressureStrategy.MISSING)
                .subscribeOn(Schedulers.single())
                .doOnSubscribe(subscription -> Log.d(TAG, "onSubscribe"))
                .doOnNext(val -> {
                    //Perform some heavy tasks.
                    SystemClock.sleep(1000);
                    Log.d(TAG, "onNext: " + val);
                })
                .doOnComplete(() -> Log.d(TAG, "onComplete"))
                .doOnError(throwable -> Log.d(TAG, throwable.getLocalizedMessage()));

        flowable.subscribe();
*/


        Observable.fromIterable(testList)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(subscription -> Log.d(TAG, "onSubscribe"))
                .doOnNext(val -> {
                    //Perform some heavy tasks.
                    //SystemClock.sleep(100000);
                    Log.d(TAG, "onNext: " + val);
                })
                .doOnComplete(() -> Log.d(TAG, "onComplete"))
                .doOnError(throwable -> Log.d(TAG, throwable.getLocalizedMessage()))
                .subscribe();

/*
        Flowable.fromIterable(testList)
                .subscribeOn(Schedulers.single())
                .doOnSubscribe(subscription -> Log.d(TAG, "onSubscribe"))
                .doOnNext(val -> Log.d(TAG, "onNext: " + val))
                .doOnComplete(() -> Log.d(TAG, "onComplete"))
                .doOnError(throwable -> Log.d(TAG, throwable.getLocalizedMessage()))
                .subscribe();
*/
    }

}
