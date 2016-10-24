package com.aca.travelsafe.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aca.travelsafe.R;
import com.aca.travelsafe.Widget.TouchImageView;
import com.aca.travelsafe.database.Setvar;
import com.aca.travelsafe.database.SubProductPlan;
import com.aca.travelsafe.database.SubProductPlan_Table;
import com.aca.travelsafe.database.SubProduct_Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * Created by Marsel on 25/11/2015.
 */
public class UtilImage {
    public static final int width = 1024;
    public static final int height = 768;
    public static final int maxByte = 1000000;

    public static String convertBase64(ImageView imageView) {
//        imageView.buildDrawingCache();
//        Bitmap bm = imageView.getDrawingCache();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
//        byte[] b = baos.toByteArray();
//
//        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//        return encodedImage;


        Bitmap bitmap = resizeImage(imageView);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bb = bos.toByteArray();
        String image = Base64.encodeToString(bb, Base64.DEFAULT);
        return image;


    }

    public static Bitmap resizeImage(ImageView imgView) {
        BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();


        while (bitmap.getByteCount() > maxByte) {
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.8), (int) (bitmap.getHeight() * 0.8), true);
        }
        return bitmap;
    }

    public static Dialog popupImage(final Context context, final String url, final String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_touch_image_view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        final TouchImageView touchImageView = (TouchImageView) dialog.findViewById(R.id.imgPhoto);
        final TextView txtGettingImage = (TextView) dialog.findViewById(R.id.txtGettingImage);
        final ImageView imgClose = (ImageView) dialog.findViewById(R.id.imgClose);

        touchImageView.setVisibility(ImageView.GONE);
        txtGettingImage.setText(message);
        txtGettingImage.setVisibility(View.VISIBLE);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });

        builder.build()
                .load(url)
//                .resize(500,0)
                .into(touchImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        touchImageView.setVisibility(View.VISIBLE);
                        txtGettingImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        txtGettingImage.setVisibility(View.GONE);
                    }
                });

        return dialog;

//        touchImageView.setImageDrawable(imageDwb);
    }


    public static String ImageToString(String path) {
        String sourceFileUri = path;
        File file = new File(sourceFileUri);
        String retValue = null;

        if (file.isFile()) {
            FileInputStream objFileIS = null;
            ByteArrayOutputStream objByteArrayOS = null;
            try {

                //resizeImageFile(file, 500);

                file = new File(sourceFileUri);

                objFileIS = new FileInputStream(file);

                objByteArrayOS = new ByteArrayOutputStream();
                byte[] byteBufferString = new byte[1024];

                for (int readNum; (readNum = objFileIS.read(byteBufferString)) != -1; )
                    objByteArrayOS.write(byteBufferString, 0, readNum);

                byte[] byteBinaryData = Base64.encode((objByteArrayOS.toByteArray()), Base64.DEFAULT);
                String strAttachmentCoded = new String(byteBinaryData);

                retValue = strAttachmentCoded;

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (objByteArrayOS != null)
                    try {
                        objByteArrayOS.close();
                        objFileIS.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }
        }

        return retValue;

    }

    public static void setImgPlanDescription(View parent,final Activity activity, final ImageView imgPlan, String planId) {
        try {
            SubProductPlan subProductPlan = new Select().from(SubProductPlan.class)
                    .where(SubProductPlan_Table.PlanCode.eq(planId))
                    .querySingle();

            if (subProductPlan == null)
                return;

            String imageName = subProductPlan.BenefitImage;
            String imageFolder = Setvar.getValue(var.UrlBenefitImage);
            final String imageUrl = imageFolder  + imageName;

            final Toast toast = Toast.makeText(activity, "Getting Plan", Toast.LENGTH_LONG);
            toast.show();

            Picasso.Builder builder = new Picasso.Builder(activity);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    exception.printStackTrace();
                }
            });
            builder.build()
                    .load(imageUrl)
                    .resize(700,0)
                    .into(imgPlan, new Callback() {
                        @Override
                        public void onSuccess() {
                            toast.cancel();

                            if (imgPlan == null)
                                return;

                            imgPlan.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    UtilImage.popupImage(activity, imageUrl, "Getting plan");
                                }
                            });
                        }

                        @Override
                        public void onError() {
                            toast.cancel();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
