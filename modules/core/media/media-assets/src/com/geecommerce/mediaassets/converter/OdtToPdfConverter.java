package com.geecommerce.mediaassets.converter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.odftoolkit.odfdom.converter.pdf.PdfConverter;
import org.odftoolkit.odfdom.converter.pdf.PdfOptions;
import org.odftoolkit.odfdom.doc.OdfDocument;

public class OdtToPdfConverter implements DocumentConverter {
    @Override
    public boolean canConvert(String mimeType) {
        if (mimeType.equals("application/vnd.oasis.opendocument.text"))
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
