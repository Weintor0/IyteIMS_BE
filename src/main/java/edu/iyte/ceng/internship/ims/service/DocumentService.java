package edu.iyte.ceng.internship.ims.service;

import edu.iyte.ceng.internship.ims.entity.Document;
import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.ErrorCode;
import edu.iyte.ceng.internship.ims.exception.FileException;
import edu.iyte.ceng.internship.ims.model.response.documents.CreateDocumentResponse;
import edu.iyte.ceng.internship.ims.repository.DocumentRepository;
import edu.iyte.ceng.internship.ims.util.DateUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@AllArgsConstructor
@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Transactional(rollbackFor = Throwable.class)
    public CreateDocumentResponse createDocument(MultipartFile file, String receiverUserId) {
        User sourceUser = authenticationService.getCurrentUser();
        User destinationUser = userService.getUserById(receiverUserId);

        byte[] content = null;
        try {
            content = file.getBytes();
        } catch (IOException e) {
            throw new FileException(e);
        }

        Document document = Document.builder()
                .sourceUser(sourceUser)
                .destinationUser(destinationUser)
                .uploadDate(DateUtil.asDate(DateUtil.now()))
                .name(file.getOriginalFilename())
                .content(content)
                .build();

        Document savedDocument = documentRepository.save(document);
        return new CreateDocumentResponse(savedDocument.getId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public Document getDocument(String documentId) {
        Document document = documentRepository.findById(documentId).orElseThrow(
                () -> new BusinessException(ErrorCode.ResourceMissing, "Document not found")
        );
        ensureReadPrivilege(document);
        return document;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateDocument(String documentId, MultipartFile file) {
        Document document = documentRepository.findById(documentId).orElseThrow(
                () -> new BusinessException(ErrorCode.ResourceMissing, "Document not found")
        );
        ensureWritePrivilege(document);

        try {
            byte[] content = file.getBytes();
            document.setContent(content);
        } catch (IOException e) {
            throw new FileException(e);
        }

        document.setName(file.getOriginalFilename());
        document.setUploadDate(DateUtil.asDate(DateUtil.now()));
        documentRepository.save(document);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteDocument(String documentId) {
        Document document = documentRepository.findById(documentId).orElseThrow(
                () -> new BusinessException(ErrorCode.ResourceMissing, "Document not found")
        );
        ensureWritePrivilege(document);
        documentRepository.deleteById(documentId);
    }

    private void ensureReadPrivilege(Document document) {
        String currentUserId = authenticationService.getCurrentUser().getId();

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
