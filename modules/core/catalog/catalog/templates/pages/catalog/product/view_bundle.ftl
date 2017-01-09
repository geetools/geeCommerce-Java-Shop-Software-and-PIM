<div class="bundle-config" bundle-id="${product.id?string}">

	<#list product.bundleGroups as bundleGroup>
		<#if bundleGroup.showInProductDetails >
			<div class="bundle-group row" group-type="${bundleGroup.type}" group-id="${bundleGroup.id?string}">

		<#--		cms_product_variants-->

			<#if bundleGroup.type?string == 'LIST' >
				<#list bundleGroup.bundleItems as bundleItem>
					<div class="row">
						<#if bundleItem.product.variantMaster>
							<div class="col-xs-12 col-sm-7">
								<@cms_product_carousel product_id="${bundleItem.product.id}" />
                            </div>
                        	<div class="col-xs-12 col-sm-5">
								<h3><@attribute src=bundleItem.product code="name" /><@attribute_exists src=bundleItem.product code="name2">, <@attribute src=bundleItem.product code="name2" /></@attribute_exists></h3>
								<br/>
								<p>
								<@attribute src=bundleItem.product code="description" parent=true/>
								</p>
                        	
								<@cms_product_variants product_id="${bundleItem.product.id}" />
                            </div>
						<#else>
                            <div class="col-xs-12 col-sm-7">
								<@cms_product_carousel product_id="${bundleItem.product.id}" />
                            </div>
                            <div class="col-xs-12 col-sm-5">
<<<<<<< HEAD
								<h2 class="bundle-group-label"><@print src=bundleGroup value="bundleGroup.label.str" /></h2>
								<h3><@attribute src=bundleItem.product code="name" /><@attribute_exists src=bundleItem.product code="name2">, <@attribute src=bundleItem.product code="name2" /></@attribute_exists></h3>
								<br/>
								<p>
								<@attribute src=bundleItem.product code="description" parent=true/>
								</p>
								
								<br/>
								<@product_details product_id="${bundleItem.product.parentId}"/>								
=======
                                <div id="prd-details-artno" class="prd-text">
									<@message text="Article No." lang="en" text2="Artikel-Nr." lang2="de" />
                                    <span><@attribute src=bundleItem.product code="article_number" /></span>
                                </div>
                                <div id="prd-details-shortdesc" class="prd-text">
									<@attribute_exists src=product code="short_description" parent=true>
										<@attribute src=bundleItem.product code="short_description" make="list" parent=true/>
									</@attribute_exists>
								</div>

								<div id="prd-details-desc" class="prd-text">
									<@attribute_exists src=product code="description" parent=true>
										<@attribute src=bundleItem.product code="description" parent=true />
									</@attribute_exists>
								</div>
