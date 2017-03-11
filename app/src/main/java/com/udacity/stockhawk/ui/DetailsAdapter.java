package com.udacity.stockhawk.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.DateUtil;
import com.udacity.stockhawk.data.QuoteHistoryModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Kasim Kovacevic
 */
public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {

    List<QuoteHistoryModel> mQuoteHistoryModelList;
    DecimalFormat dollarFormat;

    public DetailsAdapter() {
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.details_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mQuoteHistoryModelList != null ? mQuoteHistoryModelList.size() : 0;
    }

    public void setQuoteHistory(List<QuoteHistoryModel> quoteHistory) {
        this.mQuoteHistoryModelList = quoteHistory;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_date)
        protected TextView mDate;
        @BindView(R.id.tv_value)
        protected TextView mValue;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        public void bind(int position) {
            mDate.setText(DateUtil.getFormattedDate(mQuoteHistoryModelList.get(position).getDate(), DateUtil.DATE_FORMAT_DD_MM_YYYY));
            mValue.setText(dollarFormat.format(mQuoteHistoryModelList.get(position).getValue()));
        }
    }


}
