package com.example.studentmanager.profile.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class ChartView extends View {

    private final int passed;
    private final int failed;
    private final int ungraded;
    private final Context context;
    private final Paint paint;

    public ChartView(Context context, int passed, int failed, int ungraded) {
        super(context);
        this.context = context;
        this.passed = passed;
        this.failed = failed;
        this.ungraded = ungraded;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (passed == 0 && failed == 0) {
            return;
        }

        int maxValue = Math.max(passed, failed);
        float barWidth = (float) (getWidth() / 3);
        drawValues(canvas, maxValue, barWidth);
    }

    private void drawValues(Canvas canvas, int maxValue, float barWidth) {
        int currentPosition = 0;

        // Draw bar for failed
        paint.setColor(Color.RED);
        drawBar(canvas, maxValue, barWidth, currentPosition, failed, "Failed");

        currentPosition++;

        // Draw bar for passed
        paint.setColor(Color.GREEN);
        drawBar(canvas, maxValue, barWidth, currentPosition, passed, "Passed");

        currentPosition++;

        // Draw bar for ungraded
        paint.setColor(Color.BLUE);
        drawBar(canvas, maxValue, barWidth, currentPosition, ungraded, "Ungraded");
    }

    private void drawBar(Canvas canvas, int maxValue, float barWidth, int currentPosition, int value, String label) {
        float x1 = currentPosition * barWidth;
        float y1 = (1 - (float) (value) / maxValue) * getHeight();
        float x2 = x1 + barWidth;
        float y2 = getHeight();
        canvas.drawRect(x1, y1, x2, y2, paint);

        // Drawing label
        paint.setColor(Color.BLACK);
        paint.setTextSize((float) (0.16 * barWidth));
        float xLabel = (float) ((currentPosition + 0.05) * barWidth);
        float yLabel = (float) (0.95 * getHeight());
        canvas.drawText(label + " - " + value, xLabel, yLabel, paint);
    }
}
