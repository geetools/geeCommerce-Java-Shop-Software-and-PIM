<#import "${t_layout}/1column_cms.ftl" as layout>

<@layout.onecolumn title=title metaDescription=metaDescription metaRobots=metaRobots metaKeywords=metaKeywords>

<#--
<div id="toolbox" >
    <div class="w-element-controls">
        <div class="w-move-outer">
            <div class="w-move"></div>
        </div>
        <div class="w-handle-outer">
            <div class="w-handle w-handle-new"></div>
        </div>
        <div class="w-option-outer">
            <div class="w-option"></div>
        </div>
        <div class="w-delete-outer">
            <div class="w-delete" title="Delete"></div>
        </div>
        <!--          <div class="w-resize-l-outer">
                      <div class="w-resize w-resize-l"></div>
                  </div>
                  <div class="w-resize-r-outer">
                      <div class="w-resize w-resize-r"></div>
                  </div>&ndash;&gt;
    </div>
</div>
-->

    <div class="preview-container">
        <div class="gee-grid container">

        </div>
    </div>


<script language="javaScript">
    setInterval(function() {
        if(!window.focusMode) {
            window.parent.autoResizeIframe();
        }
    }, 500);
</script>
</@layout.onecolumn>