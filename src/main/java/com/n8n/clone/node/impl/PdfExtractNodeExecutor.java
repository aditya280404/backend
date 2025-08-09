package com.n8n.clone.node.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import com.n8n.clone.node.NodeExecutionContext;
import com.n8n.clone.node.NodeExecutionResult;
import com.n8n.clone.node.NodeExecutor;

/**
 * Node executor that extracts plain text from a PDF file.  It expects a
 * parameter or input field called "filePath" pointing to the PDF.  It reads
 * the file, extracts text and puts it under "text" in the output map.
 */
@Component
public class PdfExtractNodeExecutor implements NodeExecutor {

    @Override
    public String getNodeType() {
        return "pdf-extract";
    }

    @Override
    public NodeExecutionResult execute(NodeExecutionContext ctx) {
        String paramPath = (String) ctx.getParameter("filePath");
        String inputPath = ctx.getInputData() != null ? (String) ctx.getInputData().get("filePath") : null;
        String path = paramPath != null ? paramPath : inputPath;
        if (path == null) {
            return NodeExecutionResult.error("pdf-extract node requires a 'filePath' parameter or input");
        }
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            return NodeExecutionResult.error("PDF file not found at path: " + path);
        }
        try (InputStream is = new FileInputStream(file); PDDocument document = PDDocument.load(is)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            Map<String, Object> output = new HashMap<>();
            output.put("text", text);
            return NodeExecutionResult.success(output);
        } catch (Exception e) {
            return NodeExecutionResult.error("Failed to extract text from PDF: " + e.getMessage());
        }
    }
}