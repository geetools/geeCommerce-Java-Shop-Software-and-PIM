<#import "${t_layout}/1column.ftl" as layout>
<#assign csrf=JspTaglibs["/WEB-INF/taglibs/csrfguard.tld"]>
<#assign f=JspTaglibs["http://geetools.com/jsp/geemvc/form"]/>
<#assign h=JspTaglibs["http://geetools.com/jsp/geemvc/html"]/>

<@session />

<@layout.onecolumn>

<div class="customer-review-container">
    <div class="row center-block">
        <div class="col-xs-12">
            <div class="row">

                <h1 class="h1"><@message text='Report Abuse' lang="en" text2="Missbrauch melden" lang2="de"/></h1>
                <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">

                    <@login_form postLoginRedirect="${redirectUrl}" />

                    <#if customerLoggedIn>
                        <@f.form action="${formAction}" method="post" ssl=true autocomplete="off" class="form-horizontal"
                        fieldGroupClass="form-group row"
                        fieldLabelClass="col-xs-12 col-sm-6 col-md-6 col-lg-6 control-label"
                        fieldWrapperClass="col-xs-12 col-sm-6 col-md-6 col-lg-6"
                        fieldClass="form-control"
                        fieldHintClass="help-block"
                        fieldErrorClass="help-block">

                            <input type="hidden" name="<@csrf.tokenname/>" value="<@csrf.tokenvalue uri="${formAction}"/>"/>

                            <@message text="Headline<sup>*</sup>" lang="en" text2="Titel<sup>*</sup>" lang2="de" var="headlineLabel"/>
                            <@f.text name="abuseForm.headline" id="review-headline" label="${headlineLabel}"/>

                            <@message text="Abuse content<sup>*</sup>" lang="en" text2="Missbrauch Inhalt<sup>*</sup>" lang2="de" var="contentLabel"/>
                            <@f.textarea name="abuseForm.text" style="height:300px;" id="review-text" label="${contentLabel}"/>

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-xs-12">
                                        <div class="col-xs-6"></div>
                                        <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">
                                            <@f.button name="submit" class="btn"><@message text="Report abuse" lang="en" text2="Missbrauch melden" lang2="de"/> </@f.button>
                                            <a href="/review/view/${product.id}"><@message text="Cancel" lang="en" text2="Stornieren" lang2="de"/></a>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </@f.form>

                    <#else>
                        <@message text="Please log in if you wish to report abuse." lang="en" text2="Bitte melden Sie sich an, um Missbrauch zu melden." lang2="de" />
                    </#if>

                </div>
            </div>
        </div>
    </div>
</div>

</@layout.onecolumn>
