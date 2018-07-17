package com.charles.cartpartners_v1;

import android.provider.BaseColumns;

public class ItemContract {

    private ItemContract() {

    }

    static class ItemEntry implements BaseColumns {
        static final String TABLE_NAME = "ITEM_TBL";
        static final String COLUMN_ID = "ID";
        static final String COLUMN_NAME = "NAME";
        static final String COLUMN_TYPE = "TYPE";
        static final String COLUMN_PRICE = "PRICE";
        static final String COLUMN_QUANTITY = "QUANTITY";
        static final String COLUMN_DATE = "DATE";
        static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    }

}
