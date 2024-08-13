package cm.twentysix.brand.client;


import cm.twentysix.brand.constant.FileDomain;
import cm.twentysix.brand.exception.FileException;
import cm.twentysix.brand.util.FilePathUtil;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cm.twentysix.brand.exception.Error.FILE_UPLOAD_FAILED;


@Component
@Slf4j
@RequiredArgsConstructor
public class S3Client implements FileStorageClient {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;


    @Override
    public String upload(MultipartFile multipartFile, FileDomain domain) {
        String fileName = FilePathUtil.getFileNameAndExtension(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String filePath = FilePathUtil.createFilePath(domain);
        try {
            File file = File.createTempFile("file", fileName);
            multipartFile.transferTo(file);
            amazonS3.putObject(new PutObjectRequest(bucket, filePath + fileName, file));
        } catch (IOException e) {
            throw new FileException(FILE_UPLOAD_FAILED);
        }
        return filePath + fileName;
    }

    @Override
    public String upload(File file, FileDomain domain) {
        String fileName = FilePathUtil.getFileNameAndExtension(file.getName());
        String filePath = FilePathUtil.createFilePath(domain);
        amazonS3.putObject(new PutObjectRequest(bucket, filePath + fileName, file));
        return filePath + fileName;
    }

    @Override
    public void delete(String filePath) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, filePath));
    }

    @Override
    public void deleteAll(List<String> filePaths) {
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket);
        List<DeleteObjectsRequest.KeyVersion> keyVersionList = new ArrayList<>();
        filePaths.forEach(filePath -> keyVersionList.add(new DeleteObjectsRequest.KeyVersion(filePath)));
        deleteObjectsRequest.setKeys(keyVersionList);
        amazonS3.deleteObjects(deleteObjectsRequest);
    }
}
