package in.co.trapps.rxarena;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "RxArena";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRx();
            }
        });
    }

    private void startRx() {
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "In onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "In onError");
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "In onNext, Received: " + integer);
            }
        };

        Observable
                .just(1, 2, 3, 4, 5) // Emits Numbers
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        // Check if the number is odd or not. If odd return true, to emit that object.
                        return integer % 2 != 0;
                    }
                })
                .subscribe(observer);
    }
}
