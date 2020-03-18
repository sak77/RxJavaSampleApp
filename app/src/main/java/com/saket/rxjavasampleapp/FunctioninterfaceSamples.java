package com.saket.rxjavasampleapp;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by sshriwas on 2020-03-07
 */
public class FunctioninterfaceSamples {

    private static final String TAG = "FunctioninterfaceSamples";

    public FunctioninterfaceSamples() {
        //Hashmap..
        Map<String, Integer> nameMap = new HashMap<>();
        Integer value = nameMap.computeIfAbsent("test", s -> s.length());

        //Function is a functional interface with one abstract method apply()
        //it takes one param of type T and returns a value of type R
        Function<String, Integer> function = String::length;    //Since lambda expression simply calls
        //another method, we replace it with method reference.
        int length = function.apply("Saket");

        //Consumer takes a value but returns no value
        Consumer<String> consumer = s -> Log.d(TAG, "onCreate: " + s);
        consumer.accept("Saket");

        //Supplier is a functional interface with abstract method get()
        //it takes no values but returns a value of type T
        Supplier<String> supplier = ()-> "Hi thanks for calling your supplier";
        String response = supplier.get();

        //BiFunction is a functional interface with abstract method apply
        //it takes 2 (T, U) params and returns a third value R
        BiFunction<Integer, Integer, String> biFunction = (t,u) -> {
            int sum = t + u;
            return "Total is: " + sum;
        };

        String output = biFunction.apply(3,5);

        //Predicate is a functional interface with abstract method test
        //it takes one argument (T) and returns a boolean value
        Predicate<String> startsWithG = s -> s.startsWith("G");

        boolean isStartWithG = startsWithG.test("Test");
    }


}
