package com.saket.rxjavasampleapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.saket.rxjavasampleapp.observables.CombineObservables;
import com.saket.rxjavasampleapp.observables.CreateObservables;
import com.saket.rxjavasampleapp.observables.FilterObservables;
import com.saket.rxjavasampleapp.observables.TransformObservables;

public class MainActivity extends AppCompatActivity implements
        CreateObservables.updateValueListener {

    private static final String TAG = "MainActivity";
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.textView);
        //createObservables();
        //transformObservables();
        //filterObservables();
        //combineObservables();
        useSchedulers();
    }

    private void createObservables() {
        CreateObservables createObservables = new CreateObservables();
        //createObservables.useCreateforObservable(MainActivity.this);
        //createObservables.useJustforObservable();
        //createObservables.useFromforObservable();
        //createObservables.useRangeforObservables(this);
        //createObservables.useTimerforObservable();
        //createObservables.usedelayforObservable();
        createObservables.useintervalforObservable(MainActivity.this);
    }

    private void transformObservables() {
        TransformObservables transformObservables = new TransformObservables();
        //transformObservables.useBufferforObservable();
        //transformObservables.useGroupByforObservables();
        //transformObservables.useMapforObservable();
        //transformObservables.useFlatmapforObservable();
        //transformObservables.useConcatMapforObservable();
        transformObservables.useSwitchMapforObservable();
    }

    private void filterObservables() {
        FilterObservables filterObservables = new FilterObservables();
        //filterObservables.useDeboucetoFilterEmits();
        //filterObservables.useDistinctToFilterEmits();
        filterObservables.useFiltertoFilterEmits();
    }

    private void combineObservables() {
        CombineObservables combineObservables = new CombineObservables();
        //combineObservables.combineUsingCombineLatest();
        //combineObservables.useMergeToCombineObservables();
        //combineObservables.useConcatToCombineObservables();
        combineObservables.useZipToCombineObservables();
    }

    private void useSchedulers() {
        Schedulers schedulers = new Schedulers();
        schedulers.useSchedulersio();
    }

    @Override
    public void updateUI(String val) {
        mTextView.setText(val);
    }
}
