package com.geecommerce.core.web;

import java.util.List;

import net.sourceforge.stripes.controller.ActionResolver;
import net.sourceforge.stripes.controller.AnnotatedClassActionResolver;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.controller.UrlBinding;
import net.sourceforge.stripes.controller.UrlBindingParameter;

import com.geecommerce.core.Str;

public abstract class BasePagingActionBean extends BaseActionBean {
    private int page = 1;

    private int limit = 0;

    public abstract long getTotalNumResults();

    public abstract int getDefaultNumResultsPerPage();

    public abstract int[] getNumResultsPerPageList();

    public String getPagingURI() {
        String pagingURI = null;

        ActionResolver resolver = StripesFilter.getConfiguration().getActionResolver();
        if (resolver instanceof AnnotatedClassActionResolver) {
            AnnotatedClassActionResolver aResover = (AnnotatedClassActionResolver) resolver;

            UrlBinding urlBinding = aResover.getUrlBindingFactory().getBinding(getContext().getRequest().getRequestURI());

            StringBuilder pagingURIBuilder = new StringBuilder(urlBinding.getPath());

            if (!urlBinding.getPath().endsWith(Str.SLASH))
                pagingURIBuilder.append(Str.SLASH);

            List<Object> components = urlBinding.getComponents();

            if (components.size() > 0) {
                for (Object component : components) {
                    if (component instanceof UrlBindingParameter) {
                        String v = ((UrlBindingParameter) component).getValue();
                        pagingURIBuilder.append(v == null ? "" : v);
                    } else {
                        pagingURIBuilder.append(component);
                    }
                }
            }

            pagingURI = pagingURIBuilder.toString();

            pagingURI = pagingURI.replace("//", Str.SLASH);
        }

        return pagingURI == null ? getContext().getRequest().getRequestURI() : pagingURI;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page > getNumPages() && getNumPages() != 0 ? getNumPages() : page;
    }

    public int getNumPages() {
        return (int) (getTotalNumResults() + getRealNumResultsPerPage() - 1) / getRealNumResultsPerPage();
    }

    public int getNumResultsPerPage() {
        String cookie = cookieGet("limit_per_page");
        return cookie != null ? Integer.valueOf(cookie) : getLimit() > 0 ? getLimit() : getDefaultNumResultsPerPage();
    }

    public int getRealNumResultsPerPage() {
        return getNumResultsPerPage();
    }

    public int getResultsFrom() {
        return getOffset() + 1;
    }

    public int getResultsTo() {
        int to = getOffset() + getRealNumResultsPerPage();

        return to > getTotalNumResults() ? (int) getTotalNumResults() : to;
    }

    public int getOffset() {
        return (getPage() - 1) * getRealNumResultsPerPage();
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

}
