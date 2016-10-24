package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.aca.travelsafe.BaseActivity;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.UtilImage;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.Setvar;
import com.aca.travelsafe.database.SppaMain;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TermAndConditionFragment extends BaseFragment {

    @Bind(R.id.imgHalPenting)
    ImageView imgHalPenting;
    @Bind(R.id.btnBack)
    Button btnBack;
    @Bind(R.id.btnNext)
    Button btnNext;
    @Bind(R.id.viewButtonNavigation)
    LinearLayout viewButtonNavigation;
    @Bind(R.id.cbSetuju)
    AppCompatCheckBox cbSetuju;
    @Bind(R.id.pbLoading)
    ProgressBar pbLoading;

    private OnFragmentInteractionListener mListener;

    public TermAndConditionFragment() {
        // Required empty public constructor
    }

    public static TermAndConditionFragment newInstance() {
        TermAndConditionFragment fragment = new TermAndConditionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_term_and_condition, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(R.string.TermCondition);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            this.context = (Activity) context;
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    protected void init(View view) {
        setImageHalPenting();

    }


    @Override
    protected void registerListener() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void setProgressLoading(boolean show) {
        try {
            if (pbLoading == null)
                return;
            pbLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageHalPenting() {
        SppaMain sppaMain = SppaMain.get();
        String productCode = sppaMain.ProductCode;
        String imageCode = productCode.equals(var.INT) ? var.ImgPtgInt : var.ImgPtgDom;
        String imageName = Setvar.getValue(imageCode);
        String imageUrl = Setvar.getValue(var.UrlHalPtgImage);
        imageUrl = imageUrl + imageName;
        final String finalImageUrl = imageUrl;
        setProgressLoading(true);
        Picasso.with(getActivity())
                .load(imageUrl)
                .resize(700,0)
                .into(imgHalPenting, new Callback() {
                    @Override
                    public void onSuccess() {
                        try {
                            setProgressLoading(false);
                            imgHalPenting.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    UtilImage.popupImage(context, finalImageUrl, "");
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError() {
                        setProgressLoading(false);
                    }
                });

    }

    @OnClick({R.id.btnBack, R.id.btnNext})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                ((BaseActivity) getActivity()).popupFragment();
                break;

            case R.id.btnNext:
                if (!validate()) {
                    createSnackbar(context.getString(R.string.message_caption_setuju_syarat_ketentuan));
                    return;
                }
                ((BaseActivity) context).setFragment(R.id.fragment, FillPaymentFragment.newInstance());
                break;
        }
    }

    private boolean validate() {
        return cbSetuju.isChecked();
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
