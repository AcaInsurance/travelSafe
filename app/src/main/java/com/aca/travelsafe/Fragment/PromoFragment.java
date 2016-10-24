package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.aca.travelsafe.R;
import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.Util.UtilImage;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.MasterPromo;
import com.aca.travelsafe.database.Setvar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PromoFragment extends BaseFragment {
    @Bind(R.id.viewParent)
    LinearLayout viewParent;
    @Bind(R.id.viewEmptyContent)
    RelativeLayout viewEmptyContent;

    public PromoFragment() {
    }

    public static PromoFragment newInstance() {
        PromoFragment fragment = new PromoFragment();
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
        View view = inflater.inflate(R.layout.fragment_promo, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    protected void init(View view) {
        fetchPromo();
    }


    @Override
    protected void registerListener() {

    }

    private void fetchPromo() {
        createSnackbar(getString(R.string.message_caption_getting_promo), false);
        TravelService.createPromoService(null)
                .getAll()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<MasterPromo>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        createToast(context.getString(R.string.message_failed_getting_promo));
                    }

                    @Override
                    public void onNext(List<MasterPromo> promos) {
                        checking(promos);
                        showPromo(promos);
                    }
                });
    }

    private void checking(List<MasterPromo> promos) {
        if (promos == null || promos.size() == 0) {
            viewEmptyContent.setVisibility(View.VISIBLE);
        }
        else {
            viewEmptyContent.setVisibility(View.GONE);
        }
    }

    private void showPromo(List<MasterPromo> promos) {
        String imageName;
        String imageUrl;
        String imageUrlPre = Setvar.getValue(var.UrlPromoImage);

        for (MasterPromo p : promos) {
            if (p == null)
                return;

            final ProgressBar progressBar = new ProgressBar(context);
            progressBar.setIndeterminate(true);
            viewParent.addView(progressBar);


            final ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
//            imageView.setLayoutParams(new LinearLayout.LayoutParams(
//                    (int) getResources().getDimension(R.dimen.promo_width),
//                    (int) getResources().getDimension(R.dimen.promo_height)
//            ));
            imageView.setAdjustViewBounds(true);
            int spacingLarge = (int) getResources().getDimension(R.dimen.spacing_normal);
            imageView.setPadding(spacingLarge, 0 , spacingLarge, spacingLarge);
            viewParent.addView(imageView);

            imageName = p.PromoImage;
            imageUrl = imageUrlPre + imageName;


            final String finalImageUrl = imageUrl;
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .resize(700,0)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    UtilImage.popupImage(context, finalImageUrl, getString(R.string.message_caption_getting_promo));
                                }
                            });


                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);
                            debugLog("Failed to load image");
                        }
                    });
        }

    }

}
