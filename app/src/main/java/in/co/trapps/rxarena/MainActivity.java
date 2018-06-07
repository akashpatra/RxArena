package in.co.trapps.rxarena;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "RxArena";
    private Subscription subscription;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResult = findViewById(R.id.textView2);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRx();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWithoutRx();
            }
        });
    }

    private void startWithoutRx() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "In run");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateText(String.valueOf(1));
                    }
                });
            }
        }).start();
    }

    private void startRx() {
        // Observables
//        Observable observable = justObservable();
        Observable observable = zipObservable();

        // Observers
        Observer observer = completeObserver();
//        Action1 action1 = onNextAction();
        Action1 action1 = onNextZipObjectAction();

        subscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action1);
    }

    /**
     * Create a Observable, with just() operator.
     *
     * @return
     */
    private Observable justObservable() {
        return Observable
                .just(1, 2, 3, 4, 5) // Emits Numbers
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        // Check if the number is odd or not. If odd return true, to emit that object.
                        return integer % 2 != 0;
                    }
                });
    }

    /**
     * Create a Observable, with zip() operator.
     *
     * @return
     */
    private Observable zipObservable() {
        Observable<Integer> observable1 = Observable.from(new Integer[]{1, 2, 3, 4, 5});  // Emits integers
        Observable<String> observable2 = Observable.from(new String[]{"A", "B", "C", "D", "F"});  // Emits alphabets
        Observable<ZipObject> observable = Observable.zip(observable1, observable2,
                new Func2<Integer, String, ZipObject>() {
                    @Override
                    public ZipObject call(Integer integer, String s) {
                        Log.d(TAG, "In zipObservable -> Integer: " + integer + ", String: " + s);
                        ZipObject zipObject = new ZipObject();
                        zipObject.number = integer;
                        zipObject.alphabet = s;
                        return zipObject;
                    }
                });
        return observable;
    }

    // Class that combines both data streams
    class ZipObject {
        int number;
        String alphabet;
    }

    /**
     * A full Observer with all callbacks.
     *
     * @return
     */
    private Observer completeObserver() {
        return new Observer<Integer>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "In onCompleted");
                String result = tvResult.getText().toString();
                tvResult.setText(result + "completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "In onError");
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "In onNext, Received: " + integer);
                updateText(String.valueOf(integer));
            }
        };
    }

    /**
     * Observer with only onNext Callback for Integer.
     *
     * @return
     */
    private Action1 onNextAction() {
        return new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.d(TAG, "In call, Received: " + integer);
                updateText(String.valueOf(integer));
            }
        };
    }

    /**
     * Observer with only onNext Callback for ZipObject.
     *
     * @return
     */
    private Action1 onNextZipObjectAction() {
        return new Action1<ZipObject>() {
            @Override
            public void call(ZipObject zipObject) {
                Log.d(TAG, "In zipObjectObserver, Integer: " + zipObject.number + ", String: " + zipObject.alphabet);
                updateText(zipObject.alphabet, String.valueOf(zipObject.number));
            }
        };
    }

    /**
     * Show the data in TextView.
     *
     * @param args
     */
    private void updateText(String... args) {
        String result;
        for (String str : args) {
            result = tvResult.getText().toString();
            tvResult.setText(result + " " + str);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }
}
