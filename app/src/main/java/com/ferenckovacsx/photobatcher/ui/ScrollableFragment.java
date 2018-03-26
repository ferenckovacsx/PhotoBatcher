package com.ferenckovacsx.photobatcher.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.ferenckovacsx.photobatcher.R;

import java.io.File;

public class ScrollableFragment extends Fragment {

    final String TAG = "SCROLLABLE";

    private OnFragmentInteractionListener mListener;

    SubsamplingScaleImageView imageView;
    ImageView closeButton;
    String imageFilePath;

    public ScrollableFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageFilePath = getArguments().getString("filepath");
        Log.i(TAG, "onItemCLick filepath: " + imageFilePath);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View scrollableView = inflater.inflate(R.layout.fragment_scrollable, container, false);

        imageView = scrollableView.findViewById(R.id.touch_image_view);
        closeButton = scrollableView.findViewById(R.id.touch_close_button);

        Log.i(TAG, "onItemCLick filepath: " + imageFilePath);


        Uri uri = Uri.fromFile(new File(imageFilePath));
        imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
        imageView.setImage(ImageSource.uri(uri));

//        Picasso.with(getContext())
//                .load(uri)
//                .noFade()
//                .centerInside()
//                .fit()
//                .into(imageView);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return scrollableView;
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
