package com.example.group21.balancebasket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ConnectscreenFragment extends Fragment {

    private static Button motionButton;
    private static Button joystickButton;
    private static Button followButton;
    private static Button shoppinglistButton;
    private static TextView connectionText;
    private static TextView connectText;
    private static ProgressBar progressBar;
    private static LinearLayout connectionlayout;
    private OnFragmentInteractionListener mListener;
    private BroadcastReceiver receiver;

    public ConnectscreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // receive broadcastmessage
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // handle message about Bluetooth connection state
                String message = intent.getStringExtra(Bluetooth.CONNECTION_MESSAGE);
                boolean isConnected = message.contains(Bluetooth.STATE_CONNECTED);
                changeButtonState(isConnected);
            }
        };
    }

    // change button state when bluetooth connection state changes
    public void changeButtonState(boolean isConnected) {
        if(!BasketDrawer.fakeConnection) {
            if (isConnected) {
                motionButton.setEnabled(true);
                joystickButton.setEnabled(true);
                followButton.setEnabled(true);
                connectText.setText("");
                connectionText.setText("");
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                connectionlayout.setBackgroundResource(R.drawable.connected);
            } else {
                motionButton.setEnabled(false);
                joystickButton.setEnabled(false);
                followButton.setEnabled(false);
                connectText.setText("Connecting");
                connectionText.setText("Establishing Bluetooth Connection...");
                progressBar.setVisibility(ProgressBar.VISIBLE);
                connectionlayout.setBackgroundResource(R.drawable.connect);
            }
        }
        else{
            motionButton.setEnabled(true);
            joystickButton.setEnabled(true);
            followButton.setEnabled(true);
            connectText.setText("");
            connectionText.setText("");
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            connectionlayout.setBackgroundResource(R.drawable.connected);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connectscreen, container, false);

        motionButton = (Button) view.findViewById(R.id.Motion_Button);
        joystickButton = (Button) view.findViewById(R.id.Joystick_Button);
        followButton = (Button) view.findViewById(R.id.Follow_Button);
        shoppinglistButton = (Button) view.findViewById(R.id.List_Button);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        connectionText = (TextView) view.findViewById(R.id.Connection_text);
        connectText = (TextView) view.findViewById(R.id.Connect_text);
        connectionlayout = (LinearLayout) view.findViewById(R.id.connection_status);

        // initiate button listeners
        final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        makeMotionButtonListener(transaction);
        makeJoystickButtonListener(transaction);
        makeFollowButtonListener(transaction);
        makeShoppinglistButtonListener(transaction);

        // get state of bluetooth connection on start
        boolean isConnected = false;
        if (BasketDrawer.isIOIOConnected()) {
            isConnected = true;
        }
        // set initial button state
        changeButtonState(isConnected);
        BasketDrawer.buttonState = false;
        return view;
    }

    private void makeShoppinglistButtonListener(final FragmentTransaction transaction) {
        shoppinglistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListFragment shoppinglistFragment = new ShoppingListFragment();
                shoppinglistFragment.setArguments(getActivity().getIntent().getExtras());
                FollowFragment followFragment = new FollowFragment();
                followFragment.setArguments(getActivity().getIntent().getExtras());
                transaction.replace(R.id.basketDrawerFrame, shoppinglistFragment);
                transaction.commit();
            }
        });
    }

    private void makeFollowButtonListener(final FragmentTransaction transaction) {
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowFragment followFragment = new FollowFragment();
                followFragment.setArguments(getActivity().getIntent().getExtras());
                transaction.replace(R.id.basketDrawerFrame, followFragment);
                transaction.commit();
            }
        });
    }

    private void makeJoystickButtonListener(final FragmentTransaction transaction) {
        joystickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoystickFragment joystickFragment = new JoystickFragment();
                joystickFragment.setArguments(getActivity().getIntent().getExtras());
                transaction.replace(R.id.basketDrawerFrame, joystickFragment);
                transaction.commit();
            }
        });
    }

    private void makeMotionButtonListener(final FragmentTransaction transaction) {
        motionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImuFragment imuFragment = new ImuFragment();
                imuFragment.setArguments(getActivity().getIntent().getExtras());
                transaction.replace(R.id.basketDrawerFrame, imuFragment);
                transaction.commit();
            }
        });
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
    public void onResume() {
        super.onResume();
        getActivity().bindService(new Intent(getActivity(), Bluetooth.class), BasketDrawer.blueConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver((receiver),
                new IntentFilter(Bluetooth.CONNECTION_STATE));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
        super.onStop();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}