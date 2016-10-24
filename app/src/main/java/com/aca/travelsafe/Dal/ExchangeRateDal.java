package com.aca.travelsafe.Dal;

import android.content.Context;
import android.util.Log;

import com.aca.travelsafe.Retrofit.TravelPaymentService;
import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.database.ExchangeRate;
import com.raizlabs.android.dbflow.sql.language.Delete;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Marsel on 21/6/2016.
 */
public class ExchangeRateDal {
    public ExchangeRateDalListener mListener;
    public Context context;

    public ExchangeRateDal(Context context) {
        this.context = context;
    }

    public interface ExchangeRateDalListener {
        public void loadRateError(String message);
        public void loadRateComplete();
    }

    public Subscription getExchangeRate() {
        try {
            Subscription subscription =
                    TravelPaymentService
                    .createExRateService(null)
                    .get()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Double>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            mListener.loadRateError(e.getMessage());
                        }

                        @Override
                        public void onNext(Double ExchRate) {
                            try {
//                                Log.d("FillConfirmationFragment", "exchange rate" + exchangeRates.get(0).ExchRate);
                                Log.d("FillConfirmationFragment", "exchange rate" + ExchRate);

                                Delete.table(ExchangeRate.class);

//                                ExchangeRate exchangeRate = exchangeRates.get(0);
                                ExchangeRate exchangeRate = new ExchangeRate();
                                exchangeRate.ExchRate = ExchRate;
                                exchangeRate.save();

                                mListener.loadRateComplete();
                            } catch (Exception e) {
                                e.printStackTrace();
                                mListener.loadRateError(e.getMessage());
                            }
                        }
                    });

            return subscription;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
