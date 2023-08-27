package com.example.down.model;

import lombok.Data;

@Data
public class FileInfo {
  private String name;
  private byte[] bytes;

  public int getSize() {
    return bytes != null ? bytes.length : 0;
  }
}
