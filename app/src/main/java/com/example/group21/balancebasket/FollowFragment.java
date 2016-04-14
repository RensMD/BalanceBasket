package com.example.group21.balancebasket;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import com.google.android.gms.vision.Frame;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FollowFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FollowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowFragment extends Fragment {
    private ToggleButton fButton;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private int counter = 0;
    private boolean toggleButtonState;
    private FrameLayout fLayout;
    private OnFragmentInteractionListener mListener;

    public FollowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FollowFragment.
     */
    public static FollowFragment newInstance() {
        FollowFragment fragment = new FollowFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_follow, container, false);

        fButton = (ToggleButton)view.findViewById(R.id.follow_button);
        fLayout = (FrameLayout)view.findViewById(R.id.follow_layout);


        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() { // Hide the menu icon and tablerow if there is no build in gyroscope in the device
            @Override
            public void run() {
//                if (SensorFusion.IMUOutputSelection == -1)
//                    mHandler.postDelayed(this, 100); // Run this again if it hasn't initialized the sensors yet
//                else if (SensorFusion.IMUOutputSelection != 2) // Check if a gyro is supported
//                    mTableRow.setVisibility(View.GONE); // If not then hide the tablerow
            }
        }, 100); // Wait 100ms before running the code

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
                    if (BasketDrawer.bluetoothService == null)
                        return;
                    if (BasketDrawer.bluetoothService.getState() == Bluetooth.STATE_BT_CONNECTED){
                        fLayout.setBackgroundColor((Color.parseColor("@color/colorWhite")));
                        toggleButtonState = fButton.isChecked();
                        if (BasketDrawer.joystickReleased) {
                            if (toggleButtonState) {
//                                lockRotation();
                                BasketDrawer.bluetoothService.write(BasketDrawer.sendFollow + ";");
                                BasketDrawer.follow = true;
                                fButton.setBackgroundResource(R.drawable.follow_following);
                            } else {
//                                unlockRotation();
                                BasketDrawer.bluetoothService.write(BasketDrawer.sendStop);
                                BasketDrawer.follow = false;
                                fButton.setBackgroundResource(R.drawable.follow_start);
                            }
                        }
                    }
                    else {
                        fButton.setBackgroundResource(R.drawable.follow_blue);
                        fLayout.setBackgroundColor((Color.parseColor("#ffebee")));
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
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
