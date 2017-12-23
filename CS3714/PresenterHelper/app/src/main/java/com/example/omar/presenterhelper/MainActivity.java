package com.example.omar.presenterhelper;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
        implements ControlFragment.OnFragmentInteractionListener,
                   View.OnClickListener,
                   ListFragment.OnFragmentInteractionListener {

    private static final String THREAD_STATUS = "thread status";
    private static final String SECONDS = "seconds";
    private static final String MINUTES = "minutes";
    private static final String HOURS = "hours";
    private static final String LIST = "list";

    Button swap;
    ListFragment listFragment;
    ControlFragment controlFragment;
    boolean controlListSwitch = false;
    Timer stopwatch = new Timer();
    TimerAsyncTask timerAsyncTask;
    boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controlFragment = new ControlFragment();
        listFragment = new ListFragment();
        setContentView(R.layout.activity_main);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            controlFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.landControl, controlFragment).commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.landList, listFragment).commit();
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            swap = (Button) findViewById(R.id.swap);
            swap.setOnClickListener(this);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, controlFragment).commit();
        }
        controlFragment.setListFragment(listFragment);
        timerAsyncTask = new TimerAsyncTask();
        stopwatch = new Timer();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == swap.getId()) {
            if (controlListSwitch) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, controlFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                controlListSwitch = false;
                swap.setText("List");
            } else {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, listFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                controlListSwitch = true;
                swap.setText("Control");
            }
        }
    }

    @Override
    public void onStartClicked() {
        if (timerAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
            //tasks can only be executed once so we create a new instance of the task.
            running = true;
            timerAsyncTask = new TimerAsyncTask();
            timerAsyncTask.execute();
            controlFragment.start.setText("Stop");
        } else if (timerAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            //tasks can only be executed once so we create a new instance of the task.
            running = false;
            timerAsyncTask = new TimerAsyncTask();
            timerAsyncTask.cancel(true);
            controlFragment.start.setText("Start");
        }
    }

    @Override
    public void onLapClicked() {
        listFragment.addTime(String.format("%02d:%02d:%02d", stopwatch.getHours(),
                stopwatch.getMinutes(), stopwatch.getSeconds()));
        updateListLand();
    }

    @Override
    public void onResetClicked() {
        stopwatch.setSeconds(0);
        stopwatch.setMinutes(0);
        stopwatch.setHours(0);
        controlFragment.setText("00:00:00");
        running = false;
        timerAsyncTask = new TimerAsyncTask();
        timerAsyncTask.cancel(true);
        controlFragment.start.setText("Start");
        listFragment.clearTimes();
        updateListLand();
    }

    private void updateListLand() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(listFragment);
            ft.attach(listFragment);
            ft.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().beginTransaction().remove(controlFragment).commit();
        getSupportFragmentManager().beginTransaction().remove(listFragment).commit();
        if (timerAsyncTask != null &&
                timerAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            outState.putBoolean(THREAD_STATUS, true);
            timerAsyncTask.cancel(true);
        } else {
            outState.putBoolean(THREAD_STATUS, false);
        }
        outState.putStringArrayList(LIST, listFragment.getTimes());
        outState.putInt(SECONDS, stopwatch.getSeconds());
        outState.putInt(MINUTES, stopwatch.getMinutes());
        outState.putInt(HOURS, stopwatch.getHours());
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(THREAD_STATUS)) {
                timerAsyncTask = new TimerAsyncTask();
                running = true;
                stopwatch.setSeconds(savedInstanceState.getInt(SECONDS));
                stopwatch.setMinutes(savedInstanceState.getInt(MINUTES));
                stopwatch.setHours(savedInstanceState.getInt(HOURS));
                listFragment.setList(savedInstanceState.getStringArrayList(LIST));
                updateListLand();
                timerAsyncTask.execute();
                controlFragment.start.setText("Stop");
            } else {
                timerAsyncTask = new TimerAsyncTask();
                running = false;
                stopwatch.setSeconds(savedInstanceState.getInt(SECONDS));
                stopwatch.setMinutes(savedInstanceState.getInt(MINUTES));
                stopwatch.setHours(savedInstanceState.getInt(HOURS));
                listFragment.setList(savedInstanceState.getStringArrayList(LIST));
                controlFragment.setText(stopwatch.toString());
                updateListLand();
            }
        }
    }

    private class TimerAsyncTask extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            controlFragment.setText(String.format("%02d:%02d:%02d", values[2], values[1], values[0]));
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            while (running) {
                if (isCancelled()) break;
                try {
                    stopwatch.count();
                    publishProgress(stopwatch.getSeconds(),
                            stopwatch.getMinutes(), stopwatch.getHours());
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

}