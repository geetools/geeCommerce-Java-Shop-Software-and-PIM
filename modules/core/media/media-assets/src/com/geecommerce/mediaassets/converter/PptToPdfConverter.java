package com.geecommerce.mediaassets.converter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;

public class PptToPdfConverter extends SlidesToPdfConverter implements DocumentConverter {

    private List<HSLFSlide> slides = null;
    Dimension dimension = null;

    @Override
    public boolean canConvert(String mimeType) {
        if (mimeType.equals("application/vnd.ms-powerpoint"))
            return true;
        return false;
    }

    @Override
    protected void loadSlides(InputStream stream) throws IOException {
        HSLFSlideShow ppt = new HSLFSlideShow(stream);
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
        return slides.get(index).getBackground().getFill().getBackgroundColor();
    }

}
