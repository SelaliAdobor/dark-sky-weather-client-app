package com.selaliadobor.darkskyweather.layoutspecs;


import android.graphics.Color;
import android.text.Layout;
import android.view.View;

import com.facebook.litho.ClickEvent;
import com.facebook.litho.Column;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentLayout;
import com.facebook.litho.Row;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.widget.Text;
import com.facebook.yoga.YogaEdge;
import com.github.pavlospt.litho.glide.GlideImage;
import com.selaliadobor.darkskyweather.data.HourlyReport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@LayoutSpec
public class HourlyReportListItemLayoutSpec {


    @OnCreateLayout
    static ComponentLayout onCreateLayout(
            ComponentContext c,
            @Prop HourlyReport hourlyReport,
            @Prop int heightDip,
            @Prop Runnable clickEventHandler) {
        String temperatureString = String.format(Locale.ENGLISH, "%.2f° F", hourlyReport.getTemperature());
        SimpleDateFormat dateFormat = new SimpleDateFormat("H\n a\n", Locale.getDefault());
        String timeOfDay = dateFormat.format(new Date(hourlyReport.getDate())).toUpperCase();
        ComponentLayout textColumn = Column.create(c)
                .marginDip(YogaEdge.LEFT, 16)
                .child(
                        Text.create(c)
                                .text(temperatureString)
                                .textColor(Color.WHITE)
                                .textSizeSp(24)
                )
                .child(
                        Text.create(c)
                                .text(hourlyReport.getSummary())
                                .textColor(Color.WHITE)
                                .textSizeSp(14)
                ).widthPercent(70)
                .build();
        return Row.create(c)
                .heightDip(heightDip)
                .backgroundColor(Color.argb(255, 135, 206, 250))
                .child(GlideImage.create(c)
                        .resourceId(hourlyReport.getWeatherType().getDrawableId())
                        .aspectRatio(1)
                        .fitCenter(true)
                        .buildWithLayout())
                .child(textColumn)
                .child(Text.create(c)
                        .text(timeOfDay)
                        .textColor(Color.WHITE)
                        .textAlignment(Layout.Alignment.ALIGN_CENTER)
                        .textSizeSp(14))
                .marginDip(YogaEdge.ALL, 10)
                .clickHandler(HourlyReportListItemLayout.onClick(c))
                .build();
    }

    @OnEvent(ClickEvent.class)
    static void onClick(
            ComponentContext c,
            @FromEvent View view,
            @Prop Runnable clickEventHandler) {
        clickEventHandler.run();
    }
}
