package com.geecommerce.mediaassets.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class DocxToPdfConverter implements DocumentConverter {
    @Override
    public boolean canConvert(String mimeType) {
        if (mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
            return true;
        return false;
    }

    @Override
    public ByteArrayOutputStream convert(InputStream stream) {
        XWPFDocument document = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            document = new XWPFDocument(stream);
            PdfOptions options = PdfOptions.create();
            PdfConverter.getInstance().convert(document, outStream, options);
            return outStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
