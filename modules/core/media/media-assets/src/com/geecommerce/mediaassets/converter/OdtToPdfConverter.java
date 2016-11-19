package com.geecommerce.mediaassets.converter;


import org.apache.poi.xwpf.usermodel.XWPFDocument;

import org.odftoolkit.odfdom.converter.pdf.PdfConverter;
import org.odftoolkit.odfdom.converter.pdf.PdfOptions;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OdtToPdfConverter implements DocumentConverter {
    @Override
    public boolean canConvert(String mimeType) {
        if(mimeType.equals("application/vnd.oasis.opendocument.text"))
            return true;
        return false;
    }

    @Override
    public ByteArrayOutputStream convert(InputStream stream) {
        OdfDocument document = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            document = OdfDocument.loadDocument(stream);
            PdfOptions options = PdfOptions.create();
            PdfConverter.getInstance().convert(document, outStream, options);
            return outStream;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
