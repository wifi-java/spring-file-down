package com.example.down.controller.rest;

import com.example.down.service.SampleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SampleRestController {
  private final SampleService sampleService;

  @GetMapping("/download")
  public void download(HttpServletRequest request, HttpServletResponse res) throws Exception {
    String url = request.getParameter("url");
    sampleService.download(url, res);
  }
}
