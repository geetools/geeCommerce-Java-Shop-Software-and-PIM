<#if conversionId?? && conversionLabel?? && pageType?? && totalValue??>

<#if pageType == "purchase">

<!-- Google Code for N&aacute;kup Conversion Page -->
    <script type="text/javascript">
        /* <![CDATA[ */
        var google_conversion_id = ${conversionId};
        var google_conversion_language = "cs";
        var google_conversion_format = "2";
        var google_conversion_color = "ffffff";
        var google_conversion_label = "${conversionLabel}";
        var google_conversion_value = ${totalValue?c};
        /* ]]> */
    </script>
    <script type="text/javascript" src="//www.googleadservices.com/pagead/conversion.js">
    </script>
    <noscript>
        <div style="display:inline;">
            <img height="1" width="1" style="border-style:none;" alt="" src="//www.googleadservices.com/pagead/conversion/${conversionId}/?value=${totalValue?c}&amp;label=${conversionLabel}&amp;guid=ON&amp;script=0"/>
        </div>
    </noscript>
</#if>

<!-- GOOGLE_REMARKETING - START -->

      <script type="text/javascript">
        var google_tag_params = {
          ecomm_prodid: ${prodId},
          ecomm_pagetype: '${pageType}',
          ecomm_totalvalue: '${totalValue?c}'
        };
      </script>

    <script type="text/javascript">
    /* <![CDATA[ */
    var google_conversion_id = ${conversionId};
    var google_conversion_label = "${conversionLabel}";
    var google_custom_params = window.google_tag_params;
    var google_remarketing_only = true;
    /* ]]> */
    </script>
    <script type="text/javascript" src="//www.googleadservices.com/pagead/conversion.js">
    </script>
    <noscript>
    <div style="display:inline;">
    <img height="1" width="1" style="border-style:none;" alt="" src="//googleads.g.doubleclick.net/pagead/viewthroughconversion/${conversionId}/?value=0&amp;label=${conversionLabel}&amp;guid=ON&amp;script=0"/>
    </div>
    </noscript>

<!-- GOOGLE_REMARKETING - END -->

</#if>