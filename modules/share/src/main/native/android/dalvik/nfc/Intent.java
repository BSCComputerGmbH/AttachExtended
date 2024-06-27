package com.gluonhq.helloandroid.nfc;

public final class Intent {
    private Intent() {
    }

    public static final class NFC {
        /**
         * Send this intent to open the Barcodes app in scanning mode, find a barcode, and return
         * the results.
         */
        public static final String ACTION = "com.gluonhq.attachextended.nfc";

        /**
         * place for more final String defintions
         */


        private NFC() {
        }
    }
}