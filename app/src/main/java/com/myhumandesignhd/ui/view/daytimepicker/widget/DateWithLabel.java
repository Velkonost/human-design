package com.myhumandesignhd.ui.view.daytimepicker.widget;

import com.github.florent37.singledateandtimepicker.DateHelper;

import java.util.Date;

public class DateWithLabel extends com.github.florent37.singledateandtimepicker.widget.DateWithLabel {
    public final String label;
    public final Date date;

    public DateWithLabel(String label, Date date) {
        super(label, date);
        this.label = label;
        this.date = date;
        if (date == null) {
            throw new IllegalArgumentException("null value provided. " + "label=[" + label + "], date=[" + date + "]");
        }
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public int hashCode() {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DateWithLabel) {
            DateWithLabel newDate = (DateWithLabel) o;
            return label.equals(newDate.label) && DateHelper.compareDateIgnoreTime(date, newDate.date) == 0;
        }
        return false;
    }
}
