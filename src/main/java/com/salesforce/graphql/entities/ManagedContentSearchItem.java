package com.salesforce.graphql.entities;

import lombok.Data;

@Data
public class ManagedContentSearchItem {
    private ManagedContentType contentType;
    private String id;
    private String publishDate;
    private String title;
    private String urlName;
}
