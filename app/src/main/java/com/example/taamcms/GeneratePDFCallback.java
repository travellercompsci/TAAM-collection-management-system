package com.example.taamcms;

public interface GeneratePDFCallback {
    /**
     * Called to update the progress(once every item is added to the PDF)
     * @param progress how many times this method has been invoked.
     * Note: The last iteration depends on the function using this callback.
     */
    void onStatusUpdate(int progress);

    /**
     * Called when an error occurs.
     */
    void onError();
}
