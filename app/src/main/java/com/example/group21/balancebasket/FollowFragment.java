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
import android.widget.ToggleButton;

public class FollowFragment extends Fragment {
    private ToggleButton fButton;
    private FrameLayout fLayout;

    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private int counter = 0;
    private boolean toggleButtonState;

    private OnFragmentInteractionListener mListener;

    public FollowFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_follow, container, false);

        fButton = (ToggleButton)view.findViewById(R.id.follow_button);
        fLayout = (FrameLayout)view.findViewById(R.id.follow_layout);

        toggleButtonState = false;

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() { // Hide the menu icon and tablerow if there is no build in gyroscope in the device
            @Override
            public void run() {
                // Empty
            }
        }, 100); // Wait 100ms before running the code

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().bindService(new Intent(getActivity(), Bluetooth.class), BasketDrawer.blueConnection, Context.BIND_AUTO_CREATE);
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this, 50); // Update IMU data every 50ms

                counter++;
                if (counter > 2) { // Only send data every 150ms time
                    counter = 0;

                    if(!BasketDrawer.fakeConnection) {
                        if (BasketDrawer.bluetoothService == null)
                            return;
                        if (BasketDrawer.bluetoothService.getState() == Bluetooth.STATE_BT_CONNECTED) {

                            fLayout.setBackgroundColor(Color.parseColor("#E0F2F1"));
                            toggleButtonState = fButton.isChecked();
                            if (BasketDrawer.joystickReleased) {
                                if (toggleButtonState) {
                                    BasketDrawer.bluetoothService.write(BasketDrawer.sendFollow + ";");
                                    fButton.setBackgroundResource(R.drawable.follow_following);
                                } else {
                                    BasketDrawer.bluetoothService.write(BasketDrawer.sendStop);
                                    fButton.setBackgroundResource(R.drawable.follow_start);
                                }
                            }
                        } else {
                            fButton.setBackgroundResource(R.drawable.follow_connection);
                            fLayout.setBackgroundColor(Color.parseColor("#FFEBEE"));
                        }
                    } else {
                        fLayout.setBackgroundColor(Color.parseColor("#E0F2F1"));
                        toggleButtonState = fButton.isChecked();
                        if (BasketDrawer.joystickReleased) {
                            if (toggleButtonState) {
                                fButton.setBackgroundResource(R.drawable.follow_following);
                            } else {
                                fButton.setBackgroundResource(R.drawable.follow_start);
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
