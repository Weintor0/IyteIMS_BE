package edu.iyte.ceng.internship.ims.model.response.documents;

import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import edu.iyte.ceng.internship.ims.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@AssociatedWithEntity(entityName = Document.entityName)
public class CreateDocumentResponse {
    private String documentId;
}
