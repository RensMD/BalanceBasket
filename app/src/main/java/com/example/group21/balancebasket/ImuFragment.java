package com.example.group21.balancebasket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class ImuFragment extends Fragment {

    private Button mButton;
    public TextView mPitchView;
    public TextView mRollView;
    public TableRow mTableRow;
    private FrameLayout mLayout;

    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private int counter = 0;
    boolean buttonState;

    public float pitchZero = 0;
    public float rollZero = 0;
    public String newPitch;
    public String newRoll;

    public  float  intpitchZero;
    public  float  introllZero;
    public float intPitch;
    public float intRoll;

    DecimalFormat d = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);

    private OnFragmentInteractionListener mListener;

    public ImuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_imu, container, false);

//        inputview = (TextView) view.findViewById(R.id.textView3);
        mPitchView = (TextView) view.findViewById(R.id.textView1);
        mRollView = (TextView) view.findViewById(R.id.textView2);

        mButton = (Button) view.findViewById(R.id.activate_button);
        mLayout = (FrameLayout) view.findViewById(R.id.imu_layout);

        mButton.setBackgroundResource(R.drawable.no_connection);
        mLayout.setBackgroundColor(Color.parseColor("#FFEBEE"));

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() { // Hide the menu icon and tablerow if there is no build in gyroscope in the device
            @Override
            public void run() {
                if (SensorFusion.IMUOutputSelection == -1)
                    mHandler.postDelayed(this, 100); // Run this again if it hasn't initialized the sensors yet
                else if (SensorFusion.IMUOutputSelection != 2) // Check if a gyro is supported
                    mTableRow.setVisibility(View.GONE); // If not then hide the tablerow
            }
        }, 100); // Wait 100ms before running the code

        BasketDrawer.buttonState = false;

        mButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    pitchZero = Float.parseFloat(BasketDrawer.mSensorFusion.pitch);
                    rollZero = Float.parseFloat(BasketDrawer.mSensorFusion.roll);
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        d.setRoundingMode(RoundingMode.HALF_UP);
        d.setMaximumFractionDigits(1);
        d.setMinimumFractionDigits(1);
        super.onResume();
        getActivity().bindService(new Intent(getActivity(), Bluetooth.class), BasketDrawer.blueConnection, Context.BIND_AUTO_CREATE);
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this, 50); // Update IMU data every 50ms
                if (BasketDrawer.mSensorFusion == null)
                    return;

                intPitch = Float.parseFloat(BasketDrawer.mSensorFusion.pitch) ;
                intRoll =  Float.parseFloat(BasketDrawer.mSensorFusion.roll);
                intpitchZero =intPitch - pitchZero;
                introllZero =intRoll - rollZero;

                newPitch = Float.toString(Float.parseFloat(d.format(intpitchZero)));
                newRoll = Float.toString(Float.parseFloat(d.format(introllZero)));

                mPitchView.setText(newPitch);
                mRollView.setText(newRoll);

                // TODO: Test BT input Read!
//                Bluetooth.read();
//                inputview.setText(Bluetooth.input);

                counter++;
                if (counter > 2) { // Only send data every 150ms time
                    counter = 0;

                    if(!BasketDrawer.fakeConnection) {
                        if (BasketDrawer.bluetoothService == null)
                            return;
                        if (BasketDrawer.bluetoothService.getState() == Bluetooth.STATE_BT_CONNECTED) {
                            buttonState = mButton.isPressed();
                            BasketDrawer.buttonState = buttonState;
                            mLayout.setBackgroundColor(Color.parseColor("#E0F2F1"));

                            if (BasketDrawer.joystickReleased) {
                                if (buttonState) {
                                    BasketDrawer.bluetoothService.write(BasketDrawer.sendIMUValues + newPitch + ',' + newRoll + ";");
                                    mButton.setBackgroundResource(R.drawable.imu_sending);

                                } else {
                                    BasketDrawer.bluetoothService.write(BasketDrawer.sendStop);
                                    mButton.setBackgroundResource(R.drawable.imu_control);
                                }
                            }
                        } else {
                            mButton.setBackgroundResource(R.drawable.no_connection);
                            mLayout.setBackgroundColor(Color.parseColor("#FFEBEE"));
                        }
                    } else {
                        buttonState = mButton.isPressed();
                        BasketDrawer.buttonState = buttonState;
                        mLayout.setBackgroundColor(Color.parseColor("#E0F2F1"));
                        if (BasketDrawer.joystickReleased) {
                            if (buttonState) {
                                mButton.setBackgroundResource(R.drawable.imu_sending);

                            } else {
                                mButton.setBackgroundResource(R.drawable.imu_control);
                            }
                        }
                    }
                }
            }
        };
        mHandler.postDelayed(mRunnable, 50); // Update IMU data every 50ms
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
        getActivity().unbindService(BasketDrawer.blueConnection);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
