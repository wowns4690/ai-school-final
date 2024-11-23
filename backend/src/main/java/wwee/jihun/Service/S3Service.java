package wwee.jihun.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket.name}")
    private String bucket;

    // URL 이미지를 S3에 업로드하는 메서드 추가
    public String uploadFromUrl(String imageUrl, String fileName) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        try (InputStream inputStream = connection.getInputStream()) {
            // Content Length 및 Content Type 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(connection.getContentLengthLong());
            metadata.setContentType(connection.getContentType());

            // S3에 업로드
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));

//            amazonS3.putObject(bucket, fileName, inputStream, metadata);
            return amazonS3.getUrl(bucket, fileName).toString();
        }
    }
    //이미지 객체 url 받아오기
    public String getFileUrl(String fileName) {
        try{
            return amazonS3.getUrl(bucket, fileName).toString();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).toString();
        }
    }

    //s3객체 삭제하기
    public ResponseEntity<String> deleteFile(String fileName) {
        try{
            amazonS3.deleteObject(bucket, fileName);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
