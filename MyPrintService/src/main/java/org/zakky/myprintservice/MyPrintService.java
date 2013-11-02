package org.zakky.myprintservice;

import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrintDocument;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyPrintService extends PrintService {
    @Override
    protected PrinterDiscoverySession onCreatePrinterDiscoverySession() {
        Log.d("myprinter", "MyPrintService#onCreatePrinterDiscoverySession() called");

        return new PrinterDiscoverySession() {
            @Override
            public void onStartPrinterDiscovery(List<PrinterId> priorityList) {
                Log.d("myprinter", "PrinterDiscoverySession#onStartPrinterDiscovery(priorityList: " + priorityList + ") called");

                if (!priorityList.isEmpty()) {
                    return;
                }

                final List<PrinterInfo> printers = new ArrayList<>();
                final PrinterId printerId = generatePrinterId("aaa");
                final PrinterInfo.Builder builder = new PrinterInfo.Builder(printerId, "dummy printer", PrinterInfo.STATUS_IDLE);
                PrinterCapabilitiesInfo.Builder capBuilder = new PrinterCapabilitiesInfo.Builder(printerId);
                capBuilder.addMediaSize(PrintAttributes.MediaSize.ISO_A4, true);
                capBuilder.addMediaSize(PrintAttributes.MediaSize.ISO_A3, false);
                capBuilder.addResolution(new PrintAttributes.Resolution("resolutionId", "default resolution", 600, 600), true);
                capBuilder.setColorModes(PrintAttributes.COLOR_MODE_COLOR | PrintAttributes.COLOR_MODE_MONOCHROME, PrintAttributes.COLOR_MODE_COLOR);
                builder.setCapabilities(capBuilder.build());
                printers.add(builder.build());
                addPrinters(printers);
            }

            @Override
            public void onStopPrinterDiscovery() {
                Log.d("myprinter", "MyPrintService#onStopPrinterDiscovery() called");
            }

            @Override
            public void onValidatePrinters(List<PrinterId> printerIds) {
                Log.d("myprinter", "MyPrintService#onValidatePrinters(printerIds: " + printerIds + ") called");
            }

            @Override
            public void onStartPrinterStateTracking(PrinterId printerId) {
                Log.d("myprinter", "MyPrintService#onStartPrinterStateTracking(printerId: " + printerId + ") called");
            }

            @Override
            public void onStopPrinterStateTracking(PrinterId printerId) {
                Log.d("myprinter", "MyPrintService#onStopPrinterStateTracking(printerId: " + printerId + ") called");
            }

            @Override
            public void onDestroy() {
                Log.d("myprinter", "MyPrintService#onDestroy() called");
            }
        };
    }

    @Override
    protected void onPrintJobQueued(PrintJob printJob) {
        Log.d("myprinter", "queued: " + printJob.getId().toString());

        printJob.start();

        final PrintDocument document = printJob.getDocument();

        final FileInputStream in = new FileInputStream(document.getData().getFileDescriptor());
        try {
            final byte[] buffer = new byte[4];
            @SuppressWarnings("unused")
            final int read = in.read(buffer);
            Log.d("myprinter", "first " + buffer.length + "bytes of content: " + toString(buffer));
        } catch (IOException e) {
            Log.d("myprinter", "", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                assert true;
            }
        }
        printJob.complete();
    }

    private static String toString(byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Byte.toString(b)).append(',');
        }
        if (sb.length() != 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    protected void onRequestCancelPrintJob(PrintJob printJob) {
        Log.d("myprinter", "canceled: " + printJob.getId().toString());

        printJob.cancel();
    }

}
