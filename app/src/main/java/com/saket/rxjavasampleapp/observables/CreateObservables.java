package com.saket.rxjavasampleapp.observables;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;

/**
 * Created by sshriwas on 2020-03-07
 */
public class CreateObservables {
        private static final String TAG = "CreateObservables";

        public interface updateValueListener {
            void updateUI(String val);
        }

    //Create - used to create observable from scratch.
    //here you have to manually invoke the emit's onNext(), onComplete and onError() callbacks.
    public void useCreateforObservable(Activity activity) {
        String[] arrDays = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
                , "Saturday"};
        //ObservableOnSubscribe is a Functional Interface(FI) with a single method
        //subscribe that accepts an instance of ObservableEmitter
        ObservableOnSubscribe<String> handler = emitter -> {
            try {
                for (String arrDay : arrDays) emitter.onNext(arrDay);

                //Once all values are emitted, call the onComplete
                emitter.onComplete();
            } catch (Exception e) {
                //if error call onError
                emitter.onError(e);
            }
        };

        //Create new observable using create and subscribe an observer<String> instance
        //By default the observable will perform its operations on same thread on which subscribe
        //is called. However, some observables emit data  To change thread, use a Scheduler.
        Observable.create(handler)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(String s) {
                        Thread currentThread = Thread.currentThread();
                        Log.d(TAG, "onNext: " + currentThread); //Just to confirm that by default
                        //values are emitted on main thread.
                        Toast.makeText(activity, "" + s, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onNext: " + s);
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

    //From - Can be used to create observable that emits items from a list, array or some other iterable.
    //Please note that if you have a list of observables. Then calling Observable.fromIterable simply
    // emits the individual observables from that list. It does NOT subscribe to them. Hence if an API request
    //returns an observable, then in this case the API request is not executed as it is not subscribed.
    //So in this case you can use something like the merge operator which subscribes to each observable in the
    // list and returns the output.
    public void useFromforObservable() {
        List<Car> lstCars = new ArrayList<>();
        //Create list of 20 cars
        for (int i = 1; i < 20; i ++) {
            Car newCar = new Car("Car Model " + i, 100 * i);
            lstCars.add(newCar);
        }

        //Now suppose we want to create an observable that emits these 20 cars
        Observer<Car> carObserver = new Observer<Car>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Car car) {
                Log.d(TAG, "Car Model: " + car.carModel + ", Car price: " + car.carPrice);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        };

        //Now create observerable using from
        /*
        Again, Rxjava is single threaded by default. All instructions are executed on a single thread
        by default. This thread is usually the thread on which the subscribe() is called. Also, some operators
        emit items on the main thread whereas some emit items on a separate thread.
         */
        Observable.fromIterable(lstCars)
                .subscribe(carObserver);
    }


    //Just - take upto 10 items and emits them as observables
    /**
     * Just is similar to From, but note that From will dive into an array or an iterable
     * or something of that sort to pull out items to emit, while Just will simply emit the
     * array or iterable or what-have-you as it is, unchanged, as a single item.
     * In the below example we pass individual items to the observable hence the observable
     * emits the individual items. However, if we pass an array of Car items then just operator
     * will emit a single observable of array of Car items. Whereas from will create an observable
     * that will emit individual items of the Car items.
     */
    public void useJustforObservable() {

        //Define Observer of type car
        Observer<Car> carObserver = new Observer<Car>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Car car) {
                Log.d(TAG, "Car Model: " + car.carModel + ", Car price: " + car.carPrice);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        };

        //Use just to create observable that emits instance(s) of Car class.
        Observable.just(new Car("Subaru", 1000), new Car("Volvo", 2500))
                .subscribe(carObserver);
    }

