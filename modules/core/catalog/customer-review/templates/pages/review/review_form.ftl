<#import "${t_layout}/1column.ftl" as layout>
<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>
<#assign f=JspTaglibs["http://geetools.com/jsp/geemvc/form"]/>
<#assign h=JspTaglibs["http://geetools.com/jsp/geemvc/html"]/>

<@session />

<#assign productId=product.id>

<@layout.onecolumn>

<div class="customer-review-container">
    <div class="row center-block">
        <div class="col-xs-12">
            <div class="row">
                <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">

                    <#--   --@login_form postLoginRedirect="${redirectUrl}" / -->

                    <#if customerLoggedIn>
                        <@f.form action="${formAction}" method="post" ssl=true autocomplete="off" class="form-horizontal"
                        fieldGroupClass="form-group row"
                        fieldLabelClass="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label"
                        fieldWrapperClass="col-xs-12 col-sm-6 col-md-6 col-lg-6"
                        fieldClass="form-control"
                        fieldHintClass="help-block"
                        fieldErrorClass="help-block">

                            <input type="hidden" name="<@csrf.tokenname/>" value="<@csrf.tokenvalue uri="${formAction}"/>"/>


                            <div class="form-group">
                                <div class="row">
                                    <div class="col-xs-12 ">
                                        <label for="review-product-name" class="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label">
                                            <div><a href="<@uri target=product />"><@attribute src=product code="name" parent=true /></a></div>
                                            <div><a href="<@uri target=product />"><@attribute src=product code="name2" parent=true /></a></div>
                                        </label>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-xs-12 col-sm-offset-6">
                                        <div class="col-xs-12">
                                            <label for="review-star-rating" class="control-label">
                                                <@message text="Click the stars to rate it" lang="en" text2="Klicken Sie auf die Sterne, um zu bewerten" lang2="de" />
                                            </label>
                                        </div>

                                        <div class="col-xs-12">
                                            <@f.text type="hidden" name="reviewForm.rating" id="rating-saved" fieldOnly=true/>
                                            <input type="hidden" name="rating" value="${rating}"/>
                                            <#if rating??>
                                                <div class="rateit bigstars" data-rateit-value="${rating}" data-rateit-step="1" data-rateit-starwidth="32"
                                                     data-rateit-starheight="16"></div>
                                            <#else>
                                                <div class="rateit bigstars" data-rateit-step="1" data-rateit-starwidth="32" data-rateit-starheight="16"></div>
                                            </#if>
                                            <label class="rateit-label"/>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <@message text="Headline" lang="en" text2="Titel" lang2="de" var="titleLabel"/>
                            <@f.text name="reviewForm.headline" class="form-control" id="review-headline" label="${titleLabel}"/>

                            <@message text="Review" lang="en" text2="Rezension" lang2="de" var="reviewLabel"/>
                            <@f.textarea name="reviewForm.review" style="height:300px;"  id="review-text" label="${reviewLabel}"/>

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-xs-12">
                                        <div class="col-xs-6"></div>
                                        <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
                                            <@f.button name="submit" class="btn"><@message text="Submit review" lang="en" text2="Rezension abschicken" lang2="de"/> </@f.button>
                                            <a href="/review/customer/${customerId}"><@message text="Cancel" lang="en" text2="Stornieren" lang2="de"/></a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </@f.form>

                    <#else>
                        <@message text="Please log in if you wish to write a review." lang="en" text2="Bitte melden Sie sich an, um eine Kundenrezensionen zu schreiben." lang2="de" />
                    </#if>

                    <input id="review-rating-star-1" type="hidden" value='<@message text="I hate it" lang="en" text2="Ich hasse es" lang2="de"/>' />
                    <input id="review-rating-star-2" type="hidden" value='<@message text="I don't like it" lang="en" text2="Ich mag es nicht" lang2="de"/>' />
                    <input id="review-rating-star-3" type="hidden" value='<@message text="It's okay" lang="en" text2="Es ist okay" lang2="de"/>' />
                    <input id="review-rating-star-4" type="hidden" value='<@message text="I like it" lang="en" text2="Ich mag es" lang2="de"/>' />
                    <input id="review-rating-star-5" type="hidden" value='<@message text="I love it" lang="en" text2="Ich liebe es" lang2="de"/>' />

                </div>
            </div>
        </div>
    </div>
</div>


</@layout.onecolumn>
