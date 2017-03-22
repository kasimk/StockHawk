package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.DateUtil;
import com.udacity.stockhawk.data.QuoteHistoryModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.udacity.stockhawk.ui.MainActivity.STOCK_SYMBOL_EXTRA;

public class DetailsActivity extends AppCompatActivity {

    private static final int STOCK_DETAILS_LOADER = 0;
    private static final String STOCK_SYMBOL_BUNDLE_KEY = "com.udacity.stockhawk.ui.DetailsActivity.STOCK_SYMBOL_BUNDLE_KEY";


    @BindView(R.id.lc_stock_details)
    protected LineChart mChart;
    @BindView(R.id.rv_details)
    protected RecyclerView mDetailsRecyclerView;
    @BindView(R.id.tv_absolute_value)
    protected TextView mAbsoluteValueTextView;
    @BindView(R.id.tv_percentage_value)
    protected TextView mPercentageValueTextView;
    @BindView(R.id.tv_price_value)
    protected TextView mPriceValueTextView;


    private String mSymbol;
    private DetailsAdapter mDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra(STOCK_SYMBOL_EXTRA) != null) {
            mSymbol = getIntent().getStringExtra(STOCK_SYMBOL_EXTRA);
        } else if (savedInstanceState != null && savedInstanceState.getString(STOCK_SYMBOL_BUNDLE_KEY) != null) {
            mSymbol = savedInstanceState.getString(STOCK_SYMBOL_BUNDLE_KEY);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mSymbol);
        }

        mDetailsAdapter = new DetailsAdapter();
        mDetailsRecyclerView.setAdapter(mDetailsAdapter);
        getSupportLoaderManager().initLoader(STOCK_DETAILS_LOADER, null, dataCallbacks);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSymbol != null) {
            outState.putString(STOCK_SYMBOL_BUNDLE_KEY, mSymbol);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setData(Cursor data) {
        List<Entry> entries = new ArrayList<>();
        final List<QuoteHistoryModel> quoteHistory = new ArrayList<>();
        if (data.moveToFirst()) {
            String history = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
            Float mAbsoluteChange = data.getFloat(data.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
            Float mPrice = data.getFloat(data.getColumnIndex(Contract.Quote.COLUMN_PRICE));
            Float mPercentageChange = data.getFloat(data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));
            DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            dollarFormatWithPlus.setPositivePrefix("+$");
            DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
            percentageFormat.setMaximumFractionDigits(2);
            percentageFormat.setMinimumFractionDigits(2);
            percentageFormat.setPositivePrefix("+");
            String change = dollarFormatWithPlus.format(mAbsoluteChange);
            String percentage = percentageFormat.format(mPercentageChange / 100);
            mAbsoluteValueTextView.setText(change);
            mPercentageValueTextView.setText(percentage);
            mPriceValueTextView.setText(dollarFormat.format(mPrice));

            String lines[] = history.split("\\r?\\n");
            for (String line : lines) {
                String elements[] = line.split(", ");
                quoteHistory.add(0, new QuoteHistoryModel(elements[0], elements[1]));
            }
            mDetailsAdapter.setQuoteHistory(quoteHistory);
            for (int i = 0; i < quoteHistory.size(); i++) {
                entries.add(new Entry(i, quoteHistory.get(i).getValue()));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, mSymbol);
        dataSet.setDrawFilled(true);
        dataSet.setLineWidth(5f);
        int accentColorId = ContextCompat.getColor(this, R.color.colorAccent);
        dataSet.setValueTextColor(accentColorId);
        dataSet.setValueTextSize(10f);
        LineData lineData = new LineData(dataSet);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setTextColor(accentColorId);
        //set date labels on x axis
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int dateIndex = (int) value;
                if (dateIndex >= quoteHistory.size()) {
                    dateIndex = quoteHistory.size() - 1;
                }
                return DateUtil.getFormattedDate(quoteHistory.get(dateIndex).getDate(), DateUtil.DATE_FORMAT_DD_MM_YYYY);
            }
        });
        //show 5 labels at once, on smaller devices 7 labels overlapping each other
        xAxis.setLabelCount(5, true);


        YAxis yAxisLeft = mChart.getAxis(YAxis.AxisDependency.LEFT);
        yAxisLeft.setTextColor(accentColorId);
        YAxis yAxisRight = mChart.getAxis(YAxis.AxisDependency.RIGHT);
        yAxisRight.setTextColor(accentColorId);

        mChart.setData(lineData);
        mChart.animateX(500);

    }


    private LoaderManager.LoaderCallbacks<Cursor> dataCallbacks
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            return new AsyncTaskLoader<Cursor>(DetailsActivity.this) {

                @Override
                protected void onStartLoading() {
                    forceLoad();
                }

                @Override
                public Cursor loadInBackground() {
                    try {
                        Uri uri = Contract.Quote.makeUriForStock(mSymbol);
                        return getContentResolver().query(uri,
                                null,
                                null,
                                null,
                                null);

                    } catch (Exception e) {
                        Timber.e(e);
                    }
                    return null;
                }


                public void deliverResult(Cursor data) {
                    super.deliverResult(data);
                }
            };


        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            setData(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };


}
