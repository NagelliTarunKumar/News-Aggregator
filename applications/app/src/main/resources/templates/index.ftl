<#import "template.ftl" as layout />

<@layout.template>
    <div class="page-container">
        <header class="main-header">
            <h1>Welcome ${email}</h1>
        </header>

        <section class="preferences-section">
            <h2>Your News Preferences</h2>
            <form action="/update" method="POST" class="preferences-form">
                <div class="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>Select</th>
                                <th>Item</th>
                                <th>Description</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr><td><input type="checkbox" name="finance" value="true" <#if finance == true>checked</#if>></td><td>Finance</td><td>Stock Marker, Business and more.</td></tr>
                            <tr><td><input type="checkbox" name="sports" value="true" <#if sports == true>checked</#if>></td><td>Sports</td><td>All the leagues you follow</td></tr>
                            <tr><td><input type="checkbox" name="fashion" value="true" <#if fashion == true>checked</#if>></td><td>Fashion</td><td>What's hip right now?</td></tr>
                            <tr><td><input type="checkbox" name="technology" value="true" <#if technology == true>checked</#if>></td><td>Technology</td><td>Gadgets and Gizmos</td></tr>
                            <tr><td><input type="checkbox" name="politics" value="true" <#if politics == true>checked</#if>></td><td>Politics</td><td>What actually matters</td></tr>
                        </tbody>
                    </table>
                </div>
                <button type="submit" class="update-button">Update Preferences</button>
            </form>
        </section>

        <!-- Top Stories Section -->
        <#if topStories?? && topStories?has_content>
            <section class="top-stories">
                <h2>Top Stories</h2>
                <div class="stories-list">
                    <#list topStories as story>
                        <div class="news-item">
                            <h4><a href="${story.url}" target="_blank">${story.title}</a></h4>
                            <#if story.description??>
                                <p>${story.description}</p>
                            </#if>
                        </div>
                    </#list>
                </div>
            </section>
        </#if>

        <!-- Personalized Categorized News -->
        <section class="news-container">
            <#if newsMap?? && newsMap?has_content>
                <h2>Your Personalized News Feed</h2>

                <div class="news-topics-container">
                    <#list newsMap?keys as topic>
                        <div class="news-topic">
                            <h3>${topic?cap_first} News</h3>
                            <#assign categorizedNews = newsMap[topic]>
                            <#list categorizedNews?keys as category>
                                <div class="category-group">
                                    <#-- Extract just the category name if it contains "Category=" -->
                                    <#if category?contains("=")>
                                        <#assign categoryName = category?split("=")?last>
                                        <span class="category-label">${categoryName}</span>
                                    <#else>
                                        <span class="category-label">${category}</span>
                                    </#if>
                                    <#assign articles = categorizedNews[category]>

                                    <#if articles?? && articles?has_content>
                                        <div class="articles-list">
                                            <#list articles as article>
                                                <div class="news-item">
                                                    <h5><a href="${article.url}" target="_blank">${article.title}</a></h5>
                                                    <#if article.description??>
                                                        <p>${article.description}</p>
                                                    </#if>
                                                </div>
                                            </#list>
                                        </div>
                                    </#if>
                                </div>
                            </#list>
                        </div>
                    </#list>
                </div>
            <#else>
                <div class="no-news">
                    <h2>No news to display</h2>
                    <p>Please select at least one news category to see relevant articles.</p>
                </div>
            </#if>
        </section>
    </div>

    <style>
        /* Global Styles */
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: #333;
            background-color: #f5f7fa;
        }

        .page-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }

        h1, h2, h3, h4, h5 {
            font-weight: 600;
            line-height: 1.3;
            margin-bottom: 15px;
            color: #2c3e50;
        }

        h1 {
            font-size: 2.2rem;
            color: #2c3e50;
            margin-bottom: 30px;
            border-bottom: 3px solid #3498db;
            padding-bottom: 10px;
        }

        h2 {
            font-size: 1.8rem;
            margin-bottom: 20px;
        }

        /* Header Styles */
        .main-header {
            margin-bottom: 30px;
        }

        /* Preferences Section */
        .preferences-section {
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
            padding: 25px;
            margin-bottom: 40px;
        }

        .preferences-form table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
        }

        .preferences-form th,
        .preferences-form td {
            padding: 12px 15px;
            text-align: left;
            border: 1px solid #e0e0e0;
        }

        .preferences-form th {
            background-color: #f2f6fc;
            font-weight: 600;
        }

        .preferences-form tr:nth-child(even) {
            background-color: #f9f9f9;
        }

        .preferences-form tr:hover {
            background-color: #f0f7ff;
        }

        .table-responsive {
            overflow-x: auto;
            margin-bottom: 20px;
        }

        .update-button {
            background-color: #3498db;
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .update-button:hover {
            background-color: #2980b9;
        }

        /* Top Stories Section */
        .top-stories {
            background-color: #fff5e6;
            border-radius: 8px;
            padding: 25px;
            margin-bottom: 40px;
            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
        }

        .top-stories h2 {
            color: #d35400;
            border-bottom: 2px solid #e67e22;
            padding-bottom: 10px;
            margin-bottom: 25px;
        }

        .stories-list {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }

        /* News Container */
        .news-container {
            background-color: white;
            border-radius: 8px;
            padding: 25px;
            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
        }

        .news-topics-container {
            display: flex;
            flex-direction: column;
            gap: 30px;
        }

        .news-topic {
            margin-bottom: 30px;
        }

        .news-topic h3 {
            color: #2c3e50;
            border-bottom: 2px solid #3498db;
            padding-bottom: 8px;
            margin-bottom: 15px;
        }

        .category-group {
            margin-bottom: 25px;
        }

        .category-label {
            display: inline-block;
            background-color: #f0e6f6;
            color: #8e44ad;
            padding: 5px 12px;
            border-radius: 4px;
            font-size: 0.9rem;
            margin-bottom: 15px;
        }

        .articles-list {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }

        /* News Item Styles - Vertical layout with left border */
        .news-item {
            background-color: white;
            padding: 15px;
            border-radius: 6px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
            border-left: 4px solid #3498db;
            margin-bottom: 15px;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }

        .news-item:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .news-item h4,
        .news-item h5 {
            font-size: 1.1rem;
            margin-bottom: 8px;
        }

        .news-item a {
            color: #2980b9;
            text-decoration: none;
            transition: color 0.2s;
        }

        .news-item a:hover {
            color: #3498db;
            text-decoration: underline;
        }

        .news-item p {
            color: #666;
            font-size: 0.95rem;
            line-height: 1.5;
        }

        /* No News Section */
        .no-news {
            text-align: center;
            padding: 50px 20px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
        }

        .no-news h2 {
            color: #7f8c8d;
            margin-bottom: 15px;
        }

        .no-news p {
            color: #95a5a6;
            font-size: 1.1rem;
        }

        /* Responsive Design */
        @media (max-width: 768px) {
            .page-container {
                padding: 15px;
            }

            h1 {
                font-size: 1.8rem;
            }

            h2 {
                font-size: 1.5rem;
            }
        }
    </style>
</@layout.template>