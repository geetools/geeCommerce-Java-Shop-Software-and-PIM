package com.geecommerce.mediaassets.converter;

import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

public class XslxToPdfConverter extends SpreadShitToPdfConverter implements DocumentConverter {
    @Override
    public boolean canConvert(String mimeType) {
        if(mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            return true;
        return false;
    }

    @Override
    public ByteArrayOutputStream convert(InputStream stream) {
        ByteArrayOutputStream outStream;
        try {
            outStream = new ByteArrayOutputStream();
            // Read workbook into HSSFWorkbook
            XSSFWorkbook my_xls_workbook = new XSSFWorkbook(stream);
            // Read worksheet into HSSFSheet
            XSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
            // To iterate over the rows
            Iterator<Row> rowIterator = my_worksheet.iterator();
            //We will create output PDF document objects at this point
            Document pdf = new Document();
            PdfWriter.getInstance(pdf, outStream);
            pdf.open();
            //we have two columns in the Excel sheet, so we create a PDF table with two columns
            //Note: There are ways to make this dynamic in nature, if you want to.
            PdfPTable my_table = new PdfPTable(2);
            //We will use the object below to dynamically add new data to the table
            PdfPCell table_cell;
            //Loop through rows.
            printPdf(rowIterator, my_table);
            //Finally add the table to PDF document
            pdf.add(my_table);
            pdf.close();
            //we created our pdf file..
            stream.close(); //close xls
            return outStream;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return null;

    }

}
