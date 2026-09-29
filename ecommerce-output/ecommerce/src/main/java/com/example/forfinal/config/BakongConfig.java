package com.example.forfinal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BakongConfig {

    @Value("${bakong.merchant-name:My Store}")
    private String merchantName;

    @Value("${bakong.merchant-city:Phnom Penh}")
    private String merchantCity;

    @Value("${bakong.account-id:YOUR_BAKONG_ACCOUNT_ID}")
    private String accountId;

    @Value("${bakong.merchant-category-code:0000}")
    private String merchantCategoryCode;

    @Value("${bakong.currency:US}")
    private String currency;

    public String getMerchantName() { return merchantName; }
    public String getMerchantCity() { return merchantCity; }
    public String getAccountId() { return accountId; }
    public String getMerchantCategoryCode() { return merchantCategoryCode; }
    public String getCurrency() { return currency; }
}
