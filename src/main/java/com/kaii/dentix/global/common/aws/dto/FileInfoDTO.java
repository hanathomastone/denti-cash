package com.kaii.dentix.global.common.aws.dto;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoDTO {

    private String originName;
    private String name;
    private String path;
    private String fullPath;
    private String ext;
    private long size;

    @Override
    public String toString() {
        return "{"
            + "\"originName\":\"" + originName + "\""
            + ", \"name\":\"" + name + "\""
            + ", \"path\":\"" + path + "\""
            + ", \"fullPath\":\"" + fullPath + "\""
            + ", \"ext\":\"" + ext + "\""
            + ", \"size\":\"" + size + "\""
            + "}";
    }
}
