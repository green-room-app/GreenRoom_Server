package com.greenroom.modulecommon.util;

import com.greenroom.modulecommon.config.AwsConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.*;

@Component
@RequiredArgsConstructor
public class PresignerUtils {

    private static final String PROFILE_IMAGE_FOLDER = "green-room-user-image";
    private final AwsConfig awsConfig;
    private final S3Presigner presigner;

    public String getPresignedGetUrl(String filename) {
        if (isEmpty(filename)) {
            return EMPTY;
        }

        GetObjectPresignRequest presignGetRequest = toGetObjectPresignRequest(PROFILE_IMAGE_FOLDER, filename);
        return presigner.presignGetObject(presignGetRequest).url().toString();
    }

    public String getPresignedPutUrl(String filename) {
        PutObjectPresignRequest presignPutRequest = toPutObjectPresignRequest(PROFILE_IMAGE_FOLDER, filename);
        return presigner.presignPutObject(presignPutRequest).url().toString();
    }

    private GetObjectPresignRequest toGetObjectPresignRequest(String folder, String filename) {

        String filePathKey = toKey(folder, filename);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsConfig.getBucket())
                .key(filePathKey)
                .build();

        return GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();
    }

    private PutObjectPresignRequest toPutObjectPresignRequest(String folder, String filename) {

        String filePathKey = toKey(folder, filename);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(awsConfig.getBucket())
                .key(filePathKey)
                .build();

        return PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();
    }

    private String toKey(String folder, String filename) {
        checkArgument(isNotEmpty(folder), "folder 값은 필수입니다.");
        checkArgument(isNotEmpty(filename), "filename 값은 필수입니다.");

        return folder + "/" + filename;
    }
}
