<#import "template.ftl" as layout />
<script>
    const cookies = document.cookie;
    const isLoggedIn = cookies.includes("session_id")
</script>

<@layout.template>
    <h1>Fill out which interests you... TODO</h1>

</@layout.template>
