<#if slideshowId?? && slides??>

<div id="${widgetId}" class="web-slideshow web-slideshow-simple">
    <#list slides as slide>
        <div>
            <#switch slide.slideType>
                <#case "PRODUCT">
                    <#assign product = slide.product>
                    <a href="<@url target=product />">
                        <img class="img-responsive img-fluid" src="${slide.getSlideUri(746, 389)!""}"/>
                    </a>

                    <#break>
                <#case "IMAGE_LINK">
                    <#if (slide.mediaAsset?? && slide.link?has_content && slide.mediaAsset.url?has_content)>
                        <a href="${slide.link}">
                            <img class="img-responsive img-fluid" src="${slide.getSlideUri(746, 389)!""}"/>
                        </a>
                    </#if>
                    <#break>
                <#case "IMAGE">
                    <#if (slide.mediaAsset?? && slide.mediaAsset.url?has_content)>
                        <img class="img-responsive img-fluid"  src="${slide.getSlideUri(746, 389)!""}"/>
                    </#if>
                    <#break>

            </#switch>
        </div>
        <#assign index = index + 1>
    </#list>
</div>



    <#--<div id="${slideshowId}" class="carousel slide" data-ride="carousel">
        <#assign indexCounter = 0>
        <ol class="carousel-indicators">
            <#list slides as slide>
                <li data-target="#${slideshowId}" data-slide-to="${indexCounter}" class="<#if indexCounter = 0>active</#if>"></li>
                <#assign indexCounter = indexCounter + 1>
            </#list>
        </ol>
        <div class="carousel-inner" role="listbox">
            <#assign index = 1>
            <#list slides as slide>
                <div class="carousel-item <#if index = 1>active</#if>">
                    <#switch slide.slideType>
                        <#case "PRODUCT">
                            <#assign product = slide.product>
                            <a href="<@url target=product />">
                                <img src="${slide.getSlideUri(746, 389)!""}"/>
                            </a>

                            <#break>
                        <#case "IMAGE_LINK">
                            <#if (slide.mediaAsset?? && slide.link?has_content && slide.mediaAsset.url?has_content)>
                                <a href="${slide.link}">
                                    <img src="${slide.getSlideUri(746, 389)!""}"/>
                                </a>
                            </#if>
                            <#break>
                        <#case "IMAGE">
                            <#if (slide.mediaAsset?? && slide.mediaAsset.url?has_content)>
                                <img src="${slide.getSlideUri(746, 389)!""}"/>
                            </#if>
                            <#break>

                    </#switch>





&lt;#&ndash;                    <img src="..." alt="...">
                    <div class="carousel-caption">
                        <h3>...</h3>
                        <p>...</p>
                    </div>&ndash;&gt;
                </div>
                <#assign index = index + 1>
            </#list>
        </div>
        <a class="left carousel-control" href="#${slideshowId}" role="button" data-slide="prev">
            <span class="icon-prev" aria-hidden="true"></span>
            <span class="sr-only">Previous</span>
        </a>
        <a class="right carousel-control" href="#${slideshowId}" role="button" data-slide="next">
            <span class="icon-next" aria-hidden="true"></span>
            <span class="sr-only">Next</span>
        </a>
    </div>-->

</#if>