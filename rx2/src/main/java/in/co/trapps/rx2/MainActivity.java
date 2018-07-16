package in.co.trapps.rx2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "RxArena";
    private Disposable disposable;
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
//        Observer observer = completeObserver();

        // Single Action Observers
//        Action1 action1 = onNextAction();
        Consumer consumer = onNextZipObjectAction();

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    /**
     * Create a Observable, with just() operator.
     *
     * @return
     */
    private Observable justObservable() {
        return Observable
                .just(1, 2, 3, 4, 5) // Emits Numbers
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        Log.d(TAG, "In filterObservable -> Integer: " + integer);
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
        Observable<Integer> observable1 = Observable.fromArray(new Integer[]{1, 2, 3, 4, 5});  // Emits integers
        Observable<String> observable2 = Observable.fromArray(new String[]{"A", "B", "C", "D", "F"});  // Emits alphabets
        Observable<ZipObject> observable = Observable.zip(observable1, observable2,
                new BiFunction<Integer, String, ZipObject>() {
                    @Override
                    public ZipObject apply(Integer integer, String s) throws Exception {
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
            public void onComplete() {
                Log.d(TAG, "In onComplete");
                String result = tvResult.getText().toString();
                tvResult.setText(result + " complete");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "In onError");
            }

            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "In onSubscribe");
                disposable = d;
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
    private Consumer onNextAction() {
        return new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
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
    private Consumer<ZipObject> onNextZipObjectAction() {
        return new Consumer<ZipObject>() {
            @Override
            public void accept(ZipObject zipObject) throws Exception {
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

        // don't send events once the activity is destroyed
        disposable.dispose();
    }
}
