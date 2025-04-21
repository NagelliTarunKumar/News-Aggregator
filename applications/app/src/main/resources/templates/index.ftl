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
                <td>This is the third item.</td>
            </tr>
            <tr>
                <td><input type="checkbox" name="politics" value="true" <#if politics == true>checked</#if>></td>
                <td>Politics</td>
                <td>This is the third item.</td>
            </tr>
            </tbody>
        </table>
        <br>
        <input type="submit" value="Update">
    </form>

</@layout.template>
