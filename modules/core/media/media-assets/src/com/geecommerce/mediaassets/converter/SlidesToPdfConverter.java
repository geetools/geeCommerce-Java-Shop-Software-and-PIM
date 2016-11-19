package com.geecommerce.mediaassets.converter;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;


public abstract class SlidesToPdfConverter {

    public ByteArrayOutputStream convert(InputStream stream) {
        ByteArrayOutputStream outStream;

        try{
            outStream = new ByteArrayOutputStream();

            loadSlides(stream);

            Dimension pgsize = getDimension();
            double zoom = 2;
            AffineTransform at = new AffineTransform();
            at.setToScale(zoom, zoom);


            Document document = new Document();

            PdfWriter writer = PdfWriter.getInstance(document, outStream);
            document.open();

            for (int i = 0; i < getNumSlides(); i++) {

                BufferedImage bufImg = new BufferedImage((int)Math.ceil(pgsize.width*zoom), (int)Math.ceil(pgsize.height*zoom), BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = bufImg.createGraphics();
                graphics.setTransform(at);
                graphics.setPaint(getSlideBGColor(i));
                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
                try{
                    drawSlide(i, graphics);
                } catch(Exception e){
                    //Try to draw next page
                }

                Image image = Image.getInstance(bufImg, null);
                document.setPageSize(new Rectangle(image.getScaledWidth(), image.getScaledHeight()));
                document.newPage();
                image.setAbsolutePosition(0, 0);
                document.add(image);
            }
            document.close();
            writer.close();
            return outStream;
        } catch(Exception e){
            e.printStackTrace();
            //Try to draw next page
        }
        return null;
    }

    protected abstract void loadSlides(InputStream stream) throws IOException;

    protected abstract int getNumSlides();

    protected abstract Dimension getDimension();

    protected abstract void drawSlide(int index, Graphics2D graphics);/*{
        slides[index].draw(graphics);
    }*/

    protected abstract Color getSlideBGColor(int index);/*{
        return slides[index].getBackground().getFillColor();
    }*/

}
