package edu.iyte.ceng.internship.ims.controller;

import edu.iyte.ceng.internship.ims.entity.Document;
import edu.iyte.ceng.internship.ims.model.response.documents.CreateDocumentResponse;
import edu.iyte.ceng.internship.ims.service.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping("/document")
public class DocumentController {
    private final DocumentService documentService;

    @GetMapping(path = "/download/{documentId}")
    public HttpEntity<byte[]> download(@PathVariable("documentId") String documentId) {
        Document document = documentService.getDocument(documentId);

        String name = document.getName();
        byte[] contents = document.getContent();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name);
        headers.setContentLength(contents.length);

        return new HttpEntity<>(contents, headers);
    }

    @PostMapping(path = "/upload/{receivingUserId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreateDocumentResponse> upload(
            @PathVariable("receivingUserId") String receivingUserId,
            @RequestPart("file") MultipartFile file) throws IOException {
        CreateDocumentResponse response = documentService.createDocument(file, receivingUserId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(path = "/update/{documentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> update(
            @PathVariable("documentId") String documentId,
            @RequestPart("file") MultipartFile file) throws IOException {
        documentService.updateDocument(documentId, file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(path = "/delete/{documentId}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("documentId") String documentId) {
        documentService.deleteDocument(documentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
