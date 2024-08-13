package cm.twentysix.brand.client;


import cm.twentysix.brand.constant.FileDomain;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileStorageClient {
    String upload(MultipartFile multipartFile, FileDomain domain);

    String upload(File file, FileDomain domain);

    void delete(String filePath);

    void deleteAll(List<String> filePaths);
}
