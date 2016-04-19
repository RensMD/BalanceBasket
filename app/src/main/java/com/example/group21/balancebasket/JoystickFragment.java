package com.example.group21.balancebasket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class JoystickFragment extends Fragment implements JoystickView.OnJoystickChangeListener {

    DecimalFormat d = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);
    JoystickView mJoystick;
    TextView mText1;
    TextView mText2;
    FrameLayout jLayout;

    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    private double xValue, yValue;

    private boolean joystickReleased;

    private OnFragmentInteractionListener mListener;

    public JoystickFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // process the joystick data
    private void newData(double xValue, double yValue, boolean joystickReleased) {
        if (xValue == 0 && yValue == 0) joystickReleased = true;

        BasketDrawer.joystickReleased = joystickReleased;
        this.joystickReleased = joystickReleased;
        this.xValue = xValue;
        this.yValue = yValue;
        mText1.setText( d.format(xValue));
        mText2.setText( d.format(yValue));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_joystick, container, false);

        mJoystick = (JoystickView) v.findViewById(R.id.joystick);
        mJoystick.setOnJoystickChangeListener(this);

        mText1 = (TextView) v.findViewById(R.id.joystickY);
        mText1.setText(R.string.defaultJoystickValue1);

        mText2 = (TextView) v.findViewById(R.id.joystickX);
        mText2.setText(R.string.defaultJoystickValue2);

        jLayout = (FrameLayout) v.findViewById(R.id.joystick_layout);
        jLayout.setBackgroundColor(Color.parseColor("#FFEBEE"));

        BasketDrawer.joystickReleased = true;

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        d.setMaximumFractionDigits(1);
        d.setMinimumFractionDigits(1);
        getActivity().bindService(new Intent(getActivity(), Bluetooth.class), BasketDrawer.blueConnection, Context.BIND_AUTO_CREATE);

        mJoystick.invalidate();
        BasketDrawer.joystickReleased = true;

        mRunnable = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this, 150); // Send data every 150ms
                if(!BasketDrawer.fakeConnection) {
                    if (BasketDrawer.bluetoothService == null)
                        return;
                    if (BasketDrawer.bluetoothService.getState() == Bluetooth.STATE_BT_CONNECTED) {
                        jLayout.setBackgroundColor(Color.parseColor("#E0F2F1"));
                        if (joystickReleased || (xValue == 0 && yValue == 0))
                            BasketDrawer.bluetoothService.write(BasketDrawer.sendStop);
                        else {
                            String message = BasketDrawer.sendJoystickValues + d.format(xValue) + ',' + d.format(yValue) + ";";
                            BasketDrawer.bluetoothService.write(message);
                        }
                    } else {
                        jLayout.setBackgroundColor(Color.parseColor("#FFEBEE"));
                    }
                } else {
                    jLayout.setBackgroundColor(Color.parseColor("#E0F2F1"));
                }
            }
        };
        mHandler.postDelayed(mRunnable, 150); // Send data every 150ms
    }

    @Override
    public void onStart() {
        super.onStart();
        mJoystick.invalidate();
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
    public void setOnTouchListener(double xValue, double yValue) {
        newData(xValue, yValue, false);
    }

    @Override
    public void setOnMovedListener(double xValue, double yValue) {
        newData(xValue, yValue, false);
    }

    @Override
    public void setOnReleaseListener(double xValue, double yValue) {
        newData(xValue, yValue, true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mJoystick.invalidate();
        BasketDrawer.joystickReleased = true;
        mHandler.removeCallbacks(mRunnable);
        getActivity().unbindService(BasketDrawer.blueConnection);
    }

    @Override
    public void onStop() {
        super.onStop();
        mJoystick.invalidate();
        BasketDrawer.joystickReleased = true;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
