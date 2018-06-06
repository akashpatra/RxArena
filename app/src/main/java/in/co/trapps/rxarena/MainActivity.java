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
                        String result = tvResult.getText().toString();
                        tvResult.setText(result + " " + 1);
                    }
                });
            }
        }).start();
    }

    private void startRx() {
        // Create a Observable
        Observable<Integer> observable = Observable
                .just(1, 2, 3, 4, 5) // Emits Numbers
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        // Check if the number is odd or not. If odd return true, to emit that object.
                        return integer % 2 != 0;
                    }
                });

        // One Way
        Observer<Integer> observer = new Observer<Integer>() {
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
                String result = tvResult.getText().toString();
                tvResult.setText(result + " " + integer);
            }
        };

        // Second Way
        Action1<Integer> onNextAction = new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.d(TAG, "In onNext, Received: " + integer);
            }
        };

        subscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }
}
