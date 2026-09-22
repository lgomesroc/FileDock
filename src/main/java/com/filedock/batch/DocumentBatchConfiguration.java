package com.filedock.batch;

import com.filedock.document.Document;
import com.filedock.document.DocumentRepository;
import com.filedock.document.ProcessingStatus;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class DocumentBatchConfiguration {

    @Bean
    public RepositoryItemReader<Document> documentReader(
            DocumentRepository documentRepository) {

        return new RepositoryItemReaderBuilder<Document>()
                .name("documentReader")
                .repository(documentRepository)
                .methodName("findByProcessingStatus")
                .arguments(ProcessingStatus.PENDING)
                .pageSize(10)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Document, Document> documentProcessor() {
        return document -> {
            document.setProcessingStatus(ProcessingStatus.PROCESSED);
            document.setProcessedAt(LocalDateTime.now());

            return document;
        };
    }

    @Bean
    public JpaItemWriter<Document> documentWriter(
            EntityManagerFactory entityManagerFactory) {

        return new JpaItemWriterBuilder<Document>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Job documentProcessingJob(
            JobRepository jobRepository,
            Step documentProcessingStep) {

        return new JobBuilder("documentProcessingJob", jobRepository)
                .start(documentProcessingStep)
                .build();
    }

    @Bean
    public Step documentProcessingStep(
            JobRepository jobRepository,
            RepositoryItemReader<Document> documentReader,
            ItemProcessor<Document, Document> documentProcessor,
            JpaItemWriter<Document> documentWriter) {

        return new StepBuilder("documentProcessingStep", jobRepository)
                .<Document, Document>chunk(10)
                .reader(documentReader)
                .processor(documentProcessor)
                .writer(documentWriter)
                .build();
    }
}