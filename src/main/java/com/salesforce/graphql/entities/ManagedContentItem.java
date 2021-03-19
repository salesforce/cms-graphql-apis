package com.salesforce.graphql.entities;

import lombok.Data;

@Data
public class ManagedContentItem {
    private ManagedContentNodes contentNodes;
    private String contentUrlName;
    private String language;
    private String managedContentId;
    private String publishedDate;
    private String title;
    private String type;
    private String typeLabel;
    private String unauthenticatedUrl;

}
