package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.aca.travelsafe.Dal.Policy;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.Utility;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.Widget.MyWebView;
import com.aca.travelsafe.database.Setvar;
import com.aca.travelsafe.database.SppaMain;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PaymentFragment extends BaseFragment {
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.webView)
    WebView webView;
    private OnFragmentInteractionListener mListener;
    private ProgressDialog progressDialog;

    public PaymentFragment() {
    }

    public static PaymentFragment newInstance() {
        PaymentFragment fragment = new PaymentFragment();
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
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = (Activity) context;
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
    protected void init(View view) {
        prepareProgress();
        prepareWebView();
    }


    @Override
    protected void registerListener() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.stopLoading();
            webView.invalidate();
            webView.destroy();
        }

        if (progressBar != null) progressBar.invalidate();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    private void prepareProgress() {
        progressBar.setProgress(0);
        progressDialog = Utility.showProgressDialog(context);
        progressDialog.setCancelable(true);
    }

    private void prepareWebView() {
        try {
            SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
            String sppaNo = sppaMain.SppaNo;
            String nama = sppaMain.Name;
            String amount = String.valueOf(Math.round(Double.parseDouble(sppaMain.TotalPremiumAmount) * Double.parseDouble(sppaMain.ExchangeRate)));
            String klikAcaUrl = Setvar.getValue(var.KlikAcaUrl);
            String url = String.format(klikAcaUrl, sppaNo, amount, nama);

            webView = MyWebView.create(webView, new MyWebViewClient());
            webView.loadUrl(url);
            webView.setWebViewClient(webViewclient(amount));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private WebViewClient webViewclient(String amount) {
                SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
        final String sppaNo = sppaMain.SppaNo;
        final String userEmail = sppaMain.CreateBy;

        final String finishPaymentURLBII = Setvar.getValue(var.FinishPayURL);
        final String finishPaymentURLBCA = Setvar.getValue(var.FinalPayURLBCA);

        final String paymentURLCCBII = "http://172.16.88.31/klikacatestAPI/mobile/transactiondetilm.aspx?vpc_3DSXID=a16vrdGmcOrAO0P6MoKnnZuJHWo=&vpc_AVSRequestCode=Z&vpc_AVSResultCode=Unsupported&vpc_AcqAVSRespCode=Unsupported&vpc_AcqCSCRespCode=M" +
                "&vpc_Amount=" + amount +
                "&vpc_BatchNo=20160204&vpc_CSCResultCode=M&vpc_Card=VC&vpc_Command=pay&vpc_Locale=en&" +
                "vpc_MerchTxnRef=" + sppaNo + "/1&vpc_Merchant=BII009013700&vpc_Message=Approved" +
                "&vpc_OrderInfo=polis" + sppaNo + "&vpc_RiskOverallResult=ACC&vpc_SecureHash=BE598B1F91D4CE5D94BA43748E854E78&vpc_TransactionNo=3182&vpc_TxnResponseCode=0&vpc_VerSecurityLevel=06&vpc_VerStatus=E&vpc_VerType=3DS&vpc_Version=1" +
                "&vpc_ReceiptNo=603520522984" +
                "&vpc_AuthorizeId=332336";

        final String paymentURLCCBCA=
                String.format("http://172.16.88.31/wsklikpaytest/klikpayTravel.aspx?" +
                "transactionNo=%s" +
                "&totalAmount=%s" +
                "&approvalCode=564654", sppaNo, amount);

        final String paymentURLKlikPay=
                String.format("http://172.16.88.31/wsklikpaytest/klikpayTravel.aspx?" +
                "transactionNo=%s" +
                "&totalAmount=%s" +
                "&approvalCode=", sppaNo, amount);



        return new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                createSnackbar(description, false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    super.onPageFinished(view, url);
                    debugLog("Finished : " + url);
                    progressBar.setVisibility(View.INVISIBLE);
                    progressDialog.dismiss();

     /*               if (url.contains(finishPaymentURLBII)) {
                        Policy.sendEmail(sppaNo, userEmail);
                        createToast(context.getString(R.string.message_caption_sending_policy));
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                try {
                    super.onPageStarted(view, url, favicon);
                    debugLog("Page started " + url);

                    if (url.contains(finishPaymentURLBII) || url.contains(finishPaymentURLBCA)) {
                        progressDialog.setMessage(getString(R.string.message_caption_complete_your_transaction));
                        progressDialog.setCancelable(false);
                        getActivity().setResult(Activity.RESULT_OK);
                    }

                    progressBar.setVisibility(View.VISIBLE);
                    progressDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                try {
                    super.onLoadResource(view, url);
                    debugLog("On load " + url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    debugLog("override :" + url);

                    /*disable line below in release*/
                   /* if (url.equalsIgnoreCase("http://www.klikbca.com/klikpay/klikpay.html")) {
                        view.loadUrl(paymentURLKlikPay);
                        return true;
                    }
                    else if (url.equalsIgnoreCase("http://visa.co.id/ap/id/personal/security/onlineshopping.shtml")) {
                        view.loadUrl(paymentURLCCBCA);
                        return true;
                    }
                    else if (url.equalsIgnoreCase("http://www.mastercard.com/sea/consumer/index.html")) {
                        view.loadUrl(paymentURLCCBII);
                        return true;
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        };
    }


    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (PaymentFragment.this.progressBar != null)
                PaymentFragment.this.progressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }


}
