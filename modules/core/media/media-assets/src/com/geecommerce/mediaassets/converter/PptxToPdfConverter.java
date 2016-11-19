package com.geecommerce.mediaassets.converter;

import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class PptxToPdfConverter extends SlidesToPdfConverter implements DocumentConverter {

    private java.util.List<XSLFSlide> slides = null;
    Dimension dimension = null;

    @Override
    public boolean canConvert(String mimeType) {
        if(mimeType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation"))
            return true;
        return false;
    }

    @Override
    protected void loadSlides(InputStream stream) throws IOException {
        XMLSlideShow ppt = new XMLSlideShow(stream);
        dimension = ppt.getPageSize();
        slides = ppt.getSlides();
    }

    @Override
    protected int getNumSlides() {
        return slides.size();
    }

    @Override
    protected Dimension getDimension() {
        return dimension;
    }

    @Override
    protected void drawSlide(int index, Graphics2D graphics) {
        slides.get(index).draw(graphics);
    }

    @Override
    protected Color getSlideBGColor(int index) {
        return slides.get(index).getBackground().getFillColor();
    }
}
