package com.geecommerce.price.pojo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.service.AbstractPojo;
import com.geecommerce.core.service.annotation.Pojo;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.DateTimes;
import com.geecommerce.customer.CustomerConstant;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.price.helper.PriceHelper;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.model.PriceType;
import com.geecommerce.price.repository.PriceTypes;
import com.google.inject.Inject;

@Pojo
public class DefaultPriceResult extends AbstractPojo implements PriceResult {
    private static final long serialVersionUID = -217243530358946995L;
    private List<Price> priceList = new ArrayList<>();
    private Map<Id, List<Price>> childPriceLists = new HashMap<>();

    // Lazy loaded price types.
    private Map<String, PriceType> priceTypeMap = null;

    private Boolean hasAnyValidPrice = null;
    private boolean isInitialized = false;

    private final Map<PriceKey, Price> localCache = new HashMap<>();

    private static class PriceKey {
        private final String type;
        private final Integer forQty;
        private final PricingContext pricingCtx;
        private final Id storeId;
        private final Id customerId;
        private Set<Id> customerGroupIds;
        private List<Id> linkedProductIds;

        public PriceKey(String type, Integer forQty, PricingContext pricingCtx, Id storeId, Id customerId,
            Set<Id> customerGroupIds, List<Id> linkedProductIds) {
            this.type = type;
            this.forQty = forQty;
            this.pricingCtx = pricingCtx;
            this.storeId = storeId;
            this.customerId = customerId;
            this.customerGroupIds = customerGroupIds;
            this.linkedProductIds = linkedProductIds;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((linkedProductIds == null) ? 0 : linkedProductIds.hashCode());
            result = prime * result + ((customerGroupIds == null) ? 0 : customerGroupIds.hashCode());
            result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
            result = prime * result + ((forQty == null) ? 0 : forQty.hashCode());
            result = prime * result + ((pricingCtx == null) ? 0 : pricingCtx.hashCode());
            result = prime * result + ((storeId == null) ? 0 : storeId.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PriceKey other = (PriceKey) obj;
            if (customerGroupIds == null) {
                if (other.customerGroupIds != null)
                    return false;
            } else if (!customerGroupIds.equals(other.customerGroupIds))
                return false;
            if (customerId == null) {
                if (other.customerId != null)
                    return false;
            } else if (!customerId.equals(other.customerId))
                return false;
            if (forQty == null) {
                if (other.forQty != null)
                    return false;
            } else if (!forQty.equals(other.forQty))
                return false;
            if (pricingCtx == null) {
                if (other.pricingCtx != null)
                    return false;
            } else if (!pricingCtx.equals(other.pricingCtx))
                return false;
            if (storeId == null) {
                if (other.storeId != null)
                    return false;
            } else if (!storeId.equals(other.storeId))
                return false;
            if (type == null) {
                if (other.type != null)
                    return false;
            } else if (!type.equals(other.type))
                return false;
            if (linkedProductIds == null) {
                if (other.linkedProductIds != null)
                    return false;
            } else if (!linkedProductIds.equals(other.linkedProductIds))
                return false;
            return true;
        }
    }

    // Repositories
    private final transient PriceHelper priceHelper;
    private final transient PriceTypes priceTypes;

    @Inject
    public DefaultPriceResult(PriceTypes priceTypes, PriceHelper priceHelper) {
        super();
        this.priceTypes = priceTypes;
        this.priceHelper = priceHelper;
    }

    @Override
    public PriceResult init(List<Price> prices, List<Price> childPrices) {
        // Make sure that price-result object is immutable.
        if (isInitialized)
            throw new IllegalStateException("PriceResult object can only be initialized once!");

        isInitialized = true;

        priceTypeMap = priceTypes.priceTypes();

        initPrices(prices);
        initChildPrices(childPrices);

        return this;
    }