    //Range - Returns an Observable that emits a sequence of Integers within a specified range.
    public void useRangeforObservables(updateValueListener instance) {
        //Observable.range(1, 25)
        Observable.intervalRange(1,26,2500,1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())  //Seems we need to observe on mainthread or else
                //it throws exception - //Caused by: android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
                //but that mean that the observable is being created on a separate thread and being observed on that thread?
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Long lng) {
                        Log.d(TAG, "onNext: " + lng);
                        //Update UI
                        instance.updateUI("" + lng);
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

    //Repeat - It creates an observable that emits a particular item or range of items repeatedly.
    /**
     * Observable.range(2, 5)
     *                 .repeat(2)
     *                 .subscribe(new Observer<Integer>() {
     */

    //Timer - an observable that emits a single item after a given delay

    /**
     * Here as we have passed 4 seconds into the Timer operator,
     * it will go into the flatMap operator after 4 seconds.
     * This way we are doing the task after 4 seconds and emitting the value Car.
     */
    public void useTimerforObservable() {
            Observable.timer(4, TimeUnit.SECONDS)
                    //input long represents the time and output is Car
                    .flatMap(new Function<Long, ObservableSource<Car>>() {
                        @Override
                        public ObservableSource<Car> apply(Long aLong) throws Exception {
                            return Observable.create(new ObservableOnSubscribe<Car>() {
                                @Override
                                public void subscribe(ObservableEmitter<Car> emitter)
                                        throws Exception {
                                    emitter.onNext(new Car("Volvo xc40", 3000));
                                    emitter.onComplete();
                                }
                            });
                        }
                    })
            .subscribe(new Observer<Car>() {
                @Override
                public void onSubscribe(Disposable d) {
                    Log.d(TAG, "onSubscribe: ");
                }

                @Override
                public void onNext(Car car) {
                    Log.d(TAG, "Car Model: " + car.carModel + ", Car price: " + car.carPrice);
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

    //interval

    /**
     * Shift the emissions from an Observable forward in time by a particular amount.
     * We can use the take(n) operator to consider only n emissions from the observable
     */
    public void useintervalforObservable(Activity activity) {
        //Suppose we want an initial delay of 2 secs and then a delay of 1 sec for each car emission..
        Observable.interval(2000,1000,TimeUnit.MILLISECONDS)
                //flatmap function has input long which represents time and output Car
                .flatMap(new Function<Long, ObservableSource<Car>>() {  //we use the flatmap to generate new observable from output observable of the interval operator
                    @Override
                    public ObservableSource<Car> apply(Long aLong) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<Car>() {
                            @Override
                            public void subscribe(ObservableEmitter<Car> emitter) throws Exception {
                                Car newCar = new Car("Car Model " + aLong, aLong.intValue());
                                emitter.onNext(newCar);
                                emitter.onComplete();
                            }
                        });
                    }
                })
                .take(20)
                .doAfterNext(new Consumer<Car>() {
                    @Override
                    public void accept(Car car) throws Exception {
                        Log.d(TAG, "accept: " + Thread.currentThread().getName());  //Here default thread is not mainThread.
                        //instead it is RxComputationThreadPool.
                    }
                })
                //.observeOn(AndroidSchedulers.mainThread())    used to observe values on main thread.
                .subscribe(new Observer<Car>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Car car) {
                Log.d(TAG, "onNext: " + car.carModel + ", price: " + car.carPrice);
                //Since this is not main thread, the following statement will not work.
                //So instead we use observeOn operator to observe values on main thread. Then it works.
                //Toast.makeText(activity, "" + car.carModel, Toast.LENGTH_SHORT).show();
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


    //Defer - do not create the Observable until the observer subscribes,
    // and create a fresh Observable for each observer
    public void usedeferforObservable() {
        //Observable.defer(new )
    }

    //Delay - introduces a delay at beginning of creating an observable
    public void usedelayforObservable() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                for (int i = 0; i < 10; i ++) {
                    emitter.onNext("Delayed observer" + i);
                }

                emitter.onComplete();
            }
        })
                .delay(1500, TimeUnit.MILLISECONDS)     //introduces a delay of 1.5 secs before emission
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "onNext: " + s);
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

    private static class Car {
        String carModel;
        int carPrice;
        //mandatory properties are part of the constructor
        Car(String model, int price) {
            carModel = model;
            carPrice = price;
        }
    }
}
