package com.filedock.document;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findByProcessingStatus(
            ProcessingStatus processingStatus,
            Pageable pageable
    );
}