    protected void initPrices(List<Price> prices) {
        try {
            if (prices == null)
                return;

            Collection<Price> uniquePrices = prefilterPrices(prices);

            priceList.addAll(uniquePrices);

            Collections.sort(priceList,
                (Price p1, Price p2) -> p1.getPriceType().getPriority() == p2.getPriceType().getPriority() ? 0
                    : p1.getPriceType().getPriority() > p2.getPriceType().getPriority() ? 1 : -1);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected Collection<Price> prefilterPrices(List<Price> prices) {
        Map<PriceKey, Price> priceMap = new LinkedHashMap<>();

        for (Price price : prices) {
            Set<Id> customerGroupIds = new HashSet<>();
            customerGroupIds.add(price.getCustomerGroupId());

            PriceKey priceKey = new PriceKey(price.getTypeId().str(), price.getQtyFrom(), null, price.getStoreId(),
                price.getCustomerId(), customerGroupIds, price.getWithProductIds());

            Price p = priceMap.get(priceKey);

            Date currentValidFrom = price.getValidFrom();
            Date currentValidTo = price.getValidTo();

            // Simplest case - just add the price to the map.
            if (p == null && currentValidFrom == null && currentValidTo == null) {
                priceMap.put(priceKey, price);
                continue;
            }

            Date previousValidFrom = null;

            if (p != null)
                previousValidFrom = p.getValidFrom();

            if (currentValidFrom != null) {
                boolean isCurrentDateFromValid = isDateFromValid(currentValidFrom);
                boolean isCurrentDateToValid = isDateToValid(currentValidTo)
                    || (isCurrentDateFromValid && currentValidTo == null);

                if (!isCurrentDateFromValid || !isCurrentDateToValid)
                    continue;

                if (isCurrentDateFromValid && isCurrentDateToValid && (p == null || previousValidFrom == null)) {
                    priceMap.put(priceKey, price);
                    continue;
                }

                if (previousValidFrom != null) {
                    int previousValidSinceDays = numDaysValid(previousValidFrom);
                    int currentValidSinceDays = numDaysValid(currentValidFrom);

                    if (currentValidSinceDays < previousValidSinceDays) {
                        priceMap.put(priceKey, price);
                        continue;
                    } else if (currentValidSinceDays == previousValidSinceDays) {
                        if (price.getCreatedOn() != null && p.getCreatedOn() != null
                            && price.getCreatedOn().after(p.getCreatedOn())) {
                            priceMap.put(priceKey, price);
                            continue;
                        }
                    }
                }
            }
            // If the same type of date exists, then choose the newer one.
            else if (p != null && currentValidFrom == null && previousValidFrom == null) {
                if (price.getCreatedOn() != null && p.getCreatedOn() != null
                    && price.getCreatedOn().after(p.getCreatedOn())) {
                    priceMap.put(priceKey, price);
                    continue;
                }
            }
        }

        return priceMap.values();
    }

    protected boolean isDateFromValid(Date from) {
        Date now = DateTimes.newMidnightDate();
        return from != null && (now.getTime() == from.getTime() || now.after(from));
    }

    protected boolean isDateToValid(Date to) {
        Date now = DateTimes.newMidnightDate();
        return to != null && (now.getTime() == to.getTime() || now.before(to));
    }

    protected int numDaysValid(Date from) {
        Date now = DateTimes.newMidnightDate();
        return (int) Math.floor((now.getTime() - from.getTime()) / (1000 * 60 * 60 * 24));
    }

    protected PriceResult initChildPrices(List<Price> childPrices) {
        if (childPrices != null && childPrices.size() > 0) {
            Collections.sort(childPrices,
                (Price p1, Price p2) -> p1.getPriceType().getPriority() == p2.getPriceType().getPriority() ? 0
                    : p1.getPriceType().getPriority() > p2.getPriceType().getPriority() ? 1 : -1);

            if (childPriceLists == null)
                childPriceLists = new LinkedHashMap<Id, List<Price>>();

            for (Price childPrice : childPrices) {
                List<Price> childPriceList = childPriceLists.get(childPrice.getProductId());

                if (childPriceList == null) {
                    childPriceList = new ArrayList<>();
                    childPriceLists.put(childPrice.getProductId(), childPriceList);
                }

                childPriceList.add(childPrice);
            }
        }

        return this;
    }

    @Override
    public Double getPrice(String type) {
        return getPrice(type, 0);
    }

    @Override
    public Double getPrice(String type, Integer forQty) {
        return getPrice(type, forQty, null);
    }

    @Override
    public Double getPrice(String type, Integer forQty, PricingContext pricingCtx) {
        if (priceList == null || priceList.size() == 0)
            return null;

        Price p = findPrice(type, forQty, priceList, pricingCtx);

        return p == null ? null : p.getPrice();
    }

    @Override
    public Double getFinalPrice() {
        return getFinalPrice(0);
    }

    @Override
    public Double getFinalPrice(PricingContext pricingCtx) {
        return getFinalPrice(0, pricingCtx);
    }

    @Override
    public Double getFinalPrice(Integer forQty) {
        return getFinalPrice(forQty, (PricingContext) null);
    }

    @Override
    public Double getFinalPrice(Integer forQty, PricingContext pricingCtx) {
        Price finalPrice = getFinalPriceFor(forQty, pricingCtx);

        return finalPrice == null ? null : finalPrice.getPrice();
    }

    @Override
    public Price getFinalPriceFor(Integer qty) {
        return getFinalPriceFor(qty, (PricingContext) null);
    }

    @Override
    public Price getFinalPriceFor(Integer qty, PricingContext pricingCtx) {
        Set<String> priceTypes = priceTypeMap.keySet();

        Price finalPrice = null;

        for (String priceType : priceTypes) {

            Price price = findPrice(priceType, qty, priceList, pricingCtx);

            if (price != null && price.getPrice() >= 0) {
                finalPrice = price;
                break;
            }
        }

        return finalPrice;
    }

    @Override
    public Double getLowestFinalPrice() {
        return getLowestFinalPrice((PricingContext) null);
    }

    @Override
    public Double getLowestFinalPrice(PricingContext pricingCtx) {
        return getLowestFinalPrice(0, pricingCtx);
    }

    @Override
    public Double getLowestFinalPrice(Integer forQty) {
        return getLowestFinalPrice(forQty, (PricingContext) null);
    }

    @Override
    public Double getLowestFinalPrice(Integer forQty, PricingContext pricingCtx) {
        Price lowestFinalPrice = getLowestFinalPriceFor(forQty, pricingCtx);

        return lowestFinalPrice == null ? null : lowestFinalPrice.getPrice();
    }

    public Price getLowestFinalPriceFor() {
        return getLowestFinalPriceFor(0, (PricingContext) null);
    }

    @Override
    public Price getLowestFinalPriceFor(Integer qty) {
        return getLowestFinalPriceFor(qty, (PricingContext) null);
    }

    @Override
    public Price getLowestFinalPriceFor(PricingContext pricingCtx) {
        return getLowestFinalPriceFor(0, pricingCtx);
    }

    @Override
    public Price getLowestFinalPriceFor(Integer qty, PricingContext pricingCtx) {
        long start = System.currentTimeMillis();

        Price lowestFinalPriceObj = getFinalPriceFor(qty, pricingCtx);
        Double lowestFinalPrice = lowestFinalPriceObj == null ? null : lowestFinalPriceObj.getPrice();

        if (childPriceLists.size() > 0) {
            Set<String> priceTypes = priceTypeMap.keySet();

            Set<Id> productIds = childPriceLists.keySet();

            for (Id productId : productIds) {
                Double childFinalPrice = null;
                Price childFinalPriceObj = null;

                List<Price> childPriceList = childPriceLists.get(productId);

                for (String priceType : priceTypes) {
                    Price price = findPrice(priceType, qty, childPriceList, pricingCtx);

                    if (price != null && price.getPrice() >= 0) {
                        childFinalPrice = price.getPrice();
                        childFinalPriceObj = price;
                        break;
                    }
                }

                if (childFinalPrice != null && (lowestFinalPrice == null || lowestFinalPrice > childFinalPrice)) {
                    lowestFinalPrice = childFinalPrice;
                    lowestFinalPriceObj = childFinalPriceObj;
                }
            }
        }

        // System.out.println("getLowestFinalPrice TIME: " +
        // (System.currentTimeMillis() - start));

        return lowestFinalPriceObj;
    }

    @Override
    public Double getHighestFinalPrice() {
        return getHighestFinalPrice((PricingContext) null);
    }

    @Override
    public Double getHighestFinalPrice(PricingContext pricingCtx) {
        return getHighestFinalPrice(0, pricingCtx);
    }

    @Override
    public Double getHighestFinalPrice(Integer forQty) {
        return getHighestFinalPrice(forQty, (PricingContext) null);
    }

    @Override
    public Double getHighestFinalPrice(Integer forQty, PricingContext pricingCtx) {
        Price highestFinalPrice = getHighestFinalPriceFor(forQty, pricingCtx);

        return highestFinalPrice == null ? null : highestFinalPrice.getPrice();
    }

    @Override
    public Price getHighestFinalPriceFor(Integer qty) {
        return getHighestFinalPriceFor(qty, (PricingContext) null);
    }

    @Override
    public Price getHighestFinalPriceFor(Integer qty, PricingContext pricingCtx) {
        long start = System.currentTimeMillis();

        Price highestFinalPriceObj = getFinalPriceFor(qty);
        Double highestFinalPrice = highestFinalPriceObj == null ? null : highestFinalPriceObj.getPrice();

        if (childPriceLists.size() > 0) {
            Set<String> priceTypes = priceTypeMap.keySet();

            Set<Id> productIds = childPriceLists.keySet();

            for (Id productId : productIds) {
                Double childFinalPrice = null;
                Price childFinalPriceObj = null;

                List<Price> childPriceList = childPriceLists.get(productId);

                for (String priceType : priceTypes) {
                    Price price = findPrice(priceType, qty, childPriceList, pricingCtx);

                    if (price != null && price.getPrice() >= 0) {
                        childFinalPrice = price.getPrice();
                        childFinalPriceObj = price;
                        break;
                    }
                }

                if (childFinalPrice != null && (highestFinalPrice == null || highestFinalPrice < childFinalPrice)) {
                    highestFinalPrice = childFinalPrice;
                    highestFinalPriceObj = childFinalPriceObj;
                }
            }
        }

        // System.out.println("getHighestFinalPrice TIME: " +
        // (System.currentTimeMillis() - start));

        return highestFinalPriceObj;
    }

    @Override
    public boolean hasMultipleFinalPrices() {
        Double lowestPrice = getLowestFinalPrice();
        Double highestPrice = getHighestFinalPrice();

        double lp = lowestPrice == null ? 0 : lowestPrice.doubleValue();
        double hp = highestPrice == null ? 0 : highestPrice.doubleValue();

        return lp != hp;
    }

    @Override
    public boolean hasAnyValidPrice() {
        Price finalPrice = getFinalPriceFor(0);

        if (finalPrice != null)
            return true;

        if (this.hasAnyValidPrice != null)
            return hasAnyValidPrice.booleanValue();

        boolean anyValidPrice = false;

        if (childPriceLists.size() > 0) {
            Set<String> priceTypes = priceTypeMap.keySet();

            Set<Id> productIds = childPriceLists.keySet();

            for (Id productId : productIds) {
                List<Price> childPriceList = childPriceLists.get(productId);

                for (String priceType : priceTypes) {
                    Price price = findPrice(priceType, 0, childPriceList, null);

                    if (price != null && price.getPrice() >= 0) {
                        anyValidPrice = true;
                        break;
                    }
                }

                if (anyValidPrice)
                    break;
            }
        }

        this.hasAnyValidPrice = anyValidPrice;

        return anyValidPrice;
    }

    @Override
    public boolean hasValidPrice() {
        Price finalPrice = getFinalPriceFor(0);

        if (finalPrice != null)
            return true;

        return false;
    }

    @Override
    public Map<String, Double> getValidPrices() {
        return getValidPrices((PricingContext) null);
    }

    @Override
    public Map<String, Double> getValidPrices(PricingContext pricingCtx) {
        return getValidPrices(0, priceList, pricingCtx);
    }

    @Override
    public Map<String, Double> getValidPrices(Integer forQty) {
        return getValidPrices(forQty, priceList, (PricingContext) null);
    }

    @Override
    public Map<String, Double> getValidPrices(Integer forQty, PricingContext pricingCtx) {
        return getValidPrices(forQty, priceList, pricingCtx);
    }

    protected Map<String, Double> getValidPrices(Integer forQty, List<Price> priceList, PricingContext pricingCtx) {
        // long start = System.currentTimeMillis();

        Map<String, Double> validPrices = new HashMap<>();

        Set<String> priceTypes = priceTypeMap.keySet();

        for (String priceType : priceTypes) {
            Price price = findPrice(priceType, forQty, priceList, pricingCtx);

            if (price != null && price.getPrice() >= 0) {
                validPrices.put(priceType, price.getPrice());
            }
        }

        // System.out.println("PRICE TIME: " +
        // (System.currentTimeMillis()-start));

        return validPrices;
    }

    @Override
    public Map<String, Double> getLowestValidPrices() {
        return getLowestValidPrices((PricingContext) null);
    }

    @Override
    public Map<String, Double> getLowestValidPrices(PricingContext pricingCtx) {
        return getLowestValidPrices(0, pricingCtx);
    }

    @Override
    public Map<String, Double> getLowestValidPrices(Integer forQty) {
        return getLowestValidPrices(forQty, (PricingContext) null);
    }

    @Override
    public Map<String, Double> getLowestValidPrices(Integer forQty, PricingContext pricingCtx) {
        Price lowestFinalPrice = getLowestFinalPriceFor(forQty);

        return lowestFinalPrice == null ? null
            : getValidPrices(forQty, childPriceLists.get(lowestFinalPrice.getProductId()), pricingCtx);
    }

    protected Price findPrice(String type, Integer forQty, List<Price> priceList, PricingContext pricingCtx) {
        if (type == null || forQty == null || priceList == null)
            return null;

        ApplicationContext appCtx = app.context();
        RequestContext reqCtx = appCtx.getRequestContext();
        Id storeId = reqCtx.getStoreId();

        if (pricingCtx == null)
            pricingCtx = priceHelper.getPricingContext();
        
        String currency = pricingCtx.getCurrency();

        if (currency == null)
            return null;

        Set<Id> customerGroupIds = new HashSet<>();

        Customer c = app.getLoggedInCustomer();
        Id customerId = c == null ? null : c.getId();

        if (c != null) {
            customerGroupIds.addAll(c.getCustomerGroupIds());
        } else {
            // Get the price of the
            Long visitorCustomerGroupId = app.cpLong_(CustomerConstant.CUSTOMER_GROUP_DEFAULT_VISITOR_CONFIG_KEY);
            customerGroupIds.add(Id.valueOf(visitorCustomerGroupId));
        }

        Map<Id, List<Id>> linkedProductIds = pricingCtx.getLinkedProductIds();

        // TODO
//        PriceKey priceKey = new PriceKey(type, forQty, pricingCtx, storeId, customerId, customerGroupIds, linkedProductIds);

        // if (localCache.containsKey(cacheKey))
        // return localCache.get(cacheKey);

        /* Tier prices */
        Price tierPriceForCustomerAndStore = null;
        Price tierPriceForCustomer = null;
        Price tierPriceForCustomerGroupAndStore = null;
        Price tierPriceForCustomerGroup = null;
        Price tierPriceForReqCtx = null;
        Price tierPrice = null;

        /* None tier prices */
        Price priceForCustomerAndStore = null;
        Price priceForCustomer = null;
        Price priceForCustomerGroupAndStore = null;
        Price priceForCustomerGroup = null;
        Price priceForStore = null;
        Price price = null;

        Integer tierQtyDiff = null;

        List<Price> preFilteredPriceList = new ArrayList<>();

        for (Price p : priceList) {
            if (!type.equals(p.getPriceType().getCode()) || !currency.equals(p.getCurrency()))
                continue;

            if (pricingCtx != null && !pricingCtx.isPriceAvailable(p))
                continue;

            preFilteredPriceList.add(p);
        }

        if (preFilteredPriceList.isEmpty())
            return null;

        boolean hasCombinedProductPrice = hasCombinedProductPrice(preFilteredPriceList, linkedProductIds);

        if (!hasCombinedProductPrice) {
            preFilteredPriceList = removeCombinedProductPrices(preFilteredPriceList);

            if (preFilteredPriceList.isEmpty())
                return null;

            return preFilteredPriceList.get(0);
        } else {
            return findCombinedPrice(preFilteredPriceList, linkedProductIds);
        }

        // TODO: implement customer and tier prices.
        // for (Price p : preFilteredPriceList) {
        // if (p != null)
        // return p;
        //
        // //
        // --------------------------------------------------------------------------------
        // // Tier-price
        // //
        // --------------------------------------------------------------------------------
        //
        // // See if there is a tier price for the given quantity.
        // if (forQty != null && p.getQtyFrom() != null && p.getQtyFrom() > 0 &&
        // forQty >= p.getQtyFrom()
        // && (tierQtyDiff == null || (forQty - p.getQtyFrom()) <= tierQtyDiff))
        // {
        // tierQtyDiff = forQty - p.getQtyFrom();
        //
        // // Is there a customer-specific tier price?
        // if (p.getCustomerId() != null &&
        // p.getCustomerId().equals(customerId)) {
        // // Are there different prices for various contexts?
        // if (p.getStoreId() != null && p.getStoreId().equals(storeId)) {
        // tierPriceForCustomerAndStore = p;
        // } else if (p.getStoreId() == null) {
        // tierPriceForCustomer = p;
        // }
        // }
        // // Is there a customer-group-specific tier price?
        // else if (p.getCustomerGroupId() != null &&
        // p.getCustomerGroupId().longValue() != 0
        // && customerGroupIds != null &&
        // customerGroupIds.contains(p.getCustomerGroupId())) {
        // // Are there different prices for various contexts?
        // if (p.getStoreId() != null && p.getStoreId().equals(storeId)) {
        // tierPriceForCustomerGroupAndStore = p;
        // } else if (p.getStoreId() == null) {
        // tierPriceForCustomerGroup = p;
        // }
        // } else if (p.getCustomerId() == null
        // && (p.getCustomerGroupId() == null ||
        // p.getCustomerGroupId().longValue() == 0)) {
        // // Are there different prices for various contexts?
        // if (p.getStoreId() != null && p.getStoreId().equals(storeId)) {
        // tierPriceForReqCtx = p;
        // }
        // // A tier price for all exists.
        // else if (p.getStoreId() == null) {
        // tierPrice = p;
        // }
        // }
        // }
        //
        // //
        // --------------------------------------------------------------------------------
        // // None tier-price
        // //
        // --------------------------------------------------------------------------------
        // else if (p.getQtyFrom() == null || p.getQtyFrom() == 0) {
        // // Is there a customer-specific price?
        // if (p.getCustomerId() != null &&
        // p.getCustomerId().equals(customerId)) {
        // // Are there different prices for various contexts?
        // if (p.getStoreId() != null && p.getStoreId().equals(storeId)) {
        // priceForCustomerAndStore = p;
        // } else if (p.getStoreId() == null) {
        // priceForCustomer = p;
        // }
        // }
        // // Is there a customer-group-specific price?
        // else if (p.getCustomerGroupId() != null &&
        // p.getCustomerGroupId().longValue() != 0
        // && customerGroupIds != null &&
        // customerGroupIds.contains(p.getCustomerGroupId())) {
        // // Are there different prices for various contexts?
        // if (p.getStoreId() != null && p.getStoreId().equals(storeId)) {
        // priceForCustomerGroupAndStore = p;
        // } else if (p.getStoreId() == null) {
        // priceForCustomerGroup = p;
        // }
        // } else if (p.getCustomerId() == null
        // && (p.getCustomerGroupId() == null ||
        // p.getCustomerGroupId().longValue() == 0)) {
        // // Are there different prices for various contexts?
        // if (p.getStoreId() != null && p.getStoreId().equals(storeId)) {
        // priceForStore = p;
        // }
        // // A tier price for all exists.
        // else if (p.getStoreId() == null) {
        // price = p;
        // }
        // }
        // }
        // }
        //
        // if (1 == 1)
        // return null;
        //
        // // Most specific price: tier-price for customer and store.
        // if (tierPriceForCustomerAndStore != null) {
        // localCache.put(priceKey, tierPriceForCustomerAndStore);
        // return tierPriceForCustomerAndStore;
        // }
        //
        // // Second most specific price: tier-price for customer.
        // if (tierPriceForCustomer != null) {
        // localCache.put(priceKey, tierPriceForCustomer);
        // return tierPriceForCustomer;
        // }
        //
        // // Third most specific price: tier-price for customer-group and
        // store.
        // if (tierPriceForCustomerGroupAndStore != null) {
        // localCache.put(priceKey, tierPriceForCustomerGroupAndStore);
        // return tierPriceForCustomerGroupAndStore;
        // }
        //
        // // Tier-price for customer-group.
        // if (tierPriceForCustomerGroup != null) {
        // localCache.put(priceKey, tierPriceForCustomerGroup);
        // return tierPriceForCustomerGroup;
        // }
        //
        // // Tier-price for request-context.
        // if (tierPriceForReqCtx != null) {
        // localCache.put(priceKey, tierPriceForReqCtx);
        // return tierPriceForReqCtx;
        // }
        //
        // // Tier-price for no particular customer, customer-group or store.
        // if (tierPrice != null) {
        // localCache.put(priceKey, tierPrice);
        // return tierPrice;
        // }
        //
        // // Price for customer and request-context.
        // if (priceForCustomerAndStore != null) {
        // localCache.put(priceKey, priceForCustomerAndStore);
        // return priceForCustomerAndStore;
        // }
        //
        // // Price for customer.
        // if (priceForCustomer != null) {
        // localCache.put(priceKey, priceForCustomer);
        // return priceForCustomer;
        // }
        //
        // // Price for customer-group and store.
        // if (priceForCustomerGroupAndStore != null) {
        // localCache.put(priceKey, priceForCustomerGroupAndStore);
        // return priceForCustomerGroupAndStore;
        // }
        //
        // // Price for customer-group.
        // if (priceForCustomerGroup != null) {
        // localCache.put(priceKey, priceForCustomerGroup);
        // return priceForCustomerGroup;
        // }
        //
        // // Price for store.
        // if (priceForStore != null) {
        // localCache.put(priceKey, priceForStore);
        // return priceForStore;
        // }
        //
        // localCache.put(priceKey, price);
        //
        // // If none of the above exist, then just return the standard price.
        // return price;
    }

    protected List<Price> removeCombinedProductPrices(List<Price> preFilteredPriceList) {
        List<Price> newPriceList = new ArrayList<>();

        for (Price price : preFilteredPriceList) {
            if (price.getWithProductIds() == null || price.getWithProductIds().isEmpty()) {
                newPriceList.add(price);
            }
        }

        return newPriceList;
    }

    protected Price findCombinedPrice(List<Price> preFilteredPriceList, Map<Id, List<Id>> linkedProductIds) {
        Price p = null;

        for (Price price : preFilteredPriceList) {
            if (isValidCombinedProductPrice(price, linkedProductIds.get(price.getProductId()))) {
                p = price;
                break;
            }
        }

        return p;
    }

    protected boolean hasCombinedProductPrice(List<Price> preFilteredPriceList, Map<Id, List<Id>> linkedProductIds) {
        for (Price price : preFilteredPriceList) {
            if (isValidCombinedProductPrice(price, linkedProductIds.get(price.getProductId())))
                return true;
        }

        return false;
    }

    protected boolean isValidCombinedProductPrice(Price price, List<Id> linkedProductIds) {
        List<Id> withProductIds = price.getWithProductIds();

        if (linkedProductIds == null || linkedProductIds.isEmpty() || withProductIds == null || withProductIds.isEmpty())
            return false;

        for (Id withProductId : withProductIds) {
            if (!linkedProductIds.contains(withProductId))
                return false;
        }

        return true;
    }
}
