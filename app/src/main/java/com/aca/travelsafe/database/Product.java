package com.aca.travelsafe.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Marsel on 6/4/2016.
 */

@Table(database = DBMaster.class)
public class Product extends BaseModel {

    @Column
    @PrimaryKey
    public String
            ProductCode;

    @Column
    public String
                Description,
                OrderNo,
                CurrencyCode,
                IsActive;

    public static Product get(String productCode) {
        Product product = null;
        product = new Select().from(Product.class)
                .where(Product_Table.ProductCode.eq(productCode))
                .querySingle();

        return product;
    }

}
