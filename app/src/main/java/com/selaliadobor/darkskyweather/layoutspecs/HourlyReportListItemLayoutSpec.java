package com.selaliadobor.darkskyweather.layoutspecs;


import android.graphics.Color;

import com.facebook.litho.Column;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentLayout;
import com.facebook.litho.Row;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.PropDefault;
import com.facebook.litho.widget.Text;
import com.facebook.yoga.YogaEdge;
import com.github.pavlospt.litho.glide.GlideImage;
import com.selaliadobor.darkskyweather.data.HourlyReport;

import java.util.Locale;

@LayoutSpec
public class HourlyReportListItemLayoutSpec {


    @OnCreateLayout
    static ComponentLayout onCreateLayout(
            ComponentContext c,
            @Prop HourlyReport hourlyReport,
            @Prop int heightDip) {
        String temperatureString = String.format(Locale.ENGLISH, "%.2f° F", hourlyReport.getTemperature());
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
                )
                .build();
        return Row.create(c)
                .heightDip(heightDip)
                .backgroundColor(Color.argb(255,135,206,250))
                .child(GlideImage.create(c)
                    .resourceId(hourlyReport.getWeatherType().getDrawableId())
                    .aspectRatio(1)
                    .fitCenter(true)
                    .buildWithLayout())
                .child(textColumn)
                .build();
    }
}
