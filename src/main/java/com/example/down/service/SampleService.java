package com.example.down.service;

import com.example.down.model.FileInfo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class SampleService {
  private final RestTemplate restTemplate;

  public void download(String url, HttpServletResponse res) {
    try {
      FileInfo fileInfo = sendRequest(url);

      res.setHeader("Content-Disposition", "attachment;filename=" + fileInfo.getName());
      res.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
      res.setContentLength(fileInfo.getSize());
      res.getOutputStream().write(fileInfo.getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private FileInfo sendRequest(String url) {
    HttpHeaders httpHeaders = new HttpHeaders();
    HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
    ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

    if (responseEntity.getStatusCode() == HttpStatus.OK) {
      ContentDisposition contentDisposition = responseEntity.getHeaders().getContentDisposition();
      FileInfo fileInfo = new FileInfo();

      fileInfo.setBytes(responseEntity.getBody());
      String fileName = getFileName(contentDisposition, fileInfo.getBytes());
      fileInfo.setName(fileName);
      return fileInfo;
    } else {
      return null;
    }
  }

  // ContentDisposition에 파일 이름이 존재할 경우 해당 파일 이름을 사용하고 없으면 현재 날짜시분초로 파일 이름을 생성한다.
  private String getFileName(ContentDisposition contentDisposition, byte[] bytes) {
    if (StringUtils.isNotEmpty(contentDisposition.getFilename())) {
      return contentDisposition.getFilename();
    } else {
      Date date = new Date(Calendar.getInstance(Locale.KOREA).getTimeInMillis());
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
      return formatter.format(date) + "." + getExt(bytes);
    }
  }

  // tika 라이브러리를 이용해서 mite type을 체크하여 이미지에 확장자 또는 pdf 파일 여부를 체크하여 확장자를 반환한다.
  private String getExt(byte[] bytes) {
    Tika tika = new Tika();
    String mimeType = tika.detect(bytes);
    log.info("MIME Type = {}", mimeType);

    if (StringUtils.isNotEmpty(mimeType)) {
      if (mimeType.contains("jpeg") || mimeType.contains("jpg")) {
        return "jpg";
      } else if (mimeType.contains("png")) {
        return "png";
      } else if (mimeType.contains("bmp")) {
        return "bmp";
      } else if (mimeType.contains("gif")) {
        return "gif";
      } else if (mimeType.contains("webp")) {
        return "webp";
      } else if (mimeType.contains("pdf")) {
        return "pdf";
      }
    }
    return null;
  }

}
