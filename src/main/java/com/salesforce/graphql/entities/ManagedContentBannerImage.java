package com.salesforce.graphql.entities;

import lombok.Data;

@Data
public class ManagedContentBannerImage {
    private String altText;
    private String fileName;
    private String mediaType;
    private String mimeType;
    private String nodeType;
    private String resourceUrl;
    private String thumbnailUrl;
    private String title;
    private String unauthenticatedUrl;
    private String url;
}
