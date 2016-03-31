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
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConnectscreenFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConnectscreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectscreenFragment extends Fragment {

    private static Button motionButton;
    private static Button joystickButton;
    private static Button followButton;
    private static Button shoppinglistButton;
    private static TextView connectionText;
    private static TextView chooseText;
    private static ProgressBar progressBar;

    private OnFragmentInteractionListener mListener;
    private BroadcastReceiver receiver;

    public ConnectscreenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConnectscreenFragment.
     */
    public static ConnectscreenFragment newInstance() {
        ConnectscreenFragment fragment = new ConnectscreenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
    public static void changeButtonState(boolean isConnected) {
        if(isConnected){
            motionButton.setEnabled(true);
            joystickButton.setEnabled(true);
            followButton.setEnabled(true);
            connectionText.setText("Connection Established!");
            chooseText.setText("Please choose a method of control:");
            //TODO: change icon to check
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        }
        else{
            motionButton.setEnabled(false);
            joystickButton.setEnabled(false);
            followButton.setEnabled(false);
            connectionText.setText("Establishing Connection...");
            chooseText.setText(" ");
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_connect__screen, container, false);

        motionButton = (Button) view.findViewById(R.id.Motion_Button);
        joystickButton = (Button) view.findViewById(R.id.Joystick_Button);
        followButton = (Button) view.findViewById(R.id.Follow_Button);
        shoppinglistButton = (Button) view.findViewById(R.id.List_Button);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        connectionText = (TextView) view.findViewById(R.id.Connection_text);
        chooseText = (TextView) view.findViewById(R.id.choose_Text);

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
        shoppinglistButton.setEnabled(true);
        changeButtonState(isConnected);

        return view;
    }

    private void makeShoppinglistButtonListener(final FragmentTransaction transaction) {
        shoppinglistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListFragment shoppinglistFragment = new ShoppingListFragment();
                shoppinglistFragment.setArguments(getActivity().getIntent().getExtras());
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


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onResume() {
        super.onResume();
        getActivity().bindService(new Intent(getActivity(), Bluetooth.class), BasketDrawer.blueConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver((receiver),
                new IntentFilter(Bluetooth.CONNECTION_MESSAGE));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
        super.onStop();
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
