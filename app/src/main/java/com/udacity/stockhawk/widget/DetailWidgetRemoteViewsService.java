package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static com.udacity.stockhawk.ui.MainActivity.STOCK_SYMBOL_EXTRA;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    private static final String[] QUOTES_COLUMNS = {
            Contract.Quote.TABLE_NAME + "." + Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
            Contract.Quote.COLUMN_PRICE,
    };
    // these indices must match the projection
    static final int INDEX_COLUMN_ID = Contract.Quote.POSITION_ID;
    static final int INDEX_COLUMN_SYMBOL = Contract.Quote.POSITION_SYMBOL;
    static final int INDEX_ABSOLUTE_CHANGE = Contract.Quote.POSITION_ABSOLUTE_CHANGE;
    static final int INDEX_PERCENTAGE_CHANGE = Contract.Quote.POSITION_PERCENTAGE_CHANGE;
    static final int INDEX_PRICE = Contract.Quote.POSITION_PRICE;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(Contract.Quote.URI,
                        QUOTES_COLUMNS, null, null, Contract.Quote.COLUMN_SYMBOL);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.list_item_quote_widget);

                String symbol = data.getString(INDEX_COLUMN_SYMBOL);
                Float price = data.getFloat(INDEX_PRICE);
                Float absoluteChange = data.getFloat(INDEX_ABSOLUTE_CHANGE);
                Float percentageChange = data.getFloat(INDEX_PERCENTAGE_CHANGE);

                DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus.setPositivePrefix("+$");
                DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
                percentageFormat.setMaximumFractionDigits(2);
                percentageFormat.setMinimumFractionDigits(2);
                percentageFormat.setPositivePrefix("+");
                String change = dollarFormatWithPlus.format(absoluteChange);
                String percentage = percentageFormat.format(percentageChange / 100);


                views.setTextViewText(R.id.symbol, symbol);
                views.setTextViewText(R.id.price, dollarFormat.format(price));
                views.setTextViewText(R.id.change, change);
                views.setTextViewText(R.id.change_percentage, percentage);

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(STOCK_SYMBOL_EXTRA, symbol);
                views.setOnClickFillInIntent(R.id.ll_container, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote_widget);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_COLUMN_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
