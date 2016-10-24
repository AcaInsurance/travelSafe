package com.aca.travelsafe.Adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aca.travelsafe.Dal.Policy;
import com.aca.travelsafe.FillInsuredActivity;
import com.aca.travelsafe.Fragment.MyDraftFragment;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.UtilService;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.Result;
import com.aca.travelsafe.database.SppaDestinationDraft;
import com.aca.travelsafe.database.SppaDomesticDraft;
import com.aca.travelsafe.database.SppaMainDraft;
import com.aca.travelsafe.database.SppaMainDraft_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MyDraftAdapter extends RecyclerView.Adapter<MyDraftAdapter.ViewHolder> {
    public List<SppaMainDraft> sppaMainDraftList;
    public List<SppaDestinationDraft> sppaDestinationDraftList;
    public List<SppaDomesticDraft> sppaDomesticDraftList;

    public Activity activity;
    public View view;

    public int position;
    public MyDraftAdapterListener mListener;

    public MyDraftAdapter(Activity activity, View view, MyDraftFragment fragment) {
        this.activity = activity;
        this.view = view;
        this.mListener = fragment;

        sppaMainDraftList = new Select().from(SppaMainDraft.class).queryList();
        sppaDestinationDraftList = new Select().from(SppaDestinationDraft.class).queryList();
        sppaDomesticDraftList = new Select().from(SppaDomesticDraft.class).queryList();
    }


    public interface MyDraftAdapterListener {
        public void showEmptyContent();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_my_draft, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SppaMainDraft sppaMainDraft = sppaMainDraftList.get(position);

        try {
            String departureDate = UtilDate.format(sppaMainDraft.EffectiveDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);
            String arrivalDate = UtilDate.format(sppaMainDraft.ExpireDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);
            String sppaDate = UtilDate.format(sppaMainDraft.SppaDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);

            holder.sppaNo = sppaMainDraft.SppaNo;
            holder.txtNoSppa.setText(sppaMainDraft.SppaNo);
            holder.txtCoverage.setText(Policy.getCoverage(sppaMainDraft));
            holder.txtDepartureDate.setText(departureDate);
            holder.txtArrivalDate.setText(arrivalDate);
            holder.txtDestination.setText(Policy.getDestinationDraft(sppaMainDraft.SppaNo));
            holder.txtTanggal.setText(sppaDate);

            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (UtilService.isOnline(activity)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);
                        builder.setMessage(R.string.message_dialog_confirm_delete)
                                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeSPPA(holder.sppaNo, position);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();


                    } else {
                        Snackbar.make(view, R.string.message_no_connection, Snackbar.LENGTH_SHORT).show();
                    }

                }
            });

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyDraftAdapter.this.position = position;

                    Intent intent = new Intent(activity, FillInsuredActivity.class);
                    intent.putExtra(var.SPPA_NO, holder.sppaNo);
                    activity.startActivityForResult(intent, activity.getResources().getInteger(R.integer.request_code_payment));

//                    SaveDraft saveDraft = new SaveDraft(holder.sppaNo, activity);
//                    saveDraft.saveAll();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void removeSPPA(final String sppaNo, final int position) {
        SppaMainDraft sppaMainDraft;

        try {
            Policy.inActiveSppa(sppaNo);

            sppaMainDraft = new Select().from(SppaMainDraft.class)
                    .where(SppaMainDraft_Table.SppaNo.eq(sppaNo))
                    .querySingle();

            if (sppaMainDraft == null)
                return;

            TravelService.createSPPAService(null)
                    .sppaMainRemove(sppaMainDraft)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Observer<List<Result>>() {
                        @Override
                        public void onCompleted() {
                            sppaMainDraftList.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Snackbar.make(view, "Delete item failed", Snackbar.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onNext(List<Result> results) {
                            try {
                                if (results == null || results.size() == 0)
                                    return;

                                String message = Result.getMessage(results);
                                String detail = Result.getDetail(results);

                                Log.d("MyDraftAdapter", "message " + message);
                                Log.d("MyDraftAdapter", "detail " + detail);

                                if (message.equalsIgnoreCase(var.TRUE)) {
                                    Snackbar.make(view, R.string.message_item_deleted, Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Snackbar.make(view, "Can't delete item due to " + detail, Snackbar.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void removeFromList() {
        sppaMainDraftList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        if (sppaMainDraftList.size() == 0) {
            mListener.showEmptyContent();
        }

        return sppaMainDraftList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        String sppaNo;

        @Bind(R.id.lblSppaNo)
        TextView lblSppaNo;
        @Bind(R.id.txtNoSppa)
        TextView txtNoSppa;
        @Bind(R.id.txtCoverage)
        TextView txtCoverage;
        @Bind(R.id.imgCalendar)
        ImageView imgCalendar;
        @Bind(R.id.txtDepartureDate)
        TextView txtDepartureDate;
        @Bind(R.id.txtArrivalDate)
        TextView txtArrivalDate;
        @Bind(R.id.imgPlane)
        ImageView imgPlane;
        @Bind(R.id.txtDestination)
        TextView txtDestination;
        @Bind(R.id.txtTanggal)
        TextView txtTanggal;
        @Bind(R.id.imgDelete)
        FrameLayout imgDelete;
        @Bind(R.id.viewCard)
        CardView viewCard;



        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}
