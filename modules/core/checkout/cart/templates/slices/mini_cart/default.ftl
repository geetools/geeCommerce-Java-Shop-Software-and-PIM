<div class="mini-cart-content">

    {{#cart}}
    {{#empty}}
        <@message text="Cart is empty" lang="en" text2="Leer Warenkorb" lang2="de" />
    {{/empty}}

    {{^empty}}
        <table class="mc-items">
            {{#items}}
            <tr class="mc-item">
            	{{#image}}
	                <td class="mc-image">*****<a href="{{{url}}}"><img src="{{{image}}}"></a></td>
            	{{/image}}
                <td class="mc-name"><b>+++{{{name}}}</b><br>{{{name2}}}</td>
                <td class="mc-quantity">{{{quantity}}}x</td>
                <td class="mc-subtotal">{{{subtotal}}}</td>
            </tr>
            {{/items}}

            <tr class="mc-total">
                <td></td>
                <td colspan="2" class="mc-total-label">
                <@message text="Total" lang="en" text2="Gesamtsumme" lang2="de" />:
                </td>
                <td class="mc-total-val">
                    {{{total}}}
                </td>
            </tr>
        </table>
    {{/empty}}
    {{/cart}}
</div>
