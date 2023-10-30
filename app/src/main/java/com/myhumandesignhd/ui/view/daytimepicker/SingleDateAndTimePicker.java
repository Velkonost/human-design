package com.myhumandesignhd.ui.view.daytimepicker;

import static com.github.florent37.singledateandtimepicker.widget.SingleDateAndTimeConstants.DAYS_PADDING;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.myhumandesignhd.App;
import com.myhumandesignhd.R;
import com.myhumandesignhd.ui.view.daytimepicker.widget.DateWithLabel;
import com.myhumandesignhd.ui.view.daytimepicker.widget.MyWheelPicker;
import com.myhumandesignhd.ui.view.daytimepicker.widget.WheelAmPmPicker;
import com.myhumandesignhd.ui.view.daytimepicker.widget.WheelDayOfMonthPicker;
import com.myhumandesignhd.ui.view.daytimepicker.widget.WheelDayPicker;
import com.myhumandesignhd.ui.view.daytimepicker.widget.WheelHourPicker;
import com.myhumandesignhd.ui.view.daytimepicker.widget.WheelMinutePicker;
import com.myhumandesignhd.ui.view.daytimepicker.widget.WheelMonthPicker;
import com.myhumandesignhd.ui.view.daytimepicker.widget.WheelYearPicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;



public class SingleDateAndTimePicker extends LinearLayout {

    public static final boolean IS_CYCLIC_DEFAULT = true;
    public static final boolean IS_CURVED_DEFAULT = false;
    public static final boolean MUST_BE_ON_FUTURE_DEFAULT = false;
    public static final int DELAY_BEFORE_CHECK_PAST = 200;
    private static final int VISIBLE_ITEM_COUNT_DEFAULT = 7;
    private static final int PM_HOUR_ADDITION = 12;
    public static final int ALIGN_CENTER = 0;
    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_RIGHT = 2;
    private DateHelper dateHelper = new DateHelper();

    private static final CharSequence FORMAT_24_HOUR = "EEE d MMM H:mm";
    private static final CharSequence FORMAT_12_HOUR = "EEE d MMM h:mm a";

    @NonNull
    private final WheelYearPicker yearsPicker;

    @NonNull
    private final WheelMonthPicker monthPicker;

    @NonNull
    private final WheelDayOfMonthPicker daysOfMonthPicker;

    @NonNull
    private final WheelDayPicker daysPicker;
    @NonNull
    private final WheelMinutePicker minutesPicker;
    @NonNull
    private final WheelHourPicker hoursPicker;
    @NonNull
    private final WheelAmPmPicker amPmPicker;

    private final List<MyWheelPicker> pickers = new ArrayList<>();

    private final List<OnDateChangedListener> listeners = new ArrayList<>();

    private final View dtSelector;
    private boolean mustBeOnFuture;

    @Nullable
    private Date minDate;
    @Nullable
    private Date maxDate;
    @NonNull
    private Date defaultDate;

    private boolean displayYears = false;
    private boolean displayMonth = false;
    private boolean displayDaysOfMonth = false;
    private boolean displayDays = true;
    private boolean displayMinutes = true;
    private boolean displayHours = true;

    private boolean isAmPm;

    public SingleDateAndTimePicker(Context context) {
        this(context, null);
    }

    public SingleDateAndTimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleDateAndTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        defaultDate = new Date();

        isAmPm = !(DateFormat.is24HourFormat(context));

        int layoutId = R.layout.single_day_and_time_picker;
        if (App.preferences.getLocale().equals("en")) {
            layoutId = R.layout.single_day_and_time_picker_en;
        }
        inflate(context, layoutId, this);

        yearsPicker = findViewById(R.id.yearPicker);
        monthPicker = findViewById(R.id.monthPicker);
        daysOfMonthPicker = findViewById(R.id.daysOfMonthPicker);
        daysPicker = findViewById(R.id.daysPicker);
        minutesPicker = findViewById(R.id.minutesPicker);
        hoursPicker = findViewById(R.id.hoursPicker);
        amPmPicker = findViewById(R.id.amPmPicker);
        dtSelector = findViewById(R.id.dtSelector);

