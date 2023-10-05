package com.myhumandesignhd.ui.view.daytimepicker.widget;

import static com.github.florent37.singledateandtimepicker.widget.SingleDateAndTimeConstants.DAYS_PADDING;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.github.florent37.singledateandtimepicker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WheelDayPicker extends com.github.florent37.singledateandtimepicker.widget.WheelDayPicker {

    private static final String DAY_FORMAT_PATTERN = "EEE d MMM";

    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat customDateFormat;
    private int dayCount = DAYS_PADDING;

    private OnDaySelectedListener onDaySelectedListener;

    public WheelDayPicker(Context context) {
        super(context);
    }

    public WheelDayPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        simpleDateFormat = new SimpleDateFormat(DAY_FORMAT_PATTERN, getCurrentLocale());
        simpleDateFormat.setTimeZone(dateHelper.getTimeZone());
    }

    @Override
    public void setCustomLocale(Locale customLocale) {
        super.setCustomLocale(customLocale);
        simpleDateFormat = new SimpleDateFormat(DAY_FORMAT_PATTERN, getCurrentLocale());
        simpleDateFormat.setTimeZone(dateHelper.getTimeZone());
    }

    @Override
    protected com.github.florent37.singledateandtimepicker.widget.DateWithLabel initDefault() {
        return new com.github.florent37.singledateandtimepicker.widget.DateWithLabel(getTodayText(), new Date());
    }

    @NonNull
    private String getTodayText() {
        return getLocalizedString(R.string.picker_today);
    }

    @Override
    protected void onItemSelected(int position, com.github.florent37.singledateandtimepicker.widget.DateWithLabel item) {
        if (onDaySelectedListener != null) {
            onDaySelectedListener.onDaySelected(this, position, item.label, item.date);
        }
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }

    @Override
    protected List<com.github.florent37.singledateandtimepicker.widget.DateWithLabel> generateAdapterValues(boolean showOnlyFutureDates) {
        final List<com.github.florent37.singledateandtimepicker.widget.DateWithLabel> days = new ArrayList<>();

        Calendar instance = Calendar.getInstance();
        instance.setTimeZone(dateHelper.getTimeZone());
        int startDayOffset = showOnlyFutureDates ? 0 : -1 * dayCount;
        instance.add(Calendar.DATE, startDayOffset - 1);
        for (int i = startDayOffset; i < 0; ++i) {
            instance.add(Calendar.DAY_OF_MONTH, 1);
            Date date = instance.getTime();
            days.add(new com.github.florent37.singledateandtimepicker.widget.DateWithLabel(getFormattedValue(date), date));
        }

        //today
        days.add(new com.github.florent37.singledateandtimepicker.widget.DateWithLabel(getTodayText(), new Date()));

        instance = Calendar.getInstance();
        instance.setTimeZone(dateHelper.getTimeZone());

        for (int i = 0; i < dayCount; ++i) {
            instance.add(Calendar.DATE, 1);
            Date date = instance.getTime();
            days.add(new com.github.florent37.singledateandtimepicker.widget.DateWithLabel(getFormattedValue(date), date));
        }

        return days;
    }

    protected String getFormattedValue(Object value) {
        return getDateFormat().format(value);
    }

    public WheelDayPicker setDayFormatter(SimpleDateFormat simpleDateFormat) {
        simpleDateFormat.setTimeZone(dateHelper.getTimeZone());
        this.customDateFormat = simpleDateFormat;
        updateAdapter();
        return this;
    }

    public void setOnDaySelectedListener(OnDaySelectedListener onDaySelectedListener) {
        this.onDaySelectedListener = onDaySelectedListener;
    }

    public Date getCurrentDate() {
        return convertItemToDate(super.getCurrentItemPosition());
    }

    private SimpleDateFormat getDateFormat() {
        if (customDateFormat != null) {
            return customDateFormat;
        }
        return simpleDateFormat;
    }

    private Date convertItemToDate(int itemPosition) {
        Date date;
        final String itemText = adapter.getItemText(itemPosition);
        final Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTimeZone(dateHelper.getTimeZone());

        int todayPosition = -1;
        final List<com.github.florent37.singledateandtimepicker.widget.DateWithLabel> data = adapter.getData();

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).label.equals(getTodayText())) {
                todayPosition = i;
                break;
            }
        }

        if (getTodayText().equals(itemText)) {
            date = todayCalendar.getTime();
        } else {
            todayCalendar.add(Calendar.DAY_OF_YEAR, (itemPosition - todayPosition));
            date = todayCalendar.getTime();
        }
        return date;
    }

    public void setTodayText(DateWithLabel today) {
        final List<com.github.florent37.singledateandtimepicker.widget.DateWithLabel> data = adapter.getData();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).label.equals(getTodayText())) {
                adapter.getData().set(i, today);
                notifyDatasetChanged();
            }
        }
    }

    public interface OnDaySelectedListener {
        void onDaySelected(WheelDayPicker picker, int position, String name, Date date);
    }
}