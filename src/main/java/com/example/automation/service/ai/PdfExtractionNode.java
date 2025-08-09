package com.example.automation.service.ai;

import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

/**
 * Workflow node that extracts plain text from a PDF document using
 * Apache PDFBox.  Input must be a {@link MultipartFile}.  The output is
 * a {@link String} containing the extracted text.
 */
public class PdfExtractionNode implements WorkflowNode {
    @Override
    public Object execute(Object input) throws Exception {
        if (!(input instanceof MultipartFile)) {
            throw new IllegalArgumentException("PdfExtractionNode requires a MultipartFile input");
        }
        MultipartFile file = (MultipartFile) input;
        try (InputStream is = file.getInputStream(); PDDocument document = PDDocument.load(is)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}