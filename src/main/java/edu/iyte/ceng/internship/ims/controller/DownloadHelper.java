package edu.iyte.ceng.internship.ims.controller;

import edu.iyte.ceng.internship.ims.entity.Document;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class DownloadHelper {
    public HttpEntity<byte[]> fromDocument(Document document) {
        String name = document.getName();
        byte[] contents = document.getContent();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name);
        headers.setContentLength(contents.length);

        return new HttpEntity<>(contents, headers);
    }
}