        pickers.addAll(Arrays.asList(
                daysPicker,
                minutesPicker,
                hoursPicker,
                amPmPicker,
                daysOfMonthPicker,
                monthPicker,
                yearsPicker
        ));
        for (MyWheelPicker wheelPicker : pickers) {
            wheelPicker.setDateHelper(dateHelper);
        }
        init(context, attrs);
    }

    public void setDateHelper(DateHelper dateHelper) {
        this.dateHelper = dateHelper;
    }

    public void setTimeZone(TimeZone timeZone) {
        dateHelper.setTimeZone(timeZone);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        yearsPicker.setOnYearSelectedListener(new WheelYearPicker.OnYearSelectedListener() {
            @Override
            public void onYearSelected(WheelYearPicker picker, int position, int year) {
                updateListener();
                checkMinMaxDate(picker);

                if (displayDaysOfMonth) {
                    updateDaysOfMonth();
                }
            }
        });

        monthPicker.setOnMonthSelectedListener(new WheelMonthPicker.MonthSelectedListener() {
            @Override
            public void onMonthSelected(WheelMonthPicker picker, int monthIndex, String monthName) {
                updateListener();
                checkMinMaxDate(picker);

                if (displayDaysOfMonth) {
                    updateDaysOfMonth();
                }
            }
        });

        daysOfMonthPicker
                .setDayOfMonthSelectedListener(new WheelDayOfMonthPicker.DayOfMonthSelectedListener() {
                    @Override
                    public void onDayOfMonthSelected(WheelDayOfMonthPicker picker, int dayIndex) {
                        updateListener();
                        checkMinMaxDate(picker);
                    }
                });

        daysOfMonthPicker
                .setOnFinishedLoopListener(new WheelDayOfMonthPicker.FinishedLoopListener() {
                    @Override
                    public void onFinishedLoop(WheelDayOfMonthPicker picker) {
                        if (displayMonth) {
                            monthPicker.scrollTo(monthPicker.getCurrentItemPosition() + 1);
                            updateDaysOfMonth();
                        }
                    }
                });

        daysPicker
                .setOnDaySelectedListener(new WheelDayPicker.OnDaySelectedListener() {
                    @Override
                    public void onDaySelected(WheelDayPicker picker, int position, String name, Date date) {
                        updateListener();
                        checkMinMaxDate(picker);
                    }
                });

        minutesPicker
                .setOnMinuteChangedListener(new WheelMinutePicker.OnMinuteChangedListener() {
                    @Override
                    public void onMinuteChanged(WheelMinutePicker picker, int minutes) {
                        updateListener();
                        checkMinMaxDate(picker);
                    }
                })
                .setOnFinishedLoopListener(new WheelMinutePicker.OnFinishedLoopListener() {
                    @Override
                    public void onFinishedLoop(WheelMinutePicker picker) {
                        hoursPicker.scrollTo(hoursPicker.getCurrentItemPosition() + 1);
                    }
                });

        hoursPicker
                .setOnFinishedLoopListener(new WheelHourPicker.FinishedLoopListener() {
                    @Override
                    public void onFinishedLoop(WheelHourPicker picker) {
                        daysPicker.scrollTo(daysPicker.getCurrentItemPosition() + 1);
                    }
                })
                .setHourChangedListener(new WheelHourPicker.OnHourChangedListener() {
                    @Override
                    public void onHourChanged(WheelHourPicker picker, int hour) {
                        updateListener();
                        checkMinMaxDate(picker);
                    }
                });

        amPmPicker
                .setAmPmListener(new WheelAmPmPicker.AmPmListener() {
                    @Override
                    public void onAmPmChanged(WheelAmPmPicker picker, boolean isAm) {
                        updateListener();
                        checkMinMaxDate(picker);
                    }
                });

        setDefaultDate(this.defaultDate); //update displayed date
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (MyWheelPicker picker : pickers) {
            picker.setEnabled(enabled);
        }
    }

    public void setDisplayYears(boolean displayYears) {
        this.displayYears = displayYears;
        yearsPicker.setVisibility(displayYears ? VISIBLE : GONE);
    }

    public void setDisplayMonths(boolean displayMonths) {
        this.displayMonth = displayMonths;
        monthPicker.setVisibility(displayMonths ? VISIBLE : GONE);
        checkSettings();
    }

    public void setDisplayDaysOfMonth(boolean displayDaysOfMonth) {
        this.displayDaysOfMonth = displayDaysOfMonth;
        daysOfMonthPicker.setVisibility(displayDaysOfMonth ? VISIBLE : GONE);

        if (displayDaysOfMonth) {
            updateDaysOfMonth();
        }
        checkSettings();
    }

    public void setDisplayDays(boolean displayDays) {
        this.displayDays = displayDays;
        daysPicker.setVisibility(displayDays ? VISIBLE : GONE);
        checkSettings();
    }

    public void setDisplayMinutes(boolean displayMinutes) {
        this.displayMinutes = displayMinutes;
        minutesPicker.setVisibility(displayMinutes ? VISIBLE : GONE);
    }

    public void setDisplayHours(boolean displayHours) {
        this.displayHours = displayHours;
        hoursPicker.setVisibility(displayHours ? VISIBLE : GONE);

        setIsAmPm(this.isAmPm);
        hoursPicker.setIsAmPm(isAmPm);
    }

    public void setDisplayMonthNumbers(boolean displayMonthNumbers) {
        this.monthPicker.setDisplayMonthNumbers(displayMonthNumbers);
        this.monthPicker.updateAdapter();
    }

    public void setMonthFormat(String monthFormat) {
        this.monthPicker.setMonthFormat(monthFormat);
        this.monthPicker.updateAdapter();
    }

    public void setTodayText(DateWithLabel todayText) {
        if (todayText != null && todayText.label != null && !todayText.label.isEmpty()) {
            daysPicker.setTodayText(todayText);
        }
    }

    public void setItemSpacing(int size) {
        for (MyWheelPicker picker : pickers) {
            picker.setItemSpace(size);
        }
    }

    public void setCurvedMaxAngle(int angle) {
        for (MyWheelPicker picker : pickers) {
            picker.setCurvedMaxAngle(angle);
        }
    }

    public void setCurved(boolean curved) {
        for (MyWheelPicker picker : pickers) {
//            if (picker.getVisibility() == View.VISIBLE) {
                picker.setCurved(curved);
//            }

        }
    }

    public void setCyclic(boolean cyclic) {
        for (MyWheelPicker picker : pickers) {
            picker.setCyclic(cyclic);
        }
    }

    public void setTextSize(int textSize) {
        for (MyWheelPicker picker : pickers) {
            picker.setItemTextSize(textSize);
        }
    }

    public void setSelectedTextColor(int selectedTextColor) {
        for (MyWheelPicker picker : pickers) {
            picker.setSelectedItemTextColor(selectedTextColor);
        }
    }

    public void setTextColor(int textColor) {
        for (MyWheelPicker picker : pickers) {
            picker.setItemTextColor(textColor);
        }
    }

    public void setTextAlign(int align) {
        for (MyWheelPicker picker : pickers) {
            picker.setItemAlign(align);
        }
    }

    public void setTypeface(Typeface typeface) {
        if(typeface == null) return;
        for (MyWheelPicker picker : pickers) {
            picker.setTypeface(typeface);
        }
    }

    private void setFontToAllPickers(int resourceId) {
        if (resourceId > 0) {
            for (int i = 0; i< pickers.size();i++) {
                pickers.get(i).setTypeface(ResourcesCompat.getFont(getContext(), resourceId));
            }
        }
    }

    public void setSelectorColor(int selectorColor) {
//        dtSelector.setBackgroundColor(selectorColor);
    }

    public void setSelectorHeight(int selectorHeight) {
        final ViewGroup.LayoutParams dtSelectorLayoutParams = dtSelector.getLayoutParams();
        dtSelectorLayoutParams.height = selectorHeight;
        dtSelector.setLayoutParams(dtSelectorLayoutParams);
    }

    public void setVisibleItemCount(int visibleItemCount) {
        for (MyWheelPicker picker : pickers) {
            picker.setVisibleItemCount(visibleItemCount);
        }
    }

    public void setIsAmPm(boolean isAmPm) {
        this.isAmPm = isAmPm;

        amPmPicker.setVisibility((isAmPm && displayHours) ? VISIBLE : GONE);
        hoursPicker.setIsAmPm(isAmPm);
    }

    public boolean isAmPm() {
        return isAmPm;
    }

    public void setDayFormatter(SimpleDateFormat simpleDateFormat) {
        if (simpleDateFormat != null) {
            this.daysPicker.setDayFormatter(simpleDateFormat);
        }
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(dateHelper.getTimeZone());
        calendar.setTime(minDate);
        this.minDate = calendar.getTime();
        setMinYear();
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(dateHelper.getTimeZone());
        calendar.setTime(maxDate);
        this.maxDate = calendar.getTime();
        setMinYear();
    }

    public void setCustomLocale(Locale locale) {
        for (MyWheelPicker p : pickers) {
            p.setCustomLocale(locale);
            p.updateAdapter();
        }
    }

    private void checkMinMaxDate(final MyWheelPicker picker) {
        checkBeforeMinDate(picker);
        checkAfterMaxDate(picker);
    }

    private void checkBeforeMinDate(final MyWheelPicker picker) {
        picker.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (minDate != null && isBeforeMinDate(getDate())) {
                    for (MyWheelPicker p : pickers) {
                        p.scrollTo(p.findIndexOfDate(minDate));
                    }
                }
            }
        }, DELAY_BEFORE_CHECK_PAST);
    }

    private void checkAfterMaxDate(final MyWheelPicker picker) {
        picker.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (maxDate != null && isAfterMaxDate(getDate())) {
                    Date today = Calendar.getInstance().getTime();
                    for (MyWheelPicker p : pickers) {
                        if (p instanceof WheelYearPicker) {
                            p.scrollTo(((WheelYearPicker) p).getAdapter().getData().indexOf(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
                        } else p.scrollTo(p.findIndexOfDate(today));
                    }
                }
            }
        }, DELAY_BEFORE_CHECK_PAST);
    }

    private boolean isBeforeMinDate(Date date) {
        return dateHelper.getCalendarOfDate(date).before(dateHelper.getCalendarOfDate(minDate));
    }

    private boolean isAfterMaxDate(Date date) {
        return dateHelper.getCalendarOfDate(date).after(dateHelper.getCalendarOfDate(maxDate));
    }

    public void addOnDateChangedListener(OnDateChangedListener listener) {
        this.listeners.add(listener);
    }

    public void removeOnDateChangedListener(OnDateChangedListener listener) {
        this.listeners.remove(listener);
    }

    public void checkPickersMinMax() {
        for (MyWheelPicker picker : pickers) {
            checkMinMaxDate(picker);
        }
    }

    public Date getDate() {
        int hour = hoursPicker.getCurrentHour();
        if (isAmPm && amPmPicker.isPm()) {
            hour += PM_HOUR_ADDITION;
        }
        final int minute = minutesPicker.getCurrentMinute();

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(dateHelper.getTimeZone());
        if (displayDays) {
            final Date dayDate = daysPicker.getCurrentDate();
            calendar.setTime(dayDate);
        } else {
            if (displayMonth) {
                calendar.set(Calendar.MONTH, monthPicker.getCurrentMonth());
            }

            if (displayYears) {
                calendar.set(Calendar.YEAR, yearsPicker.getCurrentYear());
            }

            if (displayDaysOfMonth) {
                int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (daysOfMonthPicker.getCurrentDay() >= daysInMonth) {
                    calendar.set(Calendar.DAY_OF_MONTH, daysInMonth);
                } else {
                    calendar.set(Calendar.DAY_OF_MONTH, daysOfMonthPicker.getCurrentDay() + 1);
                }
            }
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public void setStepSizeMinutes(int minutesStep) {
        minutesPicker.setStepSizeMinutes(minutesStep);
    }

    public void setStepSizeHours(int hoursStep) {
        hoursPicker.setStepSizeHours(hoursStep);
    }

    public void setDefaultDate(Date date) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(dateHelper.getTimeZone());
            calendar.setTime(date);
            this.defaultDate = calendar.getTime();
            
            updateDaysOfMonth(calendar);

            for (MyWheelPicker picker : pickers) {
                picker.setDefaultDate(defaultDate);
            }
        }
    }

    public void selectDate(Calendar calendar) {
        if (calendar == null) {
            return;
        }

        final Date date = calendar.getTime();
        for (MyWheelPicker picker : pickers) {
            picker.selectDate(date);
        }

        if (displayDaysOfMonth) {
            updateDaysOfMonth();
        }
    }

    private void updateListener() {
        final Date date = getDate();
        final CharSequence format = isAmPm ? FORMAT_12_HOUR : FORMAT_24_HOUR;
        final String displayed = DateFormat.format(format, date).toString();
        for (OnDateChangedListener listener : listeners) {
            listener.onDateChanged(displayed, date);
        }
    }

    private void updateDaysOfMonth() {
        final Date date = getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(dateHelper.getTimeZone());
        calendar.setTime(date);
        updateDaysOfMonth(calendar);
    }

    private void updateDaysOfMonth(@NonNull Calendar calendar) {
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        daysOfMonthPicker.setDaysInMonth(daysInMonth);
        daysOfMonthPicker.updateAdapter();
    }

    public void setMustBeOnFuture(boolean mustBeOnFuture) {
        this.mustBeOnFuture = mustBeOnFuture;
        daysPicker.setShowOnlyFutureDate(mustBeOnFuture);
        if (mustBeOnFuture) {
            Calendar now = Calendar.getInstance();
            now.setTimeZone(dateHelper.getTimeZone());
            minDate = now.getTime(); //minDate is Today
        }
    }

    public boolean mustBeOnFuture() {
        return mustBeOnFuture;
    }

    private void setMinYear() {

        if (displayYears && this.minDate != null && this.maxDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(dateHelper.getTimeZone());
            calendar.setTime(this.minDate);
            yearsPicker.setMinYear(calendar.get(Calendar.YEAR));
            calendar.setTime(this.maxDate);
            yearsPicker.setMaxYear(calendar.get(Calendar.YEAR));
        }
    }

    private void checkSettings() {
        if (displayDays && (displayDaysOfMonth || displayMonth)) {
            throw new IllegalArgumentException("You can either display days with months or days and months separately");
        }
    }

    public void updateTheme() {
        if (App.preferences.isDarkTheme()) {
            monthPicker.setItemTextColor(ContextCompat.getColor(
                    getContext(), R.color.lightColor
            ));
            daysOfMonthPicker.setItemTextColor(ContextCompat.getColor(
                    getContext(), R.color.lightColor
            ));
            yearsPicker.setItemTextColor(ContextCompat.getColor(
                    getContext(), R.color.lightColor
            ));
            hoursPicker.setItemTextColor(ContextCompat.getColor(
                    getContext(), R.color.lightColor
            ));
            minutesPicker.setItemTextColor(ContextCompat.getColor(
                    getContext(), R.color.lightColor
            ));
            amPmPicker.setItemTextColor(ContextCompat.getColor(
                    getContext(), R.color.lightColor
            ));
        } else {
            monthPicker.setItemTextColor(ContextCompat.getColor(
                    getContext(), R.color.darkColor
            ));
            daysOfMonthPicker.setItemTextColor(ContextCompat.getColor(
                    getContext(), R.color.darkColor
            ));
            yearsPicker.setItemTextColor(ContextCompat.getColor(
                    getContext(), R.color.darkColor
            ));
            hoursPicker.setItemTextColor(ContextCompat.getColor(
                    getContext(), R.color.darkColor
            ));
            minutesPicker.setItemTextColor(ContextCompat.getColor(
                    getContext(), R.color.darkColor
            ));
            amPmPicker.setItemTextColor(ContextCompat.getColor(
                    getContext(), R.color.darkColor
            ));
        }
    }

    private void init(Context context, AttributeSet attrs) {
//        setCurved(true);
        TypedArray a = context.obtainStyledAttributes(attrs, com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker);

        final Resources resources = getResources();
        setTodayText(new DateWithLabel(a.getString(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_todayText), new Date()));
//        setTextColor(a.getColor(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_textColor, ContextCompat.getColor(context, com.github.florent37.singledateandtimepicker.R.color.picker_default_text_color)));
//        setSelectedTextColor(a.getColor(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_selectedTextColor, ContextCompat.getColor(context, com.github.florent37.singledateandtimepicker.R.color.picker_default_selected_text_color)));
//        setSelectorColor(a.getColor(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_selectorColor, ContextCompat.getColor(context, com.github.florent37.singledateandtimepicker.R.color.picker_default_selector_color)));

        float dip = 14f;
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        setItemSpacing(Math.round(px)
//                a.getDimensionPixelSize(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_itemSpacing, resources.getDimensionPixelSize(com.github.florent37.singledateandtimepicker.R.dimen.wheelSelectorHeight))
        );
        setCurvedMaxAngle(a.getInteger(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_curvedMaxAngle, WheelPicker.MAX_ANGLE));
        setSelectorHeight(a.getDimensionPixelSize(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_selectorHeight, resources.getDimensionPixelSize(com.github.florent37.singledateandtimepicker.R.dimen.wheelSelectorHeight)));
        setTextSize(a.getDimensionPixelSize(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_textSize, resources.getDimensionPixelSize(com.github.florent37.singledateandtimepicker.R.dimen.WheelItemTextSize)));
        setCurved(a.getBoolean(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_curved, IS_CURVED_DEFAULT));
        setCyclic(a.getBoolean(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_cyclic, IS_CYCLIC_DEFAULT));
        setMustBeOnFuture(a.getBoolean(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_mustBeOnFuture, MUST_BE_ON_FUTURE_DEFAULT));
        setVisibleItemCount(a.getInt(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_visibleItemCount, VISIBLE_ITEM_COUNT_DEFAULT));

        setStepSizeMinutes(a.getInt(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_stepSizeMinutes, 1));
        setStepSizeHours(a.getInt(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_stepSizeHours, 1));

        daysPicker.setDayCount(a.getInt(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_dayCount, DAYS_PADDING));
        setDisplayDays(a.getBoolean(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_displayDays, displayDays));
        setDisplayMinutes(a.getBoolean(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_displayMinutes, displayMinutes));
        setDisplayHours(a.getBoolean(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_displayHours, displayHours));
        setDisplayMonths(a.getBoolean(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_displayMonth, displayMonth));
        setDisplayYears(a.getBoolean(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_displayYears, displayYears));
        setDisplayDaysOfMonth(a.getBoolean(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_displayDaysOfMonth, displayDaysOfMonth));
        setDisplayMonthNumbers(a.getBoolean(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_displayMonthNumbers, monthPicker.displayMonthNumbers()));
//        setFontToAllPickers(a.getResourceId(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_fontFamily,0));
//        setFontToAllPickers(a.getResourceId(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_android_fontFamily,0));
        String monthFormat = a.getString(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_monthFormat);
        setMonthFormat(TextUtils.isEmpty(monthFormat) ? WheelMonthPicker.MONTH_FORMAT : monthFormat);
//        setTextAlign(a.getInt(com.github.florent37.singledateandtimepicker.R.styleable.SingleDateAndTimePicker_picker_textAlign, ALIGN_CENTER));

        if (App.preferences.isDarkTheme()) {
            monthPicker.setItemTextColor(ContextCompat.getColor(
                    context, R.color.lightColor
            ));
            daysOfMonthPicker.setItemTextColor(ContextCompat.getColor(
                    context, R.color.lightColor
            ));
            yearsPicker.setItemTextColor(ContextCompat.getColor(
                    context, R.color.lightColor
            ));
            hoursPicker.setItemTextColor(ContextCompat.getColor(
                    context, R.color.lightColor
            ));
            minutesPicker.setItemTextColor(ContextCompat.getColor(
                    context, R.color.lightColor
            ));
            amPmPicker.setItemTextColor(ContextCompat.getColor(
                    context, R.color.lightColor
            ));
        } else {
            monthPicker.setItemTextColor(ContextCompat.getColor(
                    context, R.color.darkColor
            ));
            daysOfMonthPicker.setItemTextColor(ContextCompat.getColor(
                    context, R.color.darkColor
            ));
            yearsPicker.setItemTextColor(ContextCompat.getColor(
                    context, R.color.darkColor
            ));
            hoursPicker.setItemTextColor(ContextCompat.getColor(
                    context, R.color.darkColor
            ));
            minutesPicker.setItemTextColor(ContextCompat.getColor(
                    context, R.color.darkColor
            ));
            amPmPicker.setItemTextColor(ContextCompat.getColor(
                    context, R.color.darkColor
            ));
        }

        checkSettings();
        setMinYear();

        a.recycle();
        if (displayDaysOfMonth) {
            Calendar now = Calendar.getInstance();
            now.setTimeZone(dateHelper.getTimeZone());
            updateDaysOfMonth(now);
        }
        daysPicker.updateAdapter(); // For MustBeFuture and dayCount
    }

    public interface OnDateChangedListener {
        void onDateChanged(String displayed, Date date);
    }
}
