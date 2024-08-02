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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    int pageCount = -1;

    Context context;
    private Canvas canvas;
    private int pageYOffset = marginSize; // For positioning individual items.
    private PdfDocument pdf;
    private PdfDocument.Page page;
    private final NormalFontText textFont;

    public ReportPDFGenerator(boolean pictureAndDescriptionOnly, ArrayList<DisplayItem> items, Context context) {
        this.context = context;
        this.items = items;

        this.pictureAndDescriptionOnly = pictureAndDescriptionOnly;
        pdf = new PdfDocument();

        textFont = new NormalFontText(context, 16);
        addPage();
    }

    /**
     * Adds the title assuming there's enough page space left.
     */
    private void addTitle(String text) {
        canvas.drawText(text, marginSize, pageYOffset, new NormalFontText(context, 32));
        pageYOffset += 24;
    }

    private void addPage() {
        if (page != null) {
            pdf.finishPage(page);
        }

        pageCount ++;

        pageYOffset = marginSize;
        page = pdf.startPage(
                new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageCount).create()
        );
        canvas = page.getCanvas();
    }

    /**
     * @param alignVertically if the width of the old image > height
     */
    private void drawItemImage(Bitmap scaledImage, boolean alignVertically) {
        // Calculate the top right pixel of the centered image.
        int startY = pageYOffset;
        int startX = marginSize;

        if (alignVertically) {
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

    private void drawItem(DisplayItem item, Bitmap rawImage) {
        // This is how much the text needs to be moved after the image is drawn(does not include left margin. 0 = no image drawn.
        int textXOffset = 0;
        // Image to be drawn at the end if needed.
        Bitmap scaledImage = null;

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
            addPage();
        }

        // Draw the image.
        if (scaledImage != null) {
            drawItemImage(scaledImage, rawImage.getWidth() > rawImage.getHeight());
        }

        // Draw the description text.
        canvas.save();
        canvas.translate(textXOffset + marginSize, pageYOffset);
        staticLayout.draw(canvas);
        canvas.restore();

        // Increment the page Y.
        pageYOffset += itemHeight;
    }

    /**
     * Finishes the current pages and saves to the downloads folder.
     * @return if the saving was successful.
     */
    private boolean savePDFFile(String fileName) {
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


    private void fetchImageAndAddItem(GeneratePDFCallback callback, int index) {
        if (index == items.size()) {
            return;
        }

        DisplayItem item = items.get(index);
        index ++;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(item.getImage());

        final int finalIndex = index;
        try {
            File tempFile = File.createTempFile("temp", ".jpg");
            storageReference.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    drawItem(item, BitmapFactory.decodeFile(tempFile.getAbsolutePath()));
                    callback.onStatusUpdate(finalIndex);

                    fetchImageAndAddItem(callback, finalIndex);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // If the image fails to load, don't draw it.
                    drawItem(item, null);
                    callback.onStatusUpdate(finalIndex);

                    fetchImageAndAddItem(callback, finalIndex);
                }
            });
        } catch (Exception err) {
            // If the image fails to load, don't draw it.
            drawItem(item, null);
            callback.onStatusUpdate(index);

            fetchImageAndAddItem(callback, index);
        }

    }

    /**
     * Generates a report file based on the items added.
     * @param fileName path to save the report PDF.
     * @param callback methods which will be called on progress or on error of the PDF generation. The onStatusUpdate will be called once for each item drawn.
     */
    public void generateReport(String fileName, GeneratePDFCallback callback) {
        addTitle("Report");

        // Item count
        canvas.drawText("Items (" + this.items.size() + ")", marginSize, marginSize + 24, textFont);
        pageYOffset += 10;

        // Generate PDF.
        fetchImageAndAddItem(new GeneratePDFCallback() {
            @Override
            public void onStatusUpdate(int progress) {
                callback.onStatusUpdate(progress);

                // Complete
                if (progress == items.size()) {
                    savePDFFile(fileName);
                }
            }

            @Override
            public void onError() {
                callback.onError();
            }
        }, 0);
    }
}
