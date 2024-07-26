package com.example.taamcms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import androidx.core.content.ContextCompat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.io.File;

public class ReportPDFGenerator {
    private static class NormalFontText extends TextPaint {
        NormalFontText (Context context, int textSize) {
            this.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            this.setColor(ContextCompat.getColor(context, R.color.black));
            this.setTextSize(textSize);
        }
    }
    // Constants
    final static int marginSize = 72;
    final static int pageWidth = 595;
    final static int pageHeight = 842;
    final static int displayImageSize = 100;

    private final ArrayList<DisplayItem> items;
    private final boolean pictureAndDescriptionOnly;
    int pageCount = 0;

    public ReportPDFGenerator(boolean pictureAndDescriptionOnly) {
        this.pictureAndDescriptionOnly = pictureAndDescriptionOnly;
        this.items = new ArrayList<>();
    }

    public ReportPDFGenerator() {
        this(false);
    }

    public void addItem(DisplayItem item) {
        items.add(item);
    }

    /**
     * Generates a report file based on the items added.
     * @param context app context.
     * @param fileName path to save the report PDF.
     * @return if the operation was successful.
     */
    public boolean generateReport(Context context, String fileName) {
        // Canvas for drawing PDF pages.
        Canvas canvas;
        int pageYOffset = marginSize; // For positioning individual items.

        // Create the PDF and page.
        PdfDocument pdf = new PdfDocument();
        PdfDocument.Page page = createPDFPage(pdf);
        canvas = page.getCanvas();

        // Add the title.
        canvas.drawText("Report", marginSize, marginSize, new NormalFontText(context, 32));
        // Add the item count.
        NormalFontText textFont = new NormalFontText(context, 16);
        canvas.drawText("Items (" + this.items.size() + ")", marginSize, marginSize + 24, textFont);

        // For drawing the individual items.
        pageYOffset += 24 + 10;
        textFont.setTextSize(16);

        for (DisplayItem item : items) {
            // This is how much the text needs to be moved after the image is drawn(does not include left margin. 0 = no image drawn.
            int textXOffset = 0;
            // Image to be drawn at the end if needed.
            Bitmap scaledImage = null;

//            Bitmap rawImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.sancai);
            // Load the image.
            Bitmap rawImage = null;
            try {
                URL imageUrl = new URL(item.getImage());
                rawImage = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            } catch (Exception ignored) {}

            if (rawImage != null) {
                // Want to bound the image in a 150 by 150 box.
                double scaleFactor = (double) displayImageSize / Math.max(rawImage.getWidth(), rawImage.getHeight());
                int adjustedWidth = (int) (rawImage.getWidth() * scaleFactor);
                int adjustedHeight = (int) (rawImage.getHeight() * scaleFactor);
                scaledImage = Bitmap.createScaledBitmap(rawImage, adjustedWidth, adjustedHeight, false);

                textXOffset += displayImageSize + 12; // 10 padding + 2 rectangle outline (see below)
            }

            // Generate the text for the item.
            /*
            Format:
                Lot Number: lot number
                Category: category
                Period: period
                Name: name
                Description: desc

             */
            String itemDataText = "";

            if (!pictureAndDescriptionOnly) {
                itemDataText += "Lot Number: " + item.getLot() + "\n";
                itemDataText += "Category: " + item.getLot() + "\n";
                itemDataText += "Period: " + item.getPeriod() + "\n\n";

                itemDataText += "Name: " + item.getTitle() + "\n";
                itemDataText += "Description: " + item.getDescription();
            } else {
                itemDataText = item.getDescription();
            }


            // Calculate the size of the text.
            int textWidth = pageWidth - marginSize * 2 - textXOffset;
            StaticLayout staticLayout = new StaticLayout(
                    itemDataText,
                    textFont,
                    textWidth,
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0,
                    false
            );

            // Height of the image + description. If it exceeds the size of the page, we request a new one.
            int itemHeight = staticLayout.getHeight();
            if (rawImage != null) {
                itemHeight = Math.max(itemHeight, 150);
            }
            itemHeight += 10; // +10 padding

            // Request a new page if we can't fit this one on one page.
            // Note this does not handle the case where 1 item does not fit on a page.
            if (pageYOffset + itemHeight > pageHeight - marginSize) {
                pdf.finishPage(page);

                pageYOffset = marginSize;
                page = createPDFPage(pdf);
                canvas = page.getCanvas();
            }

            // Draw the image.
            if (scaledImage != null) {
                // Calculate the top right pixel of the centered image.
                int startY = pageYOffset;
                int startX = marginSize;

                if (rawImage.getWidth() > rawImage.getHeight()) {
                    // width > height means it needs to be centered vertically
                    startY += (displayImageSize - scaledImage.getHeight()) / 2;
                } else {
                    startX += (displayImageSize - scaledImage.getWidth()) / 2;
                }

                // Draw the bitmap if it processed correctly.
                canvas.drawBitmap(scaledImage, startX, startY, new Paint());

                // Draw an outline around the image.
                Paint outlineStroke = new Paint();
                outlineStroke.setColor(Color.rgb(0, 0, 0));
                outlineStroke.setStrokeWidth(2);
                outlineStroke.setStyle(Paint.Style.STROKE);

                canvas.drawRect(new Rect(marginSize, pageYOffset, marginSize + displayImageSize, pageYOffset + displayImageSize), outlineStroke);
            }

            // Draw the description text.
            canvas.save();
            canvas.translate(textXOffset + marginSize, pageYOffset);
            staticLayout.draw(canvas);
            canvas.restore();

            // Increment the page Y.
            pageYOffset += itemHeight;
        }

        // Finish page and save.
        pdf.finishPage(page);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

        try {
            pdf.writeTo(new FileOutputStream(file));
        } catch (IOException err) {
            System.out.println("Failed to save PDF: " + err.getMessage());
            return false;
        }

        pdf.close();

        return true;
    }

    /**
     * Creates a new page for the PDF.
     * @return new page.
     *
     * @throws RuntimeException if the android version is less than KitKat
     */
    private PdfDocument.Page createPDFPage(PdfDocument pdf) {
        pageCount ++;

        return pdf.startPage(
                new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageCount).create()
        );
    }
}
