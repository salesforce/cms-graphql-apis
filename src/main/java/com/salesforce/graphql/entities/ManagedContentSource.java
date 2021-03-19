package com.salesforce.graphql.entities;

import lombok.Data;

@Data
public class ManagedContentSource {
    private String fileName;
    private Boolean isExternal;
    private String mediaType;
    private String mimeType;
    private String nodeType;
    private String referenceId;
    private String resourceUrl;
    private String unauthenticatedUrl;
    private String url;
}
