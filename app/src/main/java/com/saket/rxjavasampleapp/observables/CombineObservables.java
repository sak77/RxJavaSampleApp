package com.saket.rxjavasampleapp.observables;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by sshriwas on 2020-03-14
 */
public class CombineObservables {

    private static final String TAG = "CombineObservables";
    //CombineLatest
    /*
    when an item is emitted by either of two Observables,
    combine the latest item emitted by each Observable via a specified function and
    emit items based on the results of this function
     */
    public void combineUsingCombineLatest() {
        Observable<Long> observable1 = Observable.intervalRange(0,20,0,150, TimeUnit.MILLISECONDS);
        Observable<Long> observable2 = Observable.intervalRange(0,20,0,500, TimeUnit.MILLISECONDS);
        //Bi function that takes emits from both observables and emits its own string response
        BiFunction<Long, Long, String> combiner = new BiFunction<Long, Long, String>() {
            @Override
            public String apply(Long aLong, Long aLong2) throws Exception {
                return "Observable1: " + aLong + " Observable2: " + aLong2;
            }
        };

        Observable.combineLatest(observable1, observable2, combiner)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "accept: " + s);
                    }
                });
    }

    //Join
    /*
    combine items emitted by two Observables whenever an item from one Observable is emitted
    during a time window defined according to an item emitted by the other Observable. Parameters
    accepted by join operator  -
    parameters it accepts:

right – the second Observable to join items from
leftDurationSelector – a function to select a duration for each item emitted by the source Observable,
used to determine overlap

rightDurationSelector – a function to select a duration for each item emitted by the right Observable,
used to determine overlap

resultSelector – a function that computes an item to be emitted by the resulting Observable
for any two overlapping items emitted by the two Observables

Difference between combineLatest and join??
     */
    public void useJointoCombineObservables() {
        //Define both observables
        /*
        Observable<Long> leftobservable = Observable.intervalRange(0,20, 0, 1000, TimeUnit.MILLISECONDS);
        Observable<Long> rightobservable = Observable.intervalRange(3,25, 0, 1000, TimeUnit.MILLISECONDS);
        leftobservable.join(rightobservable,
                new Function<Long, ObservableSource<Long extends Object>>() {
                    @Override
                    public ObservableSource<Long extends Object> apply(Long aLong) throws Exception {
                        return Observable.timer(0, TimeUnit.MILLISECONDS);
                    }
                }), along -> Observable.timer(0, TimeUnit.MILLISECONDS),
                (l, r) -> {
                    System.out.println("Left result: " + l + " Right Result: " + r);
                    return l + r;
                })*/
    }

/*
    Observable.merge will combine multiple observables into one by merging their emits.

    Note it does not preserve the sequence of observables. So if first observable takes more time
    to execute, it will emit values from the next observable if its ready.

    This is somewhat similar to what happens in flatmap. So in below case, i have introduced a
    random delay in both observables, so it can be seen that some times, emits from observable 2
    are displayed before emits from observable 1.

    But emits within an observable are so far seen to emit in one go...but maybe that is not required either??

    In case of API request in app, it will subscribe to each observable (.i.e. execute the API request)
    and then return the response from different observables in the list.
    Unlike this, Observable.fromIterable() simply emits the individual observables without subscribing to them.
*/
    public void useMergeToCombineObservables() {
        //Even observables
        Integer[] arrEvenNumbers = new Integer[]{2,4,6,8,10};
        int randomDelay1 = new Random().nextInt(10);
        Observable<Integer> evenObservables = Observable.fromArray(arrEvenNumbers)
                .delay(randomDelay1, TimeUnit.SECONDS);

        //Odd observables
        Integer[] arrOddNumbers = new Integer[]{1,3,5,7,9};
        int randomDelay2 = new Random().nextInt(10);
        Observable<Integer> oddObservables = Observable.fromArray(arrOddNumbers)
                .delay(randomDelay2, TimeUnit.SECONDS);

        List<Observable<Integer>> lstObservables = new ArrayList<>();
        lstObservables.add(evenObservables);
        lstObservables.add(oddObservables);
        //Now we use merge to combine their emits
        Observable.merge(lstObservables)
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


    //Concat
    //Concat operator is similar to Merge operator in that it emits items from each observable. But unlike
    //Merge operator, it does so in a sequential manner. So in this case the sequence is always the same.

    public void useConcatToCombineObservables() {
        //Even observables
        Integer[] arrEvenNumbers = new Integer[]{2,4,6,8,10};
        Observable<Integer> evenObservables = Observable.fromArray(arrEvenNumbers)
                .subscribeOn(Schedulers.io());//even with this subscribeOn, the emits still happen sequentially

        //Odd observables
        Integer[] arrOddNumbers = new Integer[]{1,3,5,7,9};
        Observable<Integer> oddObservables = Observable.fromArray(arrOddNumbers)
                .subscribeOn(Schedulers.io());//even with this subscribeOn, the emits still happen sequentially

        List<Observable<Integer>> lstObservables = new ArrayList<>();
        lstObservables.add(evenObservables);
        lstObservables.add(oddObservables);
        //Now we use merge to combine their emits
        Observable.concat(lstObservables)
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

    //zip combines emissions from multiple observables, applies a function to each emit and emits a single emission.
    // So in this case it emits an array object which contains all integers.
    public void useZipToCombineObservables() {
        //Even observables
        Integer[] arrEvenNumbers = new Integer[]{2,4,6,8,10};
        Observable<Integer> evenObservables = Observable.fromArray(arrEvenNumbers);

        //Odd observables
        Integer[] arrOddNumbers = new Integer[]{1,3,5,7,9};
        Observable<Integer> oddObservables = Observable.fromArray(arrOddNumbers);

        List<Observable<Integer>> lstObservables = new ArrayList<>();
        lstObservables.add(evenObservables);
        lstObservables.add(oddObservables);
        //Now we use zip to combine their emits
        Observable.zip(lstObservables, new Function<Object[], Object[]>() {
            @Override
            public Object[] apply(Object[] objects) throws Exception {
                //This function for now does nothing except return the objects emitted by the observable.
                //also it seems its input parameter has to be object[] but output can be anything..
                return objects;
            }
        })
                .subscribe(new Observer<Object[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Object[] objects) {
                        for (Object o : objects) {
                            Log.d(TAG, "onNext: " + (Integer)o);
                        }
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
}
