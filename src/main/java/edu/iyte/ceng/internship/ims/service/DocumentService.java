package edu.iyte.ceng.internship.ims.service;

import edu.iyte.ceng.internship.ims.entity.Document;
import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.ErrorCode;
import edu.iyte.ceng.internship.ims.model.response.documents.CreateDocumentResponse;
import edu.iyte.ceng.internship.ims.repository.DocumentRepository;
import edu.iyte.ceng.internship.ims.util.DateUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@AllArgsConstructor
@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final UserService userService;

    public CreateDocumentResponse createDocument(MultipartFile file, String receiverUserId) throws IOException {
        User sourceUser = userService.getUserById(SecurityContextHolder.getContext().getAuthentication().getName());
        User destinationUser = userService.getUserById(receiverUserId);

        Document document = Document.builder()
                .sourceUser(sourceUser)
                .destinationUser(destinationUser)
                .uploadDate(DateUtil.asDate(DateUtil.now()))
                .name(file.getOriginalFilename())
                .content(file.getBytes())
                .build();

        Document savedDocument = documentRepository.save(document);
        return new CreateDocumentResponse(savedDocument.getId());
    }

    public Document getDocument(String documentId) {
        Document document = documentRepository.findById(documentId).orElseThrow(
                () -> new BusinessException(ErrorCode.ResourceMissing, "Document not found")
        );
        ensureReadPrivilege(document);
        return document;
    }

    public void updateDocument(String documentId, MultipartFile file) throws IOException {
        Document document = documentRepository.findById(documentId).orElseThrow(
                () -> new BusinessException(ErrorCode.ResourceMissing, "Document not found")
        );
        ensureWritePrivilege(document);

        document.setName(file.getOriginalFilename());
        document.setContent(file.getBytes());
        document.setUploadDate(DateUtil.asDate(DateUtil.now()));
        documentRepository.save(document);
    }

    public void deleteDocument(String documentId) {
        Document document = documentRepository.findById(documentId).orElseThrow(
                () -> new BusinessException(ErrorCode.ResourceMissing, "Document not found")
        );
        ensureWritePrivilege(document);
        documentRepository.deleteById(documentId);
    }

    private void ensureReadPrivilege(Document document) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (document.getSourceUser().getId().equals(currentUserId)) {
            return; // OK. Users can download a document if they are the sender.
        }

        if (document.getDestinationUser().getId().equals(currentUserId)) {
            return; // OK. Users can download a document if the document was sent to them.
        }

        throw new BusinessException(ErrorCode.Unauthorized,
                        "A user can only download a document if the document was " +
                        "sent by the user, or the document was sent to the user");
    }

    private void ensureWritePrivilege(Document document) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!document.getSourceUser().getId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.Unauthorized,
                    "A user cannot update or delete a document sent by another user.");
        }
    }
}
