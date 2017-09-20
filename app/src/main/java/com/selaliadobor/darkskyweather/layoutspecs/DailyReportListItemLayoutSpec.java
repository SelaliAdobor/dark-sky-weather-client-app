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
import com.selaliadobor.darkskyweather.data.DailyReport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@LayoutSpec
public class DailyReportListItemLayoutSpec {


    public static final int SMALL_TEXT_SIZE_SP = 12;
    public static final int LARGE_TEXT_SIZE_SP = 20;

    @OnCreateLayout
    static ComponentLayout onCreateLayout(
            ComponentContext c,
            @Prop DailyReport dailyReport,
            @Prop int heightDip,
            @Prop Runnable clickEventHandler) {
        String temperatureString = String.format(Locale.ENGLISH, "%.2f° | %.2f°", dailyReport.getHighTemp(), dailyReport.getLowTemp());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayOfWeek = dateFormat.format(new Date(dailyReport.getDate())).toUpperCase();
        ComponentLayout textColumn = Column.create(c)
                .child(
                        Text.create(c)
                                .text(temperatureString)
                                .textColor(Color.WHITE)
                                .textSizeSp(LARGE_TEXT_SIZE_SP)
                )
                .child(
                        Text.create(c)
                                .text(dailyReport.getSummary())
                                .textColor(Color.WHITE)
                                .textSizeSp(SMALL_TEXT_SIZE_SP)
                )
                .build();


        return Column.create(c)
                .child(Text.create(c)
                        .text(dayOfWeek)
                        .textColor(Color.WHITE)
                        .textAlignment(Layout.Alignment.ALIGN_CENTER)
                        .textSizeSp(SMALL_TEXT_SIZE_SP))
                .child(
                        Row.create(c)

                                .child(
                                        GlideImage.create(c)
                                                .resourceId(dailyReport.getWeatherType().getDrawableId())
                                                .aspectRatio(1)
                                                .fitCenter(true)
                                                .buildWithLayout())
                                .child(textColumn)
                                .marginDip(YogaEdge.BOTTOM, 8)
                )
                .backgroundColor(Color.argb(255, 135, 206, 250))
                .heightDip(heightDip)
                .marginDip(YogaEdge.ALL, 8)
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
