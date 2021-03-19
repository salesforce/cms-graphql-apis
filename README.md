

<!--
*** Thanks for checking out the CMS GraphQL API project
*** This is an open source project whose vision is to provide Salesforce CMS customers 
*** query the public CMS apis using GraphQL queries. This project is currently integrated
*** with CMS delivery apis 
*** https://developer.salesforce.com/docs/atlas.en-us.chatterapi.meta/chatterapi/connect_resources_managed_content_resources.htm
-->


<br />
<p align="center">
  <h3 align="center">CMS GraphQL API</h3>

  <p align="center">
    This is an open source project whose vision is to provide Salesforce CMS customers query the public CMS apis using GraphQL queries. 
    <br />
    This project is currently integrated with 
    <a href="https://developer.salesforce.com/docs/atlas.en-us.chatterapi.meta/chatterapi/connect_resources_managed_content_resources.htm"><strong>CMS delivery apis »</strong></a>
    <br />
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Problem statement</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

![GraphQL Sample Query](https://drive.google.com/file/d/1md3kY-Eynk2VFzyQ2w2vkQvsrQ8r8EGn/view?usp=sharing)

GraphQL provides a optimized way to fetch data which involves loq latency and high throughput

Couple of motivations behind using GraphQL for CMS delivery apis are:
* Overfetching : Currently CMS Rest endpoint have a specific response schema. It ends up overfetching un-necessary metadata
* Underfetching: In some scenarios we have to fetch the id of a related entity and request another rest endpoint to get the metadata.
* Nested Queries: Ex: In order to get related contents in a channel, first we have to fetch all the channels and then make a query to fetch all the related contents. These nested queries increase the latency between the client and server
* UI components: UI components require low latency , i.e. less nested query calls. GraphQL optimizes this by creating the nested query upfront and then provide the details to the server in one go

There are other reasons because of which GraphQL has a wide adoption across the industry. 

### Problem Statement

#### As a user I to get the channel Name, channel Type and only the managedContentId and banner image from the related content Nodes
Note this is an example of nested query. Here is how a user can achieve this 
* [Get the channel Info](https://developer.salesforce.com/docs/atlas.en-us.chatterapi.meta/chatterapi/connect_resources_managed_content_delivery_channels.htm) Rest API call to get the channel metadata
* Now user has to store this in an attribute
* [Get the related contents](https://developer.salesforce.com/docs/atlas.en-us.chatterapi.meta/chatterapi/connect_resources_managed_content_delivery_channel.htm) User again does a rest query with the channel Id to get the related contents

Imagine if a UI component is consuming this data, couple of issues this has are
* We are overfetching channel metadata along with related content metadata
* We are performing 2 nested queries, so the client has to wait for the response from both the queries before it is able to render
* We don't have a way to define a directive like if or skip (We will discuss this with an example)



<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple example steps.

### Prerequisites

* [Gradle](https://gradle.org/install/)
* [Intellij](https://www.jetbrains.com/idea/) (Will update the command line instructions soon)
* [Postman](https://www.postman.com/product/rest-client/)
* [Create a connected app path with oauth enabled ](https://help.salesforce.com/articleView?id=connected_app_create.htm&type=5)

### Installation

1. Clone the repo
   ```sh
   git clone git@git.soma.salesforce.com:nsengupta/cms-graphql-api.git
   ```
2. Open the project in Intellij

3. Go to the application.properties under src/main/resources and paste the username, password, client id and client secret (from the connected app)

4. Build and run GraphqlApiApplication 


<!-- USAGE EXAMPLES -->
## Usage

Once the project is up and running, open postman and do a post query 

Postman Url: http://localhost:8081/graphql (Refer to the project snapshot image at the top)

### Introspection

1. First let's understand what kind of objects are supported currently
GraphQL query:
```
{
  __schema {
    types {
      name
    }
  }
}
```

GrahQL response
```
{
    "data": {
        "__schema": {
            "types": [
                ...
                {
                    "name": "ManagedContentBannerImage"
                },
                {
                    "name": "ManagedContentBody"
                },
                {
                    "name": "ManagedContentExcerpt"
                },
                {
                    "name": "ManagedContentItem"
                },
                {
                    "name": "ManagedContentNodes"
                },
                {
                    "name": "ManagedContentSource"
                },
                {
                    "name": "ManagedContentTitle"
                },
                {
                    "name": "NodeTypeEnum"
                },
                {
                    "name": "Query"
                },
                ...
            ]
        }
    }
}
```
2. Let's say if you want to know more details of any object
GraphQL query:
```
{
    __type (name: "ManagedContentNodes"){
      name
      fields {
          name
          type {
              name
              kind
          }
      }
    }
  
}
```
GraphQl response:
```
{
    "data": {
        "__type": {
            "name": "ManagedContentNodes",
            "fields": [
                {
                    "name": "source",
                    "type": {
                        "name": "ManagedContentSource",
                        "kind": "OBJECT"
                    }
                },
                {
                    "name": "title",
                    "type": {
                        "name": "ManagedContentTitle",
                        "kind": "OBJECT"
                    }
                },
                {
                    "name": "bannerImage",
                    "type": {
                        "name": "ManagedContentBannerImage",
                        "kind": "OBJECT"
                    }
                },
                {
                    "name": "excerpt",
                    "type": {
                        "name": "ManagedContentExcerpt",
                        "kind": "OBJECT"
                    }
                },
                {
                    "name": "body",
                    "type": {
                        "name": "ManagedContentBody",
                        "kind": "OBJECT"
                    }
                }
            ]
        }
    }
}
```

Once we determine the fields then couple of use cases that are currently supported (Note these are examples which might not include all the fields, consider them just an example)

### As a user I want to get few metadata from the channel and few from the related content
GraphQL Query:
```sh
query ChannelWithContents {
    channelById(channelId: "<<channelId>>"){
        channelName
        channelType
        domainId
        items (managedContentId: "<<managedContentId>>"){
            managedContentId
            contentNodes {
                bannerImage {
                    mediaType
                }
            }
        }
    }
}
```

GraphQl Response:
```
{
    "data": {
        "channelById": {
            "channelName": "<<Name>>",
            "channelType": "Community",
            "domainId": null,
            "items": [
                {
                    "managedContentId": "<<id>>",
                    "contentNodes": {
                        "bannerImage": {
                            "mediaType": "Image"
                        }
                    }
                },
                {
                    "managedContentId": "<<id>>",
                    "contentNodes": {
                        "bannerImage": null
                    }
                }
            ]
        }
    }
}
```

### As a user I want to search for a particular queryTerm in a channel
GraphQL Query:
```
query SearchContent {
    searchContent (channelId: "0ap1R000000blKCQAY", queryTerm: "Addidas") {
        contentType {
            developerName
        }
        id
        publishDate
        title 
        urlName
    }
}
```

GraphQL Response:
```
{
    "data": {
        "searchContent": [
            {
                "contentType": {
                    "developerName": "news"
                },
                "id": "20Y1R000000CaV9UAK",
                "publishDate": "2020-12-28T05:57:50.000Z",
                "title": "Addidas News",
                "urlName": "addidas-news"
            }
        ]
    }
}
```


### As a user I want to define a way to include or exclude metadata from my response
GraphQL Query:
```
query ChannelWithContent ($includeBanner: Boolean!){
    channelById(channelId: "0ap1R000000blKCQAY"){
        channelName
        channelType
        domainId
        items{
            managedContentId
            contentNodes {
                bannerImage @include(if: $includeBanner){
                    mediaType
                }
            }
        }
    }
}
```
Note the variable definition at the top ($includeBanner) this is an example which defines if a section is added or removed from the response

GraphQL variable payload
```
{
    "includeBanner": false
}
```

GraphQL Response
```
{
    "data": {
        "channelById": {
            "channelName": "Ankhi Arts",
            "channelType": "Community",
            "domainId": null,
            "items": [
                {
                    "managedContentId": "20Y1R000000CaV9UAK",
                    "contentNodes": {}
                },
                {
                    "managedContentId": "20Y1R000000001EUAQ",
                    "contentNodes": {}
                }
            ]
        }
    }
}
```

<!-- ROADMAP -->
## Roadmap

See the [open issues](https://git.soma.salesforce.com/nsengupta/cms-graphql-api/issues) for a list of proposed features (and known issues).



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


<!-- ACKNOWLEDGEMENTS -->
## Acknowledgements
* [GraphQL Java](https://www.graphql-java.com/tutorials/getting-started-with-spring-boot/)

