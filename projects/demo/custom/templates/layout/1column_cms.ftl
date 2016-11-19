<#macro onecolumn args...>
<!DOCTYPE html>
<html lang="en">

<head>
    <#include "${t_includes}/head_cms.ftl" />
</head>

<#-- Get the path to the js-api-script if one exists. -->
    <#if moduleCode?has_content>
        <@js type="module" name="${moduleCode}" fetch="api" noext=true var="modueAPIScript"/>
    </#if>

<#-- Add as much information as possible to the body tag so that requireJS is able to load some scripts automatically. -->
<body data-action="${controllerCode}" data-event="${eventCode}"<#if modelId?has_content> data-id="${modelId?string}"</#if><#if moduleCode?has_content> data-module="${moduleCode}"</#if><#if modueAPIScript?has_content> data-api="${modueAPIScript}"</#if>>

<!-- ------------------------------------------------------------------------------- -->
<!-- Header
<!-- ------------------------------------------------------------------------------- -->

    <#include "${t_includes}/header.ftl" />

<!-- ------------------------------------------------------------------------------- -->
<!-- Breadcrumbs
<!-- ------------------------------------------------------------------------------- -->


    <#if .template_name != "WEB-INF/templates/pages/home.ftl" && .template_name != "WEB-INF/templates/pages/catalog/search/result.ftl" && .template_name != "WEB-INF/templates/pages/cart/view.ftl" && .template_name != "WEB-INF/templates/pages/customer/account/login_form.ftl" && .template_name != "WEB-INF/templates/pages/error/404.ftl">

    <div class="container breadcrumbs" style="display: none">
        <div class="row">
            <div class="col-xs-12">
                <div id="navig-path" class="noprint hidden-phone hidden-phone-h">
                    <@breadcrumbs_navigation breadcrumbs=args["breadcrumbs"]/>
                </div>
            </div>
        </div>
    </div>
    </#if>

<!-- ------------------------------------------------------------------------------- -->
<!-- Content
<!-- ------------------------------------------------------------------------------- -->

<div class="container">
    <#nested>
</div>

<!-- ------------------------------------------------------------------------------- -->
<!-- Footer
<!-- ------------------------------------------------------------------------------- -->

    <#include "${t_includes}/footer.ftl" />

</body>
</html>

</#macro>