>>>>>>> 52fc76d94b04466aea526578cbfebc4f44a58388
                            </div>

							<input type="hidden" name="bundleProduct" value="${bundleItem.product.id?string}" qty="${bundleItem.quantity}">
						</#if>
					</div>
				</#list>
			</#if>
				<#if bundleGroup.type?string == 'SELECT' >
					<div class="row">
						<div class="col-xs-12 col-sm-7"><!-- carousel --></div>

                        <div class="col-xs-12 col-sm-5">
						<h2 class="bundle-group-label"><@print src=bundleGroup value="bundleGroup.label.str" /></h2>

   						<#assign selectedProductId = null /> 
                        
					<select class="bundle-item">
						<#if bundleGroup.optional>
							<option value="">
								none
							</option>
						</#if>


						<#list bundleGroup.bundleItems as bundleItem>

							<#if bundleItem.product.variantMaster >
								<#list  bundleItem.product.variants as variantProduct>
									<#if variantProduct.validForSelling >
										<#if bundleItem.selected &&  bundleItem.defaultProductId?string == variantProduct.id?string>
											<#assign selectedProductId = bundleItem.product.id />
                                            <option bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" value="${variantProduct.id?string}" selected qty="${bundleItem.quantity}">
												<@attribute src=variantProduct code="name" /><@attribute_exists src=bundleItem.product code="name2">, <@attribute src=variantProduct code="name2" /></@attribute_exists>
                                            </option>
										<#else>
                                            <option bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" value="${variantProduct.id?string}" qty="${bundleItem.quantity}">
												<@attribute src=variantProduct code="name" /><@attribute_exists src=bundleItem.product code="name2">, <@attribute src=variantProduct code="name2" /></@attribute_exists>
                                            </option>
										</#if>
									</#if>
								</#list>
							<#else>
								<#if bundleItem.selected >
									<#assign selectedProductId = bundleItem.product.id />
                                    <option bundle_option="${bundleItem.product.id?string}" value="${bundleItem.product.id?string}" selected qty="${bundleItem.quantity}">
										<@attribute src=bundleItem.product code="name" /><@attribute_exists src=bundleItem.product code="name2">, <@attribute src=bundleItem.product code="name2" /></@attribute_exists>
                                    </option>
								<#else>
                                    <option bundle_option="${bundleItem.product.id?string}" value="${bundleItem.product.id?string}" qty="${bundleItem.quantity}">
										<@attribute src=bundleItem.product code="name" /><@attribute_exists src=bundleItem.product code="name2">, <@attribute src=bundleItem.product code="name2" /></@attribute_exists>
                                    </option>
								</#if>
							</#if>

						</#list>
					</select>
					
					<br/><br/>
					<@product_details product_id="${selectedProductId}"/>								
					
					</div>

					</div>					
				</#if>

				<#if bundleGroup.type?string == 'MULTISELECT' >
					<select multiple class="bundle-item">
						<#list bundleGroup.bundleItems as bundleItem>
							<#if bundleItem.product.variantMaster >
								<#list  bundleItem.product.variants as variantProduct>
									<#if variantProduct.validForSelling >
										<#if bundleItem.selected &&  bundleItem.defaultProductId?string == variantProduct.id?string>
                                            <option bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" value="${variantProduct.id?string}" selected qty="${bundleItem.quantity}">
												<@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" />
                                            </option>
										<#else>
                                            <option bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" value="${variantProduct.id?string}" qty="${bundleItem.quantity}">
												<@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" />
                                            </option>
										</#if>
									</#if>
								</#list>
							<#else>
								<#if bundleItem.selected >
                                    <option bundle_option="${bundleItem.product.id?string}" value="${bundleItem.product.id?string}" selected qty="${bundleItem.quantity}">
										<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
                                    </option>
								<#else>
                                    <option bundle_option="${bundleItem.product.id?string}" value="${bundleItem.product.id?string}" qty="${bundleItem.quantity}">
										<@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" />
                                    </option>
								</#if>
							</#if>
						</#list>
					</select>
				</#if>


				<#if bundleGroup.type?string == 'RADIOBUTTON'>
					<#if bundleGroup.optional>
						<input type="radio" class="bundle-item" name="group_${bundleGroup.id}" value=""> none </br>
					</#if>
					<#list bundleGroup.bundleItems as bundleItem>
						<#if bundleItem.product.variantMaster >
							<#list  bundleItem.product.variants as variantProduct>
								<#if variantProduct.validForSelling >
									<#if bundleItem.selected?? && bundleItem.selected && bundleItem.defaultProductId?string == variantProduct.id?string >
                                        <input type="radio" class="bundle-item" bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" qty="${bundleItem.quantity}" checked name="group_${bundleGroup.id}" value="${variantProduct.id?string}"> <@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" /> <br/>
									<#else>
                                        <input type="radio" class="bundle-item" bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" qty="${bundleItem.quantity}" name="group_${bundleGroup.id}" value="${variantProduct.id?string}"> <@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" /> <br/>
									</#if>
								</#if>
							</#list>
						<#else>
							<#if bundleItem.selected >
                                <input type="radio" class="bundle-item" bundle_option="${bundleItem.product.id?string}" qty="${bundleItem.quantity}" checked name="group_${bundleGroup.id}" value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
							<#else>
                                <input type="radio" class="bundle-item" bundle_option="${bundleItem.product.id?string}" qty="${bundleItem.quantity}" name="group_${bundleGroup.id}" value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
							</#if>
						</#if>

					</#list>
				</#if>

				<#if bundleGroup.type?string == 'CHECKBOX' >
					<#list bundleGroup.bundleItems as bundleItem>

						<#if bundleItem.product.variantMaster >
							<#list  bundleItem.product.variants as variantProduct>
								<#if variantProduct.validForSelling >
									<#if bundleItem.selected &&  bundleItem.defaultProductId?string == variantProduct.id?string>
                                        <input type="checkbox" class="bundle-item" bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" qty="${bundleItem.quantity}" checked value="${variantProduct.id?string}"> <@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" /> <br/>
									<#else>
                                        <input type="checkbox" class="bundle-item" bundle_master_option="${bundleItem.product.id?string}" bundle_option="${variantProduct.id?string}" qty="${bundleItem.quantity}" value="${variantProduct.id?string}"> <@attribute src=variantProduct code="name" />, <@attribute src=variantProduct code="name2" /> <br/>
									</#if>
								</#if>
							</#list>
						<#else>
							<#if bundleItem.selected >
                                <input type="checkbox" class="bundle-item" bundle_option="${bundleItem.product.id?string}" qty="${bundleItem.quantity}" checked value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
							<#else>
                                <input type="checkbox" class="bundle-item" bundle_option="${bundleItem.product.id?string}" qty="${bundleItem.quantity}" value="${bundleItem.product.id?string}"> <@attribute src=bundleItem.product code="name" />, <@attribute src=bundleItem.product code="name2" /> <br/>
							</#if>
						</#if>

					</#list>
				</#if>
			</div>
		</#if>
	</#list>
	<#list product.bundleGroups as bundleGroup>
		<#if bundleGroup.type?string != 'LIST' >
			<#list bundleGroup.bundleItems as bundleItem>
				<#if (bundleItem.withProductIds?? && bundleItem.withProductIds?size > 0) >
					<#list bundleItem.withProductIds as withProductId>
                    	<input type="hidden" name="condition" condition="${bundleItem.conditionType?string}" product="${bundleItem.product.id?string}" with="${withProductId?string}" />
					</#list>
				</#if>
			</#list>
		</#if>
	</#list>

	<!-- Filled dynamically via JavaScript -->
</div>
