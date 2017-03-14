<#import "${t_layout}/1column.ftl" as layout>

<@layout.onecolumn title=title metaDescription=metaDescription metaRobots=metaRobots metaKeywords=metaKeywords>

<#--    <div class="home-container">

        <div class="row">
            <div class="col-xs-6">
                <@web_slideshow name="home-slideshow" view="web_slideshow_bottom"/>
            </div>
            <div class="col-xs-6">
                <@web_slideshow name="home-slideshow" view="web_slideshow_simple"/>
            </div>
            <div class="col-xs-12">
                <@web_slideshow name="home-slideshow" view="web_slideshow_right"/>
            </div>
        </div>-->
        <div class="row">
            <@product_promotion key="test"/>
        </div>
<#--        <@web_slideshow name="home-slideshow" view="web_slideshow_simple"/>
        <@web_slideshow name="home-slideshow" view="web_slideshow_right"/>
        <@web_slideshow name="home-slideshow" view="web_slideshow_bottom"/>-->

        <@content key="home" />

        <div class="row">
            <div style="height: 25px"></div>
        </div>
    </div>


</@layout.onecolumn>