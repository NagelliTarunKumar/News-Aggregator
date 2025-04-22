<#import "template.ftl" as layout />

<@layout.template>
    <h1>Welcome ${email}</h1>

    <form action="/update" method="POST">
        <table border="1">
            <thead>
            <tr>
                <th>Select</th>
                <th>Item</th>
                <th>Description</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td><input type="checkbox" name="finance" value="true"  <#if finance == true>checked</#if>></td>
                <td>Finance</td>
                <td>This is the first item.</td>
            </tr>
            <tr>
                <td><input type="checkbox" name="sports" value="true" <#if sports == true>checked</#if>></td>
                <td>Sports</td>
                <td>This is the second item.</td>
            </tr>
            <tr>
                <td><input type="checkbox" name="fashion" value="true" <#if fashion == true>checked</#if>></td>
                <td>Fashion</td>
                <td>This is the third item.</td>
            </tr>
            <tr>
                <td><input type="checkbox" name="technology" value="true" <#if technology == true>checked</#if>></td>
                <td>Technology</td>
                <td>This is the fourth item.</td>
            </tr>
            <tr>
                <td><input type="checkbox" name="politics" value="true" <#if politics == true>checked</#if>></td>
                <td>Politics</td>
                <td>This is the fifth item.</td>
            </tr>
            </tbody>
        </table>
        <br>
        <input type="submit" value="Update">
    </form>

    <!-- ✅ Top Stories Section -->
    <#if topStories?? && topStories?has_content>
        <div class="top-stories">
            <h2>Top Stories</h2>
            <#list topStories as story>
                <div class="news-item">
                    <h4><a href="${story.url}" target="_blank">${story.title}</a></h4>
                    <#if story.description??>
                        <p>${story.description}</p>
                    </#if>
                </div>
            </#list>
        </div>
    </#if>

    <!-- Personalized News Section -->
    <div class="news-container">
        <#if newsMap?? && newsMap?has_content>
            <h2>Your Personalized News Feed</h2>

            <#list newsMap?keys as topic>
                <div class="news-topic">
                    <h3>${topic?cap_first} News</h3>
                    <div class="news-articles">
                        <#list newsMap[topic] as newsItem>
                            <div class="news-item">
                                <h4><a href="${newsItem.url}" target="_blank">${newsItem.title}</a></h4>
                                <#if newsItem.description??>
                                    <p>${newsItem.description}</p>
                                </#if>
                            </div>
                        </#list>
                    </div>
                </div>
            </#list>
        <#else>
            <div class="no-news">
                <h2>No news to display</h2>
                <p>Please select at least one news category to see relevant articles.</p>
            </div>
        </#if>
    </div>

    <style>
        .news-container {
            margin-top: 30px;
            padding: 15px;
            background-color: #f9f9f9;
            border-radius: 5px;
        }

        .top-stories {
            margin-top: 40px;
            padding: 15px;
            background-color: #fff5e6;
            border-radius: 5px;
        }

        .top-stories h2 {
            color: #d35400;
            border-bottom: 2px solid #e67e22;
            padding-bottom: 5px;
        }

        .news-topic {
            margin-bottom: 25px;
        }

        .news-topic h3 {
            color: #2c3e50;
            border-bottom: 2px solid #3498db;
            padding-bottom: 5px;
        }

        .news-item {
            background-color: white;
            padding: 15px;
            margin-bottom: 15px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }

        .news-item h4 {
            margin-top: 0;
        }

        .news-item a {
            color: #3498db;
            text-decoration: none;
        }

        .news-item a:hover {
            text-decoration: underline;
        }

        .news-item small {
            color: #7f8c8d;
            display: block;
            margin-top: 10px;
        }

        .no-news {
            text-align: center;
            padding: 30px;
            background-color: white;
            border-radius: 5px;
        }
    </style>
</@layout.template>
