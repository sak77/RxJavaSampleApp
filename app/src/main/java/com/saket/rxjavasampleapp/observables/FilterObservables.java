package com.saket.rxjavasampleapp.observables;

import android.util.Log;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Created by sshriwas on 2020-03-12
 */
public class FilterObservables {
    
    static final String TAG = "FilterObservables";

    //Debounce - only emit an item from an Observable if a particular timespan has passed
    // since the observable emitted the last item. Items emitted within the timespan will get
    // omitted and only the last observable will be kept for emission while the timer is reset.
    //this is one of the backpressure operators
    public void useDeboucetoFilterEmits() {
        //First, using range and map operators we create an observable that emits values at random intervals.
        //then we use debounce to filter out emits that happen within the interval
        Observable.range(1,10)
        .map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) throws Exception {
                //Introduce random sleep value
                int randomeval = ThreadLocalRandom.current().nextInt(1000, 5000);
                Log.d(TAG, "apply: " + randomeval);
                Thread.sleep(randomeval);
                return integer;
            }
        })
                .debounce(2000, TimeUnit.MILLISECONDS)  //using debounce to filter emits within 2secs.
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

    //Distinct
    /*
    This operator suppresses duplicate items emitted by an Observable.
    The distinct operator works very well with primitive data types.
    But in order to work with a custom dataType, we need to override the equals() and hashCode() methods.
     */
    public void useDistinctToFilterEmits() {
        Integer[] arrNumbers = new Integer[]{1,2,3,3,5,6,7,7,9};
        Observable.fromArray(arrNumbers)
                .distinct()
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
                        Log.d(TAG, "onError: " + e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }

    //Filter
    /*
    This operator emits only those items from an Observable that pass a predicate test.
     */
    public void useFiltertoFilterEmits() {
        Integer[] arrNumbers = new Integer[]{1,2,3,4,5,6,7,8,9};
        Observable.fromArray(arrNumbers)
        .filter(new Predicate<Integer>() {  //we use predicate which returns true for even numbers
            @Override
            public boolean test(Integer integer) throws Exception {
                return (integer % 2 == 0);
            }
        })
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: " + integer);
            }
        });
    }

    //IgnoreElements
    /*
    This operator does not emit any items from an Observable but mirrors its termination notification (either onComplete or onError).
    If you do not care about the items being emitted by an Observable,
    but you do want to be notified when it completes or when it terminates with an error,
    you can apply the ignoreElements() operator to the Observable,
    which will ensure that it will never call the observers’ onNext() methods.
     */

    //Skip
    /*
    skip(n) operator suppresses the first n items emitted by an Observable.
     */

    //SkipLast
    /*
    skip(n) operator suppresses the last n items emitted by an Observable.
     */

    //Take
    /*
    Exact opposite of skip. take(n) operator emits the first n items emitted by an Observable.
     */

    //TakeLast
    /*
    takeLast(n) operator emits the last n items emitted by an Observable.
     */

}
