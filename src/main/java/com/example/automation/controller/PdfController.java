package com.example.automation.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.automation.service.PdfService;

/**
 * REST controller for PDF processing.  Accepts uploaded PDF files, invokes
 * extraction/summarisation and returns the summary.  Optionally emails the
 * summary to the specified address.
 */
@RestController
@RequestMapping("/api/pdfs")
public class PdfController {

    private final PdfService pdfService;

    @Autowired
    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    /**
     * Upload a PDF for processing.  The summary can be emailed by providing
     * a "to" parameter.  Returns the generated summary.
     *
     * @param file uploaded PDF document
     * @param to   optional email address to send the summary to
     * @return summary as plain text
     */
    @PostMapping
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file,
                                            @RequestParam(value = "to", required = false) String to) {
        try {
            String summary = pdfService.processPdf(file, to);
            return ResponseEntity.ok(summary);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process PDF: " + e.getMessage());
        }
    }
}