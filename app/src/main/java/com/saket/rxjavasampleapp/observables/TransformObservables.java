package com.saket.rxjavasampleapp.observables;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sshriwas on 2020-03-10
 */
public class TransformObservables {

    private static final String TAG = "TransformObservables";

    enum MODELTYPE {
        BASIC, MEDIUM, LUXURY
    }
    
    //Buffer - periodically gather items from an Observable into bundles
    // and emit these bundles rather than emitting the items one at a time
    public void useBufferforObservable() {
        String[] arrDays = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday"};
        Observable.fromArray(arrDays)
                .buffer(2)
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(List<String> strings) {
                        Log.d(TAG, "onNext: ");
                        for (String currString : strings) {
                            Log.d(TAG, "string: " + currString);
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

    //GroupBy - divide Observable emits into a set of Observables organized by key.
    // Each group emits a different group of items from the original Observable.
    // Sequence of emits is maintained.
    //Suppose in below example we have a list of cars which we want to sort based on price into
    //3 categories - basic, medium and luxury
    public void useGroupByforObservables() {
        //Create a list of cars
        List<Car> lstCars = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Car newCar = new Car("model" + i, 1000 * i);
            lstCars.add(newCar);
        }
        
        Observable.fromIterable(lstCars)
                .groupBy(new Function<Car, Enum<MODELTYPE>>() {
                    @Override
                    public Enum<MODELTYPE> apply(Car car) throws Exception {
                        Thread.sleep(1000); //this shows that each value is emitted separately and this
                        //operation happens on the background thread.
                        if (car.carPrice < 5000) {
                            return MODELTYPE.BASIC;
                        } else if (car.carPrice < 8000) {
                            return MODELTYPE.MEDIUM;
                        } else {
                            return MODELTYPE.LUXURY;
                        }
                    }
                })
        .subscribe(new Observer<GroupedObservable<Enum<MODELTYPE>, Car>>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(GroupedObservable<Enum<MODELTYPE>, Car> enumCarGroupedObservable) {
                Log.d(TAG, "onNext: ");
                if (enumCarGroupedObservable.getKey() == MODELTYPE.BASIC) {
                    enumCarGroupedObservable.subscribe(new Consumer<Car>() {
                        @Override
                        public void accept(Car car) throws Exception {
                            Log.d(TAG, "Basic model: " + car.carModel);
                        }
                    });
                } else if (enumCarGroupedObservable.getKey() == MODELTYPE.MEDIUM) {
                        enumCarGroupedObservable.subscribe(new Consumer<Car>() {
                            @Override
                            public void accept(Car car) throws Exception {
                                Log.d(TAG, "Medium model: " + car.carModel);
                            }
                        });
                } else {
                        enumCarGroupedObservable.subscribe(new Consumer<Car>() {
                            @Override
                            public void accept(Car car) throws Exception {
                                Log.d(TAG, "Luxury Model: " + car.carModel);
                            }
                        });
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

    //Map - applies a map function to each emit from the observable and returns the result item.
    //sequence of emits is preserved.
    /*
    Suppose we want to convert the price of the car before sending the emit to the observer.
     */
    public void useMapforObservable() {
        List<Car> lstCar = new ArrayList<>();
        //Here map function takes a car instance as input and
        //returns the price as integer output
        Function<Car, Integer> mapFunction = new Function<Car, Integer>() {
            @Override
            public Integer apply(Car car) throws Exception {
                //Get car's price
                int price = car.carPrice;
                //apply conversion
                int new_price = price * 25;
                return new_price;
            }
        };

        for (int i = 0; i < 10; i++) {
            Car currCar = new Car("Model " + i, 1000*i);
            lstCar.add(currCar);
        }
        Observable.fromIterable(lstCar)
                .map(mapFunction)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "New Price: " + integer);
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


    /*
        Flatmap - this is similar to map but there are 2 crucial differences.
        1. The flatmap returns an observable while map returns the value.
        2. flatmap does not maintain the sequence of emits unlike map
        3. flatmap observable combines all the emit values into a single emit whereas
        map observable would emit individual emits.
     */
    public void useFlatmapforObservable() {
        //We use the same map example here.
        ArrayList<Car> lstCar = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            Car currCar = new Car("Model " + i, 1000*i);
            lstCar.add(currCar);
        }
        Observable.fromIterable(lstCar)
                .flatMap(new Function<Car, ObservableSource<Car>>() {
                    @Override
                    public ObservableSource<Car> apply(Car car) throws Exception {
                        //Here we apply the price conversion and update the car object
                        int new_price = car.carPrice + 25;
                        car.carPrice = new_price;
                        //now instead of returning just the car, we return an observable that emits the updated car instance.
                        return Observable.create(new ObservableOnSubscribe<Car>() {
                            @Override
                            public void subscribe(ObservableEmitter<Car> emitter) throws Exception {
                                emitter.onNext(car);
                                emitter.onComplete();
                            }
                        })
                                .subscribeOn(Schedulers.io());  //Without this, the emits happen in sequence. NEED TO INVESTIGATE??
                    }
                }).
                subscribe(new Observer<Car>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Car car) {
                        Log.d(TAG, "New Car price: " + car.carPrice);
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

    //ConcatMap
    /*
        Again, this is similar to FlatMap. But here the emits happen in sequence.
     */
    public void useConcatMapforObservable() {
        //We use the same map example here.
        ArrayList<Car> lstCar = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            Car currCar = new Car("Model " + i, 1000 * i);
            lstCar.add(currCar);
        }
        Observable.fromIterable(lstCar)
                .concatMap(new Function<Car, ObservableSource<Car>>() {
                    @Override
                    public ObservableSource<Car> apply(Car car) throws Exception {
                        //Update Car price
                        int new_price = car.carPrice + 25;
                        car.carPrice = new_price;
                        return Observable.create(new ObservableOnSubscribe<Car>() {
                            @Override
                            public void subscribe(ObservableEmitter<Car> emitter) throws Exception {
                                emitter.onNext(car);
                                emitter.onComplete();
                            }
                        })
                                .subscribeOn(Schedulers.io());  //Unlike flatmap, this does not have any impact on sequence of emits....need to investigate
                    }
                }).subscribe(new Observer<Car>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Car car) {
                Log.d(TAG, "New Car price: " + car.carPrice);
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

    //SwitchMap - unsubscribes from previous observable and emits data only from latest observable.
    /*
    So here i use the same example as the flatmap example. Except here instead of emiting all values
    the switchmap only emits the value from the last observable which is 9025.
     */
    public void useSwitchMapforObservable() {
        ArrayList<Car> lstCar = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            Car currCar = new Car("Model " + i, 1000*i);
            lstCar.add(currCar);
        }
        Observable.fromIterable(lstCar)
                .switchMap(new Function<Car, ObservableSource<Car>>() {
                    @Override
                    public ObservableSource<Car> apply(Car car) throws Exception {
                        //Here we apply the price conversion and update the car object
                        int new_price = car.carPrice + 25;
                        car.carPrice = new_price;
                        //now instead of returning just the car, we return an observable that emits the updated car instance.
                        return Observable.create(new ObservableOnSubscribe<Car>() {
                            @Override
                            public void subscribe(ObservableEmitter<Car> emitter) throws Exception {
                                emitter.onNext(car);
                                emitter.onComplete();
                            }
                        })
                                .subscribeOn(Schedulers.io());  //Without this, the emits happen in sequence. NEED TO INVESTIGATE??
                    }
                }).
                subscribe(new Observer<Car>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Car car) {
                        Log.d(TAG, "New Car price: " + car.carPrice);
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

    //Scan - map provides bifunction with current as well as previous emit

    private class Car {
        String carModel;
        int carPrice;
        //mandatory properties are part of the constructor
        Car(String model, int price) {
            carModel = model;
            carPrice = price;
        }
    }

}
